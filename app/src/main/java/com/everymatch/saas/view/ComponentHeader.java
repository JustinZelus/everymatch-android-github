package com.everymatch.saas.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.everymatch.saas.R;

/**
 * Created by dors on 7/20/15.
 */
public class ComponentHeader extends LinearLayout {

    private TextView mTextLeft;
    private TextView mTextRight;
    private IconImageView mIcon;

    public ComponentHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_component_header, this);
        mTextLeft = (TextView) findViewById(R.id.view_component_header_text_left);
        mTextRight = (TextView) findViewById(R.id.view_component_header_text_right);
        mIcon = (IconImageView) findViewById(R.id.icon);
        setTexts(context, attrs);
    }

    private void setTexts(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ComponentHeader);

            if (ta != null) {
                String leftText = ta.getString(R.styleable.ComponentHeader_leftText);
                String rightText = ta.getString(R.styleable.ComponentHeader_rightText);
                ta.recycle();

                if (!TextUtils.isEmpty(leftText)) {
                    mTextLeft.setText(leftText);
                    mTextLeft.setVisibility(View.VISIBLE);
                }

                if (!TextUtils.isEmpty(rightText)) {
                    mTextRight.setText(rightText);
                    mTextRight.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void setTexts(String leftText, String rightText) {
        mTextLeft.setText(leftText);
        mTextRight.setText(rightText);
    }
}
