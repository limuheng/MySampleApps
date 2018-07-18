package com.muheng.common

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdView
import com.muheng.fotograb.FotoGrabApp
import com.muheng.fotograb.R
import kotlinx.android.synthetic.main.layout_toolbar.view.*

/**
 * Created by Muheng Li on 2018/7/9.
 */
open class ToolbarActivity : AppCompatActivity() {

    var mContainer: ViewGroup? = null
    var mToolbar: Toolbar? = null
    var mToolbarIcon: ImageView? = null
    var mOptionMenu: View? = null
    var mFrameLayout: ViewGroup? = null

    private var mToolbarClickListener = View.OnClickListener { view ->
        when (view?.id) {
            R.id.op_menu -> {
                performOptionClicked()
            }
            R.id.toolbar -> {
                performTitleClicked()
            }
            R.id.icon -> {
                performIconClicked()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_toolbar)

        initToolBar()

        mContainer = findViewById(R.id.container)
        mFrameLayout = findViewById(R.id.frame_container)

        var adId = (application as FotoGrabApp).getJniAdMob().getTestAdId()
        val adView = (application as FotoGrabApp).createAdView(adId)//resources.getString(R.string.test_ad_unit_id)
        addAdView(adView)
    }

    private fun initToolBar() {
        mToolbar = findViewById(R.id.toolbar)
        mToolbarIcon = findViewById(R.id.icon)
        mOptionMenu = findViewById(R.id.op_menu)
        mToolbar?.setOnClickListener(mToolbarClickListener)
        mToolbarIcon?.setOnClickListener(mToolbarClickListener)
        mOptionMenu?.setOnClickListener(mToolbarClickListener)
        setSupportActionBar(mToolbar)
    }

    open fun setPrimaryTitle(title: String) {
        mToolbar?.primary_title?.text = title
    }

    open fun setSecondaryTitle(title: String) {
        mToolbar?.secondary_title?.text = title
    }

    open fun setToolbarIcon(path: String) {
        Glide.with(this).load(path).centerCrop().into(mToolbar?.icon)
        // Draw circle bitmap and set to ImageView
        //Glide.with(this).load(path).bitmapTransform(CircleTransform(this)).into(mToolbar?.icon)
    }

    fun showToolbar(isVisible: Boolean) {
        mToolbar?.visibility = if (isVisible == true) View.VISIBLE else View.GONE
    }

    fun showOptionMenu(isVisible: Boolean) {
        mOptionMenu?.visibility = if (isVisible == true) View.VISIBLE else View.GONE
    }

    open fun performTitleClicked() {
        if (!mToolbar?.primary_title?.text.isNullOrEmpty()) {
            Toast.makeText(this@ToolbarActivity, mToolbar?.primary_title?.text, Toast.LENGTH_SHORT).show()
        }
    }

    open fun performOptionClicked() {
        // Logout by default
    }

    open fun performIconClicked() {
        onBackPressed()
    }

    fun startActivity(target: Class<*>) {
        var intent = Intent(this, target)
        startActivity(intent)
        finish()
    }

    open fun addAdView(adView: AdView) {
        mContainer?.addView(adView)
    }
}