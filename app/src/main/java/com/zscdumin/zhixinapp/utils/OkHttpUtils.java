package com.zscdumin.zhixinapp.utils;

import android.os.Handler;
import android.os.Looper;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by luo-pc on 2016/5/23.
 */
public class OkHttpUtils {
    public static final String TAG = "OkHttpUtils";

    private static OkHttpUtils okInstance;
    private OkHttpClient okHttpClient;
    private Handler delivery;

    private OkHttpUtils() {
        okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
        delivery = new Handler(Looper.getMainLooper());
    }

    private synchronized static OkHttpUtils getOkInstance() {
        if (okInstance == null) {
            okInstance = new OkHttpUtils();
        }

        return okInstance;
    }


    private void getRequest(String url) {
        Request request = new Request.Builder().url(url).build();
    }

    private void postRequest(String url, List<Param> params) {
        Request request = buildPostRequest(url, params);

    }


    private void deliverResult(Request request) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String str = response.body().string();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface sendSuccessCallBack {

    }


    private Request buildPostRequest(String url, List<Param> params) {
        //这个builder应该可以将中文转换成相应的编码
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (Param param : params) {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).post(requestBody).build();
    }


    public static class Param {
        String key;
        String value;

        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

    }


}
