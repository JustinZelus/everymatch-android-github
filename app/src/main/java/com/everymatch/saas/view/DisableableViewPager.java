package com.everymatch.saas.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Dacid on 17/06/2015.
 */
public class DisableableViewPager extends ViewPager {

    private boolean mPagingEnabled = true;

    public DisableableViewPager(Context context) {
        super(context);
    }

    public DisableableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isPagingEnabled() {
        return mPagingEnabled;
    }

    public void setPagingEnabled(boolean enabled) {
        mPagingEnabled = enabled;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!mPagingEnabled) {
            return false; // do not intercept
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mPagingEnabled) {
            return false; // do not consume
        }
        return super.onTouchEvent(event);
    }


}

