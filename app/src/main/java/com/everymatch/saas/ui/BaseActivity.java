package com.everymatch.saas.ui;

import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;

/**
 * Created by dors on 7/20/15.
 */
public class BaseActivity extends AppCompatActivity {

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

    public Fragment findFragment(String tag) {
        return getSupportFragmentManager().findFragmentByTag(tag);
    }
}
