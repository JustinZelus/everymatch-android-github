package com.everymatch.saas.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.everymatch.saas.R;


/**
 * Created by Lior Iluz on 23/02/14.
 */
public class EventHeader extends LinearLayout implements View.OnClickListener {

    private Context mContext;
    public OnEventHeader mListener;

    private BaseIconTextView mBackButton;
    private BaseTextView mTitle;
    private BaseEditText mEditTitle;
    private BaseIconTextView icon1;
    private BaseIconTextView icon2;
    private BaseIconTextView icon3;
    private TextView mTextCenter;
    private BaseIconTextView tvArrowDown;

    public boolean mClick = false;

    public EventHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setOrientation(VERTICAL);

        LayoutInflater.from(context).inflate(R.layout.view_event_header, this);

        mBackButton = (BaseIconTextView) findViewById(R.id.event_header_back);
        mTitle = (BaseTextView) findViewById(R.id.event_header_title);
        mEditTitle = (BaseEditText) findViewById(R.id.event_header_title_edit);
        icon1 = (BaseIconTextView) findViewById(R.id.event_header_icon_1);
        icon2 = (BaseIconTextView) findViewById(R.id.event_header_icon_2);
        icon3 = (BaseIconTextView) findViewById(R.id.event_header_icon_3);
        mTextCenter = (TextView) findViewById(R.id.view_event_header_center_text);
        tvArrowDown = (BaseIconTextView) findViewById(R.id.event_header_arrowDown);
        tvArrowDown.setVisibility(GONE);

        mBackButton.setOnClickListener(this);
        setArrowDownVisibility(false);
        getTitle().setOnClickListener(null);


        icon1.setOnClickListener(this);
        icon2.setOnClickListener(this);
        icon3.setOnClickListener(this);
    }

    public View getTitle() {
        return mTitle;
    }

    public BaseEditText getEditTitle() {
        return mEditTitle;
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setTitle(int resId) {
        mTitle.setText(getResources().getString(resId));
    }

    public void setArrowDownVisibility(boolean visibility) {
        tvArrowDown.setVisibility(visibility ? VISIBLE : GONE);
    }

    public void setEditTitle(String title) {
        mEditTitle.setText(title);
    }

    public BaseIconTextView getBackButton() {
        return mBackButton;
    }

    public BaseIconTextView getIconOne() {
        return icon1;
    }

    public BaseIconTextView getIconTwo() {
        return icon2;
    }

    public BaseIconTextView getIconThree() {
        return icon3;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.event_header_back:
                if (mListener != null)
                    mListener.onBackButtonClicked();
                break;

            case R.id.event_header_icon_1:
                if (mListener != null)
                    mListener.onOneIconClicked();
                break;

            case R.id.event_header_icon_2:
                if (mListener != null)
                    mListener.onTwoIconClicked();
                break;

            case R.id.event_header_icon_3:
                if (mListener != null)
                    mListener.onThreeIconClicked();
                break;
        }
    }

    public TextView getCenterText() {
        return mTextCenter;
    }

    public void setListener(OnEventHeader listener) {
        mListener = listener;
    }

    /**
     * Top bar button clicks and actions listener
     */
    public interface OnEventHeader {

        void onBackButtonClicked();

        void onOneIconClicked();

        void onTwoIconClicked();

        void onThreeIconClicked();
    }
}
