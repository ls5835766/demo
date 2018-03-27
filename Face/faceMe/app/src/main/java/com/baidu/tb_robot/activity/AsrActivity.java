package com.baidu.tb_robot.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.baidu.tb_robot.R;
import com.baidu.tb_robot.api.BDManager;
import com.baidu.tb_robot.api.asr.AsrCallBack;

public class AsrActivity extends AppCompatActivity {

    private TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asr);

        mText= (TextView) findViewById(R.id.txtLog);

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BDManager.asrManager().start(new AsrCallBack() {
                    @Override
                    public void onReceive(String result) {
                        mText.setText(result);
                    }
                });
            }
        });

        findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BDManager.asrManager().stop();
            }
        });
    }
}
