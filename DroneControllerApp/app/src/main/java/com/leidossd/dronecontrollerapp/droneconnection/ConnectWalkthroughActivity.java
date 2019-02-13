package com.leidossd.dronecontrollerapp.droneconnection;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.leidossd.dronecontrollerapp.MainActivity;
import com.leidossd.dronecontrollerapp.R;
import com.leidossd.dronecontrollerapp.droneconnection.wired.WiredConnectActivity;
import com.leidossd.dronecontrollerapp.droneconnection.wireless.WirelessConnectActivity;

public class ConnectWalkthroughActivity
        extends AppCompatActivity
        implements ChooseToConnectFragment.fragmentInteractionListener,
        ChooseConnectTypeFragment.fragmentInteractionListener {

    FragmentPagerAdapter fragmentPagerAdapter;
    ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

    public void onConnectDecision(ChooseToConnectFragment.ConnectDecision connectDecision) {
        if (connectDecision == ChooseToConnectFragment.ConnectDecision.CONNECT) {
            viewPager.setCurrentItem(1);
        } else if (connectDecision == ChooseToConnectFragment.ConnectDecision.SKIP) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    public void onConnectTypeDecision(ChooseConnectTypeFragment.ConnectType connectType) {
        if(connectType == ChooseConnectTypeFragment.ConnectType.WIRED) {
            startActivity(new Intent(this, WiredConnectActivity.class));
            finish();
        } else if(connectType == ChooseConnectTypeFragment.ConnectType.WIRELESS) {
            startActivity(new Intent(this, WirelessConnectActivity.class));
            finish();
        }
    }

}

