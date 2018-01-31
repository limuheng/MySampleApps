package com.muheng.simplegallery;

import android.app.Fragment;

import com.muheng.simplegallery.interfaces.ICursorPosition;

public abstract class SimpleGalleryFragment extends Fragment implements ICursorPosition {
    public boolean onBackPressed() { return false; }
}
