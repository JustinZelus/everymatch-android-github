package com.everymatch.saas.util;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.everymatch.saas.BuildConfig;

/**
 * Created by Dor on 18/12/2014.
 */
public class EMLog {

    static final String TAG = "EveryMatchAndroid";

    public static void d(String classTag, String msg) {

        final String message = "[" + classTag + "]: " + msg;

        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        } else {
            Crashlytics.log(message);
        }
    }

    public static void d(String classTag, String msg, Exception e) {
        final String message = "[" + classTag + "]: " + msg;

        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        } else {
            Crashlytics.log(message);
        }
    }

    public static void e(String classTag, String msg, Exception e) {

        final String message = "[" + classTag + "]: " + msg;

        if (BuildConfig.DEBUG) {
            Log.e(TAG, message);
        } else {
            Crashlytics.log(message);
        }
    }

    public static void e(String classTag, String msg) {

        final String message = "[" + classTag + "]: " + msg;

        if (BuildConfig.DEBUG) {
            Log.e(TAG, message);
        } else {
            Crashlytics.log(message);
        }
    }

    public static void i(String classTag, String msg) {

        final String message = "[" + classTag + "]: " + msg;

        if (BuildConfig.DEBUG) {
            Log.i(TAG, message);
        } else {
            Crashlytics.log(message);
        }
    }

    public static void w(String classTag, String msg) {

        final String message = "[" + classTag + "]: " + msg;

        if (BuildConfig.DEBUG) {
            Log.w(TAG, message);
        } else {
            Crashlytics.log(message);
        }
    }

    public static void w(String classTag, String msg, Exception e) {

        final String message = "[" + classTag + "]: " + msg;

        if (BuildConfig.DEBUG) {
            Log.w(TAG, message);
        } else {
            Crashlytics.log(message);
        }
    }
}