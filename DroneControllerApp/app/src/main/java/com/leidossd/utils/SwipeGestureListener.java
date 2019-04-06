package com.leidossd.utils;

import android.view.GestureDetector;
import android.view.MotionEvent;

import static com.leidossd.utils.SwipeGestureListener.Direction.DOWN;
import static com.leidossd.utils.SwipeGestureListener.Direction.LEFT;
import static com.leidossd.utils.SwipeGestureListener.Direction.RIGHT;
import static com.leidossd.utils.SwipeGestureListener.Direction.UP;

public class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final String DEBUG_TAG = "Gestures";

    private SwipeListener swipeListener;

    public SwipeGestureListener(SwipeListener swipeListener) {
            this.swipeListener = swipeListener;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        switch (getSwipeDirection(event1, event2)) {
            case LEFT: swipeListener.onLeftSwipe();
            break;
            case RIGHT: swipeListener.onRightSwipe();
            break;
            case UP: swipeListener.onUpSwipe();
            break;
            case DOWN: swipeListener.onDownSwipe();
            break;
            default: return false;
        }

        return true;
    }

    public interface SwipeListener {
        void onLeftSwipe();
        void onRightSwipe();
        void onUpSwipe();
        void onDownSwipe();
    }


    private Direction getSwipeDirection(MotionEvent event1, MotionEvent event2) {
        float angle = (float) Math.toDegrees(Math.atan2(event2.getY() - event1.getY(), event2.getX() - event1.getX()));

        if(angle < -45 && angle >= -135) return UP;
        else if(angle < -135 || angle >= 135) return LEFT;
        else if(angle < 135 && angle >= 45) return DOWN;
        else return RIGHT;
    }

    public enum Direction {LEFT, RIGHT, UP, DOWN}
}
