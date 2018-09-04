package com.example.yangy.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.yang.basic.LogUtils;
import com.yang.basic.SharedPreferencesHelper;
import com.yang.basic.ToastUtils;
import com.yang.network.HttpRequest;
import com.yang.network.HttpRequestListener;
import com.yang.network.NetworkUtil;

import java.lang.reflect.Method;
import java.util.HashMap;

import static com.yang.basic.RegexUtils.checkDigit;

public class ActivityAutoTest extends Activity {
    private static final String TAG = "ActivityAutoTest";
    TextView info;
    Button start, stop;
    boolean bStop = false;
    String info_log = "";
    private MyBroadcastReceiver myBroadcastReceiver;
    private SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_test);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initView();
        sharedPreferencesHelper = new SharedPreferencesHelper(
                ActivityAutoTest.this, "settings");
        myBroadcastReceiver = new MyBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("AsrStart");
        filter.addAction("StartCall");
        filter.addAction("AsrFinish");
        filter.addAction("UpdateUI");
        filter.addAction("StartNext");
        filter.addAction("StartRepeat");
        registerReceiver(myBroadcastReceiver, filter);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auto_start();
                start.setClickable(false);
                start.setTextColor(getResources().getColor(R.color.grey));
                stop.setClickable(true);
                stop.setTextColor(getResources().getColor(R.color.black));
                bStop = false;
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bStop = true;
                stop.setClickable(false);
                stop.setTextColor(getResources().getColor(R.color.grey));
                start.setClickable(true);
                start.setTextColor(getResources().getColor(R.color.black));
                printLog("已结束自动测试", true, true);
            }
        });
    }

    private void initView() {
        info = findViewById(R.id.TextView_Auto_Info);
        start = findViewById(R.id.Button_Auto_Start);
        stop = findViewById(R.id.Button_Auto_Stop);
        stop.setClickable(false);
        stop.setTextColor(getResources().getColor(R.color.grey));
    }

    private void printLog(String text, boolean bAppend, boolean bClean) {
        LogUtils.d(TAG, text);
        if(bClean){
            info.setText("");
            info_log = "";
        }
        info.setText(info_log + text + "\n\n");
        if(bAppend){
            info_log += text + "\n\n";
        }
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        private static final String TAG = "ActivitySettings";
        Asr asr;
        Context m_context;
        String phone, result;
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
                printLog("正在识别，请稍候", true, false);
                asr.start();
            } else if (intent.getAction().equals("StartCall")) {
                phone = intent.getStringExtra("phone");
                call(phone);
            } else if (intent.getAction().equals("AsrFinish")) {
                result = intent.getStringExtra("result");
                printLog("识别结果: ”" + result + "”", true, false);
                printLog("识别结束", true, false);
                SendResult(phone, result);
            }else if(intent.getAction().equals("UpdateUI")){
                String temp = intent.getStringExtra("log");
                printLog(temp, true, false);
            }else if(intent.getAction().equals("StartNext")){
                int iTime = Integer.parseInt(sharedPreferencesHelper.getSharedPreference("next_phone_time", "5").toString().trim());
                start_wait(iTime*1000, 1000);
            }
            else if(intent.getAction().equals("StartRepeat")){
                printLog("没有读到电话号码", true, true);
                int iTime = Integer.parseInt(sharedPreferencesHelper.getSharedPreference("next_get_info", "60").toString().trim());
                start_wait(iTime*1000, 1000);
            }
        }
    }

    private void auto_start(){
        if (!NetworkUtil.isNetworkAvailable(ActivityAutoTest.this)) {
            ToastUtils.showShort(ActivityAutoTest.this,
                    R.string.tip_network_error_please_check);
            return;
        }
        String url = sharedPreferencesHelper.getSharedPreference("read_url", "http://1.192.125.57/zx/mobile_read.aspx").toString().trim();
        HttpRequest.requestNetwork(url, "", new
                HttpRequestListener() {
                    @Override
                    public void onResponse(String result) {
                        LogUtils.d(TAG, "result = " + result);
                        if(!result.equals("")){
                            Intent mIntent=new Intent("StartCall");
                            mIntent.putExtra("phone", result);
                            sendBroadcast(mIntent);
                        }else{
                            Intent mIntent=new Intent("StartRepeat");
                            sendBroadcast(mIntent);
                        }
                    }
                    @Override
                    public void onErrorResponse(String error) {
                        printLog("服务器读号码故障，故障代码：" + error, false, false );
                    }
                });
    }



    private void call(String sPhone) {
        int iTime = Integer.parseInt(sharedPreferencesHelper.getSharedPreference("hold_time", "10").toString().trim());
        LogUtils.d(TAG, "iTime = " + iTime);
        if (!checkDigit(sPhone)) {
            printLog("无效的电话号码：" + sPhone, true, true);
        } else {
            printLog("开始拨打电话：" + sPhone, true, true);
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

    boolean SendResult(String PhoneNumber, String result) {
        HashMap<String, String> map = new HashMap<>();
        LogUtils.d(TAG, "PhoneNumber = " + PhoneNumber + " result = " + result);
        map.put("mobile", PhoneNumber);
        map.put("result", result);
        String params = HttpRequest.getRequestParams(map);
        String url = sharedPreferencesHelper.getSharedPreference("write_url", "http://1.192.125.57/zx/mobile_write.aspx").toString().trim();
        HttpRequest.requestNetwork(url, params, new
                HttpRequestListener() {
                    @Override
                    public void onResponse(String result) {
                        LogUtils.d(TAG, "result = " + result);
                        Intent mIntent=new Intent("UpdateUI");
                        mIntent.putExtra("log", "返回结果：" + result);
                        sendBroadcast(mIntent);
                        if (result.equals("成功")) {
                            mIntent=new Intent("StartNext");
                            sendBroadcast(mIntent);
                        }else{
                            mIntent=new Intent("UpdateUI");
                            mIntent.putExtra("log", "服务器写失败，请检查服务器");
                            sendBroadcast(mIntent);
                        }
                    }
                    @Override
                    public void onErrorResponse(String error) {
                        Intent mIntent=new Intent("UpdateUI");
                        mIntent.putExtra("log", "服务器写结果故障，故障代码：" + error);
                        sendBroadcast(mIntent);
                    }
                });
        return true;
    }

    private void start_wait(int total_time, int once_time){
        LogUtils.d(TAG, "total time = " + total_time + "once_time = " + once_time);
        CountDownTimer countDownTimer = new CountDownTimer(total_time, once_time) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(bStop){
                    cancel();
                }else{
                    String value = String.valueOf((int) (millisUntilFinished / 1000));
                    printLog(value + " 秒后测试下一个电话号码", false, false);
                }

            }
            @Override
            public void onFinish() {
                LogUtils.d(TAG, "start test next ");
                if(!bStop){
                    auto_start();
                }
            }
        };
        countDownTimer.start();
    }


}
