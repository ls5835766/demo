/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.aip.face.turnstile;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

import com.baidu.aip.FaceDetector;
import com.baidu.aip.ImageFrame;
import com.baidu.aip.face.CameraImageSource;
import com.baidu.aip.face.FaceDetectManager;
import com.baidu.aip.face.FaceFilter;
import com.baidu.aip.face.PreviewView;
import com.baidu.aip.face.camera.CameraView;
import com.baidu.aip.face.camera.PermissionCallback;
import com.baidu.aip.face.R;
import com.baidu.aip.face.turnstile.exception.FaceError;
import com.baidu.aip.face.turnstile.model.FaceModel;
import com.baidu.aip.face.turnstile.utils.ImageUtil;
import com.baidu.aip.face.turnstile.utils.OnResultListener;
import com.baidu.idl.facesdk.FaceInfo;

import android.Manifest;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.TextView;

public class DetectActivity extends AppCompatActivity {

    private TextView nameTextView;
    // 预览View;
    private PreviewView previewView;
    // textureView用于绘制人脸框等。
    private TextureView textureView;
    // 用于检测人脸。
    private FaceDetectManager faceDetectManager = new FaceDetectManager();

    // 为了方便调式。
    private ImageView testView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detected);
        testView = (ImageView) findViewById(R.id.test_view);
        previewView = (PreviewView) findViewById(R.id.preview_view);
        textureView = (TextureView) findViewById(R.id.texture_view);

        // 从系统相机获取图片帧。
        final CameraImageSource cameraImageSource = new CameraImageSource(this);
        // 图片越小检测速度越快，闸机场景640 * 480 可以满足需求。实际预览值可能和该值不同。和相机所支持的预览尺寸有关。
        // 可以通过 camera.getParameters().getSupportedPreviewSizes() 获取支持列表。
        cameraImageSource.getCameraControl().setPreferredPreviewSize(640, 480);
        // 设置最小人脸，该值越大，检测性能越好。范围为80-200
        FaceDetector.getInstance().setMinFaceSize(80);
        FaceDetector.getInstance().setNumberOfThreads(4);
        // 设置预览
        cameraImageSource.setPreviewView(previewView);
        // 设置图片源
        faceDetectManager.setImageSource(cameraImageSource);
        faceDetectManager.getFaceFilter().setAngle(20);
        // 设置回调，回调人脸检测结果。
        faceDetectManager.setOnFaceDetectListener(new FaceDetectManager.OnFaceDetectListener() {
            @Override
            public void onDetectFace(int retCode, FaceInfo[] infos, ImageFrame frame) {
                final Bitmap bitmap =
                        Bitmap.createBitmap(frame.getArgb(), frame.getWidth(), frame.getHeight(), Bitmap.Config
                                .ARGB_8888);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        testView.setImageBitmap(bitmap);
                    }
                });
                if (infos == null) {
                    // null表示，没有人脸。
                    showFrame(null);
                    shouldUpload = true;
                }
            }
        });
        // 人脸追踪回调。没有人脸时不会回调。
        faceDetectManager.setOnTrackListener(new FaceFilter.OnTrackListener() {
            @Override
            public void onTrack(FaceFilter.TrackedModel trackedModel) {
                showFrame(trackedModel);
                if (trackedModel.meetCriteria()) {
                    // 该帧符合过虑标准，人脸质量较高。上传至服务器，进行识别
                    upload(trackedModel);
                }
            }
        });

        // 安卓6.0+ 运行时，权限回调。
        cameraImageSource.getCameraControl().setPermissionCallback(new PermissionCallback() {
            @Override
            public boolean onRequestPermission() {
                ActivityCompat.requestPermissions(DetectActivity.this,
                        new String[] {Manifest.permission.CAMERA}, 100);
                return true;
            }
        });
        nameTextView = (TextView) findViewById(R.id.name_text_view);
        textureView.setOpaque(false);

        // 不需要屏幕自动变黑。
        textureView.setKeepScreenOn(true);

        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (isPortrait) {
            previewView.setScaleType(PreviewView.ScaleType.FIT_WIDTH);
            // 相机坚屏模式
            cameraImageSource.getCameraControl().setDisplayOrientation(CameraView.ORIENTATION_PORTRAIT);
        } else {
            previewView.setScaleType(PreviewView.ScaleType.FIT_HEIGHT);
            // 相机横屏模式
            cameraImageSource.getCameraControl().setDisplayOrientation(CameraView.ORIENTATION_HORIZONTAL);
        }


//        // 摄像头的类型，是否是usb摄像头，不设置检测的图片是倒立的，无法检测到人脸
//        cameraImageSource.getCameraControl().setUsbCamera(true);
//        // USB摄像头使用镜面翻转 。相机自带的前置摄像头自己加了镜面翻转处理
//        // 但其它摄像头，如USB摄像头，或者网络摄像头没有做这样的处理。该行代码可以实现预览时的镜面翻转。
//        previewView.getTextureView().setScaleX(-1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 开始检测
        faceDetectManager.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 结束检测。
        faceDetectManager.stop();
    }

    // 屏幕上显示用户信息。
    private void showUserInfo(FaceModel model) {
        if (model != null) {
            // 把userInfo和分数显示在屏幕上
            String text = String.format(Locale.ENGLISH, "%s  %.2f",
                    model.getUserInfo(), model.getScore());
            nameTextView.setText(text);
        }
    }

    private boolean shouldUpload = true;

    // 上传一帧至服务器进行，人脸识别。
    private void upload(FaceFilter.TrackedModel model) {
        if (model.getEvent() != FaceFilter.Event.OnLeave) {
            if (!shouldUpload) {
                return;
            }
            shouldUpload = false;
            Bitmap face = model.cropFace();
            try {
                File file = File.createTempFile(UUID.randomUUID().toString() + "", ".jpg");
                // 人脸识别不需要整张图片。可以对人脸区别进行裁剪。减少流量消耗和，网络传输占用的时间消耗。
                ImageUtil.resize(face, file, 200, 200);
                APIService.getInstance().identify(new OnResultListener<FaceModel>() {
                    @Override
                    public void onResult(FaceModel result) {
                        if (result == null) {
                            return;
                        }
                        // 识别分数小于80，也可能是角度不好。可以选择重试。
                        if (result.getScore() < 80) {
                            shouldUpload = true;
                        }
                        showUserInfo(result);
                    }

                    @Override
                    public void onError(FaceError error) {
                        error.printStackTrace();
                        shouldUpload = true;
                    }
                }, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            shouldUpload = true;
        }
    }

    private Paint paint = new Paint();

    {
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(30);
    }

    RectF rectF = new RectF();

    /**
     * 绘制人脸框。
     *
     * @param model 追踪到的人脸
     */
    private void showFrame(FaceFilter.TrackedModel model) {
        Canvas canvas = textureView.lockCanvas();
        if (canvas == null) {
            return;
        }
        // 清空canvas
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (model != null) {
            model.getImageFrame().retain();
            rectF.set(model.getFaceRect());

            // 检测图片的坐标和显示的坐标不一样，需要转换。
            previewView.mapFromOriginalRect(rectF);
            if (model.meetCriteria()) {
                // 符合检测要求，绘制绿框
                paint.setColor(Color.GREEN);
            } else {
                // 不符合要求，绘制黄框
                paint.setColor(Color.YELLOW);

                String text = "请正视屏幕";
                float width = paint.measureText(text) + 50;
                float x = rectF.centerX() - width / 2;
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawText(text, x + 25, rectF.top - 20, paint);
                paint.setColor(Color.YELLOW);
            }
            paint.setStyle(Paint.Style.STROKE);
            // 绘制框
            canvas.drawRect(rectF, paint);
        }
        textureView.unlockCanvasAndPost(canvas);
    }

}
