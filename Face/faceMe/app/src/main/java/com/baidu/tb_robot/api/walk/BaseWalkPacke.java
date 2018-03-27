package com.baidu.tb_robot.api.walk;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * zhangy
 * Created by zhangy on 2017/12/14.
 */

public class BaseWalkPacke {

    private static final String TAG=BaseWalkPacke.class.getSimpleName();

    private static final String IP = "192.168.0.175";

    // Socket变量
    private Socket socket;

    // 线程池
    // 为了方便展示,此处直接采用线程池进行线程管理,而没有一个个开线程
    private ExecutorService mThreadPool;

    /**
     * 接收服务器消息 变量
     */
    // 输入流对象
    DataInputStream is;

    // 输入流读取器对象
    InputStreamReader isr ;
    BufferedReader br ;

    /**
     * 发送消息到服务器 变量
     */
    // 输出流对象
    DataOutputStream outputStream;

    private Handler mMainHandler;

    public BaseWalkPacke(final int port, Handler mHandler){
        Log.e(TAG,"BaseWalkPacke is start....");
        // 初始化线程池
        mThreadPool = Executors.newCachedThreadPool();
        this.mMainHandler=mHandler;

        // 利用线程池直接开启一个线程 & 执行该线程
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 创建Socket对象 & 指定服务端的IP 及 端口号
                    socket = new Socket(IP, port);
                    // 判断客户端和服务器是否连接成功
                    Log.e(TAG,"Connect--->"+socket.isConnected());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 接收 服务器消息
     */
    public void ReceiveMsg(){
        Log.e(TAG,"获取信息的方法--->ReceiveMsg");
        // 利用线程池直接开启一个线程 & 执行该线程
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 步骤1：创建输入流对象InputStream
                    is=new DataInputStream(socket.getInputStream());

                    StringBuilder stringBuilder=new StringBuilder();
                    while(is.available() != 0){
                        stringBuilder.append((char)is.read());
                    }
                    Log.e(TAG,"response -->"+stringBuilder.toString());
                    // 步骤4:通知主线程,将接收的消息显示到界面
                    Message msg = Message.obtain();
                    msg.what = 0;
                    msg.obj=stringBuilder.toString();
                    mMainHandler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendMessage(final short type, final short serial_number, final byte[] bytes){
        // 利用线程池直接开启一个线程 & 执行该线程
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 步骤1：从Socket 获得输出流对象OutputStream
                    // 该对象作用：发送数据
                    outputStream = new DataOutputStream(socket.getOutputStream());

                    pack(outputStream,type,serial_number,bytes);
                    outputStream.flush();
                    ReceiveMsg();//接收消息
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void disConnect(){
        try {
            // 断开 客户端发送到服务器 的连接，即关闭输出流对象OutputStream
            outputStream.close();
            // 断开 服务器发送到客户端 的连接，即关闭输入流读取器对象BufferedReader
            br.close();
            // 最终关闭整个Socket连接
            socket.close();
            // 判断客户端和服务器是否已经断开连接
            Log.e(TAG,"disConnect-->"+socket.isConnected());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     *  封装报文
     */
    public void pack(DataOutputStream out, short type, short serial_number, byte[] bytes) {
        try {
            int index = 0;
            byte[] buf_str;

            if(bytes!=null){
                buf_str=new byte[16+bytes.length];
            }else{
                buf_str = new byte[16];
            }

            buf_str[index++] = 0x5a;
            ///< 协议版本  // NOLINT
            buf_str[index++] = 0x01;

            ///< 序号，2字节  // NOLINT
            buf_str[index++] = (byte) (serial_number >> 8);
            buf_str[index++] = (byte) (serial_number >> 0);

            if(bytes!=null){
                ///< 数据区长度，4字节  // NOLINT   低位在前，高位在后
                buf_str[index++] = (byte) (bytes.length >> 24);
                buf_str[index++] = (byte) (bytes.length >> 16);
                buf_str[index++] = (byte) (bytes.length >> 8);
                buf_str[index++] = (byte) (bytes.length >> 0);
            }else {
                buf_str[index++] = 0x00;
                buf_str[index++] = 0x00;
                buf_str[index++] = 0x00;
                buf_str[index++] = 0x00;
            }

//          < 报文类型，2字节  // type是short类型  高位在前，低位在后

            buf_str[index++] = (byte) ((type & 0xFF00) >> 8);
            buf_str[index++] = (byte) (type & 0xFF);

//            ///< 保留区域， 6字节  // NOLINT
            buf_str[index++] = 0x00;
            buf_str[index++] = 0x00;
            buf_str[index++] = 0x00;
            buf_str[index++] = 0x00;
            buf_str[index++] = 0x00;
            buf_str[index++] = 0x00;

            if(bytes!=null) {
                //数据内容
                for (int i = 0; i < bytes.length; i++) {
                    buf_str[index++] = bytes[i];
                }
            }

            for (int i=0;i<buf_str.length;i++){
                Log.e(TAG,buf_str[i]+"");
            }
            out.write(buf_str);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
