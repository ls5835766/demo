package com.bamboy.androidm_fingerpassdemo;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by bamboy on 2017/3/13.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * 指纹识别
     */
    private FingerUtil mFingerUtil;
    /**
     * 指纹监听
     */
    private FingerprintManagerCompat.AuthenticationCallback mFingerListen;

    /**
     * 开始检测按钮
     */
    private Button btn_start;
    /**
     * 显示提示信息
     */
    private TextView tv_log;
    /**
     * 显示提示性动画的ImageView
     */
    private FingerImageView iv_finger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_start = (Button) findViewById(R.id.btn_start);
        tv_log = (TextView) findViewById(R.id.tv_log);
        iv_finger = (FingerImageView) findViewById(R.id.iv_finger);

        // 开始识别按钮点击事件
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // 初始化指纹按钮点击事件
                    initFingerBtnClick();
                } catch (Exception e) {
                    tv_log.setText("当前设备没有指纹识别模块\n或未遵循 Android M 指纹API规范");
                    // 指纹开启失败，显示失败图标
                    iv_finger.end(false);
                }
                // 显示提示文字
                ObjectAnimator.ofFloat(tv_log, "alpha", 0, 0.7f).setDuration(150).start();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mFingerUtil != null && mFingerListen != null) {
            mFingerUtil.startFingerListen(mFingerListen);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFingerUtil != null) {
            mFingerUtil.stopsFingerListen();
        }
    }

    /**
     * 初始化指纹按钮点击事件
     */
    private void initFingerBtnClick(){
        //检测设备是否有指纹识别模块
        if (false == checkFingerModule()) {
            tv_log.setText("当前设备没有指纹识别模块\n或未遵循 Android M 指纹API规范");
            // 不支持指纹，显示失败图标
            iv_finger.end(false);
            return;
        }

        // 检测是否需要初始化
        if (mFingerListen == null || mFingerUtil == null) {
            //初始化指纹识别
            initFinger();
            // 指纹开启成功，显示指纹图标
            iv_finger.startGif();
        } else {
            // 启动指纹识别
            mFingerUtil.startFingerListen(mFingerListen);
            // 指纹开启成功，显示指纹图标
            iv_finger.startGif();
        }
    }

    /**
     * 检测是否有指纹模块
     *
     * @return 是否有指纹模块
     */
    private boolean checkFingerModule() {
        try {
            return ((FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE)).isHardwareDetected();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 初始化指纹模块
     */
    private void initFinger() {
        mFingerListen = new FingerprintManagerCompat.AuthenticationCallback() {

            /**
             * 指纹识别成功
             */
            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                tv_log.setText("指纹识别成功");

                iv_finger.end(true);
            }

            /**
             * 识别失败
             */
            @Override
            public void onAuthenticationFailed() {
                tv_log.setText("指纹识别失败");
                iv_finger.end(false);
            }

            /**
             * Msg监听
             * @param helpMsgId Msg码
             * @param helpString Msg文案
             */
            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                if (tv_log.getTag() != null && false == (Boolean) tv_log.getTag()) {
                    return;
                }
                switch (helpMsgId) {
                    case 1001:      // 等待按下手指
                        tv_log.setText("请按下手指");
                        iv_finger.startGif();
                        break;
                    case 1002:      // 手指按下
                        tv_log.setText("正在识别…");
                        break;
                    case 1003:      // 手指抬起
                        tv_log.setText("手指抬起，请重新按下");
                        iv_finger.startGif();
                        break;
                }
            }

            /**
             * 多次指纹密码验证错误后，进入此方法；并且，不能短时间内调用指纹验证
             * @param errMsgId 错误码
             * @param errString 剩余禁用时间
             */
            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                switch (errMsgId) {
                    case 5:      // 可以进行识别
                        tv_log.setTag(true);
                        break;
                    case 7:      // 失败次数过多，禁用倒计时未结束
                        tv_log.setText("失败次数过多！请" + errString + "秒后再试");
                        tv_log.setTag(false);
                        break;
                }
            }
        };
        mFingerUtil = new FingerUtil(this);
        mFingerUtil.startFingerListen(mFingerListen);
    }

}
