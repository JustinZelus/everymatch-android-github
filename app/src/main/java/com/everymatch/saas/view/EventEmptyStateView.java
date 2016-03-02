package com.everymatch.saas.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.server.Data.DataIcon;

/**
 * Created by PopApp_laptop on 01/03/2016.
 */
public class EventEmptyStateView extends LinearLayout implements View.OnClickListener {

    //Data
    private Context context;
    private EventEmptyStateCallBack callBack;
    private String leftText;
    private DataIcon dataIcon;


    //Views
    private RelativeLayout rlEditProfile, rlCreateEvent;
    private TextView mLeftTextView;
    private IconImageView icon;

    public EventEmptyStateView(Context context, String leftText, DataIcon dataIcon) {
        super(context);
        this.context = context;
        this.dataIcon = dataIcon;
        this.leftText = leftText;

        initViews(context);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        setLayoutParams(lp);//lp is parent view
    }

    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_event_empty_state, this);

        rlCreateEvent = (RelativeLayout) findViewById(R.id.rlCreateEvent);
        rlEditProfile = (RelativeLayout) findViewById(R.id.rlEditProfile);
        mLeftTextView = (TextView) findViewById(R.id.leftText);
        icon = (IconImageView) findViewById(R.id.eventEmptyStateIcon);

        icon.setIconImage(dataIcon);
        mLeftTextView.setText(leftText);
        rlCreateEvent.setOnClickListener(this);
        rlEditProfile.setOnClickListener(this);
    }

    public void setCallBack(EventEmptyStateCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlCreateEvent:
                if (callBack != null)
                    callBack.onCreateEventClick();
                break;
            case R.id.rlEditProfile:
                if (callBack != null)
                    callBack.onEditProfileClick();
                break;
        }
    }

    public interface EventEmptyStateCallBack {
        void onEditProfileClick();

        void onCreateEventClick();
    }
}
