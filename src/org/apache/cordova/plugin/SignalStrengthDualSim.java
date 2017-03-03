package org.apache.cordova.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.LOG;

import android.content.Context;

import android.content.pm.PackageManager;
import android.os.Build;
import android.Manifest;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class SignalStrengthDualSim extends CordovaPlugin {
    // MultiSimTelephonyManager multiSimTelephonyManager1;
    // MultiSimTelephonyManager multiSimTelephonyManager2;

    SignalStrengthStateListener ssListener;
    int dbm = -1;
    TelephonyManager mTelephonyManager;
    SubscriptionManager mSubscriptionManager;

    private static final String LOG_TAG = "CordovaPluginSignalStrengthDualSim";
    private static final String SIM_ONE_ASU = "Sim1";
    private static final String SIM_TWO_ASU = "Sim2";
    private static final String SIM_COUNT = "SimCount";
    private static final String HAS_READ_PERMISSION = "hasReadPermission";
    private static final String REQUEST_READ_PERMISSION = "requestReadPermission";
    private CallbackContext callback;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        callback = callbackContext;

        LOG.i(LOG_TAG, "STARTING");
        LOG.i(LOG_TAG, "Params: " + action);


        if (SIM_COUNT.equals(action)) {

            List<SubscriptionInfo> subscriptions = mSubscriptionManager.getActiveSubscriptionInfoList();
            if (subscriptions == null) {
                this.callback.error("Subscriptions returned null");
                return false;
            }

            final int num = subscriptions.size();
            this.callback.success(num);

        } else if (SIM_ONE_ASU.equals(action) || SIM_TWO_ASU.equals(action)) {

            mSubscriptionManager = (SubscriptionManager) cordova.getActivity().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            List<SubscriptionInfo> subscriptions = mSubscriptionManager.getActiveSubscriptionInfoList();

            if (subscriptions == null) {
                return false;
            }

            final int num = subscriptions.size();
            if (num <= 0)
                return false;

            LOG.i(LOG_TAG, "Num: " + num);

            int subId = 0;
            if (action.equals("0")) {
                subId = subscriptions.get(0).getSubscriptionId();
            }
            if (action.equals("1")) {
                subId = subscriptions.get(1).getSubscriptionId();
            }

            LOG.i(LOG_TAG, "SubID: " + subId);

            ssListener = new SignalStrengthStateListener();
            mTelephonyManager = (TelephonyManager) cordova.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            TelephonyManager mTelephonyManager1 = mTelephonyManager.createForSubscriptionId(subId);
            mTelephonyManager1.listen(ssListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

            int counter = 0;
            while (dbm == -1) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    return false;
                }
                if (counter++ >= 5) {
                    break; // return -1
                }
            }

            LOG.i(LOG_TAG, "Dbm: " + dbm);
            callbackContext.success(dbm);
            return true;
        } else if (HAS_READ_PERMISSION.equals(action)) {
            hasReadPermission();
            return true;
        } else if (REQUEST_READ_PERMISSION.equals(action)) {
            requestReadPermission();
            return true;
        } else {
            return false;
        }
    }


    private void hasReadPermission() {
        this.callback.sendPluginResult(new PluginResult(PluginResult.Status.OK,
                simPermissionGranted(Manifest.permission.READ_PHONE_STATE)));
    }

    private void requestReadPermission() {
        requestPermission(Manifest.permission.READ_PHONE_STATE);
    }

    private boolean simPermissionGranted(String type) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        return cordova.hasPermission(type);
    }

    private void requestPermission(String type) {
        LOG.i(LOG_TAG, "requestPermission");
        if (!simPermissionGranted(type)) {
            cordova.requestPermission(this, 12345, type);
        } else {
            this.callback.success();
        }
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            this.callback.success();
        } else {
            this.callback.error("Permission denied");
        }
    }

    class SignalStrengthStateListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(android.telephony.SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            int tsNormSignalStrength = signalStrength.getGsmSignalStrength();
            dbm = (2 * tsNormSignalStrength) - 113;     // -> dBm
        }

    }

}