package com.example.heizepalvin.streetlive.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heizepalvin.streetlive.MainActivity;
import com.example.heizepalvin.streetlive.R;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.heizepalvin.streetlive.login.LoginActivity.naverOAuthLoginModule;

public class infoWriteActivity extends AppCompatActivity {


    @BindView(R.id.infoWriteActivityNameInput)
    EditText infoWriteActivityNameInput;
    @BindView(R.id.infoWriteActivityNameInputUnderline)
    ImageView infoWriteActivityNameInputUnderline;
    @BindView(R.id.infoWriteActivityNameCount)
    TextView infoWriteActivityNameCount;
    @BindView(R.id.infoWriteActivityGenderBtnGroup)
    RadioGroup infoWriteActivityGenderBtnGroup;
    @BindView(R.id.infoWriteActivityManBtn)
    RadioButton infoWriteActivityManBtn;
    @BindView(R.id.infoWriteActivityWomanBtn)
    RadioButton infoWriteActivityWomanBtn;
    @BindView(R.id.infoWriteActivityNextBtn)
    Button infoWriteActivityNextBtn;
    @BindView(R.id.infoWriteActivityBirthInput)
    EditText infoWriteActivityBirthInput;
    @BindView(R.id.infoWriteActivityBirthInputUnderline)
    ImageView infoWriteActivityBirthInputUnderline;
    @BindView(R.id.infoWriteActivityBirthCount)
    TextView infoWriteActivityBirthCount;


    // 카카오톡, 네이버 연동 사용자의 정보 ( notServiceUserNickname,Gender는 사용자에 의해 변경되는 값이지만 ID는 고유한 값이므로 DB에 그대로 저장

    private String notServiceUserNickname;
    private String notServiceUserGender;
    private String notServiceUserID;
    private String serviceName;

    //최종적으로 DB에 저장되는 정보
    private String saveUserNickname;
    private String saveUserGender;
    private String saveUserBirth;

    @SuppressLint("LongLogTag")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_write_activity);
        ButterKnife.bind(this);

        //닉네임 입력칸에 포커스 이벤트
        infoWriteActivityNameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    infoWriteActivityNameInputUnderline.setImageResource(R.drawable.login_textline_focus);
                } else {
                    infoWriteActivityNameInputUnderline.setImageResource(R.drawable.login_textline_nonfocus);
                }
            }
        });

        //생년월일 입력칸에 포커스 이벤트

        infoWriteActivityBirthInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    infoWriteActivityBirthInputUnderline.setImageResource(R.drawable.login_textline_focus);
                } else {
                    infoWriteActivityBirthInputUnderline.setImageResource(R.drawable.login_textline_nonfocus);
                }
            }
        });

        // 카카오톡, 네이버 연동 사용자의 닉네임,성별 정보를 Intent로 가져옴.
        Intent notServiceUserInfoGetIntent = getIntent();

        //어떤 서비스의 사용자인지 구별하는 데이터를 받음
        serviceName = notServiceUserInfoGetIntent.getStringExtra("service");
        Log.e("infoWriteGetIntent",serviceName);
        //카카오톡, 네이버 연동 사용자의 아이디를 가져옴
        notServiceUserID = notServiceUserInfoGetIntent.getStringExtra("notServiceUserID");
        Log.e("infoWriteGetIntent",notServiceUserID);
        // 카카오톡, 네이버 연동 사용자의 닉네임을 가져왔을 때 EditText에 입력
        if(notServiceUserInfoGetIntent.getStringExtra("notServiceUserNickname")!=null){
            notServiceUserNickname = notServiceUserInfoGetIntent.getStringExtra("notServiceUserNickname");
            Log.e("notServiceUserInfoNickname",notServiceUserNickname);
            infoWriteActivityNameInput.setText(notServiceUserNickname);
            infoWriteActivityNameCount.setText(infoWriteActivityNameInput.getText().toString().length()+"/15");
        }
        //카카오톡, 네이버 연동 사용자의 성별을 가져왔을때 RadioButton에 표시
        if(notServiceUserInfoGetIntent.getStringExtra("notServiceUserGender")!=null){
            notServiceUserGender = notServiceUserInfoGetIntent.getStringExtra("notServiceUserGender");
            Log.e("notServiceUserInfoGender",notServiceUserGender);
            // 사용자의 성별이 남자일때 남자 RadioButton에 표시
            if(notServiceUserGender.equals("M")){
                infoWriteActivityGenderBtnGroup.check(infoWriteActivityManBtn.getId());
            }
            // 사용자의 성별이 여자일때 여자 RadioButton에 표시
            else {
                infoWriteActivityGenderBtnGroup.check(infoWriteActivityWomanBtn.getId());
            }
        }

        //닉네임 설정하는 EditText 글자수 체크하기위해 만든 이벤트 설정
        infoWriteActivityNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                infoWriteActivityNameCount.setText(infoWriteActivityNameInput.getText().toString().length()+"/15");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //생년월일 입력하는 EditText 글자수 체크하기위해 만든 이벤트 설정

        infoWriteActivityBirthInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                infoWriteActivityBirthCount.setText(infoWriteActivityBirthInput.getText().toString().length()+"/8");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        infoWriteActivityNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //닉네임,성별을 선택하지않았을때 Toast 메시지
                if(infoWriteActivityNameInput.getText().toString().length() == 0){
                    Toast.makeText(infoWriteActivity.this, "닉네임을 입력해주세요!", Toast.LENGTH_SHORT).show();
                } else if(!infoWriteActivityManBtn.isChecked() && !infoWriteActivityWomanBtn.isChecked()) {
                    Toast.makeText(infoWriteActivity.this, "성별을 선택해주세요!", Toast.LENGTH_SHORT).show();
                } else if(infoWriteActivityBirthInput.getText().toString().length() == 0 || infoWriteActivityBirthInput.getText().toString().length() < 8){
                    Toast.makeText(infoWriteActivity.this, "생년월일을 정확히 입력해주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    //네이버 연동 사용자
                    if(serviceName.equals("naver")){
                        //postgresql 연동해서 네이버 연동 사용자의 정보를 DB에 저장
                        saveUserNickname = infoWriteActivityNameInput.getText().toString();
                        if(infoWriteActivityManBtn.isChecked()){
                            saveUserGender = "M";
                        } else if(infoWriteActivityWomanBtn.isChecked()){
                            saveUserGender = "W";
                        }
                        saveUserBirth = infoWriteActivityBirthInput.getText().toString();
                        notServiceLoginUserInfoDBSaveAsyncTask naverLoginUserInfoDBSaveAsyncTask = new notServiceLoginUserInfoDBSaveAsyncTask();
                        naverLoginUserInfoDBSaveAsyncTask.execute(notServiceUserID,saveUserNickname,saveUserGender,saveUserBirth,serviceName);


                    } else {
                        //postgresql 연동해서 카카오톡 연동 사용자의 정보를 DB에 저장
                        saveUserNickname = infoWriteActivityNameInput.getText().toString();
                        if(infoWriteActivityManBtn.isChecked()){
                            saveUserGender = "M";
                        } else if(infoWriteActivityWomanBtn.isChecked()){
                            saveUserGender = "W";
                        }
                        saveUserBirth = infoWriteActivityBirthInput.getText().toString();
                        notServiceLoginUserInfoDBSaveAsyncTask kakaoLoginUserInfoDBSaveAsyncTask = new notServiceLoginUserInfoDBSaveAsyncTask();
                        kakaoLoginUserInfoDBSaveAsyncTask.execute(notServiceUserID,saveUserNickname,saveUserGender,saveUserBirth,serviceName);
                    }
                    // Toast 메시지를 띄우고 메인화면으로 이동
                    Toast.makeText(infoWriteActivity.this, "환영합니다!", Toast.LENGTH_SHORT).show();
                    Intent infoWriteActivityToMainActivityIntent = new Intent(infoWriteActivity.this, MainActivity.class);
                    startActivity(infoWriteActivityToMainActivityIntent);
                    finish();
                }
            }
        });

    }

    //Typekit 라이브러리 (폰트 적용)를 사용하기 위해 만든 메소드
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    //뒤로가기 두번 눌렀을 때 사용자 정보를 삭제하고 로그인 화면으로 가게함

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    public void onBackPressed() {

        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if(0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime){
            super.onBackPressed();
        } else{
            backPressedTime = tempTime;
            AlertDialog.Builder notServiceUserCancelDialogBuilder = new AlertDialog.Builder(this);

            notServiceUserCancelDialogBuilder.setTitle("로그인 화면으로 돌아가기");

            notServiceUserCancelDialogBuilder.setMessage("로그인 화면으로 돌아가시겠습니까? \n(연동된 로그인 정보는 삭제됩니다!)")
                    .setCancelable(false).setPositiveButton("종료", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {


                    //네이버 연동 로그인 정보 삭제 후 로그인화면으로 돌아가기
                    naverLoginInfoRemoveAsyncTask naverLoginInfoRemoveAsyncTask = new naverLoginInfoRemoveAsyncTask();
                    naverLoginInfoRemoveAsyncTask.execute();
                    Intent notServiceUserRemoveInfoIntent = new Intent(infoWriteActivity.this,LoginActivity.class);
                    notServiceUserRemoveInfoIntent.putExtra("naverLoginInfoRemove","remove");
                    startActivity(notServiceUserRemoveInfoIntent);
                    finish();
                }
            }).setNegativeButton("돌아가기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });

            AlertDialog notServiceUserCancelDialog = notServiceUserCancelDialogBuilder.create();
            notServiceUserCancelDialog.show();
        }

    }

    //네이버 연동 해제 및 DB 정보 삭제
    private class naverLoginInfoRemoveAsyncTask extends AsyncTask<String, Void,String>{

        @SuppressLint("LongLogTag")
        @Override
        protected String doInBackground(String... params) {
            //네이버 연동 해제
            naverOAuthLoginModule.logoutAndDeleteToken(infoWriteActivity.this);

//            Connection pgConnection;
//            Statement pgStatement;
//            int pgResult;
//
//
//            String pgJDBCurl = "jdbc:postgresql://210.89.190.131/streetlive";
//            String pgUser = "postgres";
//            String pgPassword = "rmstnek123";
//            String sql;
//
//            try{
//                pgConnection = DriverManager.getConnection(pgJDBCurl,pgUser,pgPassword);
//                pgStatement = pgConnection.createStatement();
//                sql = "delete from naverlogin.naver_user where id = '"+notServiceUserID +"';";
//                pgResult = pgStatement.executeUpdate(sql);
//                Log.e("naverLoginInfoRemoveSQL문확인",sql);
//
//                if(pgResult!=0){
//                    Log.e("naverLoginInfoRemoveResult","성공 = " + pgResult);
//                    pgStatement.close();
//                }
//
//            }catch (Exception e){
//                Log.e("naverLoginInfoRemoveException",e.toString());
//            }                                                         지금은 필요없을 것 같다. 18/05/18
            return null;
        }
    }

    private class notServiceLoginUserInfoDBSaveAsyncTask extends AsyncTask<String,Void,String>{

        @SuppressLint("LongLogTag")
        @Override
        protected String doInBackground(String... params) {
            Connection pgConnection;
            Statement pgStatement;
            int pgResult;


            String pgJDBCurl = "jdbc:postgresql://210.89.190.131/streetlive";
            String pgUser = "postgres";
            String pgPassword = "rmstnek123";
            String sql;

            String userID = params[0];
            String userNickname = params[1];
            String userGender = params[2];
            String userBirth = params[3];
            String userService = params[4];

            try{
                pgConnection = DriverManager.getConnection(pgJDBCurl,pgUser,pgPassword);
                pgStatement = pgConnection.createStatement();
                sql = "insert into login."+userService+"_user (id,nickname,gender,birth) values('"
                        +userID+"','"+userNickname+"','"+userGender+"',"+userBirth+");";
//                sql = "insert into login.naver_user (id,nickname,gender,birth) values ('"
//                        +userID+"','"+userNickname+"','"+userGender+"',"+userBirth+");";
                pgResult= pgStatement.executeUpdate(sql);
            Log.e("notServiceLoginUserInfoDBSaveSQL문확인",sql);

                if(pgResult!=0){
                    Log.e("notServiceLoginUserInfoDBSaveAsyncTask","저장성공 = " + pgResult);
                    pgStatement.close();
                }

            }catch (Exception e){
                Log.e("postgresqlException", e.toString());
            }

            return null;
        }


    }
}
