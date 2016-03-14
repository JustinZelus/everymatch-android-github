package com.everymatch.saas.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.server.Data.DataIcon;

/**
 * Created by PopApp_laptop on 01/03/2016.
 */
public class EventEmptyStateView extends Fragment implements View.OnClickListener {
    public static final String TAG = "EventEmptyStateView";

    public static final String ARG_DATA_ICON = "arg.data.icon";
    public static final String ARG_LEFT_TEXT = "arg.left.text";
    //Data
    private Context context;
    private EventEmptyStateCallBack callBack;
    private String leftText;
    private DataIcon dataIcon;


    //Views
    private RelativeLayout rlEditProfile, rlCreateEvent;
    private TextView mLeftTextView;
    private IconImageView icon;


    public static EventEmptyStateView getInstance(DataIcon dataIcon, String leftText) {
        EventEmptyStateView answer = new EventEmptyStateView();
        Bundle bundle = new Bundle(2);
        bundle.putSerializable(ARG_DATA_ICON, dataIcon);
        bundle.putString(ARG_LEFT_TEXT, leftText);
        answer.setArguments(bundle);
        return answer;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataIcon = (DataIcon) getArguments().getSerializable(ARG_DATA_ICON);
        leftText = getArguments().getString(ARG_LEFT_TEXT);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_event_empty_state, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rlCreateEvent = (RelativeLayout) view.findViewById(R.id.rlCreateEvent);
        rlEditProfile = (RelativeLayout) view.findViewById(R.id.rlEditProfile);
        rlEditProfile.measure(0, 0);
        rlCreateEvent.measure(0, 0);
        int m = Math.max(rlCreateEvent.getMeasuredHeight(), rlEditProfile.getMeasuredHeight());
        rlCreateEvent.getLayoutParams().height = m;
        rlEditProfile.getLayoutParams().height = m;

        mLeftTextView = (TextView) view.findViewById(R.id.leftText);
        icon = (IconImageView) view.findViewById(R.id.eventEmptyStateIcon);

        icon.setIconImage(dataIcon);
        mLeftTextView.setText(leftText);
        rlCreateEvent.setOnClickListener(this);
        rlEditProfile.setOnClickListener(this);
    }

   /* public EventEmptyStateView(Context context, String leftText, DataIcon dataIcon) {
        super(context);
        this.context = context;
        this.dataIcon = dataIcon;
        this.leftText = leftText;

        initViews(context);

        *//*LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        setLayoutParams(lp);//lp is parent view*//*
    }*/

    /* private void initViews(Context context) {
         LayoutInflater.from(context).inflate(R.layout.view_event_empty_state, this);

         rlCreateEvent = (RelativeLayout) findViewById(R.id.rlCreateEvent);
         rlEditProfile = (RelativeLayout) findViewById(R.id.rlEditProfile);
         rlEditProfile.measure(0, 0);
         rlCreateEvent.measure(0, 0);
         int m = Math.max(rlCreateEvent.getMeasuredHeight(), rlEditProfile.getMeasuredHeight());
         rlCreateEvent.getLayoutParams().height = m;
         rlEditProfile.getLayoutParams().height = m;

         mLeftTextView = (TextView) findViewById(R.id.leftText);
         icon = (IconImageView) findViewById(R.id.eventEmptyStateIcon);

         icon.setIconImage(dataIcon);
         mLeftTextView.setText(leftText);
         rlCreateEvent.setOnClickListener(this);
         rlEditProfile.setOnClickListener(this);
     }
 */
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
