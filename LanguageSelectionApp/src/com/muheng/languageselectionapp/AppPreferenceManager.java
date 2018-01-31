package com.muheng.languageselectionapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class AppPreferenceManager {
    private final String TAG = AppPreferenceManager.class.getSimpleName();

    public static final String KEY_INT_LANGUAGE_SETTING = "key_int_language_setting";

    public static boolean putBool(Context context, String prefName, boolean prefValue) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Editor editor = prefs.edit();
            editor.putBoolean(prefName, prefValue);
            editor.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean getBool(Context context, String prefName, boolean defaultValue) {
        boolean prefValue = false;
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefValue = prefs.getBoolean(prefName, defaultValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return prefValue;
    }

    public static boolean putInt(Context context, String prefName, int prefValue) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Editor editor = prefs.edit();
            editor.putInt(prefName, prefValue);
            editor.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public static int getInt(Context context, String prefName, int defaultValue) {
        int prefValue = -1;
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefValue = prefs.getInt(prefName, defaultValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return prefValue;
    }

    public static boolean putString(Context context, String prefName, String prefValue) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Editor editor = prefs.edit();
            editor.putString(prefName, prefValue);
            editor.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public static String getString(Context context, String prefName, String defaultValue) {
        String prefValue = null;;
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefValue = prefs.getString(prefName, defaultValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return prefValue;
    }
}
