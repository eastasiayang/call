package com.example.yangy.myapplication;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yang.basic.SharedPreferencesHelper;

public class ActivitySettings extends Activity {
    private static final String TAG = "ActivitySettings";
    EditText hold_time, next_phone_time, next_get_info, read_url, write_url, callback_url;
    Button save, exit;
    private SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferencesHelper = new SharedPreferencesHelper(
                ActivitySettings.this, "settings");
        initView();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferencesHelper.put("hold_time", hold_time.getText());
                sharedPreferencesHelper.put("next_phone_time", next_phone_time.getText());
                sharedPreferencesHelper.put("next_get_info", next_get_info.getText());
                sharedPreferencesHelper.put("read_url", read_url.getText());
                sharedPreferencesHelper.put("write_url", write_url.getText());
                sharedPreferencesHelper.put("callback_url", callback_url.getText());
                finish();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        hold_time = findViewById(R.id.EditText_settings_call_hold_time);
        next_phone_time = findViewById(R.id.EditText_settings_next_time);
        next_get_info = findViewById(R.id.EditText_settings_next_get_info);
        read_url = findViewById(R.id.EditText_settings_read_url);
        write_url = findViewById(R.id.EditText_settings_write_url);
        callback_url = findViewById(R.id.EditText_settings_callback_url);
        save = findViewById(R.id.Button_Settings_save);
        exit = findViewById(R.id.Button_Settings_exit);
        hold_time.setText(sharedPreferencesHelper.getSharedPreference("hold_time", "10").toString().trim());
        next_phone_time.setText(sharedPreferencesHelper.getSharedPreference("next_phone_time", "5").toString().trim());
        next_get_info.setText(sharedPreferencesHelper.getSharedPreference("next_get_info", "60").toString().trim());
        read_url.setText(sharedPreferencesHelper.getSharedPreference("read_url", "http://1.192.125.57/zx/mobile_read.aspx").toString().trim());
        write_url.setText(sharedPreferencesHelper.getSharedPreference("write_url", "http://1.192.125.57/zx/mobile_write.aspx").toString().trim());
        callback_url.setText(sharedPreferencesHelper.getSharedPreference("callback_url", "http://122.114.14.118:19998/sd/mobile_huibo.aspx").toString().trim());
    }
}
