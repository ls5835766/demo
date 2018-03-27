/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.idl.face.platform;

import java.io.Serializable;

/**
 * 人脸检测参数配置类
 */
public class FaceConfig implements Serializable {

    // 人脸检测参数
    public float brightnessValue = FaceEnvironment.VALUE_BRIGHTNESS;
    public float blurnessValue = FaceEnvironment.VALUE_BLURNESS;
    public float occlusionValue = FaceEnvironment.VALUE_OCCLUSION;
    public int headPitchValue = FaceEnvironment.VALUE_HEAD_PITCH;
    public int headYawValue = FaceEnvironment.VALUE_HEAD_YAW;
    public int headRollValue = FaceEnvironment.VALUE_HEAD_ROLL;
    public int cropFaceValue = FaceEnvironment.VALUE_CROP_FACE_SIZE;
    public int minFaceSize = FaceEnvironment.VALUE_MIN_FACE_SIZE;
    public float notFaceValue = FaceEnvironment.VALUE_NOT_FACE_THRESHOLD;
    public boolean isCheckFaceQuality = FaceEnvironment.VALUE_IS_CHECK_QUALITY;
    // 功能参数

    public FaceConfig() {
    }


}
