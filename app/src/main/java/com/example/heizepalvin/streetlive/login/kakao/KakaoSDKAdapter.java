package com.example.heizepalvin.streetlive.login.kakao;

import android.content.Context;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;

public class KakaoSDKAdapter extends KakaoAdapter {

    //로그인시 사용 될 Session의 옵션 설정을 위한 인터페이스
    @Override
    public ISessionConfig getSessionConfig() {
        return new ISessionConfig() {
            @Override
            public AuthType[] getAuthTypes() {
                //Auth Type
                //KAKAO_TALK : 카카오톡 로그인 타입
                //KAKAO_STORY : 카카오스토리 로그인 타입
                //KAKAO_ACCOUNT : 웹뷰 다이얼로그를 통한 계정 연결 타입
                //KAKAO_TALK_EXCLUDE_NATIVE_LOGIN : 카카오톡 로그인 타입과 함께 계정 생성을 위한 버튼을 함께 제공
                //KAKAO_LOGIN_ALL : 모든 로그인 방식을 제공
                return new AuthType[]{AuthType.KAKAO_TALK};
            }

            //로그인 웹뷰에서 pause와 resume 시에 타이머를 설정하여 cpu의 소모를 절약할지의 여부를 지정
            // true로 지정할 경우, 로그인 웹뷰의 onPause()와 onResume() 에 타이머를 설정해야한다.
            @Override
            public boolean isUsingWebviewTimer() {
                return false;
            }

            //로그인 시 토큰을 저장할 때의 암호화 여부를 지정한다.
            @Override
            public boolean isSecureMode() {
                return false;
            }
            // 일반 사용자가 아닌 kakao와 제휴된 앱에서 사용되는 값
            // 값을 지정하지 않을 경우, ApprovalType.INDIVIDUAL 값으로 사용된다.
            @Override
            public ApprovalType getApprovalType() {
                return ApprovalType.INDIVIDUAL;
            }

            //로그인 웹뷰에서 email 입력 폼의 데이터를 저장할 지 여부를 지정한다.
            @Override
            public boolean isSaveFormData() {
                return false;
            }
        };
    }

    @Override
    public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                @Override
                public Context getApplicationContext() {
                    return GlobalApplication.getGlobalApplicationContext();
                }
            };
    }
}
