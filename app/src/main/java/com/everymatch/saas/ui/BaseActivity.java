package com.everymatch.saas.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.singeltones.PusherManager;
import com.everymatch.saas.ui.dialog.NetworkErrorMessageDialog;
import com.everymatch.saas.util.EMLog;

import java.io.Serializable;

/**
 * Created by dors on 7/20/15.
 */
public class BaseActivity extends AppCompatActivity {
    public final String TAG = getClass().getName();

    protected DataStore ds = DataStore.getInstance();
    protected DataManager dm = DataManager.getInstance();
    private ProgressDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ds.getIntColor(EMColor.PRIMARY));
        }

        mDialog = new ProgressDialog(BaseActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //register to error receiver
        EMLog.d(TAG, "registering receiver");
        IntentFilter filter = new IntentFilter(NetworkErrorMessageDialog.ACTION_NETWORK_ERROR);
        IntentFilter filterPusher = new IntentFilter(PusherManager.ACTION_PUSHER_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(NetworkErrorReceiver, filter);
        LocalBroadcastManager.getInstance(this).registerReceiver(PusherReceiver, filterPusher);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EMLog.d(TAG, "unregistered receiver");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(NetworkErrorReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(PusherReceiver);

    }

    /**
     * Quick fragment replace transaction
     */
    public void replaceFragment(int containerId, Fragment fragment, String tag) {
        replaceFragment(containerId, fragment, fragment.getClass().getSimpleName(), false, null);
    }

    /**
     * Quick fragment replace transaction
     */
    public void replaceFragment(int containerId, Fragment fragment, String tag, boolean addToBackStack, String backStackTag) {
        replaceFragment(containerId, fragment, tag, addToBackStack, backStackTag, -1, -1, -1, -1);
    }

    /**
     * Quick fragment replace transaction
     */
    public void replaceFragment(int containerId, Fragment fragment, String tag, boolean addToBackStack, String backStackTag, int enter, int exit, int popEnter, int popExit) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (enter != -1 && exit != -1) {
            if (popEnter != -1 && popExit != -1) {
                fragmentTransaction.setCustomAnimations(enter, exit, popEnter, popExit);
            } else {
                fragmentTransaction.setCustomAnimations(enter, exit);
            }
        }

        fragmentTransaction.replace(containerId, fragment, tag);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(backStackTag);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * Quick fragment replace transaction
     */
    public void addFragment(int containerId, Fragment fragment, String tag, boolean addToBackStack, String backStackTag, int enter, int exit, int popEnter, int popExit) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (enter != -1 && exit != -1) {
            if (popEnter != -1 && popExit != -1) {
                fragmentTransaction.setCustomAnimations(enter, exit, popEnter, popExit);
            } else {
                fragmentTransaction.setCustomAnimations(enter, exit);
            }
        }

        fragmentTransaction.add(containerId, fragment, tag);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(backStackTag);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }


    public Fragment findFragment(String tag) {
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    public BroadcastReceiver NetworkErrorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetworkErrorMessageDialog.ACTION_NETWORK_ERROR.equals(intent.getAction())) {
                String message = intent.getStringExtra(NetworkErrorMessageDialog.EXTRA_NETWORK_ERROR_TITLE);
                showErrorDialog(message);
            }
        }
    };


    public BroadcastReceiver PusherReceiver = new BroadcastReceiver() {
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
            }
        }
    };

    protected void handleBroadcast(Serializable data, String eventName) {

    }

    public void showErrorDialog(String message) {
        //start error message
        NetworkErrorMessageDialog.start(getSupportFragmentManager(), message);
    }


    public void showDialog(String title) {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.setTitle(title);
            mDialog.show();
        }
    }

    public void stopDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
