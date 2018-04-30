package com.example.heizepalvin.streetlive;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity);

        Handler introLogoDelayHandler = new Handler();
        introLogoDelayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent introLogoDelayIntent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(introLogoDelayIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }, 2000);

    }
}
