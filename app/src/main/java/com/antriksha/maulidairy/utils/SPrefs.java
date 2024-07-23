package com.antriksha.maulidairy.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class SPrefs {

    public static final String KEY_PASSWORD = "password";
    static SharedPreferences prefs;
    static SharedPreferences.Editor editor;
    private static final String TAG = "SPrefs";
    public static final String PREF_NAME = "prefs_remember_me";

    public static boolean set(Context c, String key, String value) {
        try {
            prefs = c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            editor = prefs.edit();
            editor.putString(key, value);
            return editor.commit();
        } catch (Exception e) {
            DonorLog.e(TAG, e.getStackTrace().toString());
            return false;
        }
    }

    public static String get(Context c, String key) {
        try {
            prefs = c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String data = prefs.getString(key, null);
            return data;
        } catch (Exception e) {
            DonorLog.e(TAG, e.getStackTrace().toString());
            return null;
        }
    }

    public static void remove(Context c, String key) {
        try {
            editor = c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
            editor.remove(key);
            editor.commit();
        } catch (Exception e) {
            DonorLog.e(TAG, e.getStackTrace().toString());
        }
    }

    public static Boolean getBoolean(Context c, String key) {
        try {
            prefs = c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            Boolean data = prefs.getBoolean(key, false);
            return data;
        } catch (Exception e) {
            DonorLog.e(TAG, e.getStackTrace().toString());
            return false;
        }
    }

    public static Boolean setBoolean(Context c, String key, Boolean value) {
        try {
            prefs = c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            editor = prefs.edit();
            editor.putBoolean(key, value);
            return editor.commit();
        } catch (Exception e) {
            DonorLog.e(TAG, e.getStackTrace().toString());
            return false;
        }
    }
    public static long getLong(Context c, String key) {
        try {
            prefs = c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            long data = prefs.getLong(key, -1);
            return data;
        } catch (Exception e) {
            DonorLog.e(TAG, e.getStackTrace().toString());
            return -1;
        }
    }
    public static boolean setLong(Context c, String key, long value) {
        try {
            prefs = c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            editor = prefs.edit();
            editor.putLong(key, value);
            return editor.commit();
        } catch (Exception e) {
            DonorLog.e(TAG, e.getStackTrace().toString());
            return false;
        }
    }

    public static String getString(Context c, String key) {
        try {
            prefs = c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String data = prefs.getString(key, "");
            return data;
        } catch (Exception e) {
            return "";
        }
    }

    public static String setString(Context c, String key, String value) {
        try {
            prefs = c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            editor = prefs.edit();
            editor.putString(key, value);
            return String.valueOf(editor.commit());
        } catch (Exception e) {
            DonorLog.e(TAG, e.getStackTrace().toString());
            return String.valueOf(false);
        }
    }

    public static Integer getInt(Context c, String key) {
        try {
            prefs = c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            Integer data = prefs.getInt(key, 0);
            return data;
        } catch (Exception e) {
            DonorLog.e(TAG, e.getStackTrace().toString());
            return 0;
        }
    }

    public static boolean setInt(Context c, String key, Integer value) {
        try {
            prefs = c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            editor = prefs.edit();
            editor.putInt(key, value);
            return editor.commit();
        } catch (Exception e) {
            //DonorDonorLog.d(TAG,e.getStackTrace().toString());
            return false;
        }
    }
}
