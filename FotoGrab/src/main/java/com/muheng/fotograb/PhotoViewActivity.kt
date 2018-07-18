package com.muheng.fotograb

import android.os.Bundle
import com.muheng.common.ToolbarActivity
import com.muheng.fotograb.fragment.PhotoViewFragment
import com.muheng.fotograb.utils.Constants

/**
 * Created by Muheng Li on 2018/7/14.
 */
class PhotoViewActivity : ToolbarActivity() {

    companion object {
        const val TAG = "PhotoViewActivity"
    }

    private var mFragment = PhotoViewFragment()

    private lateinit var mPhotoName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(
                    R.id.frame_container, mFragment, PhotoViewFragment.TAG).commit()
            mPhotoName = intent?.getStringExtra(Constants.EXTRA_NAME) ?: ""
        } else {
            mFragment = supportFragmentManager.findFragmentByTag(PhotoViewFragment.TAG) as PhotoViewFragment
            mPhotoName = savedInstanceState.getString(Constants.EXTRA_NAME, "")
        }

        if (mPhotoName.isEmpty()) {
            mPhotoName = resources.getString(R.string.app_name)
        }

        setPrimaryTitle(mPhotoName)
        // Disable option menu
        showOptionMenu(false)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(Constants.EXTRA_NAME, mPhotoName)
    }

}
