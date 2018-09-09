package com.example.heizepalvin.streetlive;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.tsengvn.typekit.TypekitContextWrapper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CashPaymentEndingActivity extends AppCompatActivity {

    @BindView(R.id.cashEndingActivityView)
    LottieAnimationView lottieAnimationView;
    @BindView(R.id.cashEndingActivityText)
    TextView cashEndingActivityText;
    @BindView(R.id.cashEndingActivityBackground)
    LinearLayout cashEndingActivityBackground;
    @BindView(R.id.cashEndingActivityHomeBtn)
    LottieAnimationView cashEndingActivityHomeBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_payment_ending_activity);
        ButterKnife.bind(this);
        Intent settingAnimationIntent = getIntent();
        String settingAnimationStatus = settingAnimationIntent.getStringExtra("status");
        if(settingAnimationStatus.equals("approve")){
            //결제성공시
            lottieAnimationView.setAnimation("giftbox.json");
            cashEndingActivityText.setText("결제가 완료되었습니다!");
            cashEndingActivityBackground.setBackgroundColor(Color.parseColor("#EAC820"));
        } else if(settingAnimationStatus.equals("cancel")){
            //결제취소시
            lottieAnimationView.setAnimation("emoji_wink.json");
            cashEndingActivityText.setText("결제가 취소되었습니다.");
            cashEndingActivityBackground.setBackgroundColor(Color.parseColor("#45D26E"));

        } else{
            //결제실패시
            lottieAnimationView.setAnimation("love_explosion.json");
            cashEndingActivityText.setText("결제가 실패하였습니다. 관리자에게 문의하세요");
            cashEndingActivityBackground.setBackgroundColor(Color.parseColor("#000000"));

        }

        //홈버튼 클릭시
        cashEndingActivityHomeBtn.setOnClickListener(v -> {
            finish();
        });

    }

    // Typekit 라이브러리 (폰트 적용)를 사용하기 위해 만든 메소드
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

}
