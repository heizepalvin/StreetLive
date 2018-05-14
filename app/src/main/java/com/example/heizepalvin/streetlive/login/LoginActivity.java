package com.example.heizepalvin.streetlive.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heizepalvin.streetlive.R;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnFocusChange;

public class LoginActivity extends AppCompatActivity {

    // loginActivity 의 ID,PWD EditText, ImageView 선언 (ButterKnife 사용)

    @BindView(R.id.loginActivityIDInput)
    EditText loginActivityIDInput;
    @BindView(R.id.loginActivityIDUnderlineImage)
    ImageView loginActivityIDUnderlineImage;
    @BindView(R.id.loginActivityPWInput)
    EditText loginActivityPWInput;
    @BindView(R.id.loginActivityPWUnderlineImage)
    ImageView loginActivityPWUnderlineImage;

    // loginActivity 의 로그인,회원가입,카카오톡,네이버 버튼 선언
    @BindView(R.id.loginActivityLoginBtn)
    Button loginActivityLoginBtn;
    @BindView(R.id.loginActivitySignupBtn)
    Button loginActivitySignupBtn;
    @BindView(R.id.loginActivityKakaoBtn)
    Button loginActivityKakaoBtn;
    @BindView(R.id.loginActivityNaverBtn)
    Button loginActivityNaverBtn;

    //네이버 연동 로그인 시작

    private OAuthLogin naverOAuthLoginMoudle;
    private Context naverLoginContext;

    private void setNaverLogin(){
        naverOAuthLoginMoudle = OAuthLogin.getInstance();
        naverOAuthLoginMoudle.init(this,"OPwHEtx_WJdiSFrZowv3","gCO1W3I8VS","StreetLive");
        loginActivityNaverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                naverOAuthLoginMoudle.startOauthLoginActivity(LoginActivity.this,naverOAuthLoginHandler);
            }
        });
    }

    //네이버 로그인 핸들러 시작

    @SuppressLint("HandlerLeak")
    private OAuthLoginHandler naverOAuthLoginHandler = new OAuthLoginHandler() {

        @Override
        public void run(boolean success) {
            if(success){
                String accessToken = naverOAuthLoginMoudle.getAccessToken(naverLoginContext);
                String refreshToken = naverOAuthLoginMoudle.getRefreshToken(naverLoginContext);
                long expiresAt = naverOAuthLoginMoudle.getExpiresAt(naverLoginContext);
                String tokenType = naverOAuthLoginMoudle.getTokenType(naverLoginContext);
                Log.d("naverAccessToken",accessToken);
                Log.d("naverRefreshToken",refreshToken);
                Log.d("naverExpiresAt",expiresAt+"");
                Log.d("naverTokenType",tokenType);
            } else {
                Log.d("naverLoginFail","실패요");
                String errorCode = naverOAuthLoginMoudle.getLastErrorCode(naverLoginContext).getCode();
                String errorDesc = naverOAuthLoginMoudle.getLastErrorDesc(naverLoginContext);
                Toast.makeText(naverLoginContext, "errorCode:" + errorCode + ", errorDesc:"+ errorDesc
                        , Toast.LENGTH_SHORT).show();
            }
        }
    };
    //네이버 로그인 핸들러 끝

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);
        setNaverLogin();

        // ID 입력하는 EditText 포커스 이벤트 리스너

        loginActivityIDInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    //아이디 EditText에 포커스가 있을때
                    loginActivityIDUnderlineImage.setImageResource(R.drawable.login_textline_focus);
                } else {
                    //아이디 EditText에 포커스가 없을때
                    loginActivityIDUnderlineImage.setImageResource(R.drawable.login_textline_nonfocus);
                }
            }
        });

        // 패스워드 입력하는 EditText 포커스 이벤트 리스너

        loginActivityPWInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    //패스워드 EditText에 포커스가 있을때
                    loginActivityPWUnderlineImage.setImageResource(R.drawable.login_textline_focus);
                } else {
                    //패스워드 EditText에 포커스가 없을때
                    loginActivityPWUnderlineImage.setImageResource(R.drawable.login_textline_nonfocus);
                }
            }
        });



    }


    // Typekit 라이브러리 (폰트 적용)를 사용하기 위해 만든 메소드
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
