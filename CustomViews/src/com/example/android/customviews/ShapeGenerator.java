package com.example.android.customviews;

import android.content.Context;

import com.example.android.customviews.views.CustomShapeDrawable;

public class ShapeGenerator {

    public static final int[] PREDEF_COLORS = {
        R.color.seafoam, R.color.dark_mistyrose, R.color.ivory, R.color.cornsilk,
        R.color.dark_cornsilk, R.color.darkgray, R.color.darkslateblue, R.color.teal,
        R.color.mediumspringgreen, R.color.lightslategray, R.color.powderblue, R.color.slateblue,
        R.color.firebrick, R.color.lightgray, R.color.tomato,
    };

    public static CustomShapeDrawable genOvalShape(Context context) {
        int colorIndex = (int) (Math.random() * (PREDEF_COLORS.length));

        CustomShapeDrawable drawable = new CustomShapeDrawable();
        // If the color isn't set, the shape uses black as the default.
        drawable.getPaint().setColor(context.getResources().getColor(PREDEF_COLORS[colorIndex]));

        return drawable;
    }

}
