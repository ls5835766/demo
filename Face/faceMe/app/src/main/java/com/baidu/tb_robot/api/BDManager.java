package com.baidu.tb_robot.api;

import android.app.Application;
import android.content.Context;

import com.baidu.tb_robot.api.asr.AsrManager;
import com.baidu.tb_robot.api.asr.AsrManagerImpl;
import com.baidu.tb_robot.api.tts.TtsManager;
import com.baidu.tb_robot.api.tts.TtsManagerImpl;
import com.baidu.tb_robot.api.usb.UsbCh34Manager;
import com.baidu.tb_robot.api.usb.UsbCh34ManagerImp;
import com.baidu.tb_robot.api.wp.WakeUpManager;
import com.baidu.tb_robot.api.wp.WakeUpManagerImpl;

/**
 * zhangy
 * Created by zhangy on 2017/12/11.
 * 管理api，与Activity之间进行通信
 */

public class BDManager {

    public static AsrManager asrManager(){
        if(Ext.asrManager==null){
            AsrManagerImpl.init();
        }
        return Ext.asrManager;
    }

    public static WakeUpManager wakeUpManager(){
        if(Ext.wakeUpManager==null){
            WakeUpManagerImpl.init();
        }
        return Ext.wakeUpManager;
    }

    public static TtsManager ttsManager(){
        if(Ext.ttsManager==null){
            TtsManagerImpl.init();
        }
        return Ext.ttsManager;
    }

    public static UsbCh34Manager usbCh34Manager(){
        if(Ext.usbCh34Manager==null){
            UsbCh34ManagerImp.init();
        }
        return Ext.usbCh34Manager;
    }


    public static class Ext {
        private static Application app;
        private static AsrManager asrManager;                //语音识别
        private static WakeUpManager wakeUpManager;          //语音唤醒
        private static TtsManager ttsManager;                //语音合成
        private static UsbCh34Manager usbCh34Manager;        //usb转串口

        public static void  init(Application app){
            if(Ext.app==null){
                Ext.app=app;
            }
        }
        public static void setAsrManager(AsrManager asrManager) {
            Ext.asrManager = asrManager;
        }

        public static void setWakeUpManager(WakeUpManager wakeUpManager){
            Ext.wakeUpManager=wakeUpManager;
        }

        public static void setTtsManager(TtsManager ttsManager){
            Ext.ttsManager=ttsManager;
        }

        public static void setUsbCh34Manager(UsbCh34Manager usbCh34Manager) {
            Ext.usbCh34Manager = usbCh34Manager;
        }

        public static Context getContext() {
            return app.getApplicationContext();
        }

    }
}
