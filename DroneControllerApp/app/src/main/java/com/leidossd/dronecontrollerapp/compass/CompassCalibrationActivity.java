package com.leidossd.dronecontrollerapp.compass;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.leidossd.djiwrapper.FlightControllerWrapper;
import com.leidossd.dronecontrollerapp.R;

import dji.common.flightcontroller.CompassCalibrationState;

public class CompassCalibrationActivity extends AppCompatActivity implements CompassStartPageFragment.StartPage, CompassPage1Fragment.Page1, CompassPage2Fragment.Page2 {
    FragmentPagerAdapter fragmentPagerAdapter;
    CustomViewPager viewPager;
    TextView currentPageTextView;
    TextView statusTextView;
    boolean calibStarted = false;
    AlertDialog calibFailedDialog;
    AlertDialog calibSuccessDialog;
    CompassCalibrationState currentCalibState;

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

        // tell the user that the calibration failed
        calibFailedDialog = new AlertDialog.Builder(this)
                .setTitle("Calibration Failed!")
                .setMessage("Try again?")
                .setPositiveButton("Yes", null)
                 // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("No", (dialog, which) -> finish())
                .create();

        // tell the user that the calibration was successful
        calibSuccessDialog = new AlertDialog.Builder(this)
                .setTitle("Calibration Successful!")
                .setMessage("Return to the main menu?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .create();

        FlightControllerWrapper.getInstance().compassSetCalibrationStateCallback(compassCalibrationState -> {
            // only update if there is a change
            if(compassCalibrationState != currentCalibState) {
                updateStatus(compassCalibrationState);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            finish();
        } else {
            FlightControllerWrapper.getInstance().compassStopCalibration(null);
            viewPager.setCurrentItem(0); // go to first slide and reset calibration
        }
    }

    public void updateStatus(CompassCalibrationState calibrationState) {
        currentCalibState = calibrationState;
        int oldPage = viewPager.getCurrentItem();
        boolean compassHasError = FlightControllerWrapper.getInstance().compassHasError();

        switch(calibrationState){
            case NOT_CALIBRATING:
                runOnUiThread(() -> {
                    if (oldPage != 0)
                        viewPager.setCurrentItem(0);
                    if (compassHasError) {
                        statusTextView.setText(" Not Calibrated ");
                        ((GradientDrawable) statusTextView.getBackground()).setColor(getColor(R.color.background_failure));
                        if (calibStarted) {
                            calibFailedDialog.show();
                            calibStarted = false;
                        }
                    } else {
                        statusTextView.setText(" Calibrated ");
                        ((GradientDrawable) statusTextView.getBackground()).setColor(getColor(R.color.background_success));
                        if (calibStarted) {
                            calibSuccessDialog.show();
                            calibStarted = false;
                        }
                    }
                });
                break;

            case HORIZONTAL:
                runOnUiThread(() -> {
                    statusTextView.setText(" Horizontal ");
                    ((GradientDrawable) statusTextView.getBackground()).setColor(getColor(R.color.background_inprogress));
                    if(oldPage != 1)
                        viewPager.setCurrentItem(1);
                });
                break;

            case VERTICAL:
                runOnUiThread(() -> {
                    statusTextView.setText(" Vertical ");
                    ((GradientDrawable) statusTextView.getBackground()).setColor(getColor(R.color.background_inprogress));
                    if(oldPage != 2)
                        viewPager.setCurrentItem(2);
                });
                break;

            case FAILED:
                //pop up dialog to tell the user that it failed
                runOnUiThread(() -> {
                    if(oldPage != 0)
                        viewPager.setCurrentItem(0);
                    calibFailedDialog.show();
                });
                break;

            case SUCCESSFUL:
                //pop up dialog to tell the user that it succeeded
                runOnUiThread(() -> {
                    if(oldPage != 0)
                        viewPager.setCurrentItem(0);
                    calibSuccessDialog.show();
                });
                break;

            default:
                runOnUiThread(() -> {
                    statusTextView.setText(" Unknown ");
                    ((GradientDrawable) statusTextView.getBackground()).setColor(getColor(R.color.background_unknown));
                });
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
        FlightControllerWrapper.getInstance().compassStartCalibration(null);
        calibStarted = true;
    }

    @Override
    public void exitCalibration(){
        finish();
    }

    @Override
    public void cancelPage1(){
        calibStarted = false;
        FlightControllerWrapper.getInstance().compassStopCalibration(null);
    }

    @Override
    public void cancelPage2(){
        calibStarted = false;
        FlightControllerWrapper.getInstance().compassStopCalibration(null);
    }
}