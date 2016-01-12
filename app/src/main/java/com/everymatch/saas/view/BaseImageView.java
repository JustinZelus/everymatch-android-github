package com.everymatch.saas.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;


/**
 * Created by Lior Iluz on 24/02/2014
 */
public class BaseImageView extends ImageView {

    public BaseImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(attrs);
    }

    private void initAttributes(AttributeSet attributeSet) {

        if (attributeSet == null) {
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.ResourceColor);

        if (typedArray != null) {
            int bgColor = typedArray.getInt(R.styleable.ResourceColor_bg_color, -1);

            if (bgColor != -1) {
                setBackgroundColor(Color.parseColor(DataStore.getInstance().getColor(bgColor)));
            }

            typedArray.recycle();
        }
    }

    /**
     * Set drawable base on android version
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setDrawable(Drawable drawable) {

        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(drawable);
        } else {
            setBackground(drawable);
        }
    }
}
