package com.everymatch.saas.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.util.TypeFaceProvider;
import com.everymatch.saas.util.Utils;


/**
 * Created by Lior Iluz on 24/02/2014
 */
public class BaseEditText extends EditText {

    private Context mContext;

    public BaseEditText(Context context) {
        super(context);
        this.mContext = context;
    }

    public BaseEditText(Context context, AttributeSet attrs, int defStyleAttr, Context mContext) {
        super(context, attrs, defStyleAttr);
        this.mContext = mContext;

        if (!isInEditMode())
            initAttributes(attrs);
    }

    private void init(AttributeSet attrs) {

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;

        if (!isInEditMode())
            initAttributes(attrs);

    }

    public BaseEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        if (!isInEditMode())
            initAttributes(attrs);

    }

    private void initAttributes(AttributeSet attributeSet) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attributeSet, R.styleable.BaseTextView);

        if (typedArray != null) {
            TypeFaceProvider.initByType(typedArray.getInt(R.styleable.BaseTextView_font, -1), this);
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

        typedArray = mContext.obtainStyledAttributes(attributeSet, R.styleable.BaseEditText);

        if (typedArray != null) {
            String hintRes = typedArray.getString(R.styleable.BaseEditText_hint_key);
            if (!Utils.isEmpty(hintRes)) {
                String hint = DataManager.getInstance().getResourceText(hintRes);
                setHint(hint);
            }
            typedArray.recycle();
        }

        // Remove spell check
        // setInputType(~(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT));
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            clearFocus();
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
