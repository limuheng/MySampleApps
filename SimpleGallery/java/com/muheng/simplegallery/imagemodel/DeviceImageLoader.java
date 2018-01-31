package com.muheng.simplegallery.imagemodel;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;
import android.provider.MediaStore;

public class DeviceImageLoader extends CursorLoader {

    public static final Uri QUERY_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    public static final String[] PROJECTION = new String[] {
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.DATE_TAKEN,
    };

    public DeviceImageLoader(Context context) {
        super(context);
        setUri(QUERY_URI);
        setProjection(PROJECTION);
        setSelection(MediaStore.Images.Media.DATA + " LIKE '/storage/emulated/0/Pictures/night/%'");
        setSortOrder(MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " ASC");
    }
}
