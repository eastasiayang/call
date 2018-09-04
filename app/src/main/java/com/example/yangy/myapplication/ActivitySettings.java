package com.example.yangy.myapplication;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yang.basic.SharedPreferencesHelper;

public class ActivitySettings extends Activity {
    private static final String TAG = "ActivitySettings";
    EditText hold_time, next_phone_time, next_get_info, read_url, write_url;
    private SharedPreferencesHelper sharedPreferencesHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferencesHelper = new SharedPreferencesHelper(
                ActivitySettings.this, "settings");
        initView();
    }

    private void initView() {
        hold_time = findViewById(R.id.EditText_settings_call_hold_time);
        next_phone_time = findViewById(R.id.EditText_settings_next_time);
        next_get_info = findViewById(R.id.EditText_settings_next_get_info);
        read_url = findViewById(R.id.EditText_settings_read_url);
        write_url = findViewById(R.id.EditText_settings_write_url);
    }

    @Override
    public void onDestroy() {
        sharedPreferencesHelper.put("hold_time", hold_time.getText());
        sharedPreferencesHelper.put("next_phone_time", next_phone_time.getText());
        sharedPreferencesHelper.put("next_get_info", next_get_info.getText());
        sharedPreferencesHelper.put("read_url", read_url.getText());
        sharedPreferencesHelper.put("write_url", write_url.getText());
        super.onDestroy();
    }
}
