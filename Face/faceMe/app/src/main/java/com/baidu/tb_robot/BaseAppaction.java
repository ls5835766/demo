package com.baidu.tb_robot;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.tb_robot.api.BDManager;
import com.baidu.tb_robot.api.face.APIService;
import com.baidu.tb_robot.api.face.OnResultListener;
import com.baidu.tb_robot.api.face.exception.FaceError;
import com.baidu.tb_robot.api.face.exception.HandlerAppOnCrash;
import com.baidu.tb_robot.api.face.model.AccessToken;
import com.baidu.tb_robot.config.Config;

/**
 * zhangy
 * Created by zhangy on 2017/12/11.
 */

public class BaseAppaction extends Application {

    public static CH34xUARTDriver ch34xDriver;// 需要将CH34x的驱动类写在APP类下面，使得帮助类的生命周期与整个应用程序的生命周期是相同的

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        BDManager.Ext.init(this);

        APIService.getInstance().init(this);
        APIService.getInstance().setGroupId(Config.groupID);
        APIService.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                Toast.makeText(getApplicationContext(), "access token->" + result.getAccessToken(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FaceError error) {
                Log.e("FM", error.toString());
            }
        }, Config.apiKey, Config.secretKey);

        context = getApplicationContext();

        HandlerAppOnCrash.install(this);
        HandlerAppOnCrash.setLaunchErrorActivityWhenInBackground(true);
        HandlerAppOnCrash.setRestartActivityClass(MainActivity.class);
        HandlerAppOnCrash.setEnableAppRestart(true);

    }
}
