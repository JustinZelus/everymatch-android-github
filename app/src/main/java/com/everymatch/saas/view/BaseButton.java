package com.everymatch.saas.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.util.ShapeDrawableUtils;
import com.everymatch.saas.util.TypeFaceProvider;


/**
 * Created by Lior Iluz on 24/02/2014
 */
public class BaseButton extends Button {

    private Context mContext;

    public BaseButton(Context context) {
        super(context);
        this.mContext = context;

        if (isInEditMode()) {
        } else {
            initAttributes(null);
        }
    }

    public BaseButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;

        if (isInEditMode()) {
        } else {
            initAttributes(attrs);
        }
    }

    public BaseButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        if (isInEditMode()) {
        } else {
            initAttributes(attrs);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;

        if (isInEditMode()) {
        } else {
            initAttributes(attrs);
        }
    }

    private void initAttributes(AttributeSet attributeSet) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attributeSet, R.styleable.BaseTextView);

        if (typedArray != null) {
            TypeFaceProvider.initByType(typedArray.getInt(R.styleable.BaseTextView_font, -1), this);
            setResourceText(typedArray.getString(R.styleable.BaseTextView_text_key));
            typedArray.recycle();
        }

        typedArray = mContext.obtainStyledAttributes(attributeSet, R.styleable.ResourceColor);

        if (typedArray != null) {
            int bgColor = typedArray.getInt(R.styleable.ResourceColor_bg_color, -1);

            if (bgColor != -1) {
                setBackgroundColor(Color.parseColor(DataStore.getInstance().getColor(bgColor)));
            }

            int textColor = typedArray.getInt(R.styleable.ResourceColor_text_color, -1);

            if (textColor != -1) {
                setTextColor(Color.parseColor(DataStore.getInstance().getColor(textColor)));
            }

            typedArray.recycle();
        }

        typedArray = mContext.obtainStyledAttributes(attributeSet, R.styleable.BaseButton);
        if (typedArray != null) {
             /*we can set the button background in xml */
            int bgValue = typedArray.getInt(R.styleable.BaseButton_bg, -1);

            /*rounded*/
            if (bgValue == 0)
                this.setBackgroundDrawable(ShapeDrawableUtils.getRoundendButton());
            /*stroked*/
            if (bgValue == 1)
                this.setBackgroundDrawable(ShapeDrawableUtils.getButtonStroked());

            typedArray.recycle();
        }
    }

    public void setResourceText(String key) {
        if (key != null) {
            setText(DataManager.getInstance().getResourceText(key));
        }
    }
}
