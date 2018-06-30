package com.example.heizepalvin.streetlive.mainFragment.LiveFragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.heizepalvin.streetlive.R;

import butterknife.ButterKnife;

public class LiveRoomActivity extends AppCompatActivity{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_room_activity);
        ButterKnife.bind(this);

    }




}

