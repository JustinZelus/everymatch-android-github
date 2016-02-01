package com.everymatch.saas.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;

/**
 * Created by PopApp_laptop on 27/01/2016.
 */
public class DiscoverMenuItem extends LinearLayout {
    Context mContext;
    static Typeface mTypeface;

    private BaseTextView icon;
    private BaseTextView tvTitle;


    public DiscoverMenuItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.view_discover_menu_item, this);

        if (mTypeface == null)
            mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/icomoon.ttf");

        icon = (BaseTextView) findViewById(R.id.iconDiscoverMenu);
        tvTitle = (BaseTextView) findViewById(R.id.tvDiscoverMenuTitle);

        TypedArray typeArray = mContext.obtainStyledAttributes(attrs, R.styleable.DiscoverMenuItem);

        if (typeArray != null) {

            String prevText = typeArray.getString(R.styleable.DiscoverMenuItem_dmi_title_prev_text);
            String iconCode = typeArray.getString(R.styleable.DiscoverMenuItem_dmi_icon_code);
            String textKey = typeArray.getString(R.styleable.DiscoverMenuItem_dmi_title_res);

            if (iconCode != null) {
                icon.setTypeface(mTypeface);
                icon.setText(iconCode);
            }

            if (textKey != null) {

                tvTitle.setText(DataManager.getInstance().getResourceText(textKey));
            }
            typeArray.recycle();
        }
    }

    public BaseTextView getTvTitle() {
        return tvTitle;
    }

    public BaseTextView getIcon() {
        return icon;
    }

    public void setSelected(boolean selected) {
        DataStore ds = DataStore.getInstance();
        icon.setTextColor(selected ? ds.getIntColor(EMColor.PRIMARY) : ds.getIntColor(EMColor.MORNING));
        tvTitle.setTextColor(selected ? ds.getIntColor(EMColor.PRIMARY) : ds.getIntColor(EMColor.MORNING));
    }
}
