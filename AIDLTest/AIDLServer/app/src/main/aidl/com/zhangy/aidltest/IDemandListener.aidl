// IDemandListener.aidl
package com.zhangy.aidltest;

// Declare any non-default types here with import statements

//创建回调接口
import com.zhangy.aidltest.MessageBean;

interface IDemandListener {

   void onDemandReceiver(in MessageBean msg);  //推送消息 相当于server充当了clienet的角色
}
