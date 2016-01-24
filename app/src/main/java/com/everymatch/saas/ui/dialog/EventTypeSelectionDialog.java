package com.everymatch.saas.ui.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.Data.DataEventHolder;
import com.everymatch.saas.server.Data.DataIcon;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.util.IconManager;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.BaseIconTextView;
import com.everymatch.saas.view.BaseTextView;
import com.everymatch.saas.view.IconImageView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dor on 21/11/2015.
 */
public class EventTypeSelectionDialog extends DialogFragment implements View.OnClickListener {

    private IconManager mIconManager;

    private static final String EXTRA_DATA = "extra.data";
    private static final String EXTRA_SELECTED = "extra.selected";
    private static final String EXTRA_TOP_MARGIN = "extra.top.margin";
    private static final String EXTRA_LEFT_MARGIN = "extra.left.margin";
    private static final String EXTRA_WIDTH = "extra.width";

    public static final String EXTRA_RESULT = "extra.result";

    private LinearLayout mPopup;
    private boolean mIsAnimating;
    private String mSelected;
    private int mLeftMargin;
    private int mTopMargin;
    private int mWidth;
    private String mResult;

    public static EventTypeSelectionDialog create(HashMap<String, DataEventHolder> map, String key, int leftMargin, int topMargin, int width) {
        EventTypeSelectionDialog eventTypeSelectionDialog = new EventTypeSelectionDialog();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATA, map);
        args.putString(EXTRA_SELECTED, key);
        args.putInt(EXTRA_TOP_MARGIN, topMargin);
        args.putInt(EXTRA_LEFT_MARGIN, leftMargin);
        args.putInt(EXTRA_WIDTH, width);
        eventTypeSelectionDialog.setArguments(args);
        return eventTypeSelectionDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mSelected = (String) args.getSerializable(EXTRA_SELECTED);
        mTopMargin = (int) args.getSerializable(EXTRA_TOP_MARGIN);
        mLeftMargin = (int) args.getSerializable(EXTRA_LEFT_MARGIN);
        mWidth = (int) args.getSerializable(EXTRA_WIDTH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_event_type_drop_down, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);

        mPopup = ((LinearLayout) view.findViewById(R.id.view_event_type_drop_down_container));

        mPopup.getLayoutParams().width = mWidth;
        ((FrameLayout.LayoutParams) mPopup.getLayoutParams()).leftMargin = mLeftMargin;
        ((FrameLayout.LayoutParams) mPopup.getLayoutParams()).topMargin = mTopMargin;
        setItems();

        mPopup.post(new Runnable() {
            @Override
            public void run() {
                setShowing(true);
            }
        });
    }

    private void setItems() {

        HashMap<String, DataEventHolder> map = (HashMap<String, DataEventHolder>) getArguments().getSerializable(EXTRA_DATA);

        for (Map.Entry<String, DataEventHolder> entry : map.entrySet()) {
            DataEventHolder value = entry.getValue();
            EventTypeRow eventTypeRow = new EventTypeRow(getContext());
            String text = String.format(Consts.EVENT_TYPE_FORMAT, value.text_title, value.count);
            eventTypeRow.setDetails(text, value.icon);
            eventTypeRow.setKey(entry.getKey());
            eventTypeRow.setOnClickListener(this);
            mPopup.addView(eventTypeRow);

            if (entry.getKey().equals(mSelected)) {
                eventTypeRow.setSelected(true);
            } else {
                eventTypeRow.setSelected(false);
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v instanceof EventTypeRow) {
            mResult = ((EventTypeRow) v).getKey();
            setShowing(false);
            return;
        }
    }

    private class EventTypeRow extends LinearLayout {

        private TextView mTextTitle;
        private IconImageView mLeftIcon;
        private BaseIconTextView mRightIcon;
        private String key;

        public EventTypeRow(Context context) {
            super(context);

            setGravity(Gravity.CENTER_VERTICAL);
            int padding = Utils.dpToPx(11);
            setPadding(padding, padding, padding, padding);
            setOrientation(HORIZONTAL);
            LayoutInflater.from(context).inflate(R.layout.view_event_selection_row, this);
            mLeftIcon = (IconImageView) findViewById(R.id.view_event_selection_row_left_icon);
            mRightIcon = (BaseIconTextView) findViewById(R.id.view_event_selection_row_right_icon);
            mTextTitle = (BaseTextView) findViewById(R.id.view_event_selection_row_text);
            mRightIcon.setText(Consts.Icons.icon_Done);
            mRightIcon.setTextColor(DataStore.getInstance().getIntColor(EMColor.PRIMARY));
        }

        public void setDetails(String title, DataIcon leftIcon) {
            mTextTitle.setText(title);
            mLeftIcon.setIconImage(leftIcon);
        }

        public void setSelected(boolean selected) {

            if (selected) {
                mTextTitle.setTextColor(DataStore.getInstance().getIntColor(EMColor.PRIMARY));
                mLeftIcon.getIconTextView().setTextColor(DataStore.getInstance().getIntColor(EMColor.PRIMARY));
                mRightIcon.setVisibility(View.VISIBLE);
            } else {
                mTextTitle.setTextColor(DataStore.getInstance().getIntColor(EMColor.MOON));
                mLeftIcon.getIconTextView().setTextColor(DataStore.getInstance().getIntColor(EMColor.MOON));
                mRightIcon.setVisibility(View.INVISIBLE);
            }
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    public void setShowing(boolean show) {

        if (mIsAnimating) {
            return;
        }

        mIsAnimating = true;

        ObjectAnimator translateAnimator;

        if (show) {
            translateAnimator = ObjectAnimator.ofFloat(mPopup, View.TRANSLATION_Y.getName(), -mPopup.getMeasuredHeight(), 0);
            translateAnimator.setDuration(400);
            translateAnimator.setInterpolator(new OvershootInterpolator());
            translateAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsAnimating = false;
                }
            });


        } else {
            translateAnimator = ObjectAnimator.ofFloat(mPopup, View.TRANSLATION_Y.getName(), -mPopup.getMeasuredHeight() - mTopMargin);
            translateAnimator.setDuration(500);
            translateAnimator.setInterpolator(new AnticipateOvershootInterpolator());
            translateAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsAnimating = false;

                    Intent result = new Intent();
                    result.putExtra(EXTRA_RESULT, mResult);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, result);
                }
            });

        }

        translateAnimator.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}
