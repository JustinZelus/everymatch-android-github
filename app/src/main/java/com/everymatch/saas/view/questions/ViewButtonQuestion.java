package com.everymatch.saas.view.questions;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.util.ShapeDrawableUtils;
import com.everymatch.saas.view.BaseIconTextView;
import com.everymatch.saas.view.BaseTextView;

/**
 * Created by PopApp_laptop on 22/12/2015.
 */
public class ViewButtonQuestion extends LinearLayout {

    private DataStore ds = DataStore.getInstance();
    RelativeLayout rlLayout;
    private Context mContext;
    private BaseIconTextView tvIcon;
    private BaseTextView tvText;

    public RelativeLayout getRlLayout() {
        return rlLayout;
    }

    public BaseIconTextView getTvIcon() {
        return tvIcon;
    }

    public BaseTextView getTvText() {
        return tvText;
    }


    public ViewButtonQuestion(Context context) {
        super(context);
        this.mContext = context;
        initViews(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        setLayoutParams(lp);
    }

    public void setSelected(boolean selected) {
        int color = selected ? ds.getIntColor(EMColor.PRIMARY) : ds.getIntColor(EMColor.MOON);

        getRlLayout().setBackgroundDrawable(ShapeDrawableUtils.getButtonStroked(color));
        getTvIcon().setTextColor(color);
        getTvText().setTextColor(color);
    }

    private void initViews(Context context) {
        mContext = context;
        setOrientation(VERTICAL);
        LayoutInflater.from(mContext).inflate(R.layout.question_button, this);

        rlLayout = (RelativeLayout) findViewById(R.id.yes_no_layout);
        tvIcon = (BaseIconTextView) findViewById(R.id.image);
        tvText = (BaseTextView) findViewById(R.id.text);
    }
}
