package com.baidu.tb_robot.api.tts;

/**
 * zhangy
 * Created by zhenglibin on 17/5/22.
 */

public interface TtsManager {


    boolean checkStatus();

    void speaks(String message);

    void speaks(String message, TtsCallBack ttsCallBack);

    void stop();

    void cancel();
}
