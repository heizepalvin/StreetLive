package com.example.heizepalvin.streetlive;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.heizepalvin.streetlive.mainFragment.LiveFragment.LiveFragmentActivity;
import com.example.heizepalvin.streetlive.mainFragment.MenuFragmentActivity;
import com.example.heizepalvin.streetlive.mainFragment.VodFragment.VodFragmentActivity;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mainViewpager)
    ViewPager mainViewpager;
    @BindView(R.id.mainBottomNavigation)
    BottomNavigationView mainBottomNavigation;

    private MenuItem prevBottomNavigation;

    //okhttp3
    private final OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        SharedPreferences remove = getSharedPreferences("userLoginInfo",MODE_PRIVATE);
//        SharedPreferences.Editor editor = remove.edit();
//        editor.clear();
//        editor.commit();

        //redis 테스트

        redisPostDataSend redisPostDataSend = new redisPostDataSend();
        redisPostDataSend.execute();

        mainViewpager.setAdapter(new mainViewPagerAdapter(getSupportFragmentManager()));
        mainViewpager.setCurrentItem(0);
        mainBottomNavigation.setSelectedItemId(R.id.menu_live);
        // 하단 메뉴 아이템 변경될 때 이벤트
        mainBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_live:
                        mainViewpager.setCurrentItem(0);
                        return true;
                    case R.id.menu_vod:
                        mainViewpager.setCurrentItem(1);
                        return true;
                    case R.id.menu_menu:
                        mainViewpager.setCurrentItem(2);
                        new MenuFragmentActivity();
                        return true;
                }
                return false;
            }
        });
        mainViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                // swap 될 때 해당 하단 메뉴 선택
                if(prevBottomNavigation != null){
                    prevBottomNavigation.setChecked(false);
                }
                prevBottomNavigation = mainBottomNavigation.getMenu().getItem(i);
                prevBottomNavigation.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }
    private class mainViewPagerAdapter extends FragmentStatePagerAdapter {

        public mainViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i){
                case 0:
                    return new LiveFragmentActivity();
                case 1:
                    return new VodFragmentActivity();
                case 2:
                    return new MenuFragmentActivity();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
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

    // Typekit 라이브러리 (폰트 적용)를 사용하기 위해 만든 메소드
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    //okhttp를 이용해서 서버로 POST 데이터를 보냄

    public class redisPostDataSend extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {

            //POST 요청 시
            SharedPreferences userLoginInfo = getSharedPreferences("userLoginInfo",MODE_PRIVATE);
            String userNickname = userLoginInfo.getString("nickname","null");
            Log.e("닉네임가져오기",userNickname);
            RequestBody formbody = new FormBody.Builder()
                    .add("ID",userNickname)
                    .add("View","MainActivity")
                    .add("Action","ActivityIn")
                    .build();

            Request request = new Request.Builder()
                    .url("http://106.10.43.183:80")
                    .post(formbody)
                    .build();

            okHttpClient.newCall(request).enqueue(redisResponseCallback);

            return null;

        }
    }

    private Callback redisResponseCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e("MainActivityRedis","error Message : "+ e.getMessage());
        }


        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String responseData = response.body().string();
            Log.e("MainActivityRedis","responseData : " + responseData);
        }
    };
}
