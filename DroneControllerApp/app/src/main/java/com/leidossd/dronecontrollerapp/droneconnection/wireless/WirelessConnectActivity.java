package com.leidossd.dronecontrollerapp.droneconnection.wireless;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.leidossd.dronecontrollerapp.MainApplication;
import com.leidossd.dronecontrollerapp.R;
import com.leidossd.dronecontrollerapp.droneconnection.ConnectWalkthroughActivity;

public class WirelessConnectActivity extends AppCompatActivity {
    FragmentPagerAdapter fragmentPagerAdapter;
    ViewPager viewPager;
    TextView currentPageTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wireless_connect);

        currentPageTextView = findViewById(R.id.tv_wireless_connect_step);
        viewPager = findViewById(R.id.view_pager_wireless_connect);
        fragmentPagerAdapter = new WirelessConnectPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(60, 30, 60, 30);
        viewPager.setPageMargin(30);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                currentPageTextView.setText(fragmentPagerAdapter.getPageTitle(viewPager.getCurrentItem()));
            }

            @Override
            public void onPageSelected(int i) {
                currentPageTextView.setText(fragmentPagerAdapter.getPageTitle(viewPager.getCurrentItem()));
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                currentPageTextView.setText(fragmentPagerAdapter.getPageTitle(viewPager.getCurrentItem()));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            startActivity(new Intent(getApplicationContext(), ConnectWalkthroughActivity.class));
            finish();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1); // go to prev slide
        }
    }

    public void testConnection(View view) {
        MainApplication.showToast("Testing Connection");
    }

    public static class WirelessConnectPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;

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
                    return WirelessPage1Fragment.newInstance();
                case 1:
                    return WirelessPage2Fragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return String.format("Page %s of %s", position+1, NUM_ITEMS);
        }
    }
}
