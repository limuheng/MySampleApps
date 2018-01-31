package com.muheng.adaptiveviewpager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

public class CheckBoxListAdapter extends BaseAdapter {

    protected Context mContext;
    protected LayoutInflater mInflater;

    protected List<String> mData = new ArrayList<String> ();

    private FragmentPagerManager mPagerManager;

    public CheckBoxListAdapter(Context context, LayoutInflater inflater) {
        mContext = context;
        mInflater = inflater;
        mPagerManager = FragmentPagerManager.getInstance(mContext);
    }

    public List<String> getData() {
        return mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        if ((mData != null) && (position < mData.size())) {
            return mData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String name = (String) getItem(position);
        View result = null;
        ViewHoler viewCache = null;

        if (convertView != null) {
            Object cacheObj = convertView.getTag();
            if (cacheObj instanceof ViewHoler) {
                viewCache = (ViewHoler) convertView.getTag();
                result = convertView;
            }
        }

        if (result == null) {
            result = mInflater.inflate(R.layout.checkbox_item, parent, false);
            viewCache = new ViewHoler(result, mPagerManager);
        }

        result.setTag(viewCache);

        viewCache.bindView(position, name);
        return result;
    }

    public static class ViewHoler {
        protected CheckBox _CheckBox;
        protected int _Pos;
        private FragmentPagerManager _PagerManager;

        public ViewHoler(View root, FragmentPagerManager manager) {
            _CheckBox = (CheckBox) root.findViewById(R.id.checkbox);
            _PagerManager = manager;
        }

        public void bindView(int pos, String name) {
            _Pos = pos;
            if (_Pos >= 0 && _Pos < FragmentPagerManager.FRAG_COUNT) {
                _CheckBox.setChecked(_PagerManager.isFragmentAdded(_Pos));
            }
            _CheckBox.setText(name);
            _CheckBox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Do not allow no checks case happens
                    if (_PagerManager.getAddedFragmentCount() == 1 && !((CheckBox)view).isChecked()) {
                        ((CheckBox)view).setChecked(true);
                        return ;
                    }
                    ((CheckBox)view).setChecked(((CheckBox)view).isChecked());
                    if (((CheckBox)view).isChecked()) {
                        _PagerManager.addFragment(_Pos);
                    } else {
                        _PagerManager.removeFragment(_Pos);
                    }
                }
            });
        }
    }
}
