package com.leidossd.dronecontrollerapp.droneconnection.wireless;

import android.Manifest;
import android.app.admin.DeviceAdminInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.leidossd.dronecontrollerapp.MainApplication;
import com.leidossd.dronecontrollerapp.R;
import com.leidossd.dronecontrollerapp.SDKRegistrationErrorActivity;
import com.leidossd.dronecontrollerapp.SplashActivity;
import com.leidossd.dronecontrollerapp.droneconnection.ConnectWalkthroughActivity;
import com.leidossd.dronecontrollerapp.droneconnection.barcode.BarcodeCaptureActivity;
import com.leidossd.dronecontrollerapp.missions.Mission;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.uid;

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

    public void openBarcodeScanner(View view) {
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int permission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE);
        int permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE);
        if (permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED) {
            startActivityForResult(new Intent(this, BarcodeCaptureActivity.class), 1234);
        } else {
            requestWifiPermissions();
            MainApplication.showToast("Need wifi permission to proceed");
        }
    }

    private void requestWifiPermissions(){
        String permissionsNeeded[] = new String[]{Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE};
        ActivityCompat.requestPermissions(this, permissionsNeeded , 12345);
    }

    /**
     * Result of runtime permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        openBarcodeScanner(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String barcodeData = data.getStringExtra("barcode data");

        // TODO: Actually use SSID and password to connect to drone's network
        if (resultCode == AppCompatActivity.RESULT_OK) {

            Matcher matcher = Pattern.compile("(?<=SSID:|Password:)\\S+").matcher(barcodeData);

            if(matcher.find()){
                String ssid = matcher.group();
                String password = "";
                if(matcher.find()) {
                    password = matcher.group();
                }
                final String pass = password;

                runOnUiThread(() -> MainApplication.showToast(String.format("SSID: %s Pass: %s", ssid, pass)));
            }
        }
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
