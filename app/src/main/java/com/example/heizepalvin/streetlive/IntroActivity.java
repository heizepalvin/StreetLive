package com.example.heizepalvin.streetlive;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.heizepalvin.streetlive.login.LoginActivity;
import com.tsengvn.typekit.TypekitContextWrapper;

public class IntroActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity);


        //인트로화면에서 로그인화면이나, 메인화면으로 넘어가는 핸들러
        Handler introLogoDelayHandler = new Handler();
        introLogoDelayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent introLogoDelayIntent = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(introLogoDelayIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }, 2000);

    }

    //Typekit 라이브러리 (폰트 적용)를 사용하기 위해 만든 메소드
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
