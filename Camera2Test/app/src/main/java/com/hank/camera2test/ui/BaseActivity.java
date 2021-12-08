package com.hank.camera2test.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        translucentStatusBar();
    }

    @SuppressLint("ObsoleteSdkInt")
    private void translucentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            // 確認取消半透明設置。
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN: 全螢幕顯示，status bar 不隱藏，activity 上方 layout 會被 status bar 覆蓋。
            // SYSTEM_UI_FLAG_LAYOUT_STABLE: 配合其他 flag 使用，防止 system bar 改變後 layout 的變動。
            window.getDecorView().setSystemUiVisibility(
                    SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | SYSTEM_UI_FLAG_LAYOUT_STABLE);
            // 跟系統表示要渲染 system bar 背景。
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

}
