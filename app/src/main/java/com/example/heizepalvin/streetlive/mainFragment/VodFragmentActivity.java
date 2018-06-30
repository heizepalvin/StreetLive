package com.example.heizepalvin.streetlive.mainFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.heizepalvin.streetlive.R;

public class VodFragmentActivity extends android.support.v4.app.Fragment {

    public VodFragmentActivity(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout vodFragmentLayout = (LinearLayout) inflater.inflate(R.layout.main_fragment_vod,container,false);
        return vodFragmentLayout;
//        return inflater.inflate(R.layout.main_fragment_vod,container,false);
    }
}
