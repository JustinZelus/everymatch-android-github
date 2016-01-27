package com.everymatch.saas;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.VolleyHelper;
import com.everymatch.saas.singeltones.Preferences;
import com.everymatch.saas.singeltones.PusherManager;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import io.fabric.sdk.android.Fabric;

//import com.crashlytics.android.Crashlytics;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
//import com.sm.townow.util.TLog;
//
//import io.fabric.sdk.android.Fabric;

/**
 * Created by dor on 20/01/2015.
 */
public class EverymatchApplication extends Application implements ActivityLifecycleCallbacks {

    private static final String TAG = EverymatchApplication.class.getSimpleName();

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        initComponents();
    }

    @Override
    public void onTerminate() {
        Log.i(TAG, "onTerminate");
        super.onTerminate();
    }

    /**
     * Initialize global application components
     */
    private void initComponents() {

        EverymatchApplication.context = getApplicationContext();

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();

        if (BuildConfig.DEBUG) {
            built.setLoggingEnabled(true);
            //Picasso.with(getApplicationContext()).setIndicatorsEnabled(true);
        } else {
            if (!Fabric.isInitialized())
                Fabric.with(this, new Crashlytics());
        }

        Picasso.setSingletonInstance(built);

        // Initialize Singletones
        PusherManager.getInstance();
        DataStore.getInstance();
        Preferences.getInstance();
        DataManager.getInstance();

        VolleyHelper.init(this);
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated " + activity.getLocalClassName());
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.i(TAG, "onActivityStarted " + activity.getLocalClassName());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.i(TAG, "onActivityResumed " + activity.getLocalClassName());
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.i(TAG, "onActivityPaused " + activity.getLocalClassName());
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.i(TAG, "onActivityStopped " + activity.getLocalClassName());
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.i(TAG, "onActivitySaveInstanceState " + activity.getLocalClassName());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.i(TAG, "onActivityDestroyed " + activity.getLocalClassName());
    }

    public static Context getContext() {
        return EverymatchApplication.context;
    }

}
