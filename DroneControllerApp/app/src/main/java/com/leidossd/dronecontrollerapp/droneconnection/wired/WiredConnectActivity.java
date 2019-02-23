package com.leidossd.dronecontrollerapp.droneconnection.wired;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.leidossd.dronecontrollerapp.R;
import com.leidossd.dronecontrollerapp.droneconnection.ConnectWalkthroughActivity;

public class WiredConnectActivity extends AppCompatActivity {
    FragmentPagerAdapter fragmentPagerAdapter;
    ViewPager viewPager;
    TextView currentPageTextView;
    Button testConnectButton;

    LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wired_connect);

        currentPageTextView = findViewById(R.id.tv_wired_connect_step);
        viewPager = findViewById(R.id.view_pager_wired_connect);
        fragmentPagerAdapter = new WiredConnectPagerAdapter(getSupportFragmentManager());
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

//        testConnectButton = findViewById(R.id.btn_frag_wired_test_connect);

//        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
//
//        BroadcastReceiver connectionChangeReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent connectionChangeIntent) {
//                String droneStatus = connectionChangeIntent.getStringExtra(CONNECTION_CHANGE.getResultKey());
//                testConnectButton.setText("Connect Success - Continue");
//                testConnectButton.setEnabled(true);
//                testConnectButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                        finish();
//                    }
//                });
//
//                MainApplication.showToast("Got broadcast");
//            }
//        };
//        LocalBroadcastManager.getInstance(this).registerReceiver(connectionChangeReceiver, new IntentFilter(CONNECTION_CHANGE.getActionString()));
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

//    public void testConnection(View view) {
//        DJISDKManager.getInstance().startConnectionToProduct();
//        testConnectButton.setText("Connecting...");
//        testConnectButton.setEnabled(false);
//
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                broadcastConnectionChange(DroneConnectionStatus.DRONE_CONNECTED);
//            }
//        }, 5000);
//
//
//        MainApplication.showToast("Testing Connection");
//    }
//
//    private void broadcastConnectionChange(DroneConnectionStatus droneStatus) {
//        Intent connectionChangeIntent = new Intent(CONNECTION_CHANGE.getActionString());
//        connectionChangeIntent.putExtra(CONNECTION_CHANGE.getResultKey(), droneStatus.toString());
//
//        localBroadcastManager.sendBroadcast(connectionChangeIntent);
//    }

    public static class WiredConnectPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;

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
                case 1:
                    return WiredPage2Fragment.newInstance("");
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
