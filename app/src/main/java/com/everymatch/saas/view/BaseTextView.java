package com.everymatch.saas.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.util.TypeFaceProvider;

/**
 * Created by Lior Iluz on 24/02/2014
 */
public class BaseTextView extends TextView {

    private Context mContext;

    private boolean isFirstLetterCaps;

    public BaseTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;

        if (!isInEditMode())
            initAttributes(attrs);
    }

    public BaseTextView(Context context) {
        super(context);
        this.mContext = context;

        if (!isInEditMode())
            initAttributes(null);
    }

    public BaseTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        if (!isInEditMode())
            initAttributes(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;

        if (!isInEditMode())
            initAttributes(attrs);
    }

    private void initAttributes(AttributeSet attributeSet) {

        TypedArray typedArray = mContext.obtainStyledAttributes(attributeSet, R.styleable.BaseTextView);

        if (typedArray != null) {
            TypeFaceProvider.initByType(typedArray.getInt(R.styleable.BaseTextView_font, -1), this);
            isFirstLetterCaps = typedArray.getBoolean(R.styleable.BaseTextView_first_letter_capitalize, false);
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
    }

    public void setResourceText(String key) {
        if (key != null) {
            if (isFirstLetterCaps) {
                setTextFirstLetterCaps(DataManager.getInstance().getResourceText(key));
            } else {
                setText(DataManager.getInstance().getResourceText(key));
            }
        }
    }

    public void setTextFirstLetterCaps(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }

        if (text.length() > 1) {
            setText(text.substring(0, 1).toUpperCase() + text.substring(1));
        } else {
            setText(text);
        }
    }
}
