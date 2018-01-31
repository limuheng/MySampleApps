package com.muheng.simplegallery;

import java.io.File;

import com.muheng.simplegallery.imagemodel.DeviceImageLoader;
import com.muheng.simplegallery.utils.BitmapUtils;
import com.muheng.simplegallery.utils.Constants;
import com.muheng.simplegallery.utils.SimpleAnimationUtils;
import com.muheng.simplegallery.utils.ToastUtils;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;

public class ImageFullViewFragment extends SimpleGalleryFragment {
    public static final String TAG = ImageFullViewFragment.class.getSimpleName();

    private int mCursorIdx;
    private Uri mImgUri;

    private ImageView mImgView;

    private Cursor mCursor;

    private LoaderManager.LoaderCallbacks<Cursor> mImageLoaderCallback =
            new LoaderManager.LoaderCallbacks<Cursor> () {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
            return new DeviceImageLoader(getActivity());
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (cursor != null) {
                if (mCursor != null) {
                    try {
                        mCursor.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        mCursor = null;
                    }
                }
                mCursor = cursor;
                navigateImg(0);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) { }
    };

    public ImageFullViewFragment() {
        mCursorIdx = 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(Constants.LOADER_DEVICE_IMAGE, null, mImageLoaderCallback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image_full_view, container, false);
        mImgView = (ImageView) rootView.findViewById(R.id.img_view);

        mGestureDetector = new GestureDetector(getActivity(), mGestureListener);
        mImgView.setOnTouchListener(mOnTouchListener);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        startImageLoader();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        getLoaderManager().destroyLoader(Constants.LOADER_DEVICE_IMAGE);
        super.onDestroy();
    }

    public void setImageUri(Uri imgUri) {
        if (imgUri != null) {
            mImgUri = imgUri;
            reloadImage();
        }
    }

    public void reloadImage() {
        if (mImgView != null) {
            Bitmap bitmap = BitmapUtils.downSamplingBitmap(mImgUri.getPath(), 4);
            BitmapUtils.setImageViewBitmap(mImgView, bitmap);
        }
    }

    private void navigateImg(int move) {
        if (mCursor != null) {
            mCursorIdx += move;
            if (mCursorIdx < 0) {
                mCursorIdx = 0;
                ToastUtils.getInstance().showToast(getActivity(), getActivity().getString(R.string.left_limit));
            } else if (mCursorIdx >= mCursor.getCount()) {
                mCursorIdx = mCursor.getCount() - 1;
                ToastUtils.getInstance().showToast(getActivity(), getActivity().getString(R.string.right_limit));
            } else {
                if (move == 0) {
                    setImage();
                } else {
                    SimpleAnimationUtils.applyAnimation(mImgView, SimpleAnimationUtils.getInstance().getFadeOutAnimation(), mAnimListener);
                }
            }
        }
    }

    private Animation.AnimationListener mAnimListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) { }

        @Override
        public void onAnimationEnd(Animation animation) {
            setImage();
            animation.setAnimationListener(null);
        }

        @Override
        public void onAnimationRepeat(Animation animation) { }
    };

    private void setImage() {
        try {
            mCursor.moveToPosition(mCursorIdx);
            //Log.d(TAG, "mCursor pos/count: " + mCursor.getPosition() + "/" + mCursor.getCount());
            mImgUri = Uri.fromFile(new File(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA))));
            setImageUri(mImgUri);
            //getActivity().setTitle(mImgUri.getLastPathSegment());
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startImageLoader() {
        getLoaderManager().restartLoader(Constants.LOADER_DEVICE_IMAGE, null, mImageLoaderCallback);
    }

    @Override
    public boolean onBackPressed() {
        ((SimpleGalleryActivity)getActivity()).switchFragment(SimpleGalleryActivity.FRAG_IDX_GALLERY);
        return true;
    }

    @Override
    public void setCursorIdx(int idx) {
        if (idx >= 0) {
            mCursorIdx = idx;
        } else {
            mCursorIdx = 0;
        }
        navigateImg(0);
    }

    @Override
    public int getCursorIdx() {
        return mCursorIdx;
    }

    private GestureDetector mGestureDetector;
    private float mLastStartX;
    private GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float moveDistance = e2.getX() - e1.getX();
            if (moveDistance > Constants.SWIPE_THRESHOLD) {
                if (mLastStartX == e1.getX()) {
                    return true;
                }
                mLastStartX = e1.getX();
                navigateImg(-1);
            } else if (moveDistance < -Constants.SWIPE_THRESHOLD) {
                if (mLastStartX == e1.getX()) {
                    return true;
                }
                mLastStartX = e1.getX();
                navigateImg(1);
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    };

    View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mGestureDetector != null) {
                return mGestureDetector.onTouchEvent(event);
            }
            return false;
        }
    };
}
