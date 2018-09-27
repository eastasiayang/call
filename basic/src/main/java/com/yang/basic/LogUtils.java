/* Copyright (C) 2016 Tcl Corporation Limited */
package com.yang.basic;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class LogUtils {

    private static final boolean DEBUG = true;

    private static final String TAG = "yangyadong";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.US);//日期格式;

    public static void WriteToFile(String content) {
        String state= Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            File SDPath=Environment.getExternalStorageDirectory();//SD根目录
            File file=new File(SDPath,"logcat.txt");
            try{
                FileOutputStream fos=new FileOutputStream(file, true);
                OutputStreamWriter writer=new OutputStreamWriter(fos);
                writer.write(content);
                writer.close();
                fos.close();
            }
            catch(Exception e){
                Log.d(TAG, "file write error " + e.toString());
            }
        }
    }

    public static void v(String tag, String msg) {
        if (DEBUG) {
            WriteToFile(dateFormat.format(System.currentTimeMillis())  + " " + tag + ": " + msg + "\n");
            Log.v(TAG, "[" + tag + "] " + msg);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            WriteToFile(dateFormat.format(System.currentTimeMillis())  + " " + tag + ": " + msg + "\n");
            Log.i(TAG, "[" + tag + "] " + msg);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            WriteToFile(dateFormat.format(System.currentTimeMillis())  + " " + tag + ": " + msg + "\n");
            Log.d(TAG, "[" + tag + "] " + msg);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            WriteToFile(dateFormat.format(System.currentTimeMillis())  + " " + tag + ": " + msg + "\n");
            Log.w(TAG, "[" + tag + "] " + msg);
        }
    }


    public static void e(String tag, String msg) {
        if (DEBUG) {
            WriteToFile(dateFormat.format(System.currentTimeMillis())  + " " + tag + ": " + msg + "\n");
            Log.e(TAG, "[" + tag + "] " + msg);
        }
    }

    public static void t() {
        Log.e(TAG,Log.getStackTraceString(new Throwable())); 
    }
    
}
