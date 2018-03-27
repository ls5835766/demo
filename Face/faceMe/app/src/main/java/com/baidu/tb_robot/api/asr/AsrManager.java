package com.baidu.tb_robot.api.asr;

/**
 * zhangy
 * Created by zhangy on 2017/12/11.
 */

public interface AsrManager {

    void start(AsrCallBack asrCallBack);

    void stop();
}
