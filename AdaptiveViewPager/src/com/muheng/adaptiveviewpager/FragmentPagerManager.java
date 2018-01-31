package com.muheng.adaptiveviewpager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseIntArray;

public class FragmentPagerManager {

    public static final String NOT_DEFINED = "not_defined";
    public static final int INT_NOT_DEFINED = -1;

    public static final int FRAG_COUNT = 5;
    public static final int FRAG_PAGE_1 = 0;
    public static final int FRAG_PAGE_2 = 1;
    public static final int FRAG_PAGE_3 = 2;
    public static final int FRAG_PAGE_4 = 3;
    public static final int FRAG_PAGE_5 = 4;

    public static final int[] ITEMS = {
            R.string.item1, R.string.item2, R.string.item3, R.string.item4, R.string.item5,
        };

    private static FragmentPagerManager sInstance;

    public static FragmentPagerManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new FragmentPagerManager(context);
        }
        return sInstance;
    }

    private FragmentPagerManager(Context context) {
        init(context);
    }

    private int[] mFragmentIds = {
        FRAG_PAGE_1, FRAG_PAGE_2, FRAG_PAGE_3, FRAG_PAGE_4, FRAG_PAGE_5,
    };

    private int[] mFragmentTitleResIds = {
        R.string.page1, R.string.page2, R.string.page3, R.string.page4, R.string.page5,
    };

    private SparseArray<String> mFragIdTagMap = new SparseArray<String> ();
    private SparseIntArray mFragIdTitleResMap = new SparseIntArray();
    private List<Integer> mFragAddedList = new ArrayList<Integer> ();

    public String getFragmentTag(int id) {
        return mFragIdTagMap.get(id, NOT_DEFINED);
    }

    private boolean init(Context context) {
        if (context == null) {
            return false;
        }
        for (int i = 0; i < FRAG_COUNT; i++) {
            mFragIdTitleResMap.put(mFragmentIds[i], mFragmentTitleResIds[i]);
            mFragIdTagMap.put(mFragmentIds[i], context.getString(mFragmentTitleResIds[i]));
            // Add all fragments by default
            mFragAddedList.add(mFragmentIds[i]);
        }
        return true;
    }

    public void addFragment(int id) {
        if (mFragAddedList.size() == FRAG_COUNT) {
            return ;
        }
        try {
            boolean isNew = true;
            for (Integer i : mFragAddedList) {
                if (i.intValue() == id) {
                    isNew = false;
                    break;
                }
            }
            if (isNew) {
                mFragAddedList.add(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeFragment(int id) {
        if (mFragAddedList.size() == 0) {
            return ;
        }
        try {
            for (Integer i : mFragAddedList) {
                if (i.intValue() == id) {
                    mFragAddedList.remove(i);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearAddedFragmentList() {
        mFragAddedList.clear();
    }

    public String getAddedFragmentTag(int pos) {
        Integer fragId = mFragAddedList.get(pos);
        return mFragIdTagMap.get(fragId, NOT_DEFINED);
    }

    public int getAddedFragmentTitleRes(int pos) {
        if (pos < 0 || pos > mFragAddedList.size()) {
            return -1;
        }
        Integer fragId = mFragAddedList.get(pos);
        return mFragIdTitleResMap.get(fragId, INT_NOT_DEFINED);
    }

    public Integer getAddedFragmentId(int pos) {
        return mFragAddedList.get(pos);
    }

    public int getAddedFragmentCount() {
        return mFragAddedList.size();
    }

    public boolean isFragmentAdded(int id) {
        for (Integer i : mFragAddedList) {
            if (i.intValue() == id) {
                return true;
            }
        }
        return false;
    }
}
