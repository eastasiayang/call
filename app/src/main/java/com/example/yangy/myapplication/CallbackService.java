package com.example.yangy.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.yang.basic.LogUtils;
import com.yang.basic.SharedPreferencesHelper;
import com.yang.network.HttpRequest;
import com.yang.network.HttpRequestListener;

import java.util.HashMap;

public class CallbackService extends Service {
    private static final String TAG = "CallbackService";
    String phone_number;
    private SharedPreferencesHelper sharedPreferencesHelper;
    public CallbackService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(sharedPreferencesHelper == null){
            sharedPreferencesHelper = new SharedPreferencesHelper(
                    CallbackService.this, "settings");
        }
        phone_number = intent.getStringExtra("phone");
        SendCallback(phone_number);
        return 1;
    }

    boolean SendCallback(String PhoneNumber) {
        HashMap<String, String> map = new HashMap<>();
        LogUtils.d(TAG, "PhoneNumber = " + PhoneNumber);
        if(PhoneNumber == null){
            return false;
        }
        map.put("mobile", PhoneNumber);
        String params = HttpRequest.getRequestParams(map);
        String url = sharedPreferencesHelper.getSharedPreference("callback_url", "http://122.114.14.118:19998/sd/mobile_huibo.aspx").toString().trim();
        HttpRequest.requestNetwork(url, params, new
                HttpRequestListener() {
                    @Override
                    public void onResponse(String result) {
                        LogUtils.d(TAG, "result = " + result);
                        if (result.equals("成功")) {
                            //Intent mIntent=new Intent("StartNext");
                            //mIntent.putExtra("log", "服务器写成功");
                            //sendBroadcast(mIntent);
                        }else{
                            //Intent mIntent=new Intent("Test_Failed");
                            //mIntent.putExtra("log", "服务器写失败，返回值：" + result);
                            //sendBroadcast(mIntent);
                        }
                    }
                    @Override
                    public void onErrorResponse(String error) {
                        //Intent mIntent=new Intent("Test_Failed");
                        //mIntent.putExtra("log", "服务器写故障，故障代码：" + error);
                        //sendBroadcast(mIntent);
                    }
                });
        return true;
    }
}
