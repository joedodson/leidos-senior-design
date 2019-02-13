package com.leidossd.dronecontrollerapp.droneconnection;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.leidossd.dronecontrollerapp.R;

public class ConnectWalkthroughActivity extends AppCompatActivity {
    FragmentPagerAdapter fragmentPagerAdapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_walkthrough);

        viewPager = findViewById(R.id.view_pager_connect_walkthrough);
        fragmentPagerAdapter = new UserDecisionPagerAdapter(getSupportFragmentManager());
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

    public static class UserDecisionPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;

        public UserDecisionPagerAdapter(FragmentManager fragmentManager) {
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

}

