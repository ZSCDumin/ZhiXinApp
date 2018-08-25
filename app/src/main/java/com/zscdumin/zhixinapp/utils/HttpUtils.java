package com.zscdumin.zhixinapp.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by luo-pc on 2016/5/12.
 */
public class HttpUtils {
    public static void getJsonString(final String url, final HttpCallbackListener listener) {

        HttpURLConnection connection = null;
        StringBuilder sb = new StringBuilder();
        try {
            URL dataurl = new URL(url);
            connection = (HttpURLConnection) dataurl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);

            InputStream in = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            if (listener != null) {
                listener.onFinish(sb.toString());
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onError(e);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public interface HttpCallbackListener {
        void onFinish(String response);
        void onError(Exception e);
    }
}


