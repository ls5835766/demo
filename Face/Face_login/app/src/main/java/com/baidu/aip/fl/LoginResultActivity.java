/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.aip.fl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

import com.baidu.aip.fl.utils.ImageSaveUtil;
import com.baidu.tb_robot.R;


/**
 * 此登录方式为加强安全级别 密码+ 人脸：先使用用户名密码登录拿到uid，
 * 再使用uid和人脸 调用https://aip.baidubce.com/rest/2.0/face/v2/verify接口
 * <p>
 * <p>
 * 实际应用时，为了防止破解app盗取ak，sk（为您在百度的标识，有了ak，sk就能使用您的账户），
 * 建议把ak，sk放在服务端，移动端把相关参数传给您出服务端去调用百度人脸注册和比对服务，
 * 然后再加上您的服务端返回的登录相关的返回参数给移动端进行相应的业务逻辑
 */

public class LoginResultActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 100;
    private TextView resultTv;
    private TextView uidTv;
    private TextView scoreTv;
    private Button backBtn;
    private ImageView headIv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_result);

        findView();
        addListener();
        displayData();

    }

    private void findView() {
        resultTv = (TextView) findViewById(R.id.result_tv);
        uidTv = (TextView) findViewById(R.id.uid_tv);
        scoreTv = (TextView) findViewById(R.id.score_tv);
        backBtn = (Button) findViewById(R.id.back_btn);
        headIv = (ImageView) findViewById(R.id.head_iv);
    }

    private void displayData() {
        Intent intent = getIntent();
        if (intent != null) {
            boolean loginSuccess = intent.getBooleanExtra("login_success", false);

            if (loginSuccess) {
                resultTv.setText("识别成功");
                String uid = intent.getStringExtra("uid");
                String userInfo = intent.getStringExtra("user_info");
                double score = intent.getDoubleExtra("score", 0);
                if (TextUtils.isEmpty(userInfo)) {
                    uidTv.setText(uid);
                } else {
                    uidTv.setText(userInfo);
                }

                scoreTv.setText(String.valueOf(score));


            } else {
                resultTv.setText("识别失败");
                String uid = intent.getStringExtra("uid");
                String errorMsg = intent.getStringExtra("error_msg");
                uidTv.setText(uid);
                scoreTv.setText(String.valueOf(errorMsg));
            }
            headIv.setVisibility(View.VISIBLE);
            Bitmap bmp = ImageSaveUtil.loadCameraBitmap(this, "head_tmp.jpg");
            if (bmp != null) {
                headIv.setImageBitmap(bmp);
            }

        }

    }

    private void addListener() {
        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (backBtn == v) {
            finish();
        }
    }

}
