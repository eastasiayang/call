package com.example.yangy.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yang.basic.LogUtils;
import com.yang.basic.SharedPreferencesHelper;

import java.lang.reflect.Method;
import static com.yang.basic.RegexUtils.checkDigit;


public class ActivityManualTest extends Activity {
    private static final String TAG = "ActivityManualTest";
    TextView info;
    Button start;
    EditText PhoneNumber;
    Asr asr;
    private MyBroadcastReceiver myBroadcastReceiver;
    private SharedPreferencesHelper sharedPreferencesHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_test);
        initView();
        sharedPreferencesHelper = new SharedPreferencesHelper(
                ActivityManualTest.this, "settings");
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(PhoneNumber.getText().toString());
            }
        });
        myBroadcastReceiver = new MyBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("AsrStart");
        filter.addAction("StartCall");
        filter.addAction("AsrFinish");
        registerReceiver(myBroadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(myBroadcastReceiver);
        if (asr != null) {
            asr.cancel();
        }
        super.onDestroy();
    }

    private void initView() {
        info = findViewById(R.id.TextView_Manual_Info);
        start = findViewById(R.id.Button_Manual_start);
        PhoneNumber = findViewById(R.id.EditText_Manual_number);
    }

    private void printLog(String text) {
        LogUtils.d(TAG, text);
        info.append(text + "\n\n");
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        Context m_context;

        MyBroadcastReceiver(Context con){
            m_context = con;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("AsrStart")) {
                LogUtils.d(TAG, "ASR start");
                if (asr == null) {
                    asr = new Asr(m_context);
                }
                printLog("正在识别，请稍候");
                asr.start();
            } else if (intent.getAction().equals("StartCall")) {
                String phone = intent.getStringExtra("phone");
                call(phone);
            }else if (intent.getAction().equals("AsrFinish")){
                String results = intent.getStringExtra("result");
                printLog("识别结果: ”" + results + "”");
                printLog("识别结束");
            }
        }
    }

    private void call(String sPhone) {
        int iTime = Integer.parseInt(sharedPreferencesHelper.getSharedPreference("hold_time", "10").toString().trim());
        LogUtils.d(TAG, "iTime = " + iTime);
        info.setText("");

        if (!checkDigit(sPhone)) {
            printLog("无效的电话号码：" + sPhone);
        } else {
            printLog("开始拨打电话：" + sPhone);
            Intent phoneIntent = new Intent(
                    "android.intent.action.CALL", Uri.parse("tel:"
                    + sPhone));
            // 启动
            startActivity(phoneIntent);
            LogUtils.d(TAG, "start activity");
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    try {
                        TelephonyManager telMag = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        Class<TelephonyManager> c = TelephonyManager.class;
                        Method mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
                        mthEndCall.setAccessible(true);
                        final Object obj = mthEndCall.invoke(telMag, (Object[]) null);
                        Method mt = obj.getClass().getMethod("endCall");
                        mt.setAccessible(true);
                        mt.invoke(obj);
                        LogUtils.d(TAG, "end call");
                        //Toast.makeText(MainActivity.this, "挂断电话！", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        LogUtils.d(TAG, "e = " + e.toString());
                        e.printStackTrace();
                    }
                }
            }, iTime * 1000);
        }
    }
}
