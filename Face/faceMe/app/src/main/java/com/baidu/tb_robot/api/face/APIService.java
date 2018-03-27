package com.baidu.tb_robot.api.face;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.audiofx.LoudnessEnhancer;
import android.util.Log;

import com.baidu.tb_robot.api.face.model.AccessToken;
import com.baidu.tb_robot.api.face.model.RegParams;
import com.baidu.tb_robot.api.face.model.RegResultParser;

import java.io.File;

/**
 * zhangy
 * Created by zhangy on 2017/12/26.
 */

public class APIService {

    private static final String TAG=APIService.class.getSimpleName();

    private static final String BASE_URL = "https://aip.baidubce.com";
    private static final String ACCESS_TOKEN_URL = BASE_URL + "/oauth/2.0/token?";//认证TOKEN
    private static final String CHECK_FACE_URL=BASE_URL+"/rest/2.0/face/v2/detect";//人脸检测

    private String accessToken;

    private String groupId;

    @SuppressLint("StaticFieldLeak")
    private Context context;

    private static volatile APIService instace;

    public static APIService getInstance(){
        if(instace==null){
            synchronized (APIService.class){
                if(instace==null){
                    instace=new APIService();
                }
            }
        }
        return instace;
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();

        HttpUtil.getInstance().init();
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
     * @param ak       apiKey
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

    /**
     * 人脸检测
     *
     * @param listener 回调
     * @param file     人脸图片文件
     */
    public void checkface(OnResultListener listener, File file) {
        RegParams params = new RegParams();
        params.setImageFile(file);
        Log.e(TAG,ImageUtil.fileToBase64(file));//编码的日志显示输出
        params.setFace_fields("age,beauty,expression,faceshape,glasses,gender");

        RegResultParser parser = new RegResultParser();
        String url = urlAppendCommonParams(CHECK_FACE_URL);
        HttpUtil.getInstance().post(url, params, parser, listener);
    }

    /**
     * URL append access token，sdkversion，aipdevid
     *
     * @param url
     * @return
     */
    private String urlAppendCommonParams(String url) {
        StringBuilder sb = new StringBuilder(url);
        sb.append("?access_token=").append(accessToken);

        return sb.toString();
    }

}
