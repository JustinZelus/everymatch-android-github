package com.everymatch.saas.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;

import com.everymatch.saas.ui.sign.SignActivity;

/**
 * Created by Dacid on 17/06/2015.
 */
public class BlurImageView extends BaseImageView {

    private boolean mIsBlur = false;
//    private Paint mBlurPaint;

    private Bitmap mOriginalBitmap;
    private Bitmap mBlurBitmap;

    private final int BLUR_RATIO = 260;
    private final int BLUR_COLOR = Color.parseColor("#44000000");

    public BlurImageView(Context context) {
        this(context, null);
    }

    public BlurImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


//    private void init()
//    {
//        mBlurPaint = new Paint();
//        mBlurPaint.setAntiAlias(true);
//        mBlurPaint.setStyle(Paint.Style.FILL);
//        mBlurPaint.setColor(BLUR_COLOR);
//    }

    public void setBlur(boolean isBlur) {
        if (mIsBlur == isBlur)
            return;

//        if (isBlur && mBlurBitmap != null) {
//            super.setImageBitmap(mBlurBitmap);
//            setColorFilter(new PorterDuffColorFilter(BLUR_COLOR, PorterDuff.Mode.DARKEN));
//        }
//        else if (!isBlur && mOriginalBitmap != null) {
//            super.setImageBitmap(mOriginalBitmap);
//            setColorFilter(null);
//        }

        if (isBlur) {
            super.setImageBitmap(mBlurBitmap);
            setColorFilter(new PorterDuffColorFilter(BLUR_COLOR, PorterDuff.Mode.DARKEN));
        }
        else {
            super.setImageBitmap(mOriginalBitmap);
            clearColorFilter();
        }

        mIsBlur = isBlur;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        if (mIsBlur)
//            canvas.drawRect(getLeft(), 0, getRight(), getHeight(),mBlurPaint);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (bm != null) {
            mOriginalBitmap = bm;

            mBlurBitmap = bm.copy(bm.getConfig(), false);
            final RenderScript rs = RenderScript.create( getContext() );
            final Allocation input = Allocation.createFromBitmap( rs, mBlurBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT );
            final Allocation output = Allocation.createTyped( rs, input.getType() );
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create( rs, Element.U8_4(rs) );
            script.setRadius(4.f);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(mBlurBitmap);

//            mBlurBitmap = Bitmap.createScaledBitmap(bm, bm.getWidth()/BLUR_RATIO, bm.getHeight()/BLUR_RATIO, true);

            Context context = getContext();
            if (context instanceof SignActivity) {
               // ((SignActivity) context).onImageLoaded();
            }
        }

        super.setImageBitmap(mOriginalBitmap);
    }
}
