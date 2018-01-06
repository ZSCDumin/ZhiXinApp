package com.zscdumin.zhixinapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zscdumin.zhixinapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by luo-pc on 2016/5/21.
 */
public class NewsActivity extends Activity {
    @BindView(R.id.wv_news)
    WebView wv_news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_news_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        wv_news.loadUrl(url);
        wv_news.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        WebSettings settings = wv_news.getSettings();
        //支持双击缩放
        settings.setUseWideViewPort(true);
        //支持javascript
        settings.setJavaScriptEnabled(true);
        //支持缩放
        settings.setBuiltInZoomControls(true);


        wv_news.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //开始加载网页
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wv_news.canGoBack()) {
            wv_news.goBack();
            return true;
        }
        this.finish();
        return true;
    }

}
