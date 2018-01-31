package com.muheng.adaptiveviewpager;

import com.muheng.adaptiveviewpager.interfaces.IDialogCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends Activity {

    private PagerTabStrip mPagerTab; 
    private ViewPager mViewPager;
    private AdaptiveFragmentAdapter mPagerAdapter;

    private IDialogCallback mDialogCallback = new IDialogCallback() {
        @Override
        public void onDialogDismissed(int result) {
            //mPagerAdapter.notifyDataSetChanged();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mPagerTab = (PagerTabStrip)findViewById(R.id.pagertab);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPagerTab.setTextColor(getColor(R.color.pager_tab_text_color));
            mPagerTab.setTabIndicatorColor(getColor(R.color.pager_tab_text_color));
        } else {
            mPagerTab.setTextColor(ContextCompat.getColor(this, R.color.pager_tab_text_color));
            mPagerTab.setTabIndicatorColor(ContextCompat.getColor(this, R.color.pager_tab_text_color));
        }
        mPagerTab.setDrawFullUnderline(true);

        initViewPager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_page_setting: {
                PageSettingDialogFragment pageSettingDlg = PageSettingDialogFragment.newInstance();
                pageSettingDlg.setDialogCallback(mDialogCallback);
                pageSettingDlg.show(getFragmentManager(), PageSettingDialogFragment.TAG);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mPagerAdapter = new AdaptiveFragmentAdapter(this, getFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
    }
}
