package com.baidu.tb_robot.api.usb;

import android.hardware.usb.UsbDevice;

/**
 * Created by zhenglibin on 17/6/4.
 */

public interface UsbCh34Manager {

    /**
     * 是否支持 USB HOST
     *
     * @return
     */
    boolean usbFeatureSupported();

    /**
     * @return
     */
    int resumeUsbList();

    /**
     * @return
     */
    boolean uartInit();

    /**
     *
     * @return
     */
    boolean isConnected();

    /**
     *
     * @return
     */
    int ResumeUsbPermission();

    /**
     *
     */
    void closeDevice();

    /**
     * @param bytes
     * @param length
     * @return
     */
    int readData(byte[] bytes, int length);

    /**
     * @param bytes
     * @param length
     * @return
     */
    int writeData(byte[] bytes, int length);

    /**
     * @param baudRate
     * @param dataBit
     * @param stopBit
     * @param parity
     * @param flowControl
     * @return
     */
    boolean setConfig(int baudRate, byte dataBit, byte stopBit, byte parity, byte flowControl);

    /**
     *
     * @param mDevice
     */
    void OpenDevice(UsbDevice mDevice);

    /**
     *
     * @return
     */
    UsbDevice EnumerateDevice();

}
