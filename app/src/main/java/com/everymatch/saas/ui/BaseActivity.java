package com.everymatch.saas.ui;

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
import com.everymatch.saas.ui.dialog.NetworkErrorMessageDialog;
import com.everymatch.saas.util.EMLog;

/**
 * Created by dors on 7/20/15.
 */
public class BaseActivity extends AppCompatActivity {
    public final String TAG = getClass().getName();

    protected DataStore ds = DataStore.getInstance();
    protected DataManager dm = DataManager.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ds.getIntColor(EMColor.PRIMARY));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //register to error receiver
        EMLog.d(TAG, "registering receiver");
        IntentFilter filter = new IntentFilter(NetworkErrorMessageDialog.ACTION_NETWORK_ERROR);
        LocalBroadcastManager.getInstance(this).registerReceiver(NetworkErrorReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EMLog.d(TAG, "unregistered receiver");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(NetworkErrorReceiver);

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

    public void showErrorDialog(String message) {
        //start error message
        NetworkErrorMessageDialog.start(getSupportFragmentManager(), message);
    }

}
