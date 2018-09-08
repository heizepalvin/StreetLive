package com.example.heizepalvin.streetlive;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.heizepalvin.streetlive.login.kakao.GlobalApplication;

import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KakaoPaymentWebViewActivity extends AppCompatActivity{

    @BindView(R.id.kakaoPaymentWebView)
    WebView kakaoPaymentWebView;

    private String androidScheme;
    private String redirectUrl;
    private String tid;
    private Handler javaScriptHandler = new Handler();

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kakao_payment_web_view);
        ButterKnife.bind(this);
        kakaoPaymentWebView.getSettings().setJavaScriptEnabled(true);
        Intent webViewUrlGetIntent = getIntent();
        kakaoPaymentWebView.setBackgroundColor(Color.WHITE);
        kakaoPaymentWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE,null);
        androidScheme = webViewUrlGetIntent.getStringExtra("androidScheme");
        redirectUrl = webViewUrlGetIntent.getStringExtra("redirectUrl");
        tid = webViewUrlGetIntent.getStringExtra("tid");
        Log.e("redirectURL",redirectUrl);
        kakaoPaymentWebView.setWebViewClient(new webViewClient());
        kakaoPaymentWebView.addJavascriptInterface(new JavascriptInterface(),"StreetLive");
        kakaoPaymentWebView.loadUrl(redirectUrl);

    }

    final class JavascriptInterface{
        @android.webkit.JavascriptInterface
        public void callMethodName(final String str){
                javaScriptHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent paymentEndingActivityIntent = new Intent(KakaoPaymentWebViewActivity.this,CashPaymentEndingActivity.class);
                        startActivity(paymentEndingActivityIntent);
                        finish();
                    }
                });
        }

    }


    private class webViewClient extends WebViewClient{

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            GlobalApplication.getGlobalApplicationContext().progressOn(KakaoPaymentWebViewActivity.this,null);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            GlobalApplication.getGlobalApplicationContext().progressOff();

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if(url != null && url.startsWith("intent://")){
                try{
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                    if(existPackage != null){
                        startActivity(intent);
                    }

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            view.loadUrl(url);
            return false;
        }
    }


}
