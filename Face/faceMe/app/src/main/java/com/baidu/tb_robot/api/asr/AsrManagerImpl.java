package com.baidu.tb_robot.api.asr;

import android.text.TextUtils;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.baidu.tb_robot.api.BDManager;
import com.baidu.tb_robot.config.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * zhangy
 * Created by zhangy on 2017/12/11.
 */

public class AsrManagerImpl implements AsrManager {
    private static final String TAG=AsrManagerImpl.class.getSimpleName();

    private static final String TYPE_ASR = "asr";

    private EventManager asr;
    private AsrCallBack asrCallBack;
    private static AsrManagerImpl instance;
    private static final Object lock = new Object();

    Map<String, Object> params;

    private AsrManagerImpl(){
        config();
    }

    private StringBuilder result = new StringBuilder();


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
    private void config() {
        asr = EventManagerFactory.create(BDManager.Ext.getContext(), TYPE_ASR);
        params=new HashMap<>();
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0);
        asr.registerListener(new EventListener() {
            @Override
            public void onEvent(String name, String params, byte[] data, int offset, int length) {
                try {
                    if (!TextUtils.isEmpty(params) && !params.equals("null")) {
                        JSONObject json = new JSONObject(params);
                        if (json.has("error")) {
                            if (json.optInt("error") == 0) {
                                if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
                                    Log.e(TAG, "CALLBACK_EVENT_ASR_PARTIAL---->语音识别正在识别");
                                    if (!TextUtils.isEmpty(params)) {
//                                        if (!TextUtils.isEmpty(json.optString("best_result"))) {
//                                            result.setLength(0);
//                                            result.append(json.optString("best_result"));
//                                            Log.e(TAG, "result:" + Constants.ASR_EVERY_RESULT + result.toString());
//                                            String s = Constants.ASR_EVERY_RESULT + result.toString();
//                                             asrCallBack.onReceive(s);
//                                        }
                                        RecogResult recogResult = RecogResult.parseJson(params);
                                        // 临时识别结果, 长语音模式需要从此消息中取出结果
                                        String[] results = recogResult.getResultsRecognition();
                                        if (recogResult.isFinalResult()) {
                                            String s = Constants.ASR_EVERY_RESULT + results[0];
                                            Log.e(TAG,"S---->"+s);
                                             asrCallBack.onReceive(s);
                                        }
                                    }
                                } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
                                    Log.e(TAG, "语音识别ASR" + params);
                                    Log.e(TAG, "CALLBACK_EVENT_ASR_FINISH");

                                    RecogResult recogResult = RecogResult.parseJson(params);
                                    // 临时识别结果, 长语音模式需要从此消息中取出结果
                                    String[] results = recogResult.getResultsRecognition();
                                    if (recogResult.isFinalResult()) {
                                        String s = Constants.ASR_EVERY_RESULT + results[0];
                                        Log.e(TAG,"S---->"+s);
                                        asrCallBack.onReceive(s);
                                    }

//                                    String s = Constants.ASR_RESULT;
//                                    if (!TextUtils.isEmpty(result.toString())) {
//                                        s += result.toString();
//                                        Log.e(TAG, "发给server: " + s);
//                                        asrCallBack.onReceive(s);
//                                        result.setLength(0);
//                                        Log.e(TAG,"结束语音识别返回给server");
//                                    } else {
//                                        asrCallBack.onReceive(s);
//                                    }
                                } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_END)) {
                                    Log.e(TAG, "CALLBACK_EVENT_ASR_END--->检测到说话结束");
                                } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_ERROR)) {
                                    Log.e(TAG, "CALLBACK_EVENT_ASR_ERROR");
                                    asrCallBack.onReceive("null");
                                }
                            } else {
                                Log.e(TAG, "语音识别ASR" + params);
                                if (!TextUtils.isEmpty(result.toString())) {
                                    Log.e(TAG, "语音异常返回异常前结果：");
                                    String s = Constants.ASR_RESULT + result.toString();
                                    Log.e(TAG, "发给server: " + s);
                                    asrCallBack.onReceive(s);
                                    result.setLength(0);
                                } else {
                                    Log.e(TAG, "语音异常返回 NULL");
                                    asrCallBack.onReceive("null");
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        });
    }

    public static void init(){
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new AsrManagerImpl();
                }
            }
        }
        BDManager.Ext.setAsrManager(instance);
    }

    @Override
    public void start(AsrCallBack asrCallBack) {
        this.asrCallBack=asrCallBack;
        asr.send(SpeechConstant.ASR_START, new JSONObject(params).toString(), null, 0, 0);

    }

    @Override
    public void stop() {
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0); //
    }


}
