package com.muheng.photoviewer

import android.os.Bundle
import android.widget.Toast
import com.muheng.common.ToolbarActivity
import com.muheng.facebook.MeProfile
import com.muheng.login.LoginActivity
import com.muheng.photoviewer.fragment.PhotosFragment
import com.muheng.photoviewer.presenter.FBPresenter
import com.muheng.photoviewer.presenter.IFBView
import com.muheng.photoviewer.utils.Constants

/**
 * Created by Muheng Li on 2018/7/7.
 */
class PhotosActivity : ToolbarActivity(), IFBView {

    companion object {
        const val TAG = "PhotosActivity"
    }

    private var mFragment = PhotosFragment()

    private lateinit var mPresenter: FBPresenter
    private lateinit var mAlbumName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(
                    R.id.frame_container, mFragment, PhotosFragment.TAG).commit()
            mAlbumName = intent?.getStringExtra(Constants.EXTRA_NAME) ?: resources.getString(R.string.app_name)
        } else {
            mFragment = supportFragmentManager.findFragmentByTag(PhotosFragment.TAG) as PhotosFragment
            mAlbumName = savedInstanceState.getString(Constants.EXTRA_NAME, resources.getString(R.string.app_name))
        }

        setPrimaryTitle(mAlbumName)

        mPresenter = FBPresenter(this)
        mPresenter.init()
    }

    override fun onDestroy() {
        mPresenter.dispose()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(Constants.EXTRA_NAME, mAlbumName)
    }

    override fun performOptionClicked() {
        mPresenter.logout()
    }

    override fun onMeProfile(meProfile: MeProfile) {
        // Nothing to do here
    }

    override fun onLogOut() {
        startActivity(LoginActivity::class.java)
    }

    override fun onError(error: Throwable) {
        Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
    }
}