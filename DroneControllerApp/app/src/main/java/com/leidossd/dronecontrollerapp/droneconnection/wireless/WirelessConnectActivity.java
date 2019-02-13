package com.leidossd.dronecontrollerapp.droneconnection.wireless;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.leidossd.dronecontrollerapp.R;

public class WirelessConnectActivity extends AppCompatActivity {
    FragmentPagerAdapter fragmentPagerAdapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wireless_connect);

        viewPager = findViewById(R.id.view_pager_wireless_connect);
        fragmentPagerAdapter = new WirelessConnectPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentPagerAdapter);

        findViewById(R.id.btn_wireless_connect_prev).setEnabled(false);
        if (WirelessConnectPagerAdapter.NUM_ITEMS <= 1) {
            findViewById(R.id.btn_wireless_connect_next).setEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed(); // go back to whatever activity started this
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1); // go to prev slide
        }
    }

    public void onNext(View view) {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem < WirelessConnectPagerAdapter.NUM_ITEMS-1) {
            viewPager.setCurrentItem(++currentItem);

            if (currentItem >= WirelessConnectPagerAdapter.NUM_ITEMS-1) {
                findViewById(R.id.btn_wireless_connect_next).setEnabled(false);
            }

            findViewById(R.id.btn_wireless_connect_prev).setEnabled(true);
        }
    }

    public void onPrev(View view) {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem > 0) {
            viewPager.setCurrentItem(--currentItem);

            if (currentItem == 0) {
                findViewById(R.id.btn_wireless_connect_prev).setEnabled(false);
            }

            findViewById(R.id.btn_wireless_connect_next).setEnabled(true);
        }
    }

    public static class WirelessConnectPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 1;

        public WirelessConnectPagerAdapter(FragmentManager fragmentManager) {
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
                    return WirelessPage1Fragment.newInstance("");
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
