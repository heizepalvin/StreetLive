package com.example.heizepalvin.streetlive.login.kakao;

import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.heizepalvin.streetlive.R;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.tsengvn.typekit.Typekit;


//카카오톡 SDK 초기화 하는 클래스
public class GlobalApplication extends Application{

    private static GlobalApplication instance;

    //lottie 로딩
    AppCompatDialog progressDialog;

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

    //lottie 로딩

    public void progressOn(Activity activity, String message){
        if(activity == null){
            return;
        }
        if(progressDialog != null && progressDialog.isShowing()){
            progressSet(message);
        } else {
            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.progress_loading_layout);
            progressDialog.show();
        }

        final LottieAnimationView lottieAnimationView = (LottieAnimationView) progressDialog.findViewById(R.id.progressLodingImg);
        lottieAnimationView.playAnimation();

        TextView progressLodingMsg = (TextView) progressDialog.findViewById(R.id.progressLodingMsg);
        if(!TextUtils.isEmpty(message)){
            progressLodingMsg.setText(message);
        }
    }

    public void progressSet(String message){
        if(progressDialog == null || !progressDialog.isShowing()){
            return;
        }
        TextView progressLodingMsg = (TextView) progressDialog.findViewById(R.id.progressLodingMsg);
        if(!TextUtils.isEmpty(message)){
            progressLodingMsg.setText(message);
        }
    }

    public void progressOff(){
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }
}
