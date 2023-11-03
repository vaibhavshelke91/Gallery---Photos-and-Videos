package com.vaibhav.gallery.collectiondb.store.tool;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class BrightnessLayout extends ConstraintLayout {

    private static final String TAG=BrightnessLayout.class.getSimpleName();
    protected GestureDetector detector;
    private Context context;
    private float brightness;
    private boolean isBrightnessEnabled=false;

    public BrightnessLayout(@NonNull Context context) {
        this(context,null);
    }

    public BrightnessLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BrightnessLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public BrightnessLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context=context;
        init();
    }
    private void init(){
            GestureListener listener=new GestureListener();
            detector=new GestureDetector(context,listener);
            setOnTouchListener(listener);
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public boolean isBrightnessEnabled() {
        return isBrightnessEnabled;
    }

    public void setBrightnessEnabled(boolean brightnessEnabled) {
        isBrightnessEnabled = brightnessEnabled;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
        @Override
        public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
            boolean result = false;
            if (Math.abs(distanceX) < Math.abs(distanceY)){

                if (distanceY>0){
                    Log.d(TAG,"Up Swipe : "+distanceY);
                }else {
                    Log.d(TAG,"Down Swipe : "+distanceY);
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            BrightnessLayout.this.detector.onTouchEvent(event);
            return false;
        }
    }

    public interface OnSwipeListeners{
        boolean onUpSwipe(MotionEvent start,MotionEvent end);
        boolean onDownSwipe(MotionEvent start,MotionEvent end);
    }
}
