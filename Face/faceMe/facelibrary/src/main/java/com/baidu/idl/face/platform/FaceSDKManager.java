///**
// * Copyright (C) 2017 Baidu Inc. All rights reserved.
// */
//package com.baidu.idl.face.platform;
//
//import com.baidu.idl.facesdk.FaceSDK;
//import com.baidu.idl.facesdk.FaceTracker;
//import com.baidu.idl.license.License;
//
//import android.content.Context;
//
///**
// * FaceSDK功能接口
// */
//public class FaceSDKManager {
//
//    private static FaceSDKManager instance = null;
//    private Context mContext;
//    private FaceTracker mFaceTracker;
//    private boolean mInitFlag = false;
//    private FaceConfig mFaceConfig = new FaceConfig();
//
//    private FaceSDKManager() {
//    }
//
//    public static FaceSDKManager getInstance() {
//        if (instance == null) {
//            synchronized (FaceSDKManager.class) {
//                if (instance == null) {
//                    instance = new FaceSDKManager();
//                }
//            }
//        }
//        return instance;
//    }
//
//    public void initialize(final Context context, String appId) {
//        mContext = context.getApplicationContext();
//        mFaceTracker = new FaceTracker(
//                context.getAssets(),
//                context,
//                appId,
//                "",
//                FaceSDK.AlignMethodType.CDNN,
//                FaceSDK.ParsMethodType.NOT_USE);
//        mFaceTracker.set_isFineAlign(false);
//        mFaceTracker.set_isVerifyLive(false);
//        mFaceTracker.set_isCheckQuality(false);
////        mFaceTracker.set_notFace_thr(FaceEnvironment.VALUE_NOT_FACE_THRESHOLD);
////        mFaceTracker.set_min_face_size(FaceEnvironment.VALUE_MIN_FACE_SIZE);
//        mFaceTracker.set_cropFaceSize(FaceEnvironment.VALUE_CROP_FACE_SIZE);
//        mFaceTracker.set_illum_thr(20);
//        mFaceTracker.set_blur_thr(FaceEnvironment.VALUE_BLURNESS);
////        mFaceTracker.set_occlu_thr(FaceEnvironment.VALUE_OCCLUSION);
//        mFaceTracker.set_eulur_angle_thr(
//                40,
//                40,
//                40
//        );
//        mFaceTracker.set_min_face_size(120);
//        FaceSDK.setNumberOfThreads(FaceEnvironment.FACE_DECODE_THREAD_NUM * 2);
//        mInitFlag = true;
//    }
//
//    public FaceTracker getFaceTracker() {
//        return mFaceTracker;
//    }
//
//    public FaceConfig getFaceConfig() {
//        return mFaceConfig;
//    }
//
//    public void setFaceConfig(FaceConfig config) {
//        this.mFaceConfig = config;
//        setSDKValue(mFaceConfig);
//    }
//
//    private void setSDKValue(FaceConfig options) {
//        if (mFaceTracker != null && options != null) {
//            mFaceTracker.set_isCheckQuality(options.isCheckFaceQuality);
//            mFaceTracker.set_notFace_thr(options.notFaceValue);
//            mFaceTracker.set_min_face_size(options.minFaceSize);
//            mFaceTracker.set_cropFaceSize(options.cropFaceValue);
//            mFaceTracker.set_illum_thr(options.brightnessValue);
//            mFaceTracker.set_blur_thr(options.blurnessValue);
//            mFaceTracker.set_occlu_thr(options.occlusionValue);
//            mFaceTracker.set_eulur_angle_thr(
//                    options.headPitchValue,
//                    options.headYawValue,
//                    options.headRollValue
//            );
//        }
//    }
//
//
//    public static int getLicenseRemnant() {
//        return (int) License.getInstance().getLicenseRemnant(FaceEnvironment.AG_ID);
//    }
//
//    public static boolean isLicenseSuccess() {
//        return FaceSDK.isAuthoritySucceeded();
//    }
//
//    // 释放资源
//    public static void release() {
//        synchronized (FaceSDKManager.class) {
//            if (instance != null) {
//                instance.mInitFlag = false;
//                instance.mFaceTracker = null;
//                instance.mContext = null;
//                instance = null;
//            }
//        }
//    }
//}
