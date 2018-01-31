package com.muheng.languageselectionapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

public abstract class LanguageSelectionActivity extends AppCompatActivity {

    private int mLocaleCode = LocaleUtils.LOCAE_ENG;

    @Override
    protected void attachBaseContext(Context newBase) {
        mLocaleCode = AppPreferenceManager.getInt(newBase, AppPreferenceManager.KEY_INT_LANGUAGE_SETTING, LocaleUtils.LOCAE_ENG);
        super.attachBaseContext(LocaleUtils.createContextWrapper(newBase,  LocaleUtils.getLocale(mLocaleCode)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.language_menu, menu);
        return true;
    }

    private DialogInterface.OnClickListener mLanguageSelectDlgListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE: {
                    int selectLangugaeId = 0;
                    try {
                        selectLangugaeId = mLanguageSelector.getCheckedRadioButtonId();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    switch (selectLangugaeId) {
                        case R.id.lang_en: {
                            AppPreferenceManager.putInt(LanguageSelectionActivity.this, AppPreferenceManager.KEY_INT_LANGUAGE_SETTING, LocaleUtils.LOCAE_ENG);
                            //LocaleUtils.applyLocale(MainActivity.this, Locale.ENGLISH);
                            restartSelf();
                            break;
                        }
                        case R.id.lang_cht: {
                            AppPreferenceManager.putInt(LanguageSelectionActivity.this, AppPreferenceManager.KEY_INT_LANGUAGE_SETTING, LocaleUtils.LOCAE_CHT);
                            // LocaleUtils.applyLocale(MainActivity.this, Locale.TRADITIONAL_CHINESE);
                            restartSelf();
                            break;
                        }
                        case R.id.lang_chs: {
                            AppPreferenceManager.putInt(LanguageSelectionActivity.this, AppPreferenceManager.KEY_INT_LANGUAGE_SETTING, LocaleUtils.LOCAE_CHS);
                            // LocaleUtils.applyLocale(MainActivity.this, Locale.SIMPLIFIED_CHINESE);
                            restartSelf();
                            break;
                        }
                    }
                }
                case DialogInterface.BUTTON_NEGATIVE: {
                    dialog.dismiss();
                    break;
                }
            }
        }
    };

    RadioGroup mLanguageSelector;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_language: {
                LayoutInflater inflater = getLayoutInflater();
                View rootView = inflater.inflate(R.layout.language_selector_layout, null);
                mLanguageSelector = (RadioGroup) rootView.findViewById(R.id.language_selector);

                switch (mLocaleCode) {
                    case LocaleUtils.LOCAE_ENG: {
                        mLanguageSelector.check(R.id.lang_en);
                        break;
                    }
                    case LocaleUtils.LOCAE_CHT: {
                        mLanguageSelector.check(R.id.lang_cht);
                        break;
                    }
                    case LocaleUtils.LOCAE_CHS: {
                        mLanguageSelector.check(R.id.lang_chs);
                        break;
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.select_language);
                builder.setView(rootView);
                builder.setPositiveButton(android.R.string.ok, mLanguageSelectDlgListener);
                builder.setNegativeButton(android.R.string.cancel, mLanguageSelectDlgListener);
                builder.setCancelable(false);
                builder.show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public abstract void restartSelf();
}
