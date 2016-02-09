package com.everymatch.saas.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.util.Utils;

/**
 * Created by PopApp_laptop on 11/11/2015.
 */
public class BaseSeekBar extends SeekBar {

    private Context mContext;
    private Drawable mDrawable;

    public BaseSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.ResourceColor);
        int textColor = typedArray.getInt(R.styleable.ResourceColor_text_color, -1);

        if (textColor != -1) {
            int c = Color.parseColor(DataStore.getInstance().getColor(textColor));
            ShapeDrawable thumb = new ShapeDrawable(new OvalShape());
            thumb.setIntrinsicHeight(Utils.dpToPx(30));
            thumb.setIntrinsicWidth(Utils.dpToPx(30));
            thumb.getPaint().setColor(c);
            setThumb(thumb);
        }

        mDrawable = getProgressDrawable();

        typedArray.recycle();
    }

    public void setThumbEnabled(boolean enable) {
        int primary = Color.parseColor(DataStore.getInstance().getColor(EMColor.PRIMARY));
        int moon = Color.parseColor(DataStore.getInstance().getColor(EMColor.MOON));

        ShapeDrawable shape = new ShapeDrawable(new OvalShape());
        shape.getPaint().setColor(enable ? primary : DataStore.getInstance().getIntColor(EMColor.FOG));
        shape.setIntrinsicHeight(Utils.dpToPx(25));
        shape.setIntrinsicWidth(Utils.dpToPx(25));
        shape.getPaint().setStyle(Paint.Style.FILL);
        shape.getPaint().setStrokeWidth(Utils.dpToPx(1));

        setThumb(shape);

        if (enable) {
            //setProgressDrawable(mDrawable);
            getIndeterminateDrawable().setColorFilter(primary, PorterDuff.Mode.SRC_IN);
            getProgressDrawable().setColorFilter(primary, PorterDuff.Mode.SRC_IN);
        } else {
            getIndeterminateDrawable().setColorFilter(moon, PorterDuff.Mode.SRC_IN);
            getProgressDrawable().setColorFilter(moon, PorterDuff.Mode.SRC_IN);
        }

        //getIndeterminateDrawable().setColorFilter(moon, android.graphics.PorterDuff.Mode.MULTIPLY);

    }
}
