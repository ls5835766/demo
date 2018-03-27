/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform;

/**
 * SDK全局配置信息
 */
final public class FaceEnvironment {

    // SDK基本信息
    public static final String TAG = "Baidu-IDL-FaceSDK";
    public static final String OS = "android";
    public static final String SDK_VERSION = "2.0.0";
    public static final int AG_ID = 3;
    public static final int FACE_DECODE_THREAD_NUM = 2;

    // SDK配置参数
    public static final float VALUE_BRIGHTNESS = 40f;
    public static final float VALUE_BLURNESS = 0.7f;
    public static final float VALUE_OCCLUSION = 0.5f;
    public static final int VALUE_HEAD_PITCH = 10;
    public static final int VALUE_HEAD_YAW = 10;
    public static final int VALUE_HEAD_ROLL = 10;
    public static final int VALUE_CROP_FACE_SIZE = 600;
    public static final int VALUE_MIN_FACE_SIZE = 200;
    public static final float VALUE_NOT_FACE_THRESHOLD = 0.8f;
    public static final boolean VALUE_IS_CHECK_QUALITY = true;

    // 识别策略配置参数
    public static int DETECT_HEAD_ANGLE = 10;
    public static long TIPS_REPEAT_TIME = 3500L;
    public static long MODULE_TIMEOUT = 0L;
    public static long DETECT_NO_FACE_CONTINUOUS_TIME = 500L;
    public static long DETECT_MODULE_TIMEOUT = 10 * 1000L;
    public static long LIVENESS_MODULE_TIMEOUT = 10 * 1000L;

    // 识别策略参数
    private static boolean mIsDebug = true;
    private static int[] mSoundIds;
    private static int[] mTipsTextIds;


    public static boolean isDebugable() {
        return mIsDebug;
    }

    public static int getTipsId(FaceStatusEnum status) {

        int tipsId = mTipsTextIds[status.ordinal()];
        return tipsId;
    }


    public static void setSoundId(FaceStatusEnum status, int soundId) {
        if (mSoundIds != null) {
            try {
                mSoundIds[status.ordinal()] = soundId;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void setTipsId(FaceStatusEnum status, int tipsId) {
        if (mTipsTextIds != null) {
            try {
                mTipsTextIds[status.ordinal()] = tipsId;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static FaceStatusEnum getFaceStatus(int errCode) {
        FaceStatusEnum status = FaceStatusEnum.Detect_NoFace;
        switch (errCode) {
            case 0: // ok
                status = FaceStatusEnum.OK;
                break;
            case 1: // PITCH_OUT_OF_DOWN_MAX_RANGE
                status = FaceStatusEnum.Detect_PitchOutOfDownMaxRange;
                break;
            case 2: // PITCH_OUT_OF_UP_MAX_RANGE
                status = FaceStatusEnum.Detect_PitchOutOfUpMaxRange;
                break;
            case 3: // YAW_OUT_OF_LEFT_MAX_RANGE
                status = FaceStatusEnum.Detect_PitchOutOfRightMaxRange;
                break;
            case 4: // YAW_OUT_OF_RIGHT_MAX_RANGE
                status = FaceStatusEnum.Detect_PitchOutOfLeftMaxRange;
                break;
            case 5: // POOR_ILLUMINATION
                status = FaceStatusEnum.Detect_PoorIllumintion;
                break;
            case 6: // NO_FACE_DETECTED
                status = FaceStatusEnum.Detect_NoFace;
                break;
            case 7: // DATA_NOT_READY
                status = FaceStatusEnum.Detect_DataNotReady;
                break;
            case 8: // IMG_BLURED
                status = FaceStatusEnum.Detect_ImageBlured;
                break;
            case 9: // OCCLUSION_LEFT_EYE
                status = FaceStatusEnum.Detect_OccLeftEye;
                break;
            case 10: // OCCLUSION_RIGHT_EYE
                status = FaceStatusEnum.Detect_OccRightEye;
                break;
            case 11: // OCCLUSION_NOSE
                status = FaceStatusEnum.Detect_OccNose;
                break;
            case 12: // OCCLUSION_MOUTH
                status = FaceStatusEnum.Detect_OccMouth;
                break;
            case 13: // OCCLUSION_LEFT_CONTOUR
                status = FaceStatusEnum.Detect_OccLeftContour;
                break;
            case 14: // OCCLUSION_RIGHT_CONTOUR
                status = FaceStatusEnum.Detect_OccRightContour;
                break;
            case 15: // OCCLUSION_CHIN_CONTOUR
                status = FaceStatusEnum.Detect_OccChin;
                break;
            case 16: // FACE_NOT_COMPLETE
                status = FaceStatusEnum.Detect_FaceNotComplete;
                break;
            case 17: // UNKNOW_TYPE
                status = FaceStatusEnum.Detect_FaceNotComplete;
                break;
            default:
        }
        //        Log.e(TAG, "decode status " + status.name());
        return status;
    }


}
