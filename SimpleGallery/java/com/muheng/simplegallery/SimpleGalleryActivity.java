package com.muheng.simplegallery;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;

import com.muheng.simplegallery.imagedecoder.ImageDecoderManager;
import com.muheng.simplegallery.interfaces.ICursorPosition;
import com.muheng.simplegallery.utils.SimpleAnimationUtils;

import android.os.Bundle;

public class SimpleGalleryActivity extends AppCompatActivity {
    private final String TAG = SimpleGalleryActivity.class.getSimpleName();

    public static final int FRAG_COUNT = 2;

    public static final int FRAG_IDX_GALLERY = 0;
    public static final int FRAG_IDX_FULLVIEW = 1;

    private SimpleGalleryFragment[] mFrgaments = new SimpleGalleryFragment[FRAG_COUNT];

    private FrameLayout mGalleryContainer;
    private FrameLayout mFullViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_layout);

        mGalleryContainer = (FrameLayout) findViewById(R.id.gallery_fragment_container);
        mFullViewContainer = (FrameLayout) findViewById(R.id.fullview_fragment_container);

        mFrgaments[FRAG_IDX_GALLERY] = new SimpleGalleryPageFragment();
        getFragmentManager().beginTransaction().replace(
                R.id.gallery_fragment_container, mFrgaments[FRAG_IDX_GALLERY], SimpleGalleryPageFragment.TAG).commit();

        mFrgaments[FRAG_IDX_FULLVIEW] = new ImageFullViewFragment();
        getFragmentManager().beginTransaction().replace(
                R.id.fullview_fragment_container, mFrgaments[FRAG_IDX_FULLVIEW], ImageFullViewFragment.TAG).commit();

        // Use full screen style, so we don't need to hide action bar manually
        //getSupportActionBar().hide();
    }

    @Override
    protected void onPause() {
        ImageDecoderManager.releaseInstance();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        boolean isHandled = mFrgaments[mFragIndex].onBackPressed();
        if (!isHandled) {
            super.onBackPressed();
        }
    }

    public SimpleGalleryFragment getFragment(int idx) {
        if (idx >= 0 && idx < FRAG_COUNT) {
            return mFrgaments[idx];
        }
        return null;
    }

    private int mFragIndex = FRAG_IDX_GALLERY;
    public void switchFragment(int idx) {
        mFragIndex = idx;
        switch (idx) {
            case FRAG_IDX_GALLERY: {
                SimpleAnimationUtils.applyAnimation(mGalleryContainer, SimpleAnimationUtils.getInstance().getFadeInAnimation(), mAnimListener);
                mGalleryContainer.setVisibility(View.VISIBLE);

                SimpleAnimationUtils.applyAnimation(mFullViewContainer, SimpleAnimationUtils.getInstance().getFadeOutAnimation());
                mFullViewContainer.setVisibility(View.GONE);
                break;
            }
            case FRAG_IDX_FULLVIEW: {
                SimpleAnimationUtils.applyAnimation(mGalleryContainer, SimpleAnimationUtils.getInstance().getFadeOutAnimation());
                mGalleryContainer.setVisibility(View.GONE);

                // We must call setCursorIdx before start animation to prevent image fresh in full view
                SimpleAnimationUtils.applyAnimation(mFullViewContainer, SimpleAnimationUtils.getInstance().getFadeInAnimation());
                mFullViewContainer.setVisibility(View.VISIBLE);
                break;
            }
            default: {
                Log.e(TAG, "Unsupported frgament index: " + idx);
            }
        }
    }

    private AnimationListener mAnimListener = new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) { }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mFrgaments[mFragIndex] instanceof ICursorPosition) {
                int otherIdx = (mFragIndex + 1) % 2;
                int cursorIdx = mFrgaments[otherIdx].getCursorIdx();
                mFrgaments[mFragIndex].setCursorIdx(cursorIdx);
            }
            animation.setAnimationListener(null);
        }

        @Override
        public void onAnimationRepeat(Animation animation) { }
    };
}
