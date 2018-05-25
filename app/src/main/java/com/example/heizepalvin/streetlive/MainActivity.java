package com.example.heizepalvin.streetlive;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    Fragment liveFragmentActivity;

    @BindView(R.id.mainViewpager)
    ViewPager mainViewpager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        SharedPreferences remove = getSharedPreferences("userLoginInfo",MODE_PRIVATE);
//        SharedPreferences.Editor editor = remove.edit();
//        editor.clear();
//        editor.commit();

        liveFragmentActivity = new LiveFragmentActivity(R.layout.main_fragment_live);

    }
    //ViewPager Adapter
    private class mainViewpagerAdapter extends FragmentStatePagerAdapter{

        public mainViewpagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            return null;
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
}
