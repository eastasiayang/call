package com.example.yangy.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.yang.basic.LogUtils;

/**
 * Created by hgx on 2016/6/13.
 */
public class PhoneCallReceiver extends BroadcastReceiver {
    private static final String TAG = "PhoneCallReceiver";
    private int lastCallState = TelephonyManager.CALL_STATE_IDLE;
    private boolean isIncoming = false;
    private static String contactNum;
    Intent audioRecorderService;
    public PhoneCallReceiver() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        //如果是去电
        LogUtils.d(TAG, "intent.getAction() = " + intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
            LogUtils.d(TAG, "ACTION_NEW_OUTGOING_CALL");
            contactNum = intent.getExtras().getString(Intent.EXTRA_PHONE_NUMBER);
        }else
        {
            String state = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String phoneNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            LogUtils.d(TAG, "state = " + state + " phoneNumber = " + phoneNumber);
            int stateChange = 0;
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                //空闲状态
                stateChange =TelephonyManager.CALL_STATE_IDLE;
                if (isIncoming){
                    onIncomingCallEnded(context,phoneNumber);
                }else {
                    onOutgoingCallEnded(context,phoneNumber);
                }
            }else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                //摘机状态
                stateChange = TelephonyManager.CALL_STATE_OFFHOOK;
                if (lastCallState != TelephonyManager.CALL_STATE_RINGING){
                    //如果最近的状态不是来电响铃的话，意味着本次通话是去电
                    isIncoming =false;
                    onOutgoingCallStarted(context,phoneNumber);
                }else {
                    //否则本次通话是来电
                    isIncoming = true;
                    onIncomingCallAnswered(context, phoneNumber);
                }
            }else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                //来电响铃状态
                stateChange = TelephonyManager.CALL_STATE_RINGING;
                lastCallState = stateChange;
                onIncomingCallReceived(context,contactNum);
            }
        }
    }
    protected void onIncomingCallStarted(Context context,String number){
        //Toast.makeText(context,"Incoming call is started",Toast.LENGTH_LONG).show();
        LogUtils.d(TAG, "Incoming call is started");
        //context.startService(new Intent(context,AudioRecorderService.class));
    }

    protected void onIncomingCallEnded(Context context,String number){
        //Toast.makeText(context, "Incoming call is ended", Toast.LENGTH_LONG).show();
        LogUtils.d(TAG, "Incoming call is ended");
        //context.startService(new Intent(context, AudioRecorderService.class));
    }

    protected void onIncomingCallReceived(Context context,String number){
        //Toast.makeText(context, "Incoming call is received", Toast.LENGTH_LONG).show();
        LogUtils.d(TAG, "Incoming call is received");
    }
    protected void onIncomingCallAnswered(Context context, String number) {
        //Toast.makeText(context, "Incoming call is answered", Toast.LENGTH_LONG).show();
        LogUtils.d(TAG, "Incoming call is answered");
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
        LogUtils.d(TAG, "Outgoing call is ended");
        Intent mIntent=new Intent("AsrStart");
        context.sendBroadcast(mIntent);
        context.stopService(new Intent(context, AudioRecorderService.class));
    }

}
