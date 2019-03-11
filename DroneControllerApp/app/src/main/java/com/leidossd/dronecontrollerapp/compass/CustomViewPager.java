package com.leidossd.dronecontrollerapp.compass;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {
    private boolean swipeEnabled;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.swipeEnabled = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.swipeEnabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.swipeEnabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    public void setPagingEnabled(boolean enabled) {
        this.swipeEnabled = enabled;
    }
}