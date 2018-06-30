package com.example.heizepalvin.streetlive.mainFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.heizepalvin.streetlive.R;

public class MenuFragmentActivity extends android.support.v4.app.Fragment {

    public MenuFragmentActivity(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        LinearLayout menuFragmentLayout = (LinearLayout)inflater.inflate(R.layout.main_fragment_menu,container,false);
        return menuFragmentLayout;
//        return inflater.inflate(R.layout.main_fragment_menu,container,false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
