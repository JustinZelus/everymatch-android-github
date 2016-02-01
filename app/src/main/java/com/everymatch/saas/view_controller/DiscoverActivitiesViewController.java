package com.everymatch.saas.view_controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.everymatch.saas.R;
import com.everymatch.saas.adapter.DiscoverActivitiesAdapter;
import com.everymatch.saas.server.Data.DataActivity;
import com.everymatch.saas.util.FixedSizeLinearLayoutManager;

import java.util.ArrayList;

/**
 * Created by dors on 7/26/15.
 */
public class DiscoverActivitiesViewController implements View.OnClickListener {

    public static final int ANIMATION_DURATION = 200;

    private static final String TAG = DiscoverActivitiesViewController.class.getSimpleName();

    private DiscoverActivitiesAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Button mButtonAddActivity;
    private ArrayList<DataActivity> mActivities;
    private View mActivitiesPopup;
    private DiscoverActivitiesListener mListener;
    private boolean mIsAnimating;

    public  DiscoverActivitiesViewController(View activitiesPopup, ArrayList<DataActivity> activities) {
        this.mActivities = activities;
        this.mActivitiesPopup = activitiesPopup;
        mRecyclerView = (RecyclerView) activitiesPopup.findViewById(R.id.view_activities_popup_recycler);
        mButtonAddActivity = (Button) activitiesPopup.findViewById(R.id.view_activities_popup_button_add_activity);
        mButtonAddActivity.setOnClickListener(this);
        activitiesPopup.setOnClickListener(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new FixedSizeLinearLayoutManager(activitiesPopup.getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new DiscoverActivitiesAdapter(mActivities, new DiscoverActivitiesAdapter.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onActivityClick(mActivities.get(mRecyclerView.getChildAdapterPosition(view)));
            }

            @Override
            public void onSettingsButtonClick(View view) {
                mListener.onActivitySettingsButtonClick(mActivities.get(mRecyclerView.getChildAdapterPosition(view)));
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setActivitiesListener(DiscoverActivitiesListener listener) {
        this.mListener = listener;
    }

    public void setSelectedActivity(String activityId) {
        mAdapter.setSelectedId(activityId);
    }

    /**
     * Toggle between activity popup visibility modes
     */
    public void toggleActivitySelectionPopup() {

        if (mIsAnimating) {
            return;
        }

        ObjectAnimator translateAnimator;
        ObjectAnimator alphaAnimator;

        if (mActivitiesPopup.getVisibility() == View.INVISIBLE) {
            mActivitiesPopup.setVisibility(View.VISIBLE);
            translateAnimator = ObjectAnimator.ofFloat(mActivitiesPopup, View.TRANSLATION_Y.getName(), -200, 0);
            alphaAnimator = ObjectAnimator.ofFloat(mActivitiesPopup, View.ALPHA.getName(), 0, 1);
            alphaAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsAnimating = false;
                }
            });
        } else {
            translateAnimator = ObjectAnimator.ofFloat(mActivitiesPopup, View.TRANSLATION_Y.getName(), -200);
            alphaAnimator = ObjectAnimator.ofFloat(mActivitiesPopup, View.ALPHA.getName(), 0);
            alphaAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mActivitiesPopup.setVisibility(View.INVISIBLE);
                    mIsAnimating = false;
                }
            });
        }

        translateAnimator.setDuration(ANIMATION_DURATION);
        alphaAnimator.setDuration(ANIMATION_DURATION);
        translateAnimator.start();
        alphaAnimator.start();
    }

    public void closeActivityPopup(){
        mActivitiesPopup.setVisibility(View.INVISIBLE);
        mActivitiesPopup.setTranslationY(-200);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.view_activities_popup_button_add_activity) {
            if (mListener != null) {
                mListener.onAddActivityButtonClick();
            }
        } else {
            toggleActivitySelectionPopup();
        }
    }

    public void refreshAdapter() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public interface DiscoverActivitiesListener {
        void onAddActivityButtonClick();

        void onActivitySettingsButtonClick(DataActivity activity);

        void onActivityClick(DataActivity activity);
    }
}
