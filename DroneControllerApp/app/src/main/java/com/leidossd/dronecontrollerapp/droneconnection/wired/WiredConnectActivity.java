package com.leidossd.dronecontrollerapp.droneconnection.wired;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.leidossd.dronecontrollerapp.R;
import com.leidossd.dronecontrollerapp.droneconnection.ChooseConnectTypeFragment;
import com.leidossd.dronecontrollerapp.droneconnection.ChooseToConnectFragment;
import com.leidossd.dronecontrollerapp.droneconnection.wireless.WirelessConnectActivity;

public class WiredConnectActivity extends AppCompatActivity {
    FragmentPagerAdapter fragmentPagerAdapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wireless_connect);

        viewPager = findViewById(R.id.view_pager_wired_connect);
        fragmentPagerAdapter = new WiredConnectPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentPagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed(); // go back to whatever activity started this
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1); // go to prev slide
        }
    }

    public static class WiredConnectPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 1;

        public WiredConnectPagerAdapter(FragmentManager fragmentManager) {
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
                    return WiredPage1Fragment.newInstance("");
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }
}
