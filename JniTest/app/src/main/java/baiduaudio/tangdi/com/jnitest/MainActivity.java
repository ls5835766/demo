package baiduaudio.tangdi.com.jnitest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTv= (TextView) findViewById(R.id.id_str);

        JNiClassUntils JNI=new JNiClassUntils();
        String str= JNI.helloJni();
        String str1=JNI.getKeys("vip");

        mTv.setText(str1);
    }
}
