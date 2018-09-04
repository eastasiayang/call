package com.example.yangy.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.yang.basic.LogUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class Asr implements EventListener {
    private static final String TAG = "Asr";
    protected boolean enableOffline = false; // 测试离线命令词，需要改成true
    private EventManager asr;
    Context m_context;
    private boolean bAsrFinish = false;

    Asr(Context con){
        m_context = con;
        asr = EventManagerFactory.create(m_context, "asr");
        asr.registerListener(this); //  EventListener 中 onEvent方法
        if (enableOffline) {
            loadOfflineEngine(); // 测试离线命令词请开启, 测试 ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH 参数时开启
        }
    }

    public void start() {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = null;
        event = SpeechConstant.ASR_START; // 替换成测试的event
        if (enableOffline) {
            params.put(SpeechConstant.DECODER, 2);
        }
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        //params.put(SpeechConstant.ACCEPT_AUDIO_DATA, true);
        // params.put(SpeechConstant.NLU, "enable");
        // params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0); // 长语音
        params.put(SpeechConstant.IN_FILE, "/sdcard/aditi.pcm");//sdcard/16k_test.pcm  ///sdcard/aditi.pcm
        params.put(SpeechConstant.VAD, SpeechConstant.VAD_TOUCH);//VAD_TOUCH //VAD_DNN
        // params.put(SpeechConstant.PROP ,20000);
        // params.put(SpeechConstant.PID, 1537); // 中文输入法模型，有逗号
        // 请先使用如‘在线识别’界面测试和生成识别参数。 params同ActivityRecog类中myRecognizer.start(params);
        // 复制此段可以自动检测错误
       /* (new AutoCheck(getApplicationContext(), new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 100) {
                    AutoCheck autoCheck = (AutoCheck) msg.obj;
                    synchronized (autoCheck) {
                        String message = autoCheck.obtainErrorMessage(); // autoCheck.obtainAllMessage();
                        txtLog.append(message + "\n");
                        ; // 可以用下面一行替代，在logcat中查看代码
                        // Log.w("AutoCheckMessage", message);
                    }
                }
            }
        },enableOffline)).checkAsr(params);*/
        String json = null; // 可以替换成自己的json
        json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        asr.send(event, json, null, 0, 0);
        LogUtils.d(TAG, "输入参数：" + json);
    }

    public void stop() {
        LogUtils.d(TAG, "ASR_STOP");
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
        //WriteToResult(sPhone + ":" + sResult);
    }

    public void cancel(){
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        if (enableOffline) {
            unloadOfflineEngine(); // 测试离线命令词请开启, 测试 ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH 参数时开启
        }
        asr.unregisterListener(this);
    }


    public void WriteToResult(String content) {
        String state= Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            File SDPath=Environment.getExternalStorageDirectory();//SD根目录
            File file=new File(SDPath,"data.txt");
            try{
                FileOutputStream fos=new FileOutputStream(file);
                OutputStreamWriter writer=new OutputStreamWriter(fos,"utf-8");
                writer.write(content);
                writer.close();
                fos.close();
            }
            catch(Exception e){
                LogUtils.d(TAG, "file write error");
            }
        }else {
            //new AlertDialog.Builder(MainActivity.this).setTitle("提示").setMessage("SD卡不可用")
             //       .setPositiveButton("确定",null).create().show();
        }
    }

    /**
     * enableOffline设为true时，在onCreate中调用
     */
    private void loadOfflineEngine() {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(SpeechConstant.DECODER, 2);
        params.put(SpeechConstant.ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH, "assets://baidu_speech_grammar.bsg");
        asr.send(SpeechConstant.ASR_KWS_LOAD_ENGINE, new JSONObject(params).toString(), null, 0, 0);
    }

    /**
     * enableOffline为true时，在onDestory中调用，与loadOfflineEngine对应
     */
    private void unloadOfflineEngine() {
        asr.send(SpeechConstant.ASR_KWS_UNLOAD_ENGINE, null, null, 0, 0); //
    }

    //   EventListener  回调方法
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String logTxt = "name: " + name;
        if (params != null && !params.isEmpty()) {
            logTxt += " ;params :" + params;
        }
        LogUtils.d(TAG, logTxt);
        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            RecogResult recogResult = RecogResult.parseJson(params);
            String[] results = recogResult.getResultsRecognition();
            if (recogResult.isFinalResult()) {
                bAsrFinish = true;
                Intent mIntent=new Intent("AsrFinish");
                mIntent.putExtra("result", results[0]);
                m_context.sendBroadcast(mIntent);
            }
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_EXIT)) {
            if(!bAsrFinish){
                Intent mIntent=new Intent("AsrFinish");
                mIntent.putExtra("result", "");
                m_context.sendBroadcast(mIntent);
            }
            bAsrFinish = false;
            stop();
        }
    }
}
