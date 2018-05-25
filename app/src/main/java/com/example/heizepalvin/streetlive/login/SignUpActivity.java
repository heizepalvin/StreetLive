package com.example.heizepalvin.streetlive.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.sql.Statement;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.signUpActivityNicknameInput)
    EditText signUpActivityNicknameInput;
    @BindView(R.id.signUpActivityNicknameUnderline)
    ImageView signUpActivityNicknameUnderline;
    @BindView(R.id.signUpActivityNicknameCount)
    TextView signUpActivityNicknameCount;
    @BindView(R.id.signUpActivityNicknameConfirmText)
    TextView signUpActivityNicknameConfirmText;
    @BindView(R.id.signUpActivityPwdInput)
    EditText signUpActivityPwdInput;
    @BindView(R.id.signUpActivityPwdUnderline)
    ImageView signUpActivityPwdUnderline;
    @BindView(R.id.signUpActivityPwd2Input)
    EditText signUpActivityPwd2Input;
    @BindView(R.id.signUpActivityPwd2Underline)
    ImageView signUpActivityPwd2Underline;
    @BindView(R.id.signUpActivityPwdConfirmText)
    TextView signUpActivityPwdConfirmText;
    @BindView(R.id.signUpActivityGenderGroup)
    RadioGroup signUpActivityGenderGroup;
    @BindView(R.id.signUpActivityManBtn)
    RadioButton signUpActivityManBtn;
    @BindView(R.id.signUpActivityWomanBtn)
    RadioButton signUpActivityWomanBtn;
    @BindView(R.id.signUpActivityBirthInput)
    EditText signUpActivityBirthInput;
    @BindView(R.id.signUpActivityBirthUnderline)
    ImageView signUpActivityBirthUnderline;
    @BindView(R.id.signUpActivityBirthCheck)
    TextView signUpActivityBirthCheck;
    @BindView(R.id.signUpActivityOkBtn)
    Button signUpActivityOkBtn;

    // DB에 저장될 성별

    private String serviceUserGender;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);
        ButterKnife.bind(this);

        // 닉네임 입력칸 포커스 이벤트
        signUpActivityNicknameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    signUpActivityNicknameUnderline.setImageResource(R.drawable.login_textline_focus);

                } else {
                    signUpActivityNicknameUnderline.setImageResource(R.drawable.login_textline_nonfocus);
                    if(signUpActivityNicknameInput.getText().toString().replace(" ","").equals("")){
                        signUpActivityNicknameConfirmText.setText("사용하실 닉네임을 입력해주세요.");
                        signUpActivityNicknameConfirmText.setTextColor(Color.WHITE);
                    } else {
                        //실시간 중복확인을 진행
                        nicknameConfirmAsyncTask nicknameConfirmAsyncTask = new nicknameConfirmAsyncTask();
                        nicknameConfirmAsyncTask.execute(signUpActivityNicknameInput.getText().toString());
                    }
                }
            }
        });

        //닉네임 입력칸 글자수 체크 이벤트
        signUpActivityNicknameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                signUpActivityNicknameCount.setText(signUpActivityNicknameInput.getText().toString().length()+"/15");

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 비밀번호 입력칸 포커스 이벤트
        signUpActivityPwdInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    signUpActivityPwdUnderline.setImageResource(R.drawable.login_textline_focus);
                } else {
                    signUpActivityPwdUnderline.setImageResource(R.drawable.login_textline_nonfocus);
                }
            }
        });

        // 비밀번호확인 입력칸 포커스 이벤트
        signUpActivityPwd2Input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    signUpActivityPwd2Underline.setImageResource(R.drawable.login_textline_focus);
                } else {
                    signUpActivityPwd2Underline.setImageResource(R.drawable.login_textline_nonfocus);
                }
            }
        });

        // 비밀번호 형식, 비밀번호 일치를 확인하는 이벤트

        signUpActivityPwdInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!Pattern.matches("^(?=.*[a-zA-Z])(?=.*[!@#$%^~*+=-])(?=.*[0-9]).{8,20}$",signUpActivityPwdInput.getText().toString())){
                    signUpActivityPwdConfirmText.setTextColor(Color.RED);
                    signUpActivityPwdConfirmText.setText("비밀번호 형식이 맞지 않습니다.");
                } else {

                    if(signUpActivityPwdInput.getText().toString().equals(signUpActivityPwd2Input.getText().toString())){
                        signUpActivityPwdConfirmText.setTextColor(Color.CYAN);
                        signUpActivityPwdConfirmText.setText("비밀번호가 일치합니다.");
                    } else {
                        signUpActivityPwdConfirmText.setTextColor(Color.RED);
                        signUpActivityPwdConfirmText.setText("비밀번호가 일치하지 않습니다.");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signUpActivityPwd2Input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!Pattern.matches("^(?=.*[a-zA-Z])(?=.*[!@#$%^~*+=-])(?=.*[0-9]).{8,20}$",signUpActivityPwdInput.getText().toString())){
                    signUpActivityPwdConfirmText.setTextColor(Color.RED);
                    signUpActivityPwdConfirmText.setText("비밀번호 형식이 맞지 않습니다.");
                } else {

                    if(signUpActivityPwdInput.getText().toString().equals(signUpActivityPwd2Input.getText().toString())){
                        signUpActivityPwdConfirmText.setTextColor(Color.CYAN);
                        signUpActivityPwdConfirmText.setText("비밀번호가 일치합니다.");
                    } else {
                        signUpActivityPwdConfirmText.setTextColor(Color.RED);
                        signUpActivityPwdConfirmText.setText("비밀번호가 일치하지 않습니다.");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //생년월일 입력칸 포커스 이벤트
        signUpActivityBirthInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    signUpActivityBirthUnderline.setImageResource(R.drawable.login_textline_focus);
                }else {
                    signUpActivityBirthUnderline.setImageResource(R.drawable.login_textline_nonfocus);
                }
            }
        });

        //ok 버튼 클릭 이벤트

        signUpActivityOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(signUpActivityNicknameConfirmText.getText().toString().equals("사용할 수 없는 닉네임 입니다.")){
                    Toast.makeText(SignUpActivity.this, "다른 닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    signUpActivityNicknameInput.requestFocus();
                } else if(signUpActivityNicknameConfirmText.getText().toString().equals("사용하실 닉네임을 입력해주세요.")){
                    Toast.makeText(SignUpActivity.this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    signUpActivityNicknameInput.requestFocus();
                } else if(signUpActivityPwdConfirmText.getText().toString().equals("비밀번호 형식이 맞지 않습니다.")){
                    Toast.makeText(SignUpActivity.this, "비밀번호 형식이 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                    signUpActivityPwdInput.requestFocus();
                } else if(signUpActivityPwdConfirmText.getText().toString().equals("비밀번호가 일치하지 않습니다.")){
                    Toast.makeText(SignUpActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    signUpActivityPwdInput.requestFocus();
                } else if(!signUpActivityManBtn.isChecked() && !signUpActivityWomanBtn.isChecked()){
                    Toast.makeText(SignUpActivity.this, "성별을 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else if(signUpActivityBirthInput.getText().toString().length() == 0 || signUpActivityBirthInput.getText().toString().length() < 8){
                    Toast.makeText(SignUpActivity.this, "생년월일을 정확하게 입력해주세요.", Toast.LENGTH_SHORT).show();
                    signUpActivityBirthInput.requestFocus();
                } else {

                    //성별 선택시 DB에 저장할 수 있게 분류
                    if(signUpActivityManBtn.isChecked()){
                        serviceUserGender = "M";
                    } else {
                        serviceUserGender = "W";
                    }
                    //회원가입한 정보 DB에 저장
                    serviceUserInfoSaveAsyncTask serviceUserInfoSaveAsyncTask = new serviceUserInfoSaveAsyncTask();
                    serviceUserInfoSaveAsyncTask.execute(signUpActivityNicknameInput.getText().toString(),
                                                        signUpActivityPwdInput.getText().toString(),
                                                        serviceUserGender,
                                                        signUpActivityBirthInput.getText().toString());

                }
            }
        });

    }

    //회원가입한 정보 DB에 저장하는 AsyncTask

    private class serviceUserInfoSaveAsyncTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);

            if(s!=0){
                Toast.makeText(SignUpActivity.this, "환영합니다!", Toast.LENGTH_SHORT).show();
                Intent serviceUserMoveMainIntent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(serviceUserMoveMainIntent);
                finish();
            }
        }

        @SuppressLint("LongLogTag")
        @Override
        protected Integer doInBackground(String... strings) {

            Connection pgConnection;
            Statement pgStatement;
            int pgResult;

            String pgJDBCurl = "jdbc:postgresql://210.89.190.131/streetlive";
            String pgUser = "postgres";
            String pgPassword = "rmstnek123";
            String sql;

            String userNickname = strings[0];
            String userPwd = strings[1];
            String userGender = strings[2];
            String userBirth = strings[3];



            try{
                pgConnection = DriverManager.getConnection(pgJDBCurl,pgUser,pgPassword);
                pgStatement = pgConnection.createStatement();
                sql = "insert into login.service_user (nickname,pwd,gender,birth) values('"+userNickname+"','"+userPwd+"','"+userGender+"',"+
                        userBirth+");";
                pgResult = pgStatement.executeUpdate(sql);

                Log.e("serviceUserInfoSaveAsyncTask","sql="+sql);
                if(pgResult!=0){
                    Log.e("serviceUserInfoSaveAsyncTask","저장성공 = "+pgResult);
                    pgStatement.close();
                    //SharedPreference에 로그인 정보 저장
                    SharedPreferences loginInfoUserSave = getSharedPreferences("userLoginInfo",MODE_PRIVATE);
                    SharedPreferences.Editor loginInfoUserSaveEditor = loginInfoUserSave.edit();
                    loginInfoUserSaveEditor.putString("nickname",userNickname);
                    loginInfoUserSaveEditor.putString("gender",userGender);
                    loginInfoUserSaveEditor.putString("birth",userBirth);
                    loginInfoUserSaveEditor.commit();
                    return pgResult;
                }
            }catch (Exception e){
                Log.e("serviceUserInfoSaveAsyncTask","저장실패 = "+e.toString());
                return null;
            }

            return null;
        }
    }

    //닉네임 중복확인 하는 AsyncTask
    private class nicknameConfirmAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(!s.equals("0")){
                signUpActivityNicknameConfirmText.setTextColor(Color.RED);
                signUpActivityNicknameConfirmText.setText("사용할 수 없는 닉네임 입니다.");
            } else {
                signUpActivityNicknameConfirmText.setTextColor(Color.CYAN);
                signUpActivityNicknameConfirmText.setText("사용할 수 있는 닉네임 입니다.");
            }
        }

        @SuppressLint("LongLogTag")
        @Override
        protected String doInBackground(String... strings) {

            Connection pgConnection;
            Statement pgStatement;
            ResultSet pgResultSet;

            String pgJDBCurl = "jdbc:postgresql://210.89.190.131/streetlive";
            String pgUser = "postgres";
            String pgPassword = "rmstnek123";
            String sql;
            String result;

            String userNickname = strings[0];

            try{
                pgConnection = DriverManager.getConnection(pgJDBCurl,pgUser,pgPassword);
                pgStatement = pgConnection.createStatement();
                sql = "select count(*) from login.service_user where nickname = '"+userNickname+"';";
                pgResultSet = pgStatement.executeQuery(sql);

                while(pgResultSet.next()){
                    result = pgResultSet.getString("count");
                    Log.e("nicknameConfirmAsyncTask","일치하는정보 = "+result+"개");
                    return result;
                }
                pgStatement.close();
                return null;

            }catch (Exception e){
                Log.e("nicknameConfirmException",e.toString());
                return null;
            }
        }
    }


    //Typekit 라이브러리 (폰트 적용)를 사용하기 위해 만든 메소드
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
