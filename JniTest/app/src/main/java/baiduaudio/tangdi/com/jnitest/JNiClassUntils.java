package baiduaudio.tangdi.com.jnitest;

/**
 * zhangy
 * Created by td on 2017/10/24.
 */

public class JNiClassUntils {

    /**
     * 如果申明的方法是static类型的，则该参数是在c文件里面是一个jclass类型的参数
     * 如果申明的方法是一个非static类型的，则该参数是一个jobject类型
     * @return
     */

    public native String helloJni();

    public native String getKeys(String name);

    static {
        System.loadLibrary("jnitest");   //defaultConfig.ndk.moduleName
    }
}
