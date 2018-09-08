package com.example.heizepalvin.streetlive;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.tsengvn.typekit.TypekitContextWrapper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CashPaymentEndingActivity extends AppCompatActivity {

    @BindView(R.id.cashEndingActivityView)
    LottieAnimationView lottieAnimationView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_payment_ending_activity);
        ButterKnife.bind(this);



    }

    // Typekit 라이브러리 (폰트 적용)를 사용하기 위해 만든 메소드
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

}
