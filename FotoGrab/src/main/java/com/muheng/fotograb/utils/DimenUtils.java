package com.muheng.fotograb.utils;

/**
 * Reference web page:
 * 1. http://stackoverflow.com/questions/4743116/get-screen-width-and-height
 * 2. http://stackoverflow.com/questions/8295986/how-to-calculate-dp-from-pixels-in-android-programmatically
 */

import android.content.res.Resources;

public class DimenUtils {

    public static int getScreenWidthPx() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeightPx() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidthDp() {
        return pxToDp(getScreenWidthPx());
    }

    public static int getScreenHeightDp() {
        return pxToDp(getScreenHeightPx());
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

}
