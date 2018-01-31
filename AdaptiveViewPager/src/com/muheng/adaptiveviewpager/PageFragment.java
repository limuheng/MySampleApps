package com.muheng.adaptiveviewpager;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class PageFragment extends EntryListFragment {
    public static final String TAG = PageFragment.class.getSimpleName();

    private int mPosition;

    protected OnClickListener mItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            // TODO
            //Toast.makeText(getActivity(), "Item Clicked!", Toast.LENGTH_SHORT).show();
        }
    };

    public PageFragment() {
        mPosition = 0;
    }

    public PageFragment(int pos) {
        mPosition = pos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mAdapter = new EntryListAdapter(getActivity(), inflater);
        mAdapter.setItemClickListener(mItemClickListener);
        mListView.setAdapter(mAdapter);
        initAdapter();
        return rootView;
    }

    @Override
    protected void initAdapter() {
        if (mAdapter != null) {
            List<Integer> list = mAdapter.getData();
            list.clear();
            for (int i = 0; i < (mPosition + 1); i++) {
                if (i < FragmentPagerManager.ITEMS.length) {
                    list.add(FragmentPagerManager.ITEMS[i]);
                } else {
                    break;
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }
}
