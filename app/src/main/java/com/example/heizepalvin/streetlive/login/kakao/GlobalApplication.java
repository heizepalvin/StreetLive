package com.example.heizepalvin.streetlive.login.kakao;

import android.app.Application;

import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.tsengvn.typekit.Typekit;


//카카오톡 SDK 초기화 하는 클래스
public class GlobalApplication extends Application{

    private static GlobalApplication instance;
    public static GlobalApplication getGlobalApplicationContext(){
        if(instance==null){
            throw new IllegalStateException("This Application does not inherit com.kakao.GlobalApplication");
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        //카카오 SDK 초기화
        KakaoSDK.init(new KakaoSDKAdapter());

        //Typekit 라이브러리로 폰트 적용
        Typekit.getInstance().addNormal(Typekit.createFromAsset(this,"fonts/jua.ttf"));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }
}
