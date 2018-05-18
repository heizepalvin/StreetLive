package com.example.heizepalvin.streetlive.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.heizepalvin.streetlive.MainActivity;
import com.example.heizepalvin.streetlive.R;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    public static OAuthLogin naverOAuthLoginModule;
    private String accessToken;
    private String refreshToken;
    private long expiresAt;
    private String tokenType;

    //네이버 연동 로그인 셋팅
    private void setNaverLogin(){
        naverOAuthLoginModule = OAuthLogin.getInstance();
        naverOAuthLoginModule.init(this,"OPwHEtx_WJdiSFrZowv3","gCO1W3I8VS","StreetLive");
    }

    //네이버 로그인 핸들러 시작

    @SuppressLint("HandlerLeak")
    private OAuthLoginHandler naverOAuthLoginHandler = new OAuthLoginHandler() {

        @SuppressLint("LongLogTag")
        @Override
        public void run(boolean success) {
            if(success){
                //연동 로그인에 성공했을 때 사용자 정보(AccessToken, RefreshToken, ExpiresAt, TokenType) 가져옴.
                accessToken = naverOAuthLoginModule.getAccessToken(LoginActivity.this);
                refreshToken = naverOAuthLoginModule.getRefreshToken(LoginActivity.this);
                expiresAt = naverOAuthLoginModule.getExpiresAt(LoginActivity.this);
                tokenType = naverOAuthLoginModule.getTokenType(LoginActivity.this);
                Log.e("naverAccessToken",accessToken);
                Log.e("naverRefreshToken",refreshToken);
                Log.e("naverExpiresAt",expiresAt+"");
                Log.e("naverTokenType",tokenType);
//                //연동 로그인에 성공한 사용자의 정보 (닉네임, 성별, 이메일, 연령대 )를 요청하는 코드.
                naverLoginUserInfoGetAsyncTask naverUserData = new naverLoginUserInfoGetAsyncTask();
                naverUserData.execute();

            } else {
                //연동 로그인에 실패했을 때 로그와 Toast 메시지를 띄움.
                Log.e("naverLoginFail","네이버 연동 로그인 실패");
                String errorCode = naverOAuthLoginModule.getLastErrorCode(LoginActivity.this).getCode();
                String errorDesc = naverOAuthLoginModule.getLastErrorDesc(LoginActivity.this);
                Log.e("naverLoginFailErrorCode",errorCode);
                Log.e("naverLoginFailDesc",errorDesc);

//                Toast.makeText(LoginActivity.this, "errorCode:" + errorCode + ", errorDesc:"+ errorDesc
//                            , Toast.LENGTH_SHORT).show(); 에러 Toast 메시지가 필요할 때

            }
        }
    };
    //네이버 로그인 핸들러 끝

    //네이버 연동 로그인에 성공한 사용자의 정보를 가져오기위한 AsyncTask
    private class naverLoginUserInfoGetAsyncTask extends AsyncTask<String,Void,String>{

        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //JSON 형식으로 된 사용자 정보를 가져와서 처리하는 코드
            if(result != null){
                Log.e("naverAsyncTaskOnPostExecute",result);
                naverLoginUserInfoJSONdecode(result);
            }
        }

        @SuppressLint("LongLogTag")
        @Override
        protected String doInBackground(String... params) {

            //연동 로그인에 성공한 사용자의 정보 (닉네임, 성별, 이메일, 연령대 )를 요청하는 코드.

            String naverHeader = "Bearer " + accessToken;
            try{
                String naverApiURL = "https://openapi.naver.com/v1/nid/me";
                URL naverURL = new URL(naverApiURL);
                HttpURLConnection naverURLConnection = (HttpURLConnection) naverURL.openConnection();
                naverURLConnection.setRequestMethod("GET");
                naverURLConnection.setRequestProperty("Authorization",naverHeader);
                int naverResponseCode = naverURLConnection.getResponseCode();
                BufferedReader naverBufferedReader;
                if(naverResponseCode==200){
                    //정상적으로 정보를 가져왔을때
                    naverBufferedReader = new BufferedReader(new InputStreamReader(naverURLConnection.getInputStream()));
                } else {
                    //정보를 가져오는데 실패했을때
                    naverBufferedReader = new BufferedReader(new InputStreamReader(naverURLConnection.getErrorStream()));
                }
                String naverInputLine;
                StringBuffer naverUserInfoResponse = new StringBuffer();
                while((naverInputLine = naverBufferedReader.readLine())!= null){
                    naverUserInfoResponse.append(naverInputLine);
                }
                naverBufferedReader.close();
                Log.e("naverLoginUserInfo",naverUserInfoResponse.toString());
                return naverUserInfoResponse.toString();
            } catch (Exception e){
                Log.e("naverLoginUserInfoException",e.toString());
                return null;
            }

        }
    }

    //onPostExecute 에서 보낸 JSON 형식의 사용자 정보를 Decode 해주는 코드

    private String naverLoginUserInfoID;
    private String naverLoginUserInfoNickname;
    private String naverLoginUserInfoAge;
    private String naverLoginUserInfoGender;
    private String naveRLoginUserInfoEmail;


    @SuppressLint("LongLogTag")
    private void naverLoginUserInfoJSONdecode(String infoResult){
        try{
            JSONObject naverLoginUserInfoJSONObject = new JSONObject(infoResult);
            JSONObject naverLoginUserInfoJSONdata = new JSONObject(naverLoginUserInfoJSONObject.get("response").toString());
            Log.e("naverLoginUserInfoJSONDecode",naverLoginUserInfoJSONdata+"");

            // 사용자가 나이정보 수집을 동의 했을때 저장
            if(naverLoginUserInfoJSONdata.has("age")){
                naverLoginUserInfoAge = naverLoginUserInfoJSONdata.getString("age");
                Log.e("naverLoginUserInfoJSONDecodeAge","Age: "+naverLoginUserInfoAge);
            }

            // 사용자가 닉네임정보 수집을 동의 했을때 저장
            if(naverLoginUserInfoJSONdata.has("nickname")){
                naverLoginUserInfoNickname = naverLoginUserInfoJSONdata.getString("nickname");
                Log.e("naverLoginUserInfoJSONDecodeNickname","Nickname: "+naverLoginUserInfoNickname);
            }
            // 사용자가 성별정보 수집을 동의 했을때 저장
            if(naverLoginUserInfoJSONdata.has("gender")){
                naverLoginUserInfoGender = naverLoginUserInfoJSONdata.getString("gender");
                Log.e("naverLoginUserInfoJSONDecodeGender","Gender: "+naverLoginUserInfoGender);
            }
            // 사용자 이메일정보 수집을 동의 했을때 저장
            if(naverLoginUserInfoJSONdata.has("email")){
                naveRLoginUserInfoEmail = naverLoginUserInfoJSONdata.getString("email");
                Log.e("naverLoginUserInfoJSONDecodeEmail","Email: "+naveRLoginUserInfoEmail);
            }
            // 사용자가 아이디정보 수집을 동의 했을때 저장
            if(naverLoginUserInfoJSONdata.has("id")){
                naverLoginUserInfoID = naverLoginUserInfoJSONdata.getString("id");
                Log.e("naverLoginUserInfoJSONDecodeID","ID: "+naverLoginUserInfoID);
                //DB에 사용자가 가입한 정보가 있으면 바로 메인화면으로 이동 / 없으면 정보 설정 화면으로 이동
                naverLoginUserInfoDBSearchAsyncTask naverLoginUserInfoDBSearchAsyncTask = new naverLoginUserInfoDBSearchAsyncTask();
                naverLoginUserInfoDBSearchAsyncTask.execute(naverLoginUserInfoID);
            }

        }catch (Exception e){
            Log.e("naverLoginUserInfoJSONDecodeException",e.toString());
        }
    }


    private class naverLoginUserInfoDBSearchAsyncTask extends AsyncTask<String, Void , String>{

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(!s.equals("0")){
                Toast.makeText(LoginActivity.this, "환영합니다!", Toast.LENGTH_SHORT).show();
                Intent naverLoginUserMainMoveIntent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(naverLoginUserMainMoveIntent);
                finish();
            } else {
                // 사용자가 동의한 정보를 수집한 후에 닉네임,성별을 정하는 액티비티로 이동
                Intent naverLoginUserMakeServiceInfoIntent = new Intent(LoginActivity.this, infoWriteActivity.class);
                // 사용자가 닉네임정보 수집을 동의했을때 Intent로 NicknameActivity로 데이터를 보냄.
                if(naverLoginUserInfoNickname!=null){
                    naverLoginUserMakeServiceInfoIntent.putExtra("notServiceUserNickname",naverLoginUserInfoNickname);
                }
                // 사용자가 성별정보 수집을 동의했을때 Intent로 NicknameActivity로 데이터를 보냄.
                if(naverLoginUserInfoGender!=null){
                    naverLoginUserMakeServiceInfoIntent.putExtra("notServiceUserGender",naverLoginUserInfoGender);
                }
                if(naverLoginUserInfoID!=null){
                    naverLoginUserMakeServiceInfoIntent.putExtra("notServiceUserID",naverLoginUserInfoID);
                }
                // 어떤 서비스의 사용자인지 구별하는 데이터 전송
                naverLoginUserMakeServiceInfoIntent.putExtra("service","naver");
                startActivity(naverLoginUserMakeServiceInfoIntent);
                finish();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            Connection pgConnection;
            Statement pgStatement;
            ResultSet pgResultSet;

            String pgJDBCurl = "jdbc:postgresql://210.89.190.131/streetlive";
            String pgUser = "postgres";
            String pgPassword = "rmstnek123";
            String sql;
            String result;

            String userID = params[0];

            try{

            pgConnection = DriverManager.getConnection(pgJDBCurl,pgUser,pgPassword);
            pgStatement = pgConnection.createStatement();
//            sql = "select * from naverlogin.naver_user where id = '"+userID+"';";
            sql = "select count(*) from login.naver_user where id = '"+userID+"';";
            pgResultSet = pgStatement.executeQuery(sql);

            while (pgResultSet.next()){
                result = pgResultSet.getString("count");
                Log.e("네이버로그인정보","네이버로그인정보가있다 = " + pgResultSet.getString("count"));
                return result;
            }

            pgStatement.close();
                return null;

            }catch (Exception e){
                Log.e("네이버로그인정보Exception",e.toString());
                return null;
            }

        }
    }

    private String kakaoLoginUserInfoNickname;
    private long kakaoLoginUserInfoID;

    //카카오톡 세션 콜백 함수
    private class SessionCallback implements ISessionCallback{

        @Override
        public void onSessionOpened() {
                //사용자 정보 요청 결과에 대한 Callback
                UserManagement.getInstance().requestMe(new MeResponseCallback() {
                //세션 오픈 실패. 세션이 삭제된 경우
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Log.e("SessionCallback","onSessionClosed:"+errorResult.getErrorMessage());
                }
                //회원이 아닌 경우
                @Override
                public void onNotSignedUp() {
                    Log.e("SessionCallback","onNotSignedUp");
                }
                //사용자정보 요청에 성공한 경우
                @Override
                public void onSuccess(UserProfile result) {
                    Log.e("SessionCallback","onSuccess");

                    kakaoLoginUserInfoNickname = result.getNickname();
                    kakaoLoginUserInfoID = result.getId();
                    Log.e("SessionCallback","nickname="+ kakaoLoginUserInfoNickname);
                    Log.e("SessionCallback","id="+kakaoLoginUserInfoID);

                    //카카오톡 사용자의 정보가 DB에 있으면 메인화면, 없으면 정보 설정하는 화면으로 가게하는 AsyncTask
                    kakaoLoginUserInfoDBSearchAsyncTask kakaoLoginUserInfoDBSearchAsyncTask = new kakaoLoginUserInfoDBSearchAsyncTask();
                    kakaoLoginUserInfoDBSearchAsyncTask.execute(String.valueOf(kakaoLoginUserInfoID));

                }
                //사용자 정보 요청 실패

                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        super.onFailure(errorResult);
                        Log.e("SessionCallback","onFailure"+errorResult.getErrorMessage());
                    }
                });

        }
        //로그인에 실패한 상태
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("SessionCallback","onSessionOpenFailed:"+exception.getMessage());
        }
    }
    private class kakaoLoginUserInfoDBSearchAsyncTask extends AsyncTask<String, Void , String>{

        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(!s.equals("0")){
                Toast.makeText(LoginActivity.this,"환영합니다!", Toast.LENGTH_SHORT).show();
                Intent kakaoLoginUserMainMoveIntent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(kakaoLoginUserMainMoveIntent);
                finish();
            } else {
                // 사용자가 동의한 정보를 수집한 후에 닉네임, 성별을 정하는 액티비티로 이동
                Intent kakaoLoginUserMakeServiceInfoIntent = new Intent(LoginActivity.this,infoWriteActivity.class);
                // 사용자의 닉네임정보를 Intent로 데이터 전송
                kakaoLoginUserMakeServiceInfoIntent.putExtra("notServiceUserNickname",kakaoLoginUserInfoNickname);
                Log.e("kakaoLoginUserInfoDBSearch",kakaoLoginUserInfoNickname);
                // 사용자의 ID 정보를 Intent로 데이터 전송
                kakaoLoginUserMakeServiceInfoIntent.putExtra("notServiceUserID",String.valueOf(kakaoLoginUserInfoID));
                Log.e("kakaoLoginUserInfoDBSearch", String.valueOf(kakaoLoginUserInfoID));
                //어떤 서비스 로그인 연동인지 구별하는 데이터 전송
                kakaoLoginUserMakeServiceInfoIntent.putExtra("service","kakao");
                startActivity(kakaoLoginUserMakeServiceInfoIntent);
                finish();
            }

        }

        @SuppressLint("LongLogTag")
        @Override
        protected String doInBackground(String... params) {

            Connection pgConnection;
            Statement pgStatement;
            ResultSet pgResultSet;

            String pgJDBCurl = "jdbc:postgresql://210.89.190.131/streetlive";
            String pgUser = "postgres";
            String pgPassword = "rmstnek123";
            String sql;
            String result;

            String userID = params[0];


            try{

                pgConnection = DriverManager.getConnection(pgJDBCurl,pgUser,pgPassword);
                pgStatement = pgConnection.createStatement();
                sql = "select count(*) from login.kakao_user where id = '"+userID+"';";
                pgResultSet = pgStatement.executeQuery(sql);

                while(pgResultSet.next()){
                    result = pgResultSet.getString("count");
                    Log.e("kakaoLoginUserInfoDBSearch","정보 = "+pgResultSet.getString("count"));
                    return result;
                }
                pgStatement.close();
                return null;
            }catch (Exception e){
                Log.e("kakaoLoginUserInfoDBSearchException",e.toString());
                return null;
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);

        //카카오톡 키해시 구하는 코드

//        getKakaoHashKey();

        //카카오톡으로 시작하기 버튼을 눌렀을때 이벤트

        loginActivityKakaoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Session session = Session.getCurrentSession();
                LoginActivity.SessionCallback kakaoCallback = new LoginActivity.SessionCallback();
                session.addCallback(kakaoCallback);
                session.open(AuthType.KAKAO_ACCOUNT,LoginActivity.this);
            }
        });

        //네이버로 시작하기 버튼을 눌렀을때 이벤트
        loginActivityNaverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNaverLogin();
                naverOAuthLoginModule.startOauthLoginActivity(LoginActivity.this,naverOAuthLoginHandler);
            }
        });



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

    // 뒤로가기 버튼 두번 누르면 앱 종료

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if(0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime){
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(this, "뒤로가기를 한번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        }

    }
    //카카오톡 해시키 구하는 코드
    private void getKakaoHashKey(){
        try{
            PackageInfo info = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_SIGNATURES);
            for(android.content.pm.Signature signature : info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("getKakaoHashKey","key_hash="+ Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }
}
