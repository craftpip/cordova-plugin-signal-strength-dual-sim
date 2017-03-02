package org.apache.cordova.plugin;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class SignalStrengthDualSim extends CordovaPlugin {

    int simSlot = 0;
    // MultiSimTelephonyManager multiSimTelephonyManager1;
    // MultiSimTelephonyManager multiSimTelephonyManager2;

    SignalStrengthStateListener ssListener;
    int dbm = -1;
    TelephonyManager mTelephonyManager;
    SubscriptionManager mSubscriptionManager;


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {


            mTelephonyManager = TelephonyManager.from(Context);
            mSubscriptionManager = SubscriptionManager.from(Context);

            List<SubscriptionInfo> subscriptions = mSubscriptionManager.getActiveSubscriptionInfoList();

            if (subscriptions == null){
                return false;
            }

            final int num = subscriptions.size();
            if (num <= 0)
                return false;

            if(action.equals("0")){
                ssListener = new SignalStrengthStateListener(subscriptions.get(0).getSubscriptionId());
            }
            if(action.equals("1")){
                ssListener = new SignalStrengthStateListener(subscriptions.get(1).getSubscriptionId());
            }

            mTelephonyManager.listen(ssListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

            int counter = 0;
            while ( dbm == -1) {
                    try {
                            Thread.sleep(200);
                    } catch (InterruptedException e) {
                            e.printStackTrace();
                    }
                    if (counter++ >= 5)
                    {
                            break;
                    }
            }
            callbackContext.success(dbm);
            return true;
    }


    class SignalStrengthStateListener extends PhoneStateListener {
        public SignalStrengthStateListener(int subId) { 
            super(subId); 
        } 
        @Override
        public void onSignalStrengthsChanged(android.telephony.SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                int tsNormSignalStrength = signalStrength.getGsmSignalStrength();
                dbm = (2 * tsNormSignalStrength) - 113;     // -> dBm
        }

    }

}