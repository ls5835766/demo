package com.zhangy.customview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class TobarActivity extends AppCompatActivity{

    private CustomTobar tobar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tobar);

        tobar= (CustomTobar) findViewById(R.id.topBar);
        tobar.setTobOnClickListener(new CustomTobar.tobOnClickListener() {
            @Override
            public void LeftOnClick() {
                Toast.makeText(TobarActivity.this,"left onclick!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void RightOnClick() {
                Toast.makeText(TobarActivity.this,"right onclick!",Toast.LENGTH_SHORT).show();
            }
        });

        // 控制topbar上组件的状态
        tobar.setButtonVisable(0, true);
        tobar.setButtonVisable(1, false);
    }
}
