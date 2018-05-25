package com.example.heizepalvin.streetlive;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


@SuppressLint("ValidFragment")
public class LiveFragmentActivity extends Fragment {

    private int layout;
    @SuppressLint("ValidFragment")
    public LiveFragmentActivity(int layout){
        this.layout = layout;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View liveFragmentView = inflater.inflate(this.layout,container,false);
        return liveFragmentView;
    }
}
