/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.aip.face.turnstile;

import java.io.File;

import com.baidu.aip.face.turnstile.model.AccessToken;
import com.baidu.aip.face.turnstile.model.FaceModel;
import com.baidu.aip.face.turnstile.model.FaceVerifyResult;
import com.baidu.aip.face.turnstile.model.HttpParams;
import com.baidu.aip.face.turnstile.utils.FaceVerifyParser;
import com.baidu.aip.face.turnstile.utils.HttpUtil;
import com.baidu.aip.face.turnstile.utils.OnResultListener;
import com.baidu.aip.face.turnstile.utils.RegResultParser;
import com.baidu.aip.face.turnstile.utils.VerifyParser;

/**
 * 该类封装了百度人脸识别服务器的接口。
 * 实际应用中请把这部分功能移到自己的服务器。
 */
public class APIService {

    private static final String BASE_URL = "https://aip.baidubce.com";
    private static final String ACCESS_TOKEN_URL = BASE_URL + "/oauth/2.0/token?";
    private static final String REG_URL = BASE_URL + "/rest/2.0/face/v2/faceset/user/add";
    private static final String IDENTIFY_URL = BASE_URL + "/rest/2.0/face/v2/identify";
    private static final String VERIFY_URL = BASE_URL + "/rest/2.0/face/v2/faceverify";

    private String accessToken;
    private String groupId;

    private static volatile APIService instance;

    public static APIService getInstance() {
        if (instance == null) {
            synchronized (APIService.class) {
                if (instance == null) {
                    instance = new APIService();
                }
            }
        }
        return instance;
    }

    private APIService() {

    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * 设置accessToken 如何获取 accessToken 详情见:
     *
     * @param accessToken accessToken
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    /**
     * 明文aksk获取token
     *
     * @param listener 回调
     * @param ak       appKey
     * @param sk       secretKey
     */
    public void initAccessTokenWithAkSk(final OnResultListener<AccessToken> listener, String ak,
                                        String sk) {

        StringBuilder sb = new StringBuilder();
        sb.append("client_id=").append(ak);
        sb.append("&client_secret=").append(sk);
        sb.append("&grant_type=client_credentials");
        HttpUtil.getInstance().getAccessToken(listener, ACCESS_TOKEN_URL, sb.toString());

    }

    public void verify(OnResultListener<FaceVerifyResult> listener, File file) {
        HttpParams params = new HttpParams();
        params.setImageFile(file);
        params.putParam("face_fields", "faceliveness");
        FaceVerifyParser parser = new FaceVerifyParser();
        String url = urlAppendCommonParams(VERIFY_URL);
        HttpUtil.getInstance().post(url, params, parser, listener);
    }

    /**
     * 注册人脸照片到人脸库。
     *
     * @param listener 回调
     * @param file     人脸图片文件
     * @param uid      用户uid,应用对应到，你的服务器用户的 id;
     * @param username 用户名
     */
    public void reg(OnResultListener listener, File file, String uid, String username) {
        HttpParams params = new HttpParams();
        params.setGroupId(groupId); // 替换为自己的groupID;

        params.setUid(uid);
        params.setUserInfo(username);
        params.setImageFile(file);

        RegResultParser parser = new RegResultParser();
        String url = urlAppendCommonParams(REG_URL);
        HttpUtil.getInstance().post(url, params, parser, listener);
    }

    /**
     * 人脸识别 1:N 接口
     *
     * @param listener 回调
     * @param file     人脸图片文件
     */
    public void identify(OnResultListener<FaceModel> listener, File file) {
        HttpParams params = new HttpParams();
        params.setGroupId(groupId);

        params.setUid("face_application_demo");

        params.setImageFile(file);

        VerifyParser parser = new VerifyParser();
        String url = urlAppendCommonParams(IDENTIFY_URL);
        HttpUtil.getInstance().post(url, params, parser, listener);
    }

    private String urlAppendCommonParams(String url) {
        StringBuilder sb = new StringBuilder(url);
        sb.append("?access_token=").append(accessToken);
        return sb.toString();
    }
}
