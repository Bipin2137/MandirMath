package com.antriksha.maulidairy.utils;

import android.util.Log;

import com.antriksha.maulidairy.BuildConfig;

public class DonorLog {

    private static final String TAG = "DonorLog";

    public static void d(String tag, String value) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, value);
        }
    }

    public static void d(String value) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, value);
        }
    }

    public static void e(String tag, String value) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, value);
        }
    }

    public static void w(String tag, String value) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, value);
        }
    }

    public static void i(String tag, String value) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, value);
        }
    }
}
