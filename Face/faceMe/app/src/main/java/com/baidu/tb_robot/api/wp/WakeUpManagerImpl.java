package com.baidu.tb_robot.api.wp;

import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.baidu.tb_robot.api.BDManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * zahngy
 * Created by zhangy on 2017/12/12.
 */

public class WakeUpManagerImpl implements WakeUpManager{

    private static final String TAG = "WakeUpManagerImpl";
    private static final String TYPE_WP = "wp";

    private static final Object lock = new Object();
    private static volatile WakeUpManagerImpl instance;

    private EventManager wp;
    private HashMap<String, Object> config;

    private WakeUpManagerImpl() {
        config();
    }

    private void config() {
        wp = EventManagerFactory.create(BDManager.Ext.getContext(), TYPE_WP);
        config = new HashMap<>();
        //设置唤醒词路径，唤醒词文件申请 http://yuyin.baidu.com
        config.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        wp.registerListener(new EventListener() {
            @Override
            public void onEvent(String name, String params, byte[] data
                    , int offset, int length) {
                if (name.equals(SpeechConstant.CALLBACK_EVENT_WAKEUP_SUCCESS)) {
                    try {
                        JSONObject json = new JSONObject(params);
                        int errorCode = json.getInt("errorCode");
                        if (errorCode == 0) {
                            Log.e(TAG, "唤醒成功");

                        } else {
                            Log.e(TAG, "唤醒失败");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (SpeechConstant.CALLBACK_EVENT_WAKEUP_STOPED.equals(name)) {
                    Log.e(TAG, "唤醒停止");
                }
            }
        });

    }

    public static void init() {
        if(instance==null){
            synchronized (lock) {
                if (instance == null) {
                    instance = new WakeUpManagerImpl();
                }
            }
        }
        BDManager.Ext.setWakeUpManager(instance);
    }

    @Override
    public void startWakeUp() {
        wp.send(SpeechConstant.WAKEUP_START, new JSONObject(config).toString(), null, 0, 0);
    }

    @Override
    public void stopWakeUp() {
        wp.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0);
    }


}
