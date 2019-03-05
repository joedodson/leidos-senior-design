package com.leidossd.dronecontrollerapp.compass;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.leidossd.djiwrapper.FlightControllerWrapper;
import com.leidossd.dronecontrollerapp.MainActivity;
import com.leidossd.dronecontrollerapp.R;

import dji.common.flightcontroller.CompassCalibrationState;
import dji.sdk.flightcontroller.Compass;
// import com.leidossd.dronecontrollerapp.droneconnection.ConnectWalkthroughActivity;

public class CompassCalibrationActivity extends AppCompatActivity implements CompassStartPageFragment.StartPage, CompassPage1Fragment.Page1, CompassPage2Fragment.Page2 {
    FragmentPagerAdapter fragmentPagerAdapter;
    CustomViewPager viewPager;
    TextView currentPageTextView;
    TextView statusTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass_guide);

        currentPageTextView = findViewById(R.id.tv_compass_step);
        statusTextView = findViewById(R.id.tv_compass_status);
        ((GradientDrawable) statusTextView.getBackground()).setColor(getColor(R.color.background_unknown));
        viewPager = findViewById(R.id.view_pager_compass);
        fragmentPagerAdapter = new CompassPagerAdapter(getSupportFragmentManager());
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
            finish();
        } else {
            //FlightControllerWrapper.getInstance().compassStopCalibration(null);
            viewPager.setCurrentItem(0); // go to first slide and reset calibration
        }
    }


    public void updateStatus() {
        boolean compassHasError = FlightControllerWrapper.getInstance().compassHasError();
        CompassCalibrationState calibrationState = FlightControllerWrapper.getInstance().getCompassCalibrationState(null);
        if(calibrationState == CompassCalibrationState.NOT_CALIBRATING && !compassHasError){
            statusTextView.setText(" Calibrated ");
            ((GradientDrawable) statusTextView.getBackground()).setColor(getColor(R.color.background_success));
        }
        else if(calibrationState == CompassCalibrationState.NOT_CALIBRATING){
            statusTextView.setText(" Not Calibrated ");
            ((GradientDrawable) statusTextView.getBackground()).setColor(getColor(R.color.background_failure));
        }
        else if(calibrationState == CompassCalibrationState.HORIZONTAL){
            statusTextView.setText(" Horizontal ");
            ((GradientDrawable) statusTextView.getBackground()).setColor(getColor(R.color.background_inprogress));
        }
        else if(calibrationState == CompassCalibrationState.VERTICAL){
            statusTextView.setText(" Vertical ");
            ((GradientDrawable) statusTextView.getBackground()).setColor(getColor(R.color.background_inprogress));
        }
        else {
            statusTextView.setText(" Unknown ");
            ((GradientDrawable) statusTextView.getBackground()).setColor(getColor(R.color.background_unknown));
        }

    }


    public static class CompassPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        public CompassPagerAdapter(FragmentManager fragmentManager) {
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
                    return CompassStartPageFragment.newInstance("");
                case 1:
                    return CompassPage1Fragment.newInstance("");
                case 2:
                    return CompassPage2Fragment.newInstance("");
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return String.format("%s of %s", position+1, NUM_ITEMS);
        }
    }

    @Override
    public void startCalibration(){
        //FlightControllerWrapper.getInstance().compassStartCalibration(null);
        viewPager.setCurrentItem(1,true);
    }

    @Override
    public void exitCalibration(){
        finish();
    }

    @Override
    public void cancelPage1(){
//        FlightControllerWrapper.getInstance().compassStopCalibration(null);
        viewPager.setCurrentItem(0);
    }

    @Override
    public void cancelPage2(){
//        FlightControllerWrapper.getInstance().compassStopCalibration(null);
        viewPager.setCurrentItem(0);
    }
}