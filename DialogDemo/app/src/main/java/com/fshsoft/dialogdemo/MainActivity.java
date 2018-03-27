package com.fshsoft.dialogdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        CenterDialog.OnCenterItemClickListener, BottomDialog.OnBottomMenuItemClickListener {

    private Button button;
    private CenterDialog centerDialog;
    private BottomDialog bottomDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

        centerDialog = new CenterDialog(this, R.layout.dialog_layout, new int[]{R.id.dialog_cancel, R.id.dialog_sure});
        centerDialog.setOnCenterItemClickListener(this);

        bottomDialog = new BottomDialog(this, R.layout.dialog_bottom_layout, new int[]{R.id.pick_photo_album, R.id.pick_photo_camera, R.id.pick_photo_cancel});
        bottomDialog.setOnBottomMenuItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        centerDialog.show();
        TextView dialog_sure = (TextView) centerDialog.findViewById(R.id.dialog_sure);
        dialog_sure.setText("添加图片");

//        bottomDialog.show();

    }

    @Override
    public void OnCenterItemClick(CenterDialog dialog, View view) {
        switch (view.getId()){
            case R.id.dialog_sure:
                Toast.makeText(MainActivity.this,"按钮点击",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBottomMenuItemClick(BottomDialog dialog, View view) {
        switch (view.getId()){
            case R.id.pick_photo_album:
                Toast.makeText(MainActivity.this,"从相册选取",Toast.LENGTH_SHORT).show();
                break;
            case R.id.pick_photo_camera:
                Toast.makeText(MainActivity.this,"拍照",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
