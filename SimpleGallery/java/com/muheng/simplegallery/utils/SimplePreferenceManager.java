package com.muheng.simplegallery.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SimplePreferenceManager {
    private final String TAG = SimplePreferenceManager.class.getSimpleName();

    public static final String KEY_GRID_COLUMNS = "key_grid_columns";

    public static boolean setIntPreference(Context context, String prefName, int prefValue) {
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

    public static int getIntPreference(Context context, String prefName, int defaultValue) {
        int prefValue = -1;
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefValue = prefs.getInt(prefName, defaultValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return prefValue;
    }
}
