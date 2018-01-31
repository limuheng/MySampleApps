package com.muheng.adaptiveviewpager;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class EntryListFragment extends Fragment {

    protected ListView mListView;

    protected EntryListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.entry_list_fragment, container, false);
        mListView = (ListView) rootView.findViewById(R.id.list);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initAdapter();
    }

    protected void initAdapter() {}
}
