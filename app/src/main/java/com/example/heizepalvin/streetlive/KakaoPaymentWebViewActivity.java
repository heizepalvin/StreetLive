package com.example.heizepalvin.streetlive;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KakaoPaymentWebViewActivity extends AppCompatActivity {

    @BindView(R.id.kakaoPaymentWebView)
    WebView kakaoPaymentWebView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kakao_payment_web_view);
        ButterKnife.bind(this);
        kakaoPaymentWebView.getSettings().setJavaScriptEnabled(true);
        Intent webViewUrlGetIntent = getIntent();

        String androidScheme = webViewUrlGetIntent.getStringExtra("androidScheme");
        String redirectUrl = webViewUrlGetIntent.getStringExtra("redirectUrl");
        String tid = webViewUrlGetIntent.getStringExtra("tid");

        kakaoPaymentWebView.loadUrl(redirectUrl);
        kakaoPaymentWebView.setWebChromeClient(new WebChromeClient());
    }
}
