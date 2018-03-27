/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.aip.face.turnstile;

import com.baidu.aip.FaceDetector;
import com.baidu.aip.face.turnstile.exception.FaceError;
import com.baidu.aip.face.turnstile.model.AccessToken;
import com.baidu.aip.face.turnstile.utils.OnResultListener;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class DemoApplication extends Application {

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();

        ResourceSettings.init();

        // 初始化人脸库
        FaceDetector.init(this, Config.licenseID, Config.licenseFileName);
        // 设置最小人脸，小于此值的人脸不会被识别
        FaceDetector.getInstance().setMinFaceSize(100);
        FaceDetector.getInstance().setCheckQuality(false);

        // 头部的欧拉角，大于些值的不会被识别
        FaceDetector.getInstance().setEulerAngleThreshold(45, 45, 45);

        APIService.getInstance().setGroupId(Config.groupID);
        APIService.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                Log.i("wtf", "AccessToken->" + result.getAccessToken());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DemoApplication.this, "启动成功", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(FaceError error) {
                Log.e("xx", "AccessTokenError:" + error);
                error.printStackTrace();

            }
        }, Config.apiKey, Config.secretKey);
    }
}
