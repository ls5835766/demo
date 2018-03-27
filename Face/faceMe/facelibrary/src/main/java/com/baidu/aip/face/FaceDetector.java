/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.aip.face;

import com.baidu.idl.facesdk.FaceInfo;
import com.baidu.idl.facesdk.FaceSDK;
import com.baidu.idl.facesdk.FaceTracker;

import android.content.Context;

public class FaceDetector {

    public static final int DETECT_CODE_OK = FaceTracker.ErrCode.OK.ordinal();
    public static final int DETECT_CODE_PITCH_OUT_OF_DOWN_MAX_RANGE =
            FaceTracker.ErrCode.PITCH_OUT_OF_DOWN_MAX_RANGE.ordinal();
    public static final int DETECT_CODE_PITCH_OUT_OF_UP_MAX_RANGE =
            FaceTracker.ErrCode.PITCH_OUT_OF_UP_MAX_RANGE.ordinal();
    public static final int DETECT_CODE_YAW_OUT_OF_LEFT_MAX_RANGE =
            FaceTracker.ErrCode.YAW_OUT_OF_LEFT_MAX_RANGE.ordinal();
    public static final int DETECT_CODE_YAW_OUT_OF_RIGHT_MAX_RANGE =
            FaceTracker.ErrCode.YAW_OUT_OF_RIGHT_MAX_RANGE.ordinal();
    public static final int DETECT_CODE_POOR_ILLUMINATION =
            FaceTracker.ErrCode.POOR_ILLUMINATION.ordinal();
    public static final int DETECT_CODE_NO_FACE_DETECTED =
            FaceTracker.ErrCode.NO_FACE_DETECTED.ordinal();

    public static final float DEFAULT_NOT_FACE_THRESHOLD = 0.5f;
    public static final int DEFAULT_MIN_FACE_SIZE = 200;

    // TODO
    public static final float DEFAULT_ILLUMINATION_THRESHOLD = 40.0f;
    public static final float DEFAULT_BLURRINESS_THRESHOLD = 0.7f;
    public static final float DEFAULT_OCCULTATION_THRESHOLD = 0.5f;
    public static final int DEFAULT_HEAD_ANGLE = 45;

    private FaceTracker mFaceTracker;
    private static FaceDetector sInstance;

    public static void init(Context context, String appId, String licenseFileName) {
        if (sInstance == null) {
            sInstance = new FaceDetector(context, appId, licenseFileName);
        }
    }

    public static FaceDetector getInstance() {
        return sInstance;
    }

    private FaceDetector(Context context, String appId, String licenseFileName) {
        mFaceTracker = new FaceTracker(
                context.getAssets(),
                context.getApplicationContext(),
                appId,
                licenseFileName,
                FaceSDK.AlignMethodType.CDNN,
                FaceSDK.ParsMethodType.NOT_USE);
        mFaceTracker.set_isFineAlign(false);
        mFaceTracker.set_isVerifyLive(false);
        mFaceTracker.set_isCheckQuality(false);
        mFaceTracker.set_notFace_thr(DEFAULT_NOT_FACE_THRESHOLD);
        mFaceTracker.set_min_face_size(DEFAULT_MIN_FACE_SIZE);
        mFaceTracker.set_cropFaceSize(DEFAULT_MIN_FACE_SIZE * 3);
        mFaceTracker.set_illum_thr(DEFAULT_ILLUMINATION_THRESHOLD);
        mFaceTracker.set_blur_thr(DEFAULT_BLURRINESS_THRESHOLD);
        mFaceTracker.set_occlu_thr(DEFAULT_OCCULTATION_THRESHOLD);
//        mFaceTracker.set_max_reg_img_num(1); // TODO
        mFaceTracker.set_eulur_angle_thr(
                DEFAULT_HEAD_ANGLE,
                DEFAULT_HEAD_ANGLE,
                DEFAULT_HEAD_ANGLE
        );
        FaceSDK.setNumberOfThreads(4); // TODO
    }

    /**
     * 非人脸的置信度阈值，取值范围0~1，取0则认为所有检测出来的结果都是人脸，默认0.5
     * @param threshold
     */
    public void setNotFaceThreshold(float threshold) {
        mFaceTracker.set_notFace_thr(threshold);
    }

    public void setMinFaceSize(int faceSize) {
        mFaceTracker.set_min_face_size(faceSize);
    }

    // TODO
    public void setIlluminationThreshold(float threshold) {
        mFaceTracker.set_illum_thr(threshold);
    }

    public void setBlurrinessThreshold(float threshold) {
        mFaceTracker.set_blur_thr(threshold);
    }

    // TODO
    public void setOcclulationThreshold(float threshold) {
        mFaceTracker.set_occlu_thr(threshold);
    }

    public void setCheckQuality(boolean checkQuality) {
        mFaceTracker.set_isCheckQuality(checkQuality);
    }

    public void setVerifyLiveness(boolean verifyLiveness) {
        mFaceTracker.set_isVerifyLive(verifyLiveness);
    }

    public void setIsFineAlign(boolean isFineAlign) {
        mFaceTracker.set_isFineAlign(isFineAlign);
    }

    public void setMaxRegImgNum(int num) {
        mFaceTracker.set_max_reg_img_num(num);
    }

    /**
     * 跟踪到目标后执行检测的时间间隔，单位毫秒，默认300，值越小越会更快发现新目标，但是cpu占用率会提高、处理速度变慢
     * @param interval
     */
    public void setDetectInVideoInterval(int interval) {
        mFaceTracker.set_detection_frame_interval(interval);
    }

    // TODO
    public void setEulerAngleThreshold(int yaw, int roll, int pitch) {
        mFaceTracker.set_eulur_angle_thr(yaw, roll, pitch);
    }

    public int detect(int[] argb, int width, int height) {
        return this.mFaceTracker
                .prepare_data_for_verify(argb, height, width, FaceSDK.ImgType.ARGB.ordinal(), FaceTracker.ActionType
                        .RECOGNIZE.ordinal());
    }

    public static void yuvToARGB(byte[] yuv, int width, int height, int[] argb, int rotation, int angle) {
        FaceSDK.getARGBFromYUVimg(yuv, argb, width, height, rotation, angle);
    }

    public FaceInfo[] getTrackedFaces() {
        return mFaceTracker.get_TrackedFaceInfo();
    }

    public FaceInfo getTrackedFace() {
        FaceInfo[] faces = mFaceTracker.get_TrackedFaceInfo();
        if (faces != null && faces.length > 0) {
            return mFaceTracker.get_TrackedFaceInfo()[0];
        }
        return null;
    }

    public void clearTrackedFaces() {
        mFaceTracker.clearTrackedFaces();
    }
}
