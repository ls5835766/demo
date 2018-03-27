//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.idl.face.platform;

import java.util.HashMap;

public class FaceSDKConfig {
    private static final String FACE_CONFIG_KEY_APP_ID = "id";
    private static final String FACE_CONFIG_KEY_APP_NAME = "name";
    private static final String FACE_CONFIG_KEY_APP_TOKEN = "token";
    private static final String FACE_CONFIG_KEY_CAMERA_POSITION = "cp";
    private static final String FACE_CONFIG_KEY_PREVIEW_WIDTH = "pw";
    private static final String FACE_CONFIG_KEY_PREVIEW_HEIGHT = "ph";
    private static final String FACE_CONFIG_KEY_REGISTER_LIVENESS_ANALYZE = "rela";
    private static final String FACE_CONFIG_KEY_RECOGNITION_LIVENESS_ANALYZE = "rcla";
    private static final String FACE_CONFIG_KEY_LIVENESS_TYPE_ARRAY = "lnta";
    private static final String FACE_CONFIG_KEY_LIVENESS_TIME = "lnt";
    public static final int CAMERA_FACING_FRONT = 0;
    public static final int CAMERA_FACING_BACK = 1;
    private int mCameraPosition;
    private String mFaceServerHost;
    private String mAppID;
    private String mAppName;
    private String mAppToken;
    private boolean mIsRegisterLivenessAnalyze;
    private boolean mIsRecognitionLivenessAnalyze;
    private int mPreviewWidth;
    private int mPreviewHeight;
    private long mLivenessDuration;
    private String mTokenAK;
    private String mTokenSK;

    private FaceSDKConfig() {
        this.mCameraPosition = 0;
        this.mFaceServerHost = "";
        this.mAppID = "";
        this.mAppName = "";
        this.mAppToken = "";
        this.mIsRegisterLivenessAnalyze = false;
        this.mIsRecognitionLivenessAnalyze = false;
        this.mPreviewWidth = 0;
        this.mPreviewHeight = 0;
        this.mLivenessDuration = 0L;
        this.mTokenAK = "";
        this.mTokenSK = "";
    }

    public String getTokenAK() {
        return this.mTokenAK;
    }

    public void setTokenAK(String ak) {
        this.mTokenAK = ak;
    }

    public String getTokenSK() {
        return this.mTokenSK;
    }

    public void setTokenSK(String sk) {
        this.mTokenSK = sk;
    }

    public int getCameraPosition() {
        return this.mCameraPosition;
    }

    public int getPreviewHeight() {
        return this.mPreviewHeight;
    }

    public int getPreviewWidth() {
        return this.mPreviewWidth;
    }

    public boolean isRecognitionLivenessAnalyze() {
        return this.mIsRecognitionLivenessAnalyze;
    }

    public boolean isRegisterLivenessAnalyze() {
        return this.mIsRegisterLivenessAnalyze;
    }

    public static class Builder {
        private HashMap<String, Object> map = new HashMap();

        public Builder() {
        }

        public static FaceSDKConfig.Builder getNewInstance() {
            return new FaceSDKConfig.Builder();
        }

        public FaceSDKConfig.Builder addAppId(String appId) {
            this.map.put("id", appId);
            return this;
        }

        public FaceSDKConfig.Builder addAppName(String appName) {
            this.map.put("name", appName);
            return this;
        }

        public FaceSDKConfig.Builder addAppToken(String token) {
            this.map.put("token", token);
            return this;
        }

        public FaceSDKConfig.Builder addPreviewWidth(int width) {
            this.map.put("pw", Integer.valueOf(width));
            return this;
        }

        public FaceSDKConfig.Builder addPreviewHeight(int height) {
            this.map.put("ph", Integer.valueOf(height));
            return this;
        }

        public FaceSDKConfig.Builder setRegisterLivenessAnalyze(boolean flag) {
            this.map.put("rela", Boolean.valueOf(flag));
            return this;
        }

        public FaceSDKConfig.Builder setRecognitionLivenessAnalyze(boolean flag) {
            this.map.put("rcla", Boolean.valueOf(flag));
            return this;
        }


        public FaceSDKConfig build() {
            FaceSDKConfig config = new FaceSDKConfig();
            Object obj = this.map.get("id");
            if(obj != null) {
                config.mAppID = (String)obj;
            }

            obj = this.map.get("name");
            if(obj != null) {
                config.mAppName = (String)obj;
            }

            obj = this.map.get("token");
            if(obj != null) {
                config.mAppToken = (String)obj;
            }

            obj = this.map.get("cp");
            if(obj != null) {
                config.mCameraPosition = ((Integer)obj).intValue();
            }

            obj = this.map.get("pw");
            if(obj != null) {
                config.mPreviewWidth = ((Integer)obj).intValue();
            }

            obj = this.map.get("ph");
            if(obj != null) {
                config.mPreviewHeight = ((Integer)obj).intValue();
            }

            obj = this.map.get("rela");
            if(obj != null) {
                config.mIsRegisterLivenessAnalyze = ((Boolean)obj).booleanValue();
            }

            obj = this.map.get("rcla");
            if(obj != null) {
                config.mIsRecognitionLivenessAnalyze = ((Boolean)obj).booleanValue();
            }


            obj = this.map.get("lnt");
            if(obj != null) {
                config.mLivenessDuration = ((Long)obj).longValue();
            }

            return config;
        }
    }
}
