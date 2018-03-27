package com.baidu.tb_robot.api.usb;

/**
 * Created by zhenglibin on 17/6/22.
 */

public interface UsbCallBack {

    void onReceive(byte[] bytes);
}
