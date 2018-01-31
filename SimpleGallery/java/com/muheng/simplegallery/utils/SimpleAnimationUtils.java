package com.muheng.simplegallery.utils;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

public class SimpleAnimationUtils {
    private final String TAG = SimpleAnimationUtils.class.getSimpleName();

    private static SimpleAnimationUtils sInstance;

    public static SimpleAnimationUtils getInstance() {
        if (sInstance == null) {
            sInstance = new SimpleAnimationUtils();
        }
        return sInstance;
    }

    private final int ANIMA_DURATION = 350;

    private Animation mFadeIn;
    private Animation mFadeOut;

    private Animation mLeftOut;
    private Animation mLeftIn;
    private Animation mRightOut;
    private Animation mRightIn;

    private SimpleAnimationUtils() {}

    public Animation getFadeInAnimation() {
        if (mFadeIn == null) {
            mFadeIn = new AlphaAnimation(0, 1);
            mFadeIn.setInterpolator(new DecelerateInterpolator());
            mFadeIn.setDuration(ANIMA_DURATION);
        }
        return mFadeIn;
    }

    public Animation getFadeOutAnimation() {
        if (mFadeOut == null) {
            mFadeOut = new AlphaAnimation(1, 0);
            mFadeOut.setInterpolator(new AccelerateInterpolator());
            mFadeOut.setDuration(ANIMA_DURATION);
        }
        return mFadeOut;
    }

    public Animation getLeftOutAnimation() {
        if (mLeftOut == null) {
            mLeftOut = new TranslateAnimation(DimenUtils.dpToPx(0), -DimenUtils.getScreenWidthPx(), 0, 0);
            mLeftOut.setInterpolator(new DecelerateInterpolator());
            mLeftOut.setDuration(ANIMA_DURATION * 3);
        }
        return mLeftOut;
    }

    public Animation getLeftInAnimation() {
        if (mLeftIn == null) {
            mLeftIn = new TranslateAnimation(DimenUtils.getScreenWidthPx(), DimenUtils.dpToPx(0), 0, 0);
            mLeftIn.setInterpolator(new DecelerateInterpolator());
            mLeftIn.setDuration(ANIMA_DURATION * 3);
        }
        return mLeftIn;
    }

    public Animation getRightOutAnimation() {
        if (mRightOut == null) {
            mRightOut = new TranslateAnimation(DimenUtils.dpToPx(0), DimenUtils.dpToPx(640), 0, 0);
            mRightOut.setInterpolator(new DecelerateInterpolator());
            mRightOut.setDuration(ANIMA_DURATION * 3);
        }
        return mRightOut;
    }

    public Animation getRightInAnimation() {
        if (mRightIn == null) {
            mRightIn = new TranslateAnimation(DimenUtils.dpToPx(-640), DimenUtils.dpToPx(0), 0, 0);
            mRightIn.setInterpolator(new DecelerateInterpolator());
            mRightIn.setDuration(ANIMA_DURATION * 3);
        }
        return mRightIn;
    }

    public static void applyAnimation(View view, Animation animation) {
        if (view != null) {
            animation.reset();
            view.clearAnimation();
            view.startAnimation(animation);
        }
    }

    public static void applyAnimation(View view, Animation animation, AnimationListener listener) {
        if (view != null) {
            animation.reset();
            animation.setAnimationListener(listener);
            view.clearAnimation();
            view.startAnimation(animation);
        }
    }
}
