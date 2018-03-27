/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.tb_robot.api.face.model;


import com.baidu.tb_robot.util.LogUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 封装的请求类
 */
@SuppressWarnings("unused")
public class RegParams implements RequestParams {

    private Map<String, String> params = new HashMap<>();
    private Map<String, File> fileMap = new HashMap<>();


    @Override
    public Map<String, File> getFileParams() {
        return fileMap;
    }

    @Override
    public Map<String, String> getStringParams() {
        return params;
    }

    //参数
    public void setFace_fields(String face_fields) {
        putParam("face_fields", face_fields);
    }

    public void setBase64Img(String base64Img) {
        putParam("image", base64Img);
    }

    public void setToken(String token) {
        putParam("access_token", token);
    }

    public void setImageFile(File imageFile) {
        fileMap.put(imageFile.getName(), imageFile);
        LogUtil.i("OCR", "rsetImageFile " + imageFile.getName());
    }

    private void putParam(String key, String value) {
        if (value != null) {
            params.put(key, value);
        } else {
            params.remove(key);
        }
    }

    private void putParam(String key, boolean value) {
        if (value) {
            putParam(key, "true");
        } else {
            putParam(key, "false");
        }
    }
}
