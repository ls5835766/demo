package com.baidu.tb_robot.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.baidu.tb_robot.R;
import com.baidu.tb_robot.api.BDManager;
import com.baidu.tb_robot.api.asr.AsrCallBack;

public class WakeUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up);

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BDManager.asrManager().start(new AsrCallBack() {
                    @Override
                    public void onReceive(String result) {
                        BDManager.wakeUpManager().startWakeUp();
                    }
                });
            }
        });

        findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               BDManager.wakeUpManager().stopWakeUp();
            }
        });


    }
}
