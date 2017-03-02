package org.apache.cordova.plugin;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
// import android.telephony.MultiSimTelephonyManager;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignalStrengthDualSim extends CordovaPlugin {

    int simSlot = 0;
    // MultiSimTelephonyManager multiSimTelephonyManager1;
    // MultiSimTelephonyManager multiSimTelephonyManager2;

    SignalStrengthStateListener ssListener;
    int dbm = -1;


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

    // MultiSimClass = Class.forName("android.telephony.MultiSimTelephonyManager");
    // for (Constructor<?> constructor : MultiSimClass.getConstructors()){
    //      if (constructor.getParameterTypes().length == 2){
    //           try {
    //     //     if(action.equals("0")){
    //     //            multiSimTelephonyManager1 = constructor.newInstance(context,0);
    //     //     }else{
    //     //            multiSimTelephonyManager2 = constructor.newInstance(context,1);
    //     //     }
    //           }
    //           catch (InterruptedException e) {
    //     //   e.printStackTrace();
    //           }
    //      }
    // }

            ssListener = new SignalStrengthStateListener();

             final Class<?> tmClass = Class.forName("android.telephony.MultiSimTelephonyManager");
                // MultiSimTelephonyManager Class found
                // getDefault() gets the manager instances for specific slots
                Method methodDefault = tmClass.getDeclaredMethod("getDefault", int.class);
                methodDefault.setAccessible(true);
                try {
                    if(action.equals("0")){
                        MultiSimTelephonyManager telephonyManagerMultiSim = (MultiSimTelephonyManager)methodDefault.invoke(null, 0);
                    }
                    if(action.equals("1")){
                        MultiSimTelephonyManager telephonyManagerMultiSim = (MultiSimTelephonyManager)methodDefault.invoke(null, 1);
                    }
                    telephonyManagerMultiSim.listen(ssListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
                } catch (ArrayIndexOutOfBoundsException e) {
                    return false;
                }


          //  if (action.equals("0")) {
          //          multiSimTelephonyManager1.listen(ssListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
         //   }
         //   if (action.equals("1")) {
         //           multiSimTelephonyManager2.listen(ssListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
         //   }

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

        @Override
        public void onSignalStrengthsChanged(android.telephony.SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                int tsNormSignalStrength = signalStrength.getGsmSignalStrength();
                dbm = (2 * tsNormSignalStrength) - 113;     // -> dBm
        }

    }

}