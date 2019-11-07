package com.example.yangy.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
//import android.widget.Toast;
import com.yang.basic.LogUtils;

/**
 * Created by hgx on 2016/6/13.
 */
public class PhoneCallReceiver extends BroadcastReceiver {
    private static final String TAG = "PhoneCallReceiver";
    private static int lastCallState = TelephonyManager.CALL_STATE_IDLE;
    private static boolean isCalling = false;
    public PhoneCallReceiver() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        //如果是去电
        //LogUtils.d(TAG, "intent.getAction() = " + intent.getAction());
        String contactNum = null;
        if (Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())){
            contactNum = intent.getExtras().getString(Intent.EXTRA_PHONE_NUMBER);
            LogUtils.d(TAG, "ACTION_NEW_OUTGOING_CALL, contactNum = " + contactNum);
        }else
        {
            String state = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String phoneNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            LogUtils.d(TAG, "state = " + state + " phoneNumber = " + phoneNumber);
            if(phoneNumber == null){
                return;
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){//挂机状态
                //如果是响铃状态，则现在为来电挂机
                if (lastCallState == TelephonyManager.CALL_STATE_RINGING){
                    onIncomingCallEnded(context,phoneNumber);
                    lastCallState = TelephonyManager.CALL_STATE_IDLE;
                }else {//如果不是响铃状态，则现在是去电挂机
                    onOutgoingCallEnded(context,phoneNumber);
                }
            }else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){//接听状态

                if (lastCallState == TelephonyManager.CALL_STATE_RINGING){
                    onIncomingCallAnswered(context, phoneNumber);
                }else{//如果不是响铃状态，则现在是去电
                    onOutgoingCallStarted(context,phoneNumber);
                }
            }else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                //来电响铃状态
                lastCallState = TelephonyManager.CALL_STATE_RINGING;
                onIncomingCallReceived(context,phoneNumber);
            }
        }
    }
    protected void onIncomingCallStarted(Context context,String number){
        //Toast.makeText(context,"Incoming call is started",Toast.LENGTH_LONG).show();
        LogUtils.d(TAG, "Incoming call is started, phone number = " + number);
        //Intent i = new Intent(context, CallbackService.class);
        //i.putExtra("phone", number);
        //context.startService(i);
    }

    protected void onIncomingCallEnded(Context context,String number){
        //Toast.makeText(context, "Incoming call is ended", Toast.LENGTH_LONG).show();
        LogUtils.d(TAG, "Incoming call is ended");
        context.stopService(new Intent(context, CallbackService.class));
    }

    protected void onIncomingCallReceived(Context context,String number){
        //Toast.makeText(context, "Incoming call is received", Toast.LENGTH_LONG).show();
        LogUtils.d(TAG, "Incoming call is received, phone number = " + number);
        if(number != null) {
            Intent i = new Intent(context, CallbackService.class);
            i.putExtra("phone", number);
            context.startService(i);
        }
    }
    protected void onIncomingCallAnswered(Context context, String number) {
        //Toast.makeText(context, "Incoming call is answered", Toast.LENGTH_LONG).show();
        LogUtils.d(TAG, "Incoming call is answered, number = " + number);
    }

    protected void onOutgoingCallStarted(Context context,String number){
        //Toast.makeText(context, "Outgoing call is started", Toast.LENGTH_LONG).show();
        LogUtils.d(TAG, "Outgoing call is started, number = " + number);
        Intent i = new Intent(context, AudioRecorderService.class);
        i.putExtra("phone", number);
        context.startService(i);
    }

    protected void onOutgoingCallEnded(Context context,String number){
        //Toast.makeText(context, "Outgoing call is ended", Toast.LENGTH_LONG).show();
        LogUtils.d(TAG, "Outgoing call is ended, number = " + number);
        Intent mIntent=new Intent("AsrStart");
        context.sendBroadcast(mIntent);
        context.stopService(new Intent(context, AudioRecorderService.class));
    }
}
