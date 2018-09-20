package com.example.yangy.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yang.basic.LogUtils;
import com.yang.network.NetworkUtil;

public class NetWorkStateReceiver extends BroadcastReceiver {
    private static final String TAG = "NetWorkStateReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        if (!NetworkUtil.isNetworkAvailable(context)) {
            LogUtils.d(TAG, "NetWork_Disabled");
            Intent mIntent=new Intent("NetWork_Disabled");
            context.sendBroadcast(mIntent);
        }else{
            LogUtils.d(TAG, "NetWork_Enabled");
            Intent mIntent=new Intent("NetWork_Enabled");
            context.sendBroadcast(mIntent);
        }
    }
}
