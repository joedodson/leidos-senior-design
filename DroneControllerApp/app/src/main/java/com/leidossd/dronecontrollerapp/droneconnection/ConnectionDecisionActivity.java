package com.leidossd.dronecontrollerapp.droneconnection;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.leidossd.dronecontrollerapp.MainActivity;
import com.leidossd.dronecontrollerapp.R;

import java.util.List;

public class ConnectionDecisionActivity extends AppCompatActivity {
    FragmentPagerAdapter fragmentPagerAdapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_decision);

        viewPager = findViewById(R.id.view_pager_connection_decision);
        fragmentPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setCurrentItem(FragmentPageEnum.CHOOSE_TO_CONNECT.ordinal());
    }

    public void connectToDrone(View view) {
        viewPager.setCurrentItem(FragmentPageEnum.CHOOSE_CONNECTION_TYPE.ordinal());
    }

    public void continueWithoutConnecting(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == FragmentPageEnum.CHOOSE_TO_CONNECT.ordinal()) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(FragmentPageEnum.CHOOSE_TO_CONNECT.ordinal());
        }
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ChooseToConnectFragment.newInstance("");
                case 1:
                    return ChooseConnectTypeFragment.newInstance("");
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }

    private enum FragmentPageEnum {
        CHOOSE_TO_CONNECT, CHOOSE_CONNECTION_TYPE
    }
}
