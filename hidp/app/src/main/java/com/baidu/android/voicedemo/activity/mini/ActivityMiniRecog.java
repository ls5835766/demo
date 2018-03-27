package com.baidu.android.voicedemo.activity.mini;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.baidu.speech.recognizerdemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by fujiayi on 2017/8/15.
 */

public class ActivityMiniRecog extends AppCompatActivity implements EventListener {
    private static final String TAG=ActivityMiniRecog.class.getSimpleName();
    protected TextView txtLog;
    protected TextView txtResult;
    protected Button btn;
    protected Button stopBtn;
    private static String DESC_TEXT = "精简版识别，带有SDK唤醒运行的最少代码，仅仅展示如何调用，\n" +
            "也可以用来反馈测试SDK输入参数及输出回调。\n" +
            "本示例需要自行根据文档填写参数，可以使用之前识别示例中的日志中的参数。\n" +
            "需要完整版请参见之前的识别示例。\n" +
            "需要测试离线命令词识别功能可以将本类中的enableOffline改成true，首次测试离线命令词请联网使用。之后请说出“打电话给张三”";

    private EventManager asr;

    private boolean logTime = true;

    private boolean enableOffline = true; // 测试离线命令词，需要改成true

    /**
     * 测试参数填在这里
     *
     * DECODER 参数
     * DECODER = 0 ，表示禁用离线功能，只使用在线的；
     * DECODER = 2 ，表示启用离线功能，但是SDK强制在线优先。
     *
     * PID参数决定了语言，输入法或者搜索模型，及是否需要在线语义解析
     * 语言共有4种：中文普通话、英语、粤语及四川话。
     * 输入法模型和搜索模型：分别适用于长句的录音（有标点）输入和短句输入（无标点）。 只有普通话，搜索模型，才可以开启在线语义解析。
     *
     * VAD_ENDPOINT_TIMEOUT=0 开启长语音
     * 注意这个参数 >0 时，表示关闭长语音。作用是静音断句的时长设置，描述请见下文。 普通的录音限制60s, 长语音可以识别数小时的音频。注意选输入法模型。
     *
     * PROP 识别领域选择，仅仅略微提高准确率
     * 有十多个领域供选择。注意选搜索模型生效。
     *
     * DISABLE_PUNCTUATION 选择输入法模型时禁用标点
     * 官网应用设置“语音识别词库设置
     * 可以自定义识别词，提升准确率。仅在搜索模型下生效。
     * 任意网络状态都生效的识别参数
     *
     * IN_FILE 输入音频
     * 从默认麦克风的音频输入，可以改为用户自定义的音频文件或者自定义的音频流。适用于对于音频输入有定制化的情况。
     *
     * VAD 根据静音时长切分句子的算法
     * 默认自动判断用户一句话是否讲完。也可以禁用。
     *
     * VAD_ENDPOINT_TIMEOUT：800ms-3000ms
     * 在VAD开启的情况下，可以自定义静音时长。即xx毫秒静音后，SDK认为用户一句话讲完
     *
     * AUDIO_MILLS 设置回溯时间 用于唤醒+识别连续说的场景。SDK中有15s的音频缓存，该参数可以使用这15s的缓存。
     * 如设置为System.currentTimeMillis() -1500， 表示 识别从 1.5s之前开始。
     *
     */
    private void start() {
        txtLog.setText("");
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = null;
        event = SpeechConstant.ASR_START; // 替换成测试的event

        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.VAD,SpeechConstant.VAD_DNN);
        if (enableOffline){
            params.put(SpeechConstant.DECODER, 2);
        }
        //  params.put(SpeechConstant.NLU, "enable");
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0);
        // params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
        //  params.put(SpeechConstant.PROP ,20000);
        String json = null; //可以替换成自己的json
        json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        asr.send(event, json, null, 0, 0);
        printLog("输入参数：" + json);
    }

    private void stop() {
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0); //
    }

    private void loadOfflineEngine() {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(SpeechConstant.DECODER, 2);
        params.put(SpeechConstant.ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH, "assets://baidu_speech_grammar.bsg");
        asr.send(SpeechConstant.ASR_KWS_LOAD_ENGINE, new JSONObject(params).toString(), null, 0, 0);
    }

    private void unloadOfflineEngine() {
        asr.send(SpeechConstant.ASR_KWS_UNLOAD_ENGINE, null, null, 0, 0); //
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_mini);
        initView();
        initPermission();
        asr = EventManagerFactory.create(this, "asr");
        asr.registerListener(this); //  EventListener 中 onEvent方法
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                start();
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stop();
            }
        });
        if (enableOffline) {
            loadOfflineEngine(); //测试离线命令词请开启, 测试 ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH 参数时开启
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        if (enableOffline) {
            unloadOfflineEngine(); //测试离线命令词请开启, 测试 ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH 参数时开启
        }
    }

    //   EventListener  回调方法
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String logTxt = "name: " + name;


        if (params != null && !params.isEmpty()) {
            logTxt += " ;params :" + params;
        }
        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            if (params.contains("\"nlu_result\"")) {
                if (length > 0 && data.length > 0) {
                    logTxt += ", 语义解析结果：" + new String(data, offset, length);
                }
            }
        } else if (data != null) {
            logTxt += " ;data length=" + data.length;
        }
        printLog(logTxt);
    }

    private void printLog(String text) {
        if (logTime) {
            text += "  ;time=" + System.currentTimeMillis();
        }
        text += "\n";
        Log.i(getClass().getName(), text);
        txtLog.append(text + "\n");
    }


    private void initView() {
        txtResult = (TextView) findViewById(R.id.txtResult);
        txtLog = (TextView) findViewById(R.id.txtLog);
        btn = (Button) findViewById(R.id.btn);
        stopBtn = (Button) findViewById(R.id.btn_stop);
        txtLog.setText(DESC_TEXT + "\n");
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm :permissions){
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()){
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }

}
