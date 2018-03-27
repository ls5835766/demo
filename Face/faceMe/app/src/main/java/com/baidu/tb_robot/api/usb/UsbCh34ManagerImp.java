package com.baidu.tb_robot.api.usb;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.baidu.tb_robot.BaseAppaction;
import com.baidu.tb_robot.CH34xUARTDriver;
import com.baidu.tb_robot.api.BDManager;
import com.baidu.tb_robot.config.Config;


/**
 * zhangy
 * Created by zhenglibin on 17/6/5.
 */

public class UsbCh34ManagerImp implements UsbCh34Manager {

    private static final String TAG = "UsbCh34ManagerImp";

    private static final String ACTION_USB_PERMISSION = "cn.wch.wchusbdriver.USB_PERMISSION";

    private static final int TYPE_QUERY_WAKE_UP_STATUS = 1;

    private static final int TYPE_QUERY_WAKE_UP_ANGLE = 2;

    private static final Object lock = new Object();
    private static volatile UsbCh34ManagerImp instance;
    private CH34xUARTDriver ch34xDriver;

    private UsbCh34ManagerImp() {
        ch34xDriver= BaseAppaction.ch34xDriver;
        ch34xDriver = new CH34xUARTDriver((UsbManager) BDManager.Ext.getContext()
                .getSystemService(Context.USB_SERVICE), BDManager.Ext.getContext(), ACTION_USB_PERMISSION);
    }

    public static void init() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new UsbCh34ManagerImp();
                }
            }
        }
        BDManager.Ext.setUsbCh34Manager(instance);
    }


    @Override
    public boolean usbFeatureSupported() {
        return ch34xDriver.UsbFeatureSupported();
    }

    @Override
    public int resumeUsbList() {
        return ch34xDriver.ResumeUsbList();
    }

    @Override
    public boolean uartInit() {
        return ch34xDriver.UartInit();
    }

    @Override
    public boolean isConnected() {
        return ch34xDriver.isConnected();
    }

    @Override
    public int ResumeUsbPermission() {
        return ch34xDriver.ResumeUsbPermission();
    }

    @Override
    public void closeDevice() {
        ch34xDriver.CloseDevice();
    }


    @Override
    public int readData(byte[] bytes, int length) {
        return ch34xDriver.ReadData(bytes, length);
    }

    @Override
    public int writeData(byte[] bytes, int length) {
        return ch34xDriver.WriteData(bytes, length);
    }

    @Override
    public boolean setConfig(int baudRate, byte dataBit, byte stopBit, byte parity, byte flowControl) {
        return ch34xDriver.SetConfig(Config.Usb.BAUD_RATE, Config.Usb.DATA_BIT,
                Config.Usb.STOP_BIT, Config.Usb.PARITY,
                Config.Usb.FLOW_CONTROL);
    }

    @Override
    public void OpenDevice(UsbDevice mDevice) {
        ch34xDriver.OpenDevice(mDevice);
    }

    @Override
    public UsbDevice EnumerateDevice() {
        return ch34xDriver.EnumerateDevice();
    }

}
