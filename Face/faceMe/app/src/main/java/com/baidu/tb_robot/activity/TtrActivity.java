package com.baidu.tb_robot.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.baidu.tb_robot.R;
import com.baidu.tb_robot.api.BDManager;
import com.baidu.tb_robot.api.tts.TtsCallBack;

public class TtrActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ttr);

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BDManager.ttsManager().speaks("你好啊,你在哪里呢？", new TtsCallBack() {
                    @Override
                    public void onFinish(byte[] bytes) {
                        Log.e("zhangy",bytes.toString());
                    }
                });
            }
        });

        findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BDManager.ttsManager().stop();
            }
        });
    }
}
