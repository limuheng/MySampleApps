package com.muheng.photoviewer

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.muheng.common.ToolbarActivity
import com.muheng.facebook.MeProfile
import com.muheng.login.LoginActivity
import com.muheng.photoviewer.fragment.AlbumsFragment
import com.muheng.photoviewer.presenter.FBPresenter
import com.muheng.photoviewer.presenter.IFBView
import com.muheng.photoviewer.utils.CircleTransform
import kotlinx.android.synthetic.main.layout_toolbar.view.*

/**
 * Created by Muheng Li on 2018/7/7.
 */
class MainActivity : ToolbarActivity(), IFBView {

    companion object {
        const val TAG = "MainActivity"
    }

    private var mFragment = AlbumsFragment()

    private lateinit var mPresenter: FBPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(
                    R.id.frame_container, mFragment, AlbumsFragment.TAG).commit()
        } else {
            mFragment = supportFragmentManager.findFragmentByTag(AlbumsFragment.TAG) as AlbumsFragment
        }

        mPresenter = FBPresenter(this)
        mPresenter.init()
        mPresenter.reqMeProfile()
    }

    override fun onDestroy() {
        mPresenter.dispose()
        super.onDestroy()
    }

    override fun setToolbarIcon(path: String) {
        // Draw circle bitmap and set to ImageView
        Glide.with(this).load(path).bitmapTransform(CircleTransform(this)).into(mToolbar?.icon)
    }

    override fun performOptionClicked() {
        mPresenter.logout()
    }

    override fun performIconClicked() {
        performTitleClicked()
    }

    override fun onMeProfile(meProfile: MeProfile) {
        setPrimaryTitle(meProfile.name)
        setToolbarIcon(meProfile.picture.data.url)
    }

    override fun onLogOut() {
        startActivity(LoginActivity::class.java)
    }

    override fun onError(error: Throwable) {
        Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
    }
}