package com.everymatch.saas.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.Data.DataActivity;
import com.everymatch.saas.view.BaseIconTextView;
import com.everymatch.saas.view.IconImageView;

import java.util.ArrayList;

/**
 * Created by dors on 7/26/15.
 */
public class DiscoverActivitiesAdapter extends BaseRecyclerViewAdapter<DiscoverActivitiesAdapter.ActivityHolder> {

    private final OnClickListener mOnClickListener;
    private ArrayList<DataActivity> mActivities;
    private String mSelectedActivity;

    public void setSelectedId(String activityId) {
        this.mSelectedActivity = activityId;
    }

    public DiscoverActivitiesAdapter(ArrayList<DataActivity> activities, OnClickListener onClickListener) {
        this.mActivities = activities;
        this.mOnClickListener = onClickListener;
    }

    /**
     * View holder
     */
    static class ActivityHolder extends RecyclerView.ViewHolder {

        public IconImageView icon;
        public TextView title;
        public BaseIconTextView settingsButton;

        public ActivityHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.view_discover_activity_item_title);
            icon = (IconImageView) v.findViewById(R.id.view_discover_activity_item_icon);
            settingsButton = (BaseIconTextView) v.findViewById(R.id.view_discover_activity_item_settings_button);
        }
    }

    @Override
    public ActivityHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_discover_activity_item, viewGroup, false);
        final ActivityHolder activityHolder = new ActivityHolder(view);

        activityHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onClick(activityHolder.itemView);
            }
        });

        activityHolder.settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onSettingsButtonClick(activityHolder.itemView);
            }
        });

        return new ActivityHolder(view);
    }

    @Override
    public void onBindViewHolder(ActivityHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);

        DataActivity activity = mActivities.get(position);

        if (activity != null) {
            if (mSelectedActivity != null && mSelectedActivity.equals(activity.client_id)) {
                viewHolder.itemView.setBackgroundColor(DataStore.getInstance().getIntColor(EMColor.MOON));
            } else {
                viewHolder.itemView.setBackgroundColor(DataStore.getInstance().getIntColor(EMColor.PRIMARY));
            }

            viewHolder.title.setText(activity.text_title);
            viewHolder.icon.setIconImage(activity.icon);
        }
    }

    @Override
    public int getItemCount() {
        return mActivities == null ? 0 : mActivities.size();
    }

    public interface OnClickListener {
        void onClick(View view);

        void onSettingsButtonClick(View view);
    }
}