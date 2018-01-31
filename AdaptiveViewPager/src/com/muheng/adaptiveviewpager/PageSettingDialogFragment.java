package com.muheng.adaptiveviewpager;

import com.muheng.adaptiveviewpager.interfaces.IDialogCallback;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import java.util.List;

public class PageSettingDialogFragment extends DialogFragment {
    public static final String TAG = PageSettingDialogFragment.class.getSimpleName();

    private Dialog mDialog;
    private ListView mListView;

    private CheckBoxListAdapter mAdapter;

    private IDialogCallback mDialogCallback;

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (mDialog != null) {
                mDialog.cancel();
            }
        }
    };

    public static PageSettingDialogFragment newInstance() {
        PageSettingDialogFragment dialog = new PageSettingDialogFragment();
        return dialog;
    }

    private PageSettingDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.page_setting_dlg, null);

        mListView = (ListView) rootView.findViewById(R.id.list);

        mAdapter = new CheckBoxListAdapter(getActivity(), getActivity().getLayoutInflater());
        List<String> data = mAdapter.getData();
        FragmentPagerManager pagerManager = FragmentPagerManager.getInstance(getActivity());
        for (int i = 0; i < FragmentPagerManager.FRAG_COUNT; i++) {
            data.add(pagerManager.getFragmentTag(i));
        }

        mListView.setAdapter(mAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.page_setting);
        builder.setView(rootView);
        builder.setPositiveButton(android.R.string.ok, mOnClickListener);

        mDialog = builder.create();
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        return mDialog;
    }

    public void setDialogCallback(IDialogCallback dlgCallback) {
        mDialogCallback = dlgCallback;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mDialogCallback != null) {
            mDialogCallback.onDialogDismissed(0);
        } else {
            Log.e(TAG, "mDialogCallback is null !");
        }
    }
}
