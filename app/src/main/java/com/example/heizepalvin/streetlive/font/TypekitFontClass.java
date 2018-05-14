package com.example.heizepalvin.streetlive.font;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

//Typekit 라이브러리를 사용하기위해 만든 Application 클래스

public class TypekitFontClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Typekit 라이브러리로 폰트 적용시키는 구문
        Typekit.getInstance().addNormal(Typekit.createFromAsset(this,"fonts/jua.ttf"));
    }
}
