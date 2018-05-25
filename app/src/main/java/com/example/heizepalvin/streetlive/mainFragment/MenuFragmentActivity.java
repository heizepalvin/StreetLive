package com.example.heizepalvin.streetlive.mainFragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.heizepalvin.streetlive.R;

public class MenuFragmentActivity extends Fragment {

    public MenuFragmentActivity(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        LinearLayout menuFragmentLinear = (LinearLayout)inflater.inflate(R.layout.main_fragment_menu,container,false);
        return menuFragmentLinear;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
