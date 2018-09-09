package com.example.heizepalvin.streetlive;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.heizepalvin.streetlive.login.kakao.GlobalApplication;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CashPaymentActivity extends AppCompatActivity {

    @BindView(R.id.paymentUserCashView)
    TextView paymentUserCashView;
    @BindView(R.id.paymentKakaoBtn)
    ImageView paymentKakaoBtn;
    @BindView(R.id.paymentSpinner)
    Spinner paymentSpinner;
    @BindView(R.id.paymentCashEditText)
    EditText paymentCashEditText;

    ArrayList paymentSpinner_items;

    private OkHttpClient okHttpClient = new OkHttpClient();

    private String userSelectPrice;
    private String userNickname;
    private String nextRedirectAppUrl;
    private String userLoginServiceInfo;

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_payment_activity);
        ButterKnife.bind(this);
        sharedPreferences = getSharedPreferences("userLoginInfo",MODE_PRIVATE);
        userNickname = sharedPreferences.getString("nickname","null");
        userLoginServiceInfo = sharedPreferences.getString("service","null");
        Log.e("userLoginServiceInfo?",userLoginServiceInfo);
        //사용자 별풍선 정보를 가져옴
        userBalloonsGetToDB userBalloonsGetToDB = new userBalloonsGetToDB();
        userBalloonsGetToDB.execute();

        paymentSpinner_items = new ArrayList();

        //Spinner 설정
        paymentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)parent.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView)parent.getChildAt(0)).setGravity(Gravity.CENTER);

                if(parent.getItemAtPosition(position).toString().equals("직접입력")){
                    paymentCashEditText.setVisibility(View.VISIBLE);
                } else {
                    paymentCashEditText.setVisibility(View.GONE);
                    //키보드 숨기는 메소드
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(paymentCashEditText.getWindowToken(),0);
                }
                Log.e("paymentSpinner",parent.getItemAtPosition(position).toString());
                userSelectPrice = parent.getItemAtPosition(position).toString();
                Log.e("ddd",userSelectPrice.substring(0,userSelectPrice.length()-1));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter paymentSpinner_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentSpinner_items);

        paymentSpinner_items.add("금액을 선택해주세요.");
        paymentSpinner_items.add("1000원");
        paymentSpinner_items.add("3000원");
        paymentSpinner_items.add("5000원");
        paymentSpinner_items.add("10000원");
        paymentSpinner_items.add("직접입력");

        paymentSpinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentSpinner.setAdapter(paymentSpinner_adapter);

        //카카오페이버튼이미지변형
        Glide.with(this).load(R.drawable.kakaopay)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(100)))
                .into(paymentKakaoBtn);

        //카카오페이 버튼 클릭 이벤트
        paymentKakaoBtn.setOnClickListener(v -> {
            if(paymentCashEditText.getVisibility() == View.VISIBLE){
                //카카오페이 버튼 클릭시 사용자가 직접 금액을 입력했을때 예외처리

                if(paymentCashEditText.getText().toString().replace(" ","").equals("")){
                    Toast.makeText(this, "충전하실 금액을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Integer paymentCashEditTextNumber = Integer.parseInt(paymentCashEditText.getText().toString());
                    if(paymentCashEditTextNumber >= 1000001 || paymentCashEditTextNumber <= 999){
                        Toast.makeText(this, "한번에 1000원이상 100만원이하만 충전 가능합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        userSelectPrice = paymentCashEditText.getText().toString() + "원";
                        kakaopayPaymentRequest kakaopayPaymentRequest = new kakaopayPaymentRequest();
                        kakaopayPaymentRequest.execute(userSelectPrice,userNickname);
                    }
                }

            } else {
                //카카오페이 버튼 클릭시 사용자가 직접 금액을 입력하지 않았을때 예외처리

                if(userSelectPrice.equals("금액을 선택해주세요.")){
                    Toast.makeText(this, "충전하실 금액을 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    kakaopayPaymentRequest kakaopayPaymentRequest = new kakaopayPaymentRequest();
                    kakaopayPaymentRequest.execute(userSelectPrice,userNickname);
                }
            }
        });

    }
    //카카오페이 결제요청 api AsyncTask
    private class kakaopayPaymentRequest extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {

//            for(int i =0; i<5; i++){
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }

            String userPaymentPrice = strings[0];
            String userNickname = strings[1];
            Log.e("userPaymentPrice",strings[0]);
            RequestBody formbody = new FormBody.Builder()
                    .add("cid","TC0ONETIME")
                    .add("partner_order_id","StreetLive")
                    .add("partner_user_id",userNickname)
                    .add("item_name","별풍선 " + userPaymentPrice)
                    .add("quantity",userPaymentPrice.substring(0,userPaymentPrice.length()-1))
                    .add("total_amount",userPaymentPrice.substring(0,userPaymentPrice.length()-1))
                    .add("tax_free_amount","0")
                    .add("vat_amount","0")
                    .add("approval_url","http://210.89.190.131/kakaoPaymentApprove.php?userid="+userNickname)
                    .add("fail_url","http://210.89.190.131/kakaoPaymentFail.php?userid="+userNickname)
                    .add("cancel_url","http://210.89.190.131/kakaoPaymentCancel.php?userid="+userNickname)
                    .build();

            Request request = new Request.Builder()
                    .url("https://kapi.kakao.com/v1/payment/ready")
                    .post(formbody)
                    .addHeader("Authorization","KakaoAK a4dc70fa79ece1aeb64f46da172c4af8")
                    .build();

            okHttpClient.newCall(request).enqueue(kakaoPaymentRequestCallback);

            return null;
        }
    }

    //카카오페이 결제요청 API 결과
    private Callback kakaoPaymentRequestCallback = new Callback() {
        @SuppressLint("LongLogTag")
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e("kakaoPaymentRequestCallback","error Message : "+ e.getMessage());
        }


        @SuppressLint("LongLogTag")
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String responseData = response.body().string();
            Log.e("kakaoPaymentRequestCallback","responseData : " + responseData);
            try {
                JSONObject jsonObject = new JSONObject(responseData);
                String tid = jsonObject.getString("tid");
                nextRedirectAppUrl = jsonObject.getString("next_redirect_app_url");
                String androidAppScheme = jsonObject.getString("android_app_scheme");

                kakaopayTidSendToApproveUrl kakaopayTidSendToApproveUrl = new kakaopayTidSendToApproveUrl();
                kakaopayTidSendToApproveUrl.execute(tid,userNickname);

                Intent webViewSendUrl = new Intent(CashPaymentActivity.this,KakaoPaymentWebViewActivity.class);
                webViewSendUrl.putExtra("redirectUrl",nextRedirectAppUrl);
                webViewSendUrl.putExtra("tid",tid);
                webViewSendUrl.putExtra("androidScheme",androidAppScheme);
                startActivity(webViewSendUrl);
                finish();


                Log.e("tid는?",nextRedirectAppUrl);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    //카카오페이 결제승인 API 요청 전 TID  redis에 저장하기위해 tid와 유저 닉네임 보내주는 AsyncTask
    private class kakaopayTidSendToApproveUrl extends AsyncTask<String,String,String>{


        @Override
        protected String doInBackground(String... strings) {
            String userTid = strings[0];
            String userId = strings[1];
            RequestBody formbody = new FormBody.Builder()
                    .add("ID",userId)
                    .add("tid",userTid)
                    .build();

            Request request = new Request.Builder()
                    .url("http://106.10.43.183:80/kakaopay/")
                    .post(formbody)
                    .build();
            okHttpClient.newCall(request).enqueue(kakaoPaymentRequestTidCallback);

            return null;
        }
    }

    //카카오페이 결제승인 API 요청 전 TID  redis에 저장하기위해 tid와 유저 닉네임 보내준 결과
    private Callback kakaoPaymentRequestTidCallback = new Callback() {
        @SuppressLint("LongLogTag")
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e("kakaoPaymentReqestTidCallback","error Message : "+ e.getMessage());

        }
        @SuppressLint("LongLogTag")
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String responseData = response.body().string();
            Log.e("kakaoPaymentReqestTidCallback","responseData : " + responseData);
        }
    };

    //사용자 별풍선 정보 데이터베이스에서 가져오는 AsyncTask

    private class userBalloonsGetToDB extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalApplication.getGlobalApplicationContext().progressOn(CashPaymentActivity.this,null);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            GlobalApplication.getGlobalApplicationContext().progressOff();
        }

        @Override
        protected String doInBackground(String... strings) {

            Connection pgConnection;
            Statement pgStatement;
            ResultSet pgResult;

            String pgJDBCurl = "jdbc:postgresql://210.89.190.131/streetlive";
            String pgUser = "postgres";
            String pgPassword = "rmstnek123";
            String sql;
            try{
                pgConnection = DriverManager.getConnection(pgJDBCurl,pgUser,pgPassword);
                pgStatement = pgConnection.createStatement();
                sql = "select * from login."+userLoginServiceInfo+"_user where nickname ='"+userNickname+"';";
                pgResult = pgStatement.executeQuery(sql);
                while(pgResult.next()){
                    String balloons = pgResult.getString("balloons");
                    paymentUserCashView.setText(balloons);
                }
                pgStatement.close();
            }catch (Exception e){
                Log.e("UserBalloonsGetToDB",e.toString());
            }

            return null;
        }
    }

    // Typekit 라이브러리 (폰트 적용)를 사용하기 위해 만든 메소드
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }


}
