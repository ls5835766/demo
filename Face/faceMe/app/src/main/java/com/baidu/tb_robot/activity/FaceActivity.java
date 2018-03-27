package com.baidu.tb_robot.activity;

import android.Manifest;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.widget.TextView;

import com.baidu.aip.ImageFrame;
import com.baidu.aip.face.CameraImageSource;
import com.baidu.aip.face.FaceCropper;
import com.baidu.aip.face.FaceDetectManager;
import com.baidu.aip.face.FaceDetector;
import com.baidu.aip.face.FaceFilter;
import com.baidu.aip.face.PreviewView;
import com.baidu.aip.face.camera.CameraView;
import com.baidu.aip.face.camera.PermissionCallback;
import com.baidu.idl.facesdk.FaceInfo;
import com.baidu.tb_robot.api.face.FaceHandler;
import com.baidu.tb_robot.R;
import com.baidu.tb_robot.api.face.VerifyFaceHandler;
import com.baidu.tb_robot.api.face.APIService;
import com.baidu.tb_robot.api.face.OnResultListener;
import com.baidu.tb_robot.api.face.exception.FaceError;
import com.baidu.tb_robot.api.face.model.RegResult;
import com.baidu.tb_robot.api.face.utils.ImageUtil;
import com.baidu.tb_robot.config.Config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class FaceActivity extends AppCompatActivity {

    private TextView nameTextView;
    private PreviewView previewView;
    private TextureView textureView;

    private FaceDetectManager faceDetectManager = new FaceDetectManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);

        init();//初始化人脸库
        findView ();

        final CameraImageSource cameraImageSource = new CameraImageSource(this);
        cameraImageSource.setPreviewView(previewView);

        final VerifyFaceHandler faceHandler = new VerifyFaceHandler();
        faceDetectManager.setImageSource(cameraImageSource);
        faceDetectManager.setOnFaceDetectListener(new FaceDetectManager.OnFaceDetectListener() {
            @Override
            public void onDetectFace(int retCode, FaceInfo[] infos, ImageFrame frame) {
                showFrame(frame.getArgb(),infos, frame.getWidth(), frame.getHeight());

                if (infos != null && retCode == 0) {
                    ArrayList<FaceHandler.FaceCache> faceCacheArrayList = new ArrayList<>();
                    for (FaceInfo info : infos) {
                        FaceHandler.FaceCache faceCache = new FaceHandler.FaceCache();
                        faceCache.croppedImage = FaceCropper.getFace(frame.getArgb(), info, frame.getWidth());;
                        faceCache.faceInfo = info;
                        faceCache.detectValue = retCode;
                        faceCacheArrayList.add(faceCache);
                    }

                    faceHandler.handleFaces(faceCacheArrayList);
                }
            }
        });
        faceDetectManager.setOnTrackListener(new FaceFilter.OnTrackListener() {
            @Override
            public void onTrack(FaceFilter.TrackedModel trackedModel) {
                // showFrame(trackedModel);
                if (trackedModel.meetCriteria()) {
                    upload(trackedModel);
                }
            }
        });
        cameraImageSource.getCameraControl().setPermissionCallback(new PermissionCallback() {
            @Override
            public boolean onRequestPermission() {
                ActivityCompat.requestPermissions(FaceActivity.this,
                        new String[] {Manifest.permission.CAMERA}, 100);
                return true;
            }
        });

        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
       // boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (isPortrait) {
            previewView.setScaleType(PreviewView.ScaleType.FIT_WIDTH);
            cameraImageSource.getCameraControl().setDisplayOrientation(CameraView.ORIENTATION_PORTRAIT);
        } else {
            previewView.setScaleType(PreviewView.ScaleType.FIT_HEIGHT);
            cameraImageSource.getCameraControl().setDisplayOrientation(CameraView.ORIENTATION_HORIZONTAL);
        }
    }

    private void findView () {
        previewView = (PreviewView) findViewById(R.id.preview_view);
        textureView = (TextureView) findViewById(R.id.texture_view);
        textureView.setOpaque(false);
        textureView.setKeepScreenOn(true);

        nameTextView = (TextView) findViewById(R.id.name_text_view);
    }

    private void init() {

        // 初始化人脸库
        FaceDetector.init(this, Config.licenseID, Config.licenseFileName);
        // 设置最小人脸，小于此值的人脸不会被识别,可设置范围100-200
        FaceDetector.getInstance().setMinFaceSize(200);
        FaceDetector.getInstance().setBlurrinessThreshold(1.0f);
        FaceDetector.getInstance().setOcclulationThreshold(0.1f);
        FaceDetector.getInstance().setNotFaceThreshold(0.1f);
        FaceDetector.getInstance().setCheckQuality(false);
        // FaceDetector.getInstance().setDetectInVideoInterval(200);
        // 头部的欧拉角，大于些值的不会被识别
        FaceDetector.getInstance().setEulerAngleThreshold(40, 40, 40);
        FaceDetector.getInstance().setVerifyLiveness(false);
        FaceDetector.getInstance().setIsFineAlign(false);
        FaceDetector.getInstance().setMaxRegImgNum(1);

    }

    @Override
    protected void onStart() {
        super.onStart();
        faceDetectManager.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        faceDetectManager.stop();
    }

    private boolean shouldUpload = true;
    private void upload(FaceFilter.TrackedModel model) {
        if (model.getEvent() != FaceFilter.Event.OnLeave) {
            if (!shouldUpload) {
                return;
            }
            shouldUpload = false;
            long start = System.currentTimeMillis();
            Bitmap face = model.cropFace();

            try {
                File file = File.createTempFile(UUID.randomUUID().toString() + "", ".jpg");
                ImageUtil.resize(face, file, 200, 200);
                Log.e("xx", "getFace:" + (System.currentTimeMillis() - start));
                APIService.getInstance().checkface(new OnResultListener<RegResult>() {
                    @Override
                    public void onResult(RegResult result) {
                        Log.e("xx", "onResult" + result);
                        if (result == null) {
                            return;
                        }
//                        if (result.getScore() < 80) {
//                            shouldUpload = true;
//                        }
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

    private void showUserInfo(RegResult result) {
            if (result != null) {
                for (RegResult.ResultBean re:result.getResult()){

                    if(re.getGender().equals("male")){
                        String text = String.format(Locale.ENGLISH, "%s,%s",
                                re.getAge(),"男");
                        nameTextView.setText(text);
                    }else{
                        String text = String.format(Locale.ENGLISH, "%s,%s",
                                re.getAge(),"女");
                        nameTextView.setText(text);
                    }
                    Log.e("zhangy",re.getAge()+"--"+re.getGender());
                }
            }
    }

    private Paint paint = new Paint();
    {
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
    }

    RectF rectF = new RectF();
    private void showFrame(int[] argbc, FaceInfo[] infos, int iwidth, int iheight) {
        Canvas canvas = textureView.lockCanvas();

        if (canvas == null) {
            return;
        }
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if (infos != null) {

            int[] points = new int[8];
            for (FaceInfo faceInfo : infos) {

                faceInfo.getRectPoints(points);

                int left = points[2];
                int top = points[3];
                int right = points[6];
                int bottom = points[7];
                //
                int width = right - left;
                int height = bottom - top;

                left = faceInfo.mCenter_x - width / 2;
                top = faceInfo.mCenter_y - height / 2;

                rectF.top = top;
                rectF.left = left;
                rectF.right = left + width;
                rectF.bottom = top + height;

                previewView.mapFromOriginalRect(rectF);
                canvas.drawRect(rectF, paint);
            }
        }
        String text = iwidth + "*" + iheight;
        canvas.drawText(text, textureView.getWidth() - 100, 20, paint);
        textureView.unlockCanvasAndPost(canvas);
    }

}
