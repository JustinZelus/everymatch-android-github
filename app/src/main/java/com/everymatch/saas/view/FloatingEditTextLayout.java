package com.everymatch.saas.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.singeltones.Consts;

/**
 * Created by PopApp_laptop on 06/10/2015.
 */
public class FloatingEditTextLayout extends LinearLayout {

    private Context context;

    private BaseTextView tvTitle;
    private BaseEditText etValue;
    private BaseIconTextView tvIconLeft;


    public FloatingEditTextLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.view_floating_edit_text_settings_row, this);
        tvTitle = (BaseTextView) findViewById(R.id.tvTitle);
        etValue = (BaseEditText) findViewWithTag("etValue");
        tvIconLeft = (BaseIconTextView) findViewById(R.id.iconLeft);

        tvIconLeft.setText(Consts.Icons.icon_Arrowright);

        if (isInEditMode()) {
        } else {
            initAttrs(attrs);
        }
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.FloatingEditTextLayout);

        if (typeArray != null) {
            String titleRes = typeArray.getString(R.styleable.FloatingEditTextLayout_fl_title_resource);
            String hintRes = typeArray.getString(R.styleable.FloatingEditTextLayout_fl_hint_resource);
            boolean isLeftIconVisible = typeArray.getBoolean(R.styleable.FloatingEditTextLayout_fl_isLeftIconVisible, true);

            if (titleRes != null)
                tvTitle.setText(DataManager.getInstance().getResourceText(titleRes));
            if (hintRes != null) {
                etValue.setHint(DataManager.getInstance().getResourceText(hintRes));
            }
            tvIconLeft.setVisibility(isLeftIconVisible?VISIBLE:INVISIBLE);

            typeArray.recycle();
        }
    }


    public BaseTextView getTvTitle() {
        return tvTitle;
    }

    public BaseEditText getEtValue() {
        return etValue;
    }


    /*public void setTextWrapping(boolean wrapText) {
        mTextWrapper.setMinimumHeight(mTextWrapper.getLayoutParams().height);
        mTextWrapper.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mTextWrapper.requestLayout();
    }*/
}
