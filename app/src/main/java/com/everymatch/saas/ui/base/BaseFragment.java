package com.everymatch.saas.ui.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.singeltones.PusherManager;
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.IconManager;
import com.everymatch.saas.view.NoConnectionView;

import java.io.Serializable;

/**
 * Created by dors on 1/21/15.
 * <p/>
 * Base class for all fragments
 */
public abstract class BaseFragment extends Fragment implements NoConnectionView.Callbacks {

    private final String TAG = getClass().getSimpleName();
    protected IconManager im;
    /**
     * Dialog for modal blocking
     */
    protected Dialog mDialog;

    /**
     * Progress bar for waiting to data
     */
    protected View mProgressBar;

    protected DataManager dm = DataManager.getInstance();
    protected DataStore ds = DataStore.getInstance();
    protected ServerConnector server = ServerConnector.getInstance();

    private IntentFilter intentFilter;

    private View mDimView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        im = IconManager.getInstance(activity);
    }

    public void showDialog(String title) {
        ((BaseActivity) getActivity()).showDialog(title);
    }

    public void stopDialog() {
        ((BaseActivity) getActivity()).stopDialog();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            switch (action) {
                case PusherManager.ACTION_PUSHER_EVENT:
                    Serializable data = intent.getSerializableExtra(PusherManager.EXTRA_PUSHER_EVENT_DATA);
                    String eventName = intent.getStringExtra(PusherManager.EXTRA_PUSHER_EVENT_NAME);

                    EMLog.i(TAG, "onReceive: " + eventName + "\nevent data = " + data);

                    if (data != null) {
                        handleBroadcast(data, eventName);
                    }
                    break;

               /* case NetworkErrorMessageDialog.ACTION_NETWORK_ERROR:
                    String message = intent.getStringExtra(NetworkErrorMessageDialog.EXTRA_NETWORK_ERROR_TITLE);
                    //NetworkErrorMessageDialog.show(getActivity(), message);
                    NetworkErrorMessageDialog.start((BaseActivity) getActivity(), getChildFragmentManager(), message);
                    break;*/
            }
        }
    };

    protected void showDimView() {
        if (mDimView == null) {
            mDimView = new View(getActivity());
            mDimView.setBackgroundColor(Color.parseColor("#99000000"));
            mDimView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDimViewClick();
                }
            });
        }

        //mDimView.getParent()
        try {
            ((ViewGroup) getView()).removeView(mDimView);
            ((ViewGroup) getView()).addView(mDimView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ObjectAnimator.ofFloat(mDimView, View.ALPHA.getName(), 0, 1).setDuration(250).start();
        } catch (Exception ex) {
        }

    }

    protected void hideDimView(boolean immediate) {

        if (mDimView == null) {
            return;
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(mDimView, View.ALPHA.getName(), 1, 0).setDuration(immediate ? 1 : 250);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (getView() != null) {
                    ((ViewGroup) getView()).removeView(mDimView);
                    mDimView = null;
                }
            }
        });

        animator.start();
    }

    protected void onDimViewClick() {
    }

    protected void handleBroadcast(Serializable eventObject, String eventName) {
        EMLog.i(TAG, "handleBroadcast");
        // Nothing in here
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   EMLog.i(TAG, "onCreate");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // EMLog.i(TAG, "onViewCreated");
        mProgressBar = view.findViewById(android.R.id.progress);

        registerReceiver();
    }

    /**
     * Fragments that want to consume back event from their hosted
     * activity, should implement this method and return true
     *
     * @return true if back consumed
     */
    public boolean handleBackPress() {
        return false;
    }

    /**
     * Initialize the action bar to the specific type
     * of this fragment
     *
     * @param actionBar the action bar
     */
    protected void initActionBar(ActionBar actionBar) {
        // Nothing for the base
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        //  DialogUtils.dismissDialog(mDialog);

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

        cancelRequests();

        super.onDetach();
    }

    @Override
    public void onDestroy() {
        //    EMLog.i(TAG, "onDestroy");
        super.onDestroy();
    }

    /**
     * Find fragment in the child fragment manager of this fragment
     */
    public Fragment findFragment(String tag) {
        return getChildFragmentManager().findFragmentByTag(tag);
    }

    @Override
    public void onTryAgainClick() {
        // Nothing in here
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //    EMLog.i(TAG, "onAttach");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //    EMLog.i(TAG, "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //    EMLog.i(TAG, "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        //    EMLog.i(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        //   EMLog.i(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        hideKeyboard();
    }

    protected void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }


    @Override
    public void onStop() {
        hideDimView(true);
        super.onStop();
        //   EMLog.i(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterReceiver();
        //   EMLog.i(TAG, "onDestroyView");
    }

    protected void registerReceiver() {
        EMLog.i(TAG, "registerReceiver");
        intentFilter = new IntentFilter();
        intentFilter.addAction(PusherManager.ACTION_PUSHER_EVENT);
        //intentFilter.addAction(NetworkErrorMessageDialog.ACTION_NETWORK_ERROR);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        EMLog.i(TAG, "unregisterReceiver");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }

    // Each child fragment should cancel its requests in here
    protected void cancelRequests() {
    }

    protected void cancelPendingRequests(String... tags) {
        if (tags != null && tags.length > 0) {
            for (String tag : tags) {
                ServerConnector.getInstance().cancelPendingRequests(tag);
            }
        }
    }
}
