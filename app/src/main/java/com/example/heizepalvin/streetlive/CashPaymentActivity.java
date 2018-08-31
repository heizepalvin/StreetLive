package com.example.heizepalvin.streetlive;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_payment_activity);
        ButterKnife.bind(this);

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

        paymentKakaoBtn.setOnClickListener(v -> {
            if(paymentCashEditText.getVisibility() == View.VISIBLE){
                Integer paymentCashEditTextNumber = Integer.parseInt(paymentCashEditText.getText().toString());
                if(paymentCashEditTextNumber >= 1000001 || paymentCashEditTextNumber <= 999){
                    Toast.makeText(this, "한번에 1000원이상 100만원이하만 충전 가능합니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                kakaopayRequestTest kakaopayRequestTest = new kakaopayRequestTest();
                kakaopayRequestTest.execute();
            }
        });

    }

    private class kakaopayRequestTest extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {

            RequestBody formbody = new FormBody.Builder()
                    .add("Authorization","KakaoAK a4dc70fa79ece1aeb64f46da172c4af8")
                    .add("cid","TC0ONETIME")
                    .add("partner_order_id","partner_order_id")
                    .add("partner_user_id","partner_user_id")
                    .add("item_name","별풍선")
                    .add("quantity","1000")
                    .add("total_amount","1000")
                    .add("tax_free_amount","0")
                    .add("vat_amount","100")
                    .add("approval_url","http://210.89.190.131")
                    .add("fail_url","http://106.10.43.183")
                    .add("cancel_url","http://115.71.232.155")
                    .build();

            Request request = new Request.Builder()
                    .url("https://kapi.kakao.com/v1/payment/ready")
                    .post(formbody)
                    .addHeader("Authorization","KakaoAK a4dc70fa79ece1aeb64f46da172c4af8")
                    .build();

            okHttpClient.newCall(request).enqueue(kakaoPaymentTestCallback);



            return null;
        }
    }

    private Callback kakaoPaymentTestCallback = new Callback() {
        @SuppressLint("LongLogTag")
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e("kakaoPaymentTestCallback","error Message : "+ e.getMessage());
        }


        @SuppressLint("LongLogTag")
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String responseData = response.body().string();
            Log.e("kakaoPaymentTestCallback","responseData : " + responseData);
            try {
                JSONObject jsonObject = new JSONObject(responseData);
                String tid = jsonObject.getString("tid");
                String nextRedirectAppUrl = jsonObject.getString("next_redirect_app_url");
                String androidAppScheme = jsonObject.getString("android_app_scheme");
                Intent webViewSendUrl = new Intent(CashPaymentActivity.this,KakaoPaymentWebViewActivity.class);
                webViewSendUrl.putExtra("redirectUrl",nextRedirectAppUrl);
                webViewSendUrl.putExtra("tid",tid);
                webViewSendUrl.putExtra("androidScheme",androidAppScheme);
                startActivity(webViewSendUrl);
                Log.e("tid는?",nextRedirectAppUrl);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    // Typekit 라이브러리 (폰트 적용)를 사용하기 위해 만든 메소드
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }


}
