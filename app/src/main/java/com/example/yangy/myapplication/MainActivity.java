package com.example.yangy.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yang.basic.LogUtils;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener  {
    private static final String TAG = "MainActivity";
    Button AutoTest, manual, settings;
    TextView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        version.setText(getVersionName(this));
        Log.d(TAG, getVersionName(this));
        initPermission();
    }

    void initView(){
        AutoTest = findViewById(R.id.Button_Main_Auto);
        manual = findViewById(R.id.Button_Main_Manual);
        settings = findViewById(R.id.Button_Main_Settings);
        version = findViewById(R.id.TextView_Main_version);

        AutoTest.setOnClickListener(this);
        manual.setOnClickListener(this);
        settings.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Button_Main_Auto:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ActivityAutoTest.class);
                startActivity(intent);
                break;
            case R.id.Button_Main_Manual:
                intent = new Intent();
                intent.setClass(MainActivity.this, ActivityManualTest.class);
                startActivity(intent);
                break;
            case R.id.Button_Main_Settings:
                intent = new Intent();
                intent.setClass(MainActivity.this, ActivitySettings.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    //版本名
    public static String getVersionName(Context context) {
        return "版本号：" + getPackageInfo(context).versionName;
    }
    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }


    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CALL_PHONE

        };
        ArrayList<String> toApplyList = new ArrayList<String>();
        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }
}
