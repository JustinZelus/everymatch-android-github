package com.everymatch.saas.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;

/**
 * Created by dors on 10/27/15.
 */
public class BaseRadionButton extends RadioButton {

    public BaseRadionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(attrs);
    }

    private void initAttributes(AttributeSet attributeSet) {

        if (attributeSet == null){
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.ResourceColor);

        if (typedArray != null){
            int textColor = typedArray.getInt(R.styleable.ResourceColor_text_color, -1);

            if (textColor != -1){
                setTextColor(Color.parseColor(DataStore.getInstance().getColor(textColor)));
            }

            typedArray.recycle();
        }
    }
}
