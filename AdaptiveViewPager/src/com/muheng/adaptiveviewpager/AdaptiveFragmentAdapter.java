package com.muheng.adaptiveviewpager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class AdaptiveFragmentAdapter extends PagerAdapter {

    private Context mContext;
    private FragmentTransaction mTransaction;
    private FragmentManager mFragmentManager;
    private FragmentPagerManager mPagerManager;

    public AdaptiveFragmentAdapter(Context context, FragmentManager manager) {
        mContext = context;
        mFragmentManager = manager;
        mPagerManager = FragmentPagerManager.getInstance(mContext);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mTransaction == null) {
            mTransaction = mFragmentManager.beginTransaction();
        }
        mTransaction.detach((Fragment)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mTransaction == null) {
            mTransaction = mFragmentManager.beginTransaction();
        }
        String tag = mPagerManager.getAddedFragmentTag(position);
        Fragment fragment = mFragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            mTransaction.attach(fragment);
        } else {
            fragment = getFragment(position);
            if (fragment != null) {
                mTransaction.add(container.getId(), fragment, tag);
            }
        }
        return fragment;
    }

    public Fragment getFragment(int position) {
        Integer fragId = mPagerManager.getAddedFragmentId(position);
        switch (fragId.intValue()) {
            case FragmentPagerManager.FRAG_PAGE_1: {
                return new PageFragment(position);
            }
            case FragmentPagerManager.FRAG_PAGE_2: {
                return new PageFragment(position);
            }
            case FragmentPagerManager.FRAG_PAGE_3: {
                return new PageFragment(position);
            }
            case FragmentPagerManager.FRAG_PAGE_4: {
                return new PageFragment(position);
            }
            case FragmentPagerManager.FRAG_PAGE_5: {
                return new PageFragment(position);
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return mPagerManager.getAddedFragmentCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (object != null) ? (view == ((Fragment)object).getView()) : false;
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mTransaction != null) {
            mTransaction.commitAllowingStateLoss();
            mTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        int resId = mPagerManager.getAddedFragmentTitleRes(position);
        if (resId > 0) {
            return mContext.getString(resId);
        }
        return null;
    }

}
