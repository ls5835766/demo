package com.baidu.tb_robot;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.tb_robot.api.BDManager;
import com.baidu.tb_robot.api.asr.AsrCallBack;
import com.baidu.tb_robot.api.tts.TtsCallBack;
import com.baidu.tb_robot.config.Config;
import com.baidu.tb_robot.config.Constants;
import com.baidu.tb_robot.util.BytesUtils;
import com.baidu.tb_robot.util.PreferenceUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class SocketActivity extends AppCompatActivity {

    private static final String TAG = SocketActivity.class.getSimpleName();
    private static final String IP = "192.168.0.108";
    private static final int PORT = 60003;
    private static final int BUFFER = 4 * 1024;

    private static final int RECONNECT = 3 * 1000;
    private static final int CHECK_CONNECT = 60 * 1000;

    private Socket socket;

    private EditText et_ip_address;
    private TextView tv_server_status;



    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(SocketActivity .this, "请填写IP地址", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    tv_server_status.setText("sucessful");
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        initPermission();
        initView();
        initData();
        initTCPClient();
        keepConnect();
    }

    private void initView() {
        et_ip_address = (EditText) this.findViewById(R.id.et_ip_address);
        tv_server_status = (TextView) this.findViewById(R.id.tv_server_status);
    }

    private void initData() {
        if (!TextUtils.isEmpty(PreferenceUtil.getString("BASE_IP"))) {
            String base_ip = PreferenceUtil.getString("BASE_IP");
            et_ip_address.setText(base_ip);
        } else {
            et_ip_address.setText(IP);
            PreferenceUtil.putString("IP", IP);
        }
    }

    private void initTCPClient() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                connectServerWithTCPSocket();
            }
        }.start();
    }


    //考虑网络异常情况
    protected void connectServerWithTCPSocket() {
        while (true) {
            Message message = new Message();
            try {// 创建一个Socket对象，并指定服务端的IP及端口号
                if (TextUtils.isEmpty(et_ip_address.getText().toString())) {
                    message.what = 0;
                    handler.sendMessage(message);
                    break;
                }
                String ip = PreferenceUtil.getString("IP");
                socket = new Socket(ip, PORT);
                // address = socket.getRemoteSocketAddress();
                socket.setKeepAlive(true);
                socket.setSoTimeout(0);
                // 检查连接状态
                if (socket.isConnected()) {
                    message.what = 1;
                    Log.e(TAG, "connect sucessful" + socket.getInetAddress());
                }
                handler.sendMessage(message);
                // 创建一个InputStream用户读取要发送的文件。
                InputStream inputStream = socket.getInputStream();
                // 获取Socket的OutputStream对象用于发送数据。
                OutputStream clientOutputStream = socket.getOutputStream();
                // 创建一个byte类型的buffer字节数组，用于存放读取的本地文件
                byte buffer[] = new byte[BUFFER];
                int temp;
                // 循环读取文件
                while ((temp = inputStream.read(buffer)) != -1) {
                    //把数据写入到OuputStream对象中
                    Log.e(TAG, "server return " + new String(buffer, 0, temp, Config.DEFAULT_CHARSET));
                    String s = new String(buffer, 0, temp, Config.DEFAULT_CHARSET);
                    if (!TextUtils.isEmpty(s)) {
                        String[] strings = s.split("\\u007C");
                        for (int i = 1; i < strings.length; i++) {
                            if (strings[i].length() >= 4) {
                                parsingCommands(strings[i],
                                        strings[i].substring(0, 4), clientOutputStream);
                            }
                        }
                    }
                    // 发送读取的数据到服务端
                    clientOutputStream.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(RECONNECT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void parsingCommands(String content, String commands, final OutputStream outputStream)
            throws Exception {

        Log.e(TAG,"content-->"+content+"---commands-->"+commands);

        if (!TextUtils.isEmpty(commands) && commands.length() == 4) {
            // 开启语音识别
            if (commands.equals(Constants.TD_START_ASR)) {
                //已在主线程中，可以更新UI
                BDManager.asrManager().start(new AsrCallBack() {
                    @Override
                    public void onReceive(final String result) {
                        Log.e(TAG,"result is --->"+result);
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    outputStream.write(result.getBytes(Config.DEFAULT_CHARSET));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                });
                //关闭语音识别
            } else if (commands.equals(Constants.TD_STOP_ASR)) {
                BDManager.asrManager().stop();

                //开启语音合成
            } else if (commands.equals(Constants.TD_START_TTS)) {
                if (!TextUtils.isEmpty(content)) {
                    Log.e(TAG, "send to ttsManager：" + content);
                    BDManager.ttsManager().speaks(content, new TtsCallBack() {
                        @Override
                        public void onFinish(byte[] bytes) {
                            Log.e(TAG,bytes.toString());
                            try {
                                outputStream.write(bytes);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Log.e(TAG, "send to ttsManager：message为空");
                }
                //关闭语音合成
            } else if (commands.equals(Constants.TD_STOP_TTS)) {
                BDManager.ttsManager().stop();
                //设置唤醒角度角度
            } else if (commands.equals(Constants.SET_ARRY_ANGLE)) {
                output = Constants.QUERY_WAKE_UP_ANGLE;
            } else {

            }
        }
    }

    public void keepConnect() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    try {
                        sleep(CHECK_CONNECT);
                        if (socket != null) {
                            if (isServerClose(socket)) {
                                connectServerWithTCPSocket();
                            } else {
                                Log.e(TAG, socket.getRemoteSocketAddress() + " service is normal");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
    /**
     * 判断是否断开连接，断开返回true,没有返回false
     *
     * @param socket 网络连接
     * @return boolean server running == false,else true;
     */
    public Boolean isServerClose(Socket socket) {
        try {
            socket.sendUrgentData(0xFF);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return false;
        } catch (Exception se) {
            return true;
        }
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm :permissions){
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()){
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }


    private void initUsb() {

        if (! BDManager.usbCh34Manager().usbFeatureSupported()) {
            Log.e(TAG, "Not support USB HOST");
            return;
        }
        int retVal =BDManager.usbCh34Manager().resumeUsbList();
        Log.e(TAG,"retVal:"+retVal);
        // ResumeUsbList方法用于枚举CH34X设备以及打开相关设备
        if (retVal == -1) {
            Log.e(TAG, "open usb device failed");
            BDManager.usbCh34Manager().closeDevice();

        } else if (retVal == 0) {
            Log.e(TAG, "retVal is ready--" + retVal);
            //对串口设备进行初始化操作
            if (!BDManager.usbCh34Manager().uartInit()) {
                Log.e(TAG, "usb device init failed,open usbdevice failed");
                return;
            }

            Log.e(TAG, "open usbdevice sucessful");
            new ReadThread().start();//开启读线程读取串口接收的数据
        }else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setTitle("未授权限");
            builder.setMessage("确认退出吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
//								MainFragmentActivity.this.finish();
                    System.exit(0);
                }
            });
            builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub

                }
            });
            builder.show();

        }
        if (BDManager.usbCh34Manager().setConfig(Config.Usb.BAUD_RATE, Config.Usb.DATA_BIT,
                //配置串口波特率，函数说明可参照编程手册
                Config.Usb.STOP_BIT, Config.Usb.PARITY,
                Config.Usb.FLOW_CONTROL)) {
            Log.e(TAG, "serial prrt sucessful");
        } else {
            Log.e(TAG, "serial prrt failed");
        }
    }

    public void onResume() {
        super.onResume();
        Handler handler = new Handler();
        if(!BDManager.usbCh34Manager().isConnected()) {
            int ret= BDManager.usbCh34Manager().ResumeUsbPermission();
            if (ret == 0) {
                handler.postDelayed(new splashhandler(), 1000);
            } else if (ret == -2) {
                Toast.makeText(SocketActivity.this, "请在10秒内响应权限!",
                        Toast.LENGTH_SHORT).show();
                handler.postDelayed(new splashhandler(), 10000);
            }
        }
    }
    class splashhandler implements Runnable
    {
        public void run()
        {
            initUsb();
        }
    }

    private String output = Constants.QUERY_WAKE_UP_STATUS;

    private boolean ALREADY_QUERY_ANGLE = false;

    private class ReadThread extends Thread {

        private int SLEEP = 100;
        private int BUFFER = 64;

        @Override
        public void run() {
            super.run();
            byte[] buffer = new byte[BUFFER];
            while (true) {
                try {
                    byte[] to_send = BytesUtils.toByteArray(output);
                    final int retVal =  BDManager.usbCh34Manager().writeData(to_send, to_send.length);
                    //写数据，第一个参数为需要发送的字节数组，第二个参数为需要发送的字节长度，返回实际发送的字节长度
                    if (retVal < 0) {
                        Log.e(TAG, "写入失败");
                        break;
                    }
                    sleep(SLEEP);
                    int length =  BDManager.usbCh34Manager().readData(buffer, BUFFER);
                    if (length > 0) {
                        if (!ALREADY_QUERY_ANGLE) {
                            if (Constants.QUERY_WAKE_UP_STATUS.equals(output)) {
                                output = Constants.QUERY_WAKE_UP_ANGLE;
                            } else if (Constants.QUERY_WAKE_UP_ANGLE.equals(output)) {
                                Log.e(TAG, "语音角度返回");
                                String angle = BytesUtils.toHexString(buffer, length).replace(" ", "");
                                Log.e(TAG, "angle : " + angle);
                                int a = Integer.parseInt(angle, 16);
                                String angleResult = Constants.QUERY_ANGLE + String.valueOf(a);
                                Log.e(TAG, "angleResult : " + angleResult);
                                socket.getOutputStream().write(angleResult.getBytes(Config.DEFAULT_CHARSET));
                                output = Constants.QUERY_WAKE_UP_STATUS;
                                ALREADY_QUERY_ANGLE = true;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BDManager.usbCh34Manager().closeDevice();
    }
}
