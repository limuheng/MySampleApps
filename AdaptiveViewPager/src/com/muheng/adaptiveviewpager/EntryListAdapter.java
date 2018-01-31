package com.muheng.adaptiveviewpager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EntryListAdapter extends BaseAdapter {

    protected Context mContext;
    protected LayoutInflater mInflater;

    protected OnClickListener mItemClickListener;
    protected ViewHoler.IBindName mIBindName;

    protected List<Integer> mData = new ArrayList<Integer> ();

    public EntryListAdapter(Context context, LayoutInflater inflater) {
        mContext = context;
        mInflater = inflater;

        mIBindName = new ViewHoler.IBindName() {
            @Override
            public String getName(Integer type) {
                if (type != null) {
                    return mContext.getString(type.intValue());
                }
                return mContext.getString(R.string.no_data);
            }
        };
    }

    public List<Integer> getData() {
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
        final Integer type = (Integer) getItem(position);
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
            result = mInflater.inflate(R.layout.entry_list_item, parent, false);
            viewCache = new ViewHoler(result, mIBindName);
        }

        result.setTag(viewCache);

        viewCache.bindView(type, mItemClickListener);
        return result;
    }

    public void setItemClickListener(OnClickListener listener) {
        mItemClickListener = listener;
    }

    public static class ViewHoler {
        protected TextView _TextView;
        protected IBindName _IBindName;

        public ViewHoler(View root, IBindName iBindName) {
            _TextView = (TextView) root.findViewById(R.id.type_name);
            _IBindName = iBindName;
        }

        public void bindView(Integer type, OnClickListener listener) {
            if (type != null) {
                if (_IBindName != null) {
                    String typeName = _IBindName.getName(type);
                    _TextView.setText(typeName);
                    _TextView.setTag(type);
                    _TextView.setOnClickListener(listener);
                }
            }
        }

        public interface IBindName {
            public String getName(Integer type);
        }
    }
}
