/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.aip.face.turnstile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import com.baidu.aip.ImageFrame;
import com.baidu.aip.face.CameraImageSource;
import com.baidu.aip.face.DetectRegionProcessor;
import com.baidu.aip.face.FaceCropper;
import com.baidu.aip.face.FaceDetectManager;
import com.baidu.aip.face.LivenessDetector;
import com.baidu.aip.face.PreviewView;
import com.baidu.aip.face.camera.CameraView;
import com.baidu.aip.face.camera.PermissionCallback;
import com.baidu.aip.face.R;
import com.baidu.aip.face.turnstile.utils.ImageUtil;
import com.baidu.idl.facesdk.FaceInfo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 自动检测获取人脸
 */
public class RegDetectActivity extends AppCompatActivity {

    private PreviewView previewView;
    private View rectView;
    private ImageView displayAvatar;
    private TextView hintTextView;

    private int lastTipResId;
    private boolean success = false;

    private FaceDetectManager faceDetectManager = new FaceDetectManager();

    private DetectRegionProcessor cropProcessor = new DetectRegionProcessor();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_detected);
        previewView = (PreviewView) findViewById(R.id.texture_view);
        displayAvatar = (ImageView) findViewById(R.id.display_avatar);
        rectView = findViewById(R.id.rect_view);
        rectView.setKeepScreenOn(true);
        hintTextView = (TextView) findViewById(R.id.hint_text_view);
        init();

        hintTextView.setText(R.string.hint_move_into_frame);
    }

    private Handler handler = new Handler();

    private void init() {

        final CameraImageSource cameraImageSource = new CameraImageSource(this);
        // 从相机获取图片
        faceDetectManager.setImageSource(cameraImageSource);
        // 设置预览View
        cameraImageSource.setPreviewView(previewView);
        // 添加PreProcessor对图片进行裁剪。
        faceDetectManager.addPreProcessor(cropProcessor);

        rectView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rectView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // 打开相机，映射View到相机之间的坐标
                start();
            }
        });

        faceDetectManager.setOnFaceDetectListener(new FaceDetectManager.OnFaceDetectListener() {
            @Override
            public void onDetectFace(int status, FaceInfo[] infos, ImageFrame frame) {
                handleFace(status, infos, frame.getArgb(), frame.getWidth());
            }
        });

        // 安卓6.0+的系统相机权限是动态获取的，当没有该权限时会回调。
        cameraImageSource.getCameraControl().setPermissionCallback(new PermissionCallback() {
            @Override
            public boolean onRequestPermission() {
                ActivityCompat
                        .requestPermissions(RegDetectActivity.this, new String[] {Manifest.permission.CAMERA}, 100);
                return true;
            }
        });

        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        if (isPortrait) {
            previewView.setScaleType(PreviewView.ScaleType.FIT_WIDTH);
            cameraImageSource.getCameraControl().setDisplayOrientation(CameraView.ORIENTATION_PORTRAIT);
        } else {
            previewView.setScaleType(PreviewView.ScaleType.FIT_HEIGHT);
            cameraImageSource.getCameraControl().setDisplayOrientation(CameraView.ORIENTATION_HORIZONTAL);
        }

//        // 摄像头的类型，是否是usb摄像头，不设置检测的图片是倒立的，无法检测到人脸
//        cameraImageSource.getCameraControl().setUsbCamera(true);
//        // USB摄像头使用镜面翻转 。相机自带的前置摄像头自己加了镜面翻转处理
//        // 但其它摄像头，如USB摄像头，或者网络摄像头没有做这样的处理。该行代码可以实现预览时的镜面翻转。
//        previewView.getTextureView().setScaleX(-1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        faceDetectManager.stop();
    }

    private void start() {
        Rect detectedRect = new Rect();
        rectView.getGlobalVisibleRect(detectedRect);
        RectF newDetectedRect = new RectF(detectedRect);
        cropProcessor.setDetectedRect(newDetectedRect);
        faceDetectManager.start();
    }

    private long lastTipTime;

    private Rect rect = new Rect();
    private RectF rectF = new RectF(rect);

    private void handleFace(int retCode, FaceInfo[] faces, int[] argb, int width) {
        if (success) {
            return;
        }
        if (faces == null || faces.length == 0) {
            // 获取状态码
            int hint = LivenessDetector.getInstance().getHintCode(retCode, null);
            // 根据状态码获取相应的资源ID
            final int resId = LivenessDetector.getInstance().getTip(hint);
            displayTip(resId);
            lastTipResId = resId;
            return;
        }

        rectView.getGlobalVisibleRect(rect);
        // 屏幕显示坐标对应到，实际图片坐标。
        rectF.set(rect);
        previewView.mapToOriginalRect(rectF);
        LivenessDetector.getInstance().setDetectRect(rectF);

        // 获取状态码
        final int hint = LivenessDetector.getInstance().getHintCode(retCode, faces[0]);
        // 根据状态码获取相应的资源ID
        final int resId = LivenessDetector.getInstance().getTip(hint);

        final Bitmap bitmap = FaceCropper.getFace(argb, faces[0], width);

        handler.post(new Runnable() {
            @Override
            public void run() {
                displayAvatar.setImageBitmap(bitmap);
                // 在主线程，显示。
                displayTip(resId);
                lastTipResId = resId;
            }
        });

        if (hint != LivenessDetector.HINT_OK) {
            return;
        }

        try {
            final File file = File.createTempFile(UUID.randomUUID().toString() + "", ".jpg");
            ImageUtil.resize(bitmap, file, 300, 300);

            Intent intent = new Intent();
            intent.putExtra("file_path", file.getAbsolutePath());
            setResult(Activity.RESULT_OK, intent);
            success = true;
            finish();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DisplayTipRunnable displayTipRunnable;

    private void displayTip(final int resId) {
        if (lastTipTime != 0 && (System.currentTimeMillis() - lastTipTime < 1500) && lastTipResId == resId) {
            lastTipTime = System.currentTimeMillis();
            return;
        }

        if (resId != 0) {
            handler.removeCallbacks(displayTipRunnable);
            displayTipRunnable = new DisplayTipRunnable(resId);
            handler.postDelayed(displayTipRunnable, 500);
        }
        lastTipTime = System.currentTimeMillis();
    }

    private class DisplayTipRunnable implements Runnable {

        private int resId;

        DisplayTipRunnable(int resId) {
            this.resId = resId;
        }

        @Override
        public void run() {
            hintTextView.setText(resId);
        }
    }
}
