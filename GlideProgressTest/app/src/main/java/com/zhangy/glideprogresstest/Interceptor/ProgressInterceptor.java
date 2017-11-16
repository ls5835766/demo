package com.zhangy.glideprogresstest.Interceptor;

import com.zhangy.glideprogresstest.progress.ProgressListener;
import com.zhangy.glideprogresstest.progress.ProgressResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 自定义okhttp中的拦截器
 * 空拦截器
 * zhangy
 * Created by zhangy on 2017/11/14.
 */

public class ProgressInterceptor implements Interceptor {

    /**
     * 注册下载监听和取消注册下载监听的方法
     */
    public  static final Map<String, ProgressListener> LISTENER_MAP = new HashMap<>();

    public static void addListener(String url, ProgressListener listener) {
        LISTENER_MAP.put(url, listener);
    }

    public static void removeListener(String url) {
        LISTENER_MAP.remove(url);
    }

    /**这个拦截器中我们可以说是什么都没有做。
    就是拦截到了OkHttp的请求，然后调用proceed()方法去处理这个请求，
    最终将服务器响应的Response返回。*/

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request=chain.request();
        Response response=chain.proceed(request);
        String url = request.url().toString();
        ResponseBody body = response.body();
        Response newResponse = response.newBuilder().body(new ProgressResponseBody(url, body)).build();
        return newResponse;
    }
}
