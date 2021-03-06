package com.everymatch.saas.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;

/**
 * Created by dors on 10/26/15.
 */
public class BaseScrollView extends ScrollView{

    public BaseScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(attrs);
    }

    private void initAttributes(AttributeSet attributeSet) {

        if (attributeSet == null){
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.ResourceColor);

        if (typedArray != null){
            int bgColor = typedArray.getInt(R.styleable.ResourceColor_bg_color, -1);

            if (bgColor != -1){
                setBackgroundColor(Color.parseColor(DataStore.getInstance().getColor(bgColor)));
            }

            typedArray.recycle();
        }
    }
}
