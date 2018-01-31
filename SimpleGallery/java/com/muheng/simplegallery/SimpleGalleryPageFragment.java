package com.muheng.simplegallery;

import java.util.List;

import com.muheng.simplegallery.imagemodel.DeviceImageLoader;
import com.muheng.simplegallery.imagemodel.ImageItem;
import com.muheng.simplegallery.interfaces.ICursorPosition;
import com.muheng.simplegallery.pageview.GalleryPageAdapter;
import com.muheng.simplegallery.pageview.GalleryPageManager;
import com.muheng.simplegallery.utils.BitmapUtils;
import com.muheng.simplegallery.utils.BlurUtils;
import com.muheng.simplegallery.utils.Constants;
import com.muheng.simplegallery.utils.DimenUtils;
import com.muheng.simplegallery.utils.SimpleAnimationUtils;
import com.muheng.simplegallery.utils.SimpleKeyCode;
import com.muheng.simplegallery.utils.ToastUtils;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

public class SimpleGalleryPageFragment extends SimpleGalleryFragment implements ICursorPosition {
    public static final String TAG = SimpleGalleryPageFragment.class.getSimpleName();

    private GalleryPageManager mPageManager;

    private Cursor mCursor;
    private GalleryPageAdapter[] mAdapters = new GalleryPageAdapter[GalleryPageManager.PAGE_SIZE];
    private GridView[] mGridViews = new GridView[GalleryPageManager.PAGE_SIZE];

    private FrameLayout mFloatingPreview;

    private boolean mIsScrolling = false;

    // Portrait mode default
    private int mGridViewNumColumns = COLUMNS_2;
    private int mGridViewNumRows = ROWS_4;

    private LoaderManager.LoaderCallbacks<Cursor> mImageLoaderCallback =
            new LoaderManager.LoaderCallbacks<Cursor> () {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
            return new DeviceImageLoader(getActivity());
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (cursor != null) {
                mCursor = cursor;
                mCursor.moveToFirst();
                mPageManager.setTotalItemCount(mCursor.getCount());
                bindPages();
                mPageManager.updateVisibility(mGridViews);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) { }
    };

    private OnItemClickListener mItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (parent instanceof GridView) {
                try {
                    GalleryPageAdapter adapter = (GalleryPageAdapter) ((GridView)parent).getAdapter();
                    if (adapter != null) {
                        int oldPos = adapter.getSelectedIdx();
                        if (oldPos >= 0) {
                            adapter.setSelectedIdx(position);
                            mPageManager.setCurrentItemPosition((position - oldPos));
                            showPreview();
                        }
                    }
                    adapter.notifyDataSetChanged();
                    parent.setSelection(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private AdapterView.OnItemLongClickListener mItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            showImageFullView();
            return true;
        }
    };

    private GalleryPageManager.IPageScrollListener mPageScrollListener = new GalleryPageManager.IPageScrollListener() {
        @Override
        public void onScrollToRight(boolean isScrolled) {
            if (isScrolled) {
                swipeToNextPage();
            }
        }

        @Override
        public void onScrollToLeft(boolean isScrolled) {
            if (isScrolled) {
                swipeToPrevPage();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
//        setHasOptionsMenu(true);
        getLoaderManager().initLoader(Constants.LOADER_DEVICE_IMAGE, null, mImageLoaderCallback);
        mPageManager = GalleryPageManager.getInstance();
        mPageManager.setPageScrollListener(mPageScrollListener);
        startImageLoader();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        //getActivity().setTitle(R.string.app_name);
        // Fix press back and return to APP gets a blank activity issue
        mPageManager.updateVisibility(mGridViews);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        getLoaderManager().destroyLoader(Constants.LOADER_DEVICE_IMAGE);
        releaseBitmap(mActivityBg);
        GalleryPageManager.releaseInstance();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_navigation_gallery_page, container, false);

        mGridViews[0] = (GridView) rootView.findViewById(R.id.list_prev);
        mGridViews[1] = (GridView) rootView.findViewById(R.id.list);
        mGridViews[2] = (GridView) rootView.findViewById(R.id.list_next);

        mGestureDetector = new GestureDetector(getActivity(), mGestureListener);
        for (int i = 0; i < mGridViews.length; i++) {
            mGridViews[i].setOnTouchListener(mOnTouchListener);
        }

        mFloatingPreview = (FrameLayout) rootView.findViewById(R.id.floating_container);
        mFloatingPreview.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dismissPreview();
                return true;
            }
        });

        for (int i = 0; i < GalleryPageManager.PAGE_SIZE; i++) {
            mAdapters[i] = new GalleryPageAdapter(getActivity(), inflater);
            mGridViews[i].setAdapter(mAdapters[i]);
        }

        initGridView(mPageManager.getCurBufIdx(), 0);
        initGridView(mPageManager.getPrevBufIdx(), -1);
        initGridView(mPageManager.getNextBufIdx(), -1);

        int screenWidthDp = DimenUtils.getScreenWidthDp();
        int screenHeightDp = DimenUtils.getScreenHeightDp();
        adjustGridViewRowNum(screenWidthDp, screenHeightDp);

        return rootView;
    }

    private void initGridView(int index, int pos) {
        if (index >= 0 && index < mGridViews.length) {
            mGridViews[index].setSelection(pos);
            mGridViews[index].setOnItemClickListener(mItemClickListener);
            mGridViews[index].setOnItemLongClickListener(mItemLongClickListener);
        }
    }

    private void startImageLoader() {
        getLoaderManager().restartLoader(Constants.LOADER_DEVICE_IMAGE, null, mImageLoaderCallback);
    }

    private void swipeToNextPage() {
        mIsScrolling = true;
        mAdapters[mPageManager.getCurBufIdx()].setSelectedIdx(-1);
        mAdapters[mPageManager.getCurBufIdx()].notifyDataSetChanged();
        SimpleAnimationUtils.applyAnimation(mGridViews[mPageManager.getCurBufIdx()], SimpleAnimationUtils.getInstance().getLeftOutAnimation());
        mGridViews[mPageManager.getNextBufIdx()].setVisibility(View.VISIBLE);
        SimpleAnimationUtils.applyAnimation(mGridViews[mPageManager.getNextBufIdx()], SimpleAnimationUtils.getInstance().getLeftInAnimation(), mLeftInListener);
    }

    private void swipeToPrevPage() {
        mIsScrolling = true;
        mAdapters[mPageManager.getCurBufIdx()].setSelectedIdx(-1);
        mAdapters[mPageManager.getCurBufIdx()].notifyDataSetChanged();
        SimpleAnimationUtils.applyAnimation(mGridViews[mPageManager.getCurBufIdx()], SimpleAnimationUtils.getInstance().getRightOutAnimation());
        mGridViews[mPageManager.getPrevBufIdx()].setVisibility(View.VISIBLE);
        SimpleAnimationUtils.applyAnimation(mGridViews[mPageManager.getPrevBufIdx()], SimpleAnimationUtils.getInstance().getRightInAnimation(), mRightInListener);
    }

    @Override
    public boolean onBackPressed() {
        if (mFloatingPreview != null && mFloatingPreview.getVisibility() == View.VISIBLE) {
            dismissPreview();
            return true;
        }
        return false;
    }

    public Bitmap mActivityBg;

    private void releaseBitmap(Bitmap bmp) {
        if (bmp != null) {
            bmp.recycle();
        }
    }

    private void showPreview() {
        if (mFloatingPreview == null) {
            return ;
        }

        ImageView preview = (ImageView) mFloatingPreview.findViewById(R.id.floating_preview);

        if (preview != null && mCursor != null) {
            mCursor.moveToPosition(mPageManager.getCurrentItemPosition());
            String filePath = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            Bitmap bitmap = BitmapUtils.downSamplingBitmap(filePath, 8);
            BitmapUtils.setImageViewBitmap(preview, bitmap);
        }

        if (mActivityBg != null) {
            mActivityBg.recycle();
        }
        mActivityBg = BlurUtils.getScreenshot(getView());
        if (mActivityBg != null) {
            Bitmap background = BlurUtils.blur(getActivity(), mActivityBg);
            BitmapDrawable drawableBmp = new BitmapDrawable(getResources(), background);
            mFloatingPreview.setBackground(drawableBmp);
        }

        SimpleAnimationUtils.applyAnimation(mFloatingPreview, SimpleAnimationUtils.getInstance().getFadeInAnimation());
        mFloatingPreview.setVisibility(View.VISIBLE);
    }

    private void dismissPreview() {
        if (mFloatingPreview.getVisibility() == View.VISIBLE) {
            SimpleAnimationUtils.applyAnimation(mFloatingPreview, SimpleAnimationUtils.getInstance().getFadeOutAnimation());
            mFloatingPreview.setVisibility(View.GONE);
        }
    }

    private void showImageFullView() {
        if (mCursor != null) {
            SimpleGalleryFragment fragment = ((SimpleGalleryActivity)getActivity()).getFragment(SimpleGalleryActivity.FRAG_IDX_FULLVIEW);
            // We must call setCursorIdx before start animation to prevent image fresh in full view
            fragment.setCursorIdx(mPageManager.getCurrentItemPosition());
            ((SimpleGalleryActivity)getActivity()).switchFragment(SimpleGalleryActivity.FRAG_IDX_FULLVIEW);
        }
    }

    private int mSingleItemHeight;
    private int mVerticalMargin;
    // For landscape mode
    private int adjustGridViewRowNum(int screenWidthDp, int screenHeightDp) {
        if (screenHeightDp <= SD_HEIGHT) {
            mGridViewNumRows = ROWS_3;
        } else if (screenHeightDp < HD_HEIGHT) {
            mGridViewNumRows = ROWS_4;
        } else {
            mGridViewNumRows = ROWS_5;
        }

        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing) * (mGridViewNumRows - 1);  // space between two rows
        mVerticalMargin = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin) * 2;  // top and bottom margin of this activity
        int gridViewDpWSize = (screenWidthDp - DimenUtils.pxToDp(mVerticalMargin));
        int gridViewDpHSize = (screenHeightDp - DimenUtils.pxToDp(spacing) - DimenUtils.pxToDp(mVerticalMargin));

        mSingleItemHeight = gridViewDpHSize / mGridViewNumRows;
        mGridViewNumColumns = gridViewDpWSize / (mSingleItemHeight + DimenUtils.pxToDp(spacing));

        mPageManager.setPageItemCount(mGridViewNumRows * mGridViewNumColumns);

        for (int i = 0; i < GalleryPageManager.PAGE_SIZE; i++) {
            mAdapters[i].setNumColumns(mGridViewNumColumns);
            mGridViews[i].setNumColumns(mGridViewNumColumns);
            mAdapters[i].setItemSize(mSingleItemHeight, mSingleItemHeight);
            mAdapters[i].notifyDataSetChanged();
        }

        return mGridViewNumRows;
    }

    private static final int SD_WIDTH = 640;  // 720p for 5" screen
    private static final int HD_WIDTH = 960;  // 1080p for 5" screen
    private static final int SD_HEIGHT = 360;  // 720p for 5" screen
    private static final int HD_HEIGHT = 540;  // 1080p for 5" screen
    private static final int ROWS_3 = 3;
    private static final int ROWS_4 = 4;
    private static final int ROWS_5 = 5;
    private static final int COLUMNS_2 = 2;
    private static final int COLUMNS_3 = 3;
    private static final int COLUMNS_5 = 5;

    // Keep the current selection, once back from ImageFullView, restore the selection by this value
    @Override
    public void setCursorIdx(int idx) {
        if (mCursor != null && (idx >= 0 && idx < mCursor.getCount())) {
            mPageManager.move(idx - mPageManager.getCurrentItemPosition());

            mAdapters[mPageManager.getCurBufIdx()].setSelectedIdx((mPageManager.getCurrentItemPosition() % mPageManager.getPageItemCount()));
            mAdapters[mPageManager.getCurBufIdx()].notifyDataSetChanged();

            mAdapters[mPageManager.getPrevBufIdx()].setSelectedIdx(-1);
            mAdapters[mPageManager.getPrevBufIdx()].notifyDataSetChanged();

            mAdapters[mPageManager.getNextBufIdx()].setSelectedIdx(-1);
            mAdapters[mPageManager.getNextBufIdx()].notifyDataSetChanged();
        }
        // Just in case that floating window is not closed
        mFloatingPreview.setVisibility(View.GONE);
    }

    @Override
    public int getCursorIdx() {
        if (mPageManager != null) {
            return mPageManager.getCurrentItemPosition();
        }
        return -1;
    }

    private boolean bindPages() {
        if (mCursor == null || mCursor.isClosed()) {
            Log.e(TAG, "mCursor is null! [bindPages]");
            return false;
        }

        int currntPageIdx = mPageManager.getCurrentPageidx();

        if (bindPage(mCursor, currntPageIdx, mAdapters[mPageManager.getCurBufIdx()].getData())) {
            mAdapters[mPageManager.getCurBufIdx()].setSelectedIdx((mPageManager.getCurrentItemPosition() % mPageManager.getPageItemCount()));
            mAdapters[mPageManager.getCurBufIdx()].notifyDataSetChanged();
        }

        if (mPageManager.hasPreviousPage()) {
            if (bindPage(mCursor, mPageManager.getPrevPageIdx(), mAdapters[mPageManager.getPrevBufIdx()].getData())) {
                mAdapters[mPageManager.getPrevBufIdx()].setSelectedIdx(-1);
                mAdapters[mPageManager.getPrevBufIdx()].notifyDataSetChanged();
            }
        }

        if (mPageManager.hasNextPage()) {
            if (bindPage(mCursor, mPageManager.getNextPageIdx(), mAdapters[mPageManager.getNextBufIdx()].getData())) {
                mAdapters[mPageManager.getNextBufIdx()].setSelectedIdx(-1);
                mAdapters[mPageManager.getNextBufIdx()].notifyDataSetChanged();
            }
        }
        return true;
    }

    private boolean bindPage(Cursor cursor, int pageIdx, List<ImageItem> list) {
        if (list == null) {
            Log.e(TAG, "list is null or empty! [bindPage]");
            return false;
        }

        int firstitem = mPageManager.getPageFirstItemPos(pageIdx);
        if (cursor.moveToPosition(firstitem)) {
            list.clear();
            for (int i = 0; i < mPageManager.getPageItemCount(); i++) {
                ImageItem item = new ImageItem();
                item.setData(cursor);
                list.add(item);
                if (!cursor.moveToNext()) {
                    break;
                }
            }
        }
        return true;
    }

    private AnimationListener mLeftInListener = new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {
            mPageManager.switchBufferIdx(SimpleKeyCode.RIGHT);
            mPageManager.updateVisibility(mGridViews);
            bindPages();
            //mAdapters[mPageManager.getCurBufIdx()].setSelectedIdx(0);
            //mAdapters[mPageManager.getCurBufIdx()].notifyDataSetChanged();
            mIsScrolling = false;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {}
    };

    private AnimationListener mRightInListener = new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {
            mPageManager.switchBufferIdx(SimpleKeyCode.LEFT);
            mPageManager.updateVisibility(mGridViews);
            bindPages();
            //mAdapters[mPageManager.getCurBufIdx()].setSelectedIdx(mPageManager.getPageItemCount() - 1);
            //mAdapters[mPageManager.getCurBufIdx()].notifyDataSetChanged();
            mIsScrolling = false;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {}
    };

    private GestureDetector mGestureDetector;

    private float mLastStartX;
    private SimpleOnGestureListener mGestureListener = new SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float moveDistance = e2.getX() - e1.getX();
            if (moveDistance > Constants.SWIPE_THRESHOLD) {
                if (mLastStartX == e1.getX()) {
                    return true;
                }
                mLastStartX = e1.getX();
                if (!mIsScrolling) {
                    if (mPageManager.hasPreviousPage()) {
                        int newPos = mPageManager.getPageLastItemPos(mPageManager.getPrevPageIdx());
                        mPageManager.move(newPos - mPageManager.getCurrentItemPosition());
                    } else {
                        ToastUtils.getInstance().showToast(getActivity(), getString(R.string.left_limit));
                    }
                } else {
                    Log.d(TAG, "[SimpleOnGestureListener] Gallery is scrolling, discard scrolling left!");
                }
            } else if (moveDistance < -Constants.SWIPE_THRESHOLD) {
                if (mLastStartX == e1.getX()) {
                    return true;
                }
                mLastStartX = e1.getX();
                if (!mIsScrolling) {
                    if (mPageManager.hasNextPage()) {
                        int newPos = mPageManager.getPageFirstItemPos(mPageManager.getNextPageIdx());
                        mPageManager.move(newPos - mPageManager.getCurrentItemPosition());
                    } else {
                        ToastUtils.getInstance().showToast(getActivity(), getString(R.string.right_limit));
                    }
                } else {
                    Log.d(TAG, "[SimpleOnGestureListener] Gallery is scrolling, discard scrolling right!");
                }
            }
            return true;
        }
    };

    OnTouchListener mOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mGestureDetector != null) {
                return mGestureDetector.onTouchEvent(event);
            }
            return false;
        }
    };
}
