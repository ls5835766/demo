/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.aip.fl;


import java.io.File;

import com.baidu.aip.fl.exception.FaceError;
import com.baidu.aip.fl.model.FaceModel;
import com.baidu.aip.fl.utils.OnResultListener;
import com.baidu.tb_robot.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 此登录方式 识别登录：直接使用人脸调用https://aip.baidubce.com/rest/2.0/face/v2/identify接口返回识别分数和uid，根据分数判断是否登录成功。
 * 该登录方式对于同一用户注册多个帐号将会出现混淆，但在登录时不需要知道uid
 * 演示示例为了跑通流程，简单省略的服务端，实际使用中建议采用，在移动端使用用户名+人脸（替代密码）请求你的服务端，
 * 在您的服务端使用人脸 调用百度identify接口，根据identify返回的分数和uid判断是否是通一个人（建议80分，），以此判断是否登录成功，
 * 最后登录信息返回给移动端
 */

public class IdentifyLoginActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int REQUEST_CODE = 100;
    private ProgressBar loading;
    private LinearLayout resultLL;
    private TextView resultTv;
    private TextView uidTv;
    private TextView scoreTv;
    private Button backBtn;

    private String filePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_identify_login);

        // TODO 实际应用时，为了防止破解app盗取ak，sk（为您在百度的标识，有了ak，sk就能使用您的账户），
        // TODO 建议把ak，sk放在服务端，移动端把相关参数传给您出服务端去调用百度人脸注册和比对服务，
        // TODO 然后再加上您的服务端返回的登录相关的返回参数给移动端进行相应的业务逻辑

        findView();

        Intent intent = new Intent(IdentifyLoginActivity.this, FaceDetectExpActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }


    private void findView() {
        loading = (ProgressBar) findViewById(R.id.loading);
        resultLL = (LinearLayout) findViewById(R.id.result_ll);
        resultTv = (TextView) findViewById(R.id.result_tv);
        uidTv = (TextView) findViewById(R.id.uid_tv);
        scoreTv = (TextView) findViewById(R.id.score_tv);
        backBtn = (Button) findViewById(R.id.back_btn);

        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == backBtn) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (data != null) {
                filePath = data.getStringExtra("file_path");

                faceLogin(filePath);
            } else {
                finish();
            }

        }
    }

    /**
     * 上传图片进行比对，分数达到80认为是同一个人，认为登录可以通过
     * 建议上传自己的服务器，在服务器端调用https://aip.baidubce.com/rest/2.0/face/v2/verify，比对分数阀值（如：80分），认为登录通过
     * 返回登录认证的参数给客户端
     *
     * @param filePath
     */
    private void faceLogin(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            Toast.makeText(this, "人脸图片不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loading.setVisibility(View.VISIBLE);
        final File file = new File(filePath);
        APIService.getInstance().identify(new OnResultListener<FaceModel>() {
            @Override
            public void onResult(FaceModel result) {
                deleteFace(file);
                loading.setVisibility(View.GONE);
                if (result == null) {
                    return;
                }
                displayData(result);
            }

            @Override
            public void onError(FaceError error) {
                error.printStackTrace();
                loading.setVisibility(View.GONE);
                deleteFace(file);
                displayError(error);
            }
        }, file);
    }

    private void deleteFace(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    private void displayData(FaceModel result) {

        resultLL.setVisibility(View.VISIBLE);
        if (result.getScore() < 80) {
            resultTv.setText("识别失败");
            scoreTv.setText("人脸识别分数过低：" + result.getScore());
        } else if (result.getFaceliveness() < 0.834963) {
            resultTv.setText("识别失败");
            scoreTv.setText("活体分数过低：" + result.getFaceliveness());
        } else {
            // 分数可以根据安全级别调整，
            resultTv.setText("识别成功");
            uidTv.setText(result.getUid());
            scoreTv.setText("人脸识别分数:" + result.getScore());
        }
    }

    private void displayError(FaceError error) {
        resultTv.setText("识别失败");
        scoreTv.setText(error.getErrorMessage());
    }
}
