package com.everymatch.saas.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.IconType;
import com.everymatch.saas.server.Data.DataIcon;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.util.IconManager;
import com.everymatch.saas.util.Utils;
import com.squareup.picasso.Picasso;


/**
 * Created by Lior Iluz on 23/02/14.
 */
public class EventDataRow extends LinearLayout {

    public static int PADDING_TOP_BOTTOM_DP = 14;
    private Context mContext;

    private LinearLayout wrapperLayout;
    private BaseIconTextView mLeftIcon;
    private BaseTextView mTitle;
    private BaseTextView mDetails;
    private BaseIconTextView mRightIcon;
    private BaseTextView mTextRight;
    private BaseImageView mImageLeft;
    private View mLeftMediaContainer;

    public EventDataRow(Context context) {
        super(context);
        initViews(context);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        setLayoutParams(lp);//lp is parent view
    }

    public EventDataRow(Context context, AttributeSet attrs) {
        super(context, attrs);

        initViews(context);

        if (isInEditMode()) {
        } else {
            initAttrs(attrs);
        }
    }

    private void initViews(Context context) {
        mContext = context;
        setOrientation(VERTICAL);
        LayoutInflater.from(mContext).inflate(R.layout.view_event_data_row, this);

        mLeftIcon = (BaseIconTextView) findViewById(R.id.event_data_row_icon_left);
        mTitle = (BaseTextView) findViewById(R.id.event_data_row_title);
        mDetails = (BaseTextView) findViewById(R.id.event_data_row_details);
        mRightIcon = (BaseIconTextView) findViewById(R.id.event_data_row_icon_right);
        wrapperLayout = (LinearLayout) findViewById(R.id.llEdrWrapper);
        mTextRight = (BaseTextView) findViewById(R.id.event_data_row_text_right);
        mImageLeft = (BaseImageView) findViewById(R.id.view_event_data_row_image_left);
        mLeftMediaContainer = findViewById(R.id.view_vent_data_row_left_media_container);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typeArray = mContext.obtainStyledAttributes(attrs, R.styleable.EventDataRow);

        if (typeArray != null) {
            String title = typeArray.getString(R.styleable.EventDataRow_row_title);
            String details = typeArray.getString(R.styleable.EventDataRow_row_details);
            boolean isLeftIconVisible = typeArray.getBoolean(R.styleable.EventDataRow_left_icon_visible, false);
            boolean isRightIconVisible = typeArray.getBoolean(R.styleable.EventDataRow_right_icon_visible, true);
            boolean isDetaileVisible = typeArray.getBoolean(R.styleable.EventDataRow_is_detail_visible, true);
            boolean isLeftMediaContainerVisible = typeArray.getBoolean(R.styleable.EventDataRow_left_media_container_visible, true);
            boolean isRightTextVisible = typeArray.getBoolean(R.styleable.EventDataRow_right_text_visible, false);
            mDetails.setVisibility(isDetaileVisible ? VISIBLE : GONE);
            mTitle.setText(title);
            mDetails.setText(details);
            mLeftIcon.setVisibility(isLeftIconVisible ? VISIBLE : GONE);
            mRightIcon.setVisibility(isRightIconVisible ? VISIBLE : GONE);
            //mTextRight.setVisibility(isRightTextVisible ? VISIBLE : GONE);
            mLeftMediaContainer.setVisibility(isLeftMediaContainerVisible ? VISIBLE : GONE);

            mRightIcon.setText(Consts.Icons.icon_Arrowright);
            mRightIcon.setTextSize(20);

            int bgColor = typeArray.getInt(R.styleable.EventDataRow_bg_color, -1);

            if (bgColor != -1) {
                setBackgroundColor(Color.parseColor(DataStore.getInstance().getColor(bgColor)));
            }

            typeArray.recycle();
        }

        typeArray = mContext.obtainStyledAttributes(attrs, R.styleable.IconFont);

        if (typeArray != null) {
            String text = typeArray.getString(R.styleable.IconFont_iconText);
            mLeftIcon.setText(IconManager.getInstance(mContext).getIconString(text));
            typeArray.recycle();
        }

        typeArray = mContext.obtainStyledAttributes(attrs, R.styleable.BaseTextView);

        if (typeArray != null) {
            mTitle.setResourceText(typeArray.getString(R.styleable.BaseTextView_text_key));
            mDetails.setResourceText(typeArray.getString(R.styleable.BaseTextView_text_sub_key));
            typeArray.recycle();
        }
    }

    public void setTitle(String name) {
        mTitle.setText(name);
    }

    public void setDetails(String text) {
        if (Utils.isEmpty(text)) {
            getDetailsView().setVisibility(GONE);
            return;
        }
        getDetailsView().setText(text);
        getDetailsView().setVisibility(VISIBLE);
    }

    public BaseTextView getTitleView() {
        return mTitle;
    }

    public BaseTextView getDetailsView() {
        return mDetails;
    }

    public BaseIconTextView getLeftIcon() {
        return mLeftIcon;
    }

    public BaseIconTextView getRightIcon() {
        return mRightIcon;
    }

    public LinearLayout getWrapperLayout() {
        return wrapperLayout;
    }

    public BaseTextView getRightText() {
        return mTextRight;
    }

    public BaseImageView getLeftImage() {
        return mImageLeft;
    }

    public void setLeftIconVisibility(boolean leftIconVisubility) {

    }

    public void setBackground(int bg) {
        wrapperLayout.setBackgroundColor(bg);
    }

    public void setLeftIconOrImage(DataIcon icon) {
        if (IconType.FONT.equals(icon.getType())) {
            getLeftImage().setVisibility(INVISIBLE);
            getLeftIcon().setVisibility(VISIBLE);
            getLeftIcon().setText(IconManager.getInstance(getContext()).getIconString(icon.getValue()));
        } else {
            getLeftImage().setVisibility(VISIBLE);
            getLeftIcon().setVisibility(INVISIBLE);
            Picasso.with(getContext())
                    .load(icon.getValue())
                    .into(getLeftImage());
        }
    }

    public void setLargePaddingTopBottom() {
        int value = Utils.dpToPx(PADDING_TOP_BOTTOM_DP);
        wrapperLayout.setPadding(wrapperLayout.getPaddingLeft(), value, wrapperLayout.getPaddingRight(), value);
        wrapperLayout.requestLayout();
    }

    /**
     * if text is empty, TEXT VIEW will be GONE else it will be VISIBLE
     */
    public void setRightText(String text) {
        if (Utils.isEmpty(text)) {
            getRightText().setVisibility(GONE);
            return;
        }
        mTextRight.setText(text);
        mTextRight.setVisibility(VISIBLE);
    }

    /**
     * if text is empty, icon will be GONE else it will be VISIBLE
     */
    public void setRightIconText(String text) {
        if (Utils.isEmpty(text)) {
            getRightIcon().setVisibility(GONE);
            return;
        }
        getRightIcon().setText(text);
        getRightIcon().setVisibility(VISIBLE);
    }

    public View getLeftMediaContainer() {
        return mLeftMediaContainer;
    }
}
