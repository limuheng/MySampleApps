package com.muheng.languageselectionapp;

import java.util.Locale;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;

public class LocaleUtils {
    public static final int LOCAE_ENG = 0;
    public static final int LOCAE_CHT = 1;
    public static final int LOCAE_CHS = 2;

    private static SparseArray<Locale> sLocaleMap = new SparseArray<Locale> ();

    static {
        sLocaleMap.put(LOCAE_ENG, Locale.ENGLISH);
        sLocaleMap.put(LOCAE_CHT, Locale.TRADITIONAL_CHINESE);
        sLocaleMap.put(LOCAE_CHS, Locale.SIMPLIFIED_CHINESE);
    }

    public static Locale getLocale(int setting) {
        Locale locale = sLocaleMap.get(setting);
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        return locale;
    }

    public static ContextWrapper createContextWrapper(Context context, Locale lang) {
        Locale.setDefault(lang);
        Configuration config = context.getResources().getConfiguration();
        if (context != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(lang);
                context = context.createConfigurationContext(config);
            } else {
                config.locale = lang;
                Resources res = context.getResources();
                res.updateConfiguration(config, res.getDisplayMetrics());
            }
        }
        return new ContextWrapper(context);
    }
}
