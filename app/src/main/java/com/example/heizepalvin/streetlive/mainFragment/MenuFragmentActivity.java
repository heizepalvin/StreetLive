package com.example.heizepalvin.streetlive.mainFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import com.example.heizepalvin.streetlive.CashPaymentActivity;
import com.example.heizepalvin.streetlive.R;
import com.example.heizepalvin.streetlive.UnityPlayerActivity;

import butterknife.BindView;

public class MenuFragmentActivity extends android.support.v4.app.Fragment {



    public MenuFragmentActivity(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
//        LinearLayout menuFragmentLayout = (LinearLayout)inflater.inflate(R.layout.main_fragment_menu,container,false);
        CoordinatorLayout menuFragmentLayout = (CoordinatorLayout) inflater.inflate(R.layout.main_fragment_menu,container,false);
//        NestedScrollView menuFragmentLayoutAdd = (NestedScrollView) inflater.inflate(R.layout.main_fragment_menu_add,container,false);
        LinearLayout menuFragmentARBtn = menuFragmentLayout.findViewById(R.id.mainFragmentARBtn);
        LinearLayout menuFragmentCashBtn = menuFragmentLayout.findViewById(R.id.mainFragmentCashBtn);
        menuFragmentARBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ARActivityMoveIntent = new Intent(getContext(), UnityPlayerActivity.class);
                startActivity(ARActivityMoveIntent);
            }
        });

        menuFragmentCashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cashActivityMoveIntent = new Intent(getContext(), CashPaymentActivity.class);
                startActivity(cashActivityMoveIntent);
            }
        });

        return menuFragmentLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
