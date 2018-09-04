/* Copyright (C) 2016 Tcl Corporation Limited */
package com.yang.basic;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class LogUtils {

    private static final boolean DEBUG = true;

    private static final String TAG = "yangyadong";

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
                LogUtils.d(TAG, "file write error");
            }
        }
    }

    public static void v(String tag, String msg) {
        if (DEBUG) {
            WriteToFile(tag + ": " + msg + "\n");
            Log.v(TAG, "[" + tag + "] " + msg);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            WriteToFile(tag + ": " + msg + "\n");
            Log.i(TAG, "[" + tag + "] " + msg);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            WriteToFile(tag + ": " + msg + "\n");
            Log.d(TAG, "[" + tag + "] " + msg);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            WriteToFile(tag + ": " + msg + "\n");
            Log.w(TAG, "[" + tag + "] " + msg);
        }
    }


    public static void e(String tag, String msg) {
        if (DEBUG) {
            WriteToFile(tag + ": " + msg + "\n");
            Log.e(TAG, "[" + tag + "] " + msg);
        }
    }

    public static void t() {
        Log.e(TAG,Log.getStackTraceString(new Throwable())); 
    }
    
}
