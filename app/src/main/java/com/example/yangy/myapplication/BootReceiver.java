package com.example.yangy.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yang.basic.LogUtils;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d(TAG, "boot complete!");
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            Intent i = new Intent(context, ActivityAutoTest.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
