package com.everymatch.saas.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.util.IconManager;
import com.everymatch.saas.util.TypeFaceProvider;

/**
 * Created by Lior Iluz on 24/02/2014
 */
public class BaseIconTextView extends TextView {

    private CharSequence mText;
    private int mIndex;
    private CharSequence mText2;
    private int mIndex2;
    private long mDelay = 150; //Default 150ms delay
    private boolean textAnimating;

    public boolean isTextAnimating() {
        return textAnimating;
    }

    public void setTextAnimating(boolean textIsAnimating) {

        textAnimating = textIsAnimating;
    }

    public boolean isTextEmpty(CharSequence text) {

        if (text.length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    public interface animationListener {
        void onAnimationFinished();
    }

    public void setCharacterDelay(long millis) {
        mDelay = millis;
    }

    private Context mContext;

    public BaseIconTextView(Context context) {
        this(context, null);
    }

    public BaseIconTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BaseIconTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;

        if (isInEditMode()) {
            return;
        } else {
            initAttributes(attrs);
        }
    }

    private void initAttributes(AttributeSet attributeSet) {

        setTypeface(TypeFaceProvider.getTypeFace(TypeFaceProvider.FONT_ICOMOON));

        TypedArray typedArray = mContext.obtainStyledAttributes(attributeSet, R.styleable.ResourceColor);

        typedArray = mContext.obtainStyledAttributes(attributeSet, R.styleable.ResourceColor);

        if (typedArray != null){
            int bgColor = typedArray.getInt(R.styleable.ResourceColor_bg_color, -1);

            if (bgColor != -1){
                setBackgroundColor(Color.parseColor(DataStore.getInstance().getColor(bgColor)));
            }

            int textColor = typedArray.getInt(R.styleable.ResourceColor_text_color, -1);

            if (textColor != -1){
                setTextColor(Color.parseColor(DataStore.getInstance().getColor(textColor)));
            }

            typedArray.recycle();
        }

        typedArray = mContext.obtainStyledAttributes(attributeSet, R.styleable.IconFont);

        if (typedArray != null){
            String text = typedArray.getString(R.styleable.IconFont_iconText);
            typedArray.recycle();
            setText(IconManager.getInstance(mContext).getIconString(text));
        }
    }
}
