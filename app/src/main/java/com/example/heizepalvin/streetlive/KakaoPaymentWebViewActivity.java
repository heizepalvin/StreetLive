package com.example.heizepalvin.streetlive;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.airbnb.lottie.L;
import com.example.heizepalvin.streetlive.login.kakao.GlobalApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KakaoPaymentWebViewActivity extends AppCompatActivity{

    @BindView(R.id.kakaoPaymentWebView)
    WebView kakaoPaymentWebView;

    private String androidScheme;
    private String redirectUrl;
    private String tid;
    private Handler javaScriptHandler = new Handler();

    String quantity;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kakao_payment_web_view);
        ButterKnife.bind(this);
        kakaoPaymentWebView.getSettings().setJavaScriptEnabled(true);
        Intent webViewUrlGetIntent = getIntent();
//        kakaoPaymentWebView.setBackgroundColor(Color.WHITE);
//        kakaoPaymentWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE,null);
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
        public void paymentApproveMethod(final String result){
            javaScriptHandler.post(() -> {
                Log.e("result?",result);
                try {
                    JSONObject kakaopayObject = new JSONObject(result);
                    String tid = kakaopayObject.getString("tid");
                    String userNickname = kakaopayObject.getString("partner_user_id");
                    String paymentType = kakaopayObject.getString("payment_method_type");
                    String itemName = kakaopayObject.getString("item_name");
                    quantity = kakaopayObject.getString("quantity");
                    String amount = kakaopayObject.getString("amount");
                    JSONObject kakaopayAmount = new JSONObject(amount);
                    String totalAmount = kakaopayAmount.getString("total");
                    String approveDateTime = kakaopayObject.getString("approved_at");
                    kakaoPaymentSaveToDB kakaoPaymentSaveToDB = new kakaoPaymentSaveToDB();
                    kakaoPaymentSaveToDB.execute(userNickname,tid,itemName,quantity,totalAmount,approveDateTime,paymentType);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            });
        }
        @android.webkit.JavascriptInterface
        public void paymentCancelMethod(final String str){
            javaScriptHandler.post(() -> {
                Intent paymentCancelActivityIntent = new Intent(KakaoPaymentWebViewActivity.this,CashPaymentEndingActivity.class);
                paymentCancelActivityIntent.putExtra("status","cancel");
                startActivity(paymentCancelActivityIntent);
                finish();
            });
        }
        @android.webkit.JavascriptInterface
        public void paymentFailMethod(final String str){
            javaScriptHandler.post(() ->{
                Intent paymentFailActivityIntent = new Intent(KakaoPaymentWebViewActivity.this,CashPaymentEndingActivity.class);
                paymentFailActivityIntent.putExtra("status","fail");
                startActivity(paymentFailActivityIntent);
                finish();
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
    //결제정보저장
    private class kakaoPaymentSaveToDB extends AsyncTask<String,String, String>{

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            userBalloonsUpdateToDB userBalloonsUpdateToDB = new userBalloonsUpdateToDB();
            userBalloonsUpdateToDB.execute();
        }

        @Override
        protected String doInBackground(String... strings) {

            Connection pgConnection;
            Statement pgStatement;
            int pgResult;

            String pgJDBCurl = "jdbc:postgresql://210.89.190.131/streetlive";
            String pgUser = "postgres";
            String pgPassword = "rmstnek123";
            String sql;

            String nickname = strings[0];
            String tid = strings[1];
            String itemName = strings[2];
            String quantity = strings[3];
            String amount = strings[4];
            String approveDateTime = strings[5];
            String paymentType = strings[6];

            try{
                pgConnection = DriverManager.getConnection(pgJDBCurl,pgUser,pgPassword);
                pgStatement = pgConnection.createStatement();
                sql = "insert into payment.kakaopay_list (nickname,tid,item_name,amount,quantity,approve_datetime,payment_type) values('"
                        +nickname+"','"+tid+"','"+itemName+"','"+amount+"','"+quantity+"','"+approveDateTime+"','"+paymentType+"');";
                pgResult = pgStatement.executeUpdate(sql);
                if(pgResult!=0){
                    Log.e("kakaoPaymentSaveToDB","kakaopay Save To Database Success!");
                    pgStatement.close();
                }
            }catch (Exception e){
                Log.e("kakaoPaymentSaveToDB",e.toString());
            }
            return null;
        }
    }

    private class userBalloonsUpdateToDB extends AsyncTask<String,String,String>{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Intent paymentApproveActivityIntent = new Intent(KakaoPaymentWebViewActivity.this,CashPaymentEndingActivity.class);
            paymentApproveActivityIntent.putExtra("status","approve");
            startActivity(paymentApproveActivityIntent);
            finish();
        }

        @Override
        protected String doInBackground(String... strings) {

            Connection pgConnection;
            Statement pgStatement;
            int pgResult;

            String pgJDBCurl = "jdbc:postgresql://210.89.190.131/streetlive";
            String pgUser = "postgres";
            String pgPassword = "rmstnek123";
            String sql;

            try{
                SharedPreferences sharedPreferences = getSharedPreferences("userLoginInfo",MODE_PRIVATE);
                String userServiceInfo = sharedPreferences.getString("service","null");
                String userNickname = sharedPreferences.getString("nickname","null");
                pgConnection = DriverManager.getConnection(pgJDBCurl,pgUser,pgPassword);
                pgStatement = pgConnection.createStatement();
                sql = "update login."+userServiceInfo+"_user set balloons = balloons+"+quantity+" where nickname = '"+userNickname+"';";
                pgResult = pgStatement.executeUpdate(sql);
                if(pgResult!=0){
                    Log.e("userBalloonsUpdateToDB","update Success!");
                    pgStatement.close();
                }
            }catch (Exception e){
                Log.e("userBalloonsUpdateToDB",e.toString());
            }

            return null;
        }
    }


}
