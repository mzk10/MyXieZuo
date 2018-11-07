package com.meng.hui.android.xiezuo.util.http;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpRequester {

    public static final String URL_ENCODE = "UTF-8";
    public static final int METHOD_GET = 0;
    public static final int METHOD_POST = 1;

    private static ExecutorService exec;

    private HttpRequester(){}

    public static void request(
            String url,
            Map<String, Object> params,
            int method,
            OnRequestListener listener){

        if (exec == null){
            exec = Executors.newScheduledThreadPool(5);
        }
        HttpRequest request = new HttpRequest(url, params, method, listener);
        request.executeOnExecutor(exec, "");
    }

    public static void getContentLength(@NonNull final String url, @NonNull final BackContentLength length){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                length.backContentLength(msg.what);
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL u = new URL(url);
                    URLConnection conn = u.openConnection();
                    int contentLength = conn.getContentLength();
                    handler.sendEmptyMessage(contentLength);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public interface BackContentLength{
        void backContentLength(int length);
    }


    public interface OnRequestListener<T> {
        T onRequestInBackground(String json);
        void onRequest(T result);
    }


}
