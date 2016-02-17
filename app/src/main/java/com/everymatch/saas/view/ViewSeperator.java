package com.everymatch.saas.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.everymatch.saas.R;

/**
 * Created by PopApp_laptop on 20/12/2015.
 */
public class ViewSeperator extends BaseLinearLayout {
    public ViewSeperator(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_seperator, this);
    }
}
