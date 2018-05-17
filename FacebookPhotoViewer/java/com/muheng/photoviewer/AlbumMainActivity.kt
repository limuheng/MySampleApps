package com.muheng.photoviewer

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.muheng.photoviewer.utils.*
import org.json.JSONObject
import java.util.*

class AlbumMainActivity : AppCompatActivity(), UIHandler.Companion.IUIHandler {
    var mCallbackManager : CallbackManager = CallbackManager.Factory.create()
    private var mFacebookCallback : FacebookCallback<LoginResult> = object : FacebookCallback<LoginResult> {
        override fun onCancel() {
            Toast.makeText(
                    this@AlbumMainActivity,
                    R.string.cancel, Toast.LENGTH_SHORT).show()
        }

        override fun onSuccess(result: LoginResult?) {
            Log.d("123", "[mFacebookCallback] onSuccess")
            Toast.makeText(
                    this@AlbumMainActivity,
                    R.string.success, Toast.LENGTH_SHORT).show()
            mFragments[Constants.FRAG_ALBUM_LIST]?.executeRequest()
            mHandler?.sendEmptyMessage(Constants.MSG_UPDATE_UI)

            getUserProfile()
        }

        override fun onError(error: FacebookException?) {
            Toast.makeText(
                    this@AlbumMainActivity,
                    R.string.error, Toast.LENGTH_SHORT).show()
        }
    }

    private var mFragIdx : Int = Constants.FRAG_ALBUM_LIST
    var mFragments : Array<FacebookFragment?> = arrayOf(AlbumListFragment(), AlbumPhotosFragment())
    var mFragContainers : Array<ViewGroup?> = arrayOfNulls<ViewGroup>(2)

    var mToolbar : Toolbar? = null
    var mMenuLogout : MenuItem? = null
    var mLoginButton : LoginButton? = null
    var mLoginContainer : View? = null
    var mMainUiContainer : ViewGroup? = null

    var mMeProfileRequest: GraphRequest? = null
    var mMeProfileCallback: GraphRequest.GraphJSONObjectCallback = object : GraphRequest.GraphJSONObjectCallback {
        override fun onCompleted(obj: JSONObject?, response: GraphResponse?) {
            FacebookManager.getInstance()?.loginSuccess(obj)
            updateToolBarTitle()
        }
    }

    private var mHandler : UIHandler? = null

    override fun handleMessage(msg: Message?) {
        when (msg?.what) {
            Constants.MSG_UPDATE_UI -> {
                updateLoginUI(isLoggedIn())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)

        mLoginContainer = findViewById(R.id.login_container)
        mMainUiContainer = findViewById(R.id.main_ui_container)

        mLoginButton = findViewById(R.id.login_button)
        mLoginButton?.setReadPermissions(Arrays.asList(Constants.PUBLIC_PROFILE, Constants.USER_PHOTOS))
        mLoginButton?.registerCallback(mCallbackManager, mFacebookCallback)

        mFragContainers[Constants.FRAG_ALBUM_LIST] = findViewById(R.id.fragment_container)
        mFragContainers[Constants.FRAG_ALBUM_PHOTOS] = findViewById(R.id.detail_fragment_container)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().
                    add(R.id.fragment_container, mFragments[Constants.FRAG_ALBUM_LIST], AlbumPhotosFragment.TAG).commit()

            supportFragmentManager.beginTransaction().
                    add(R.id.detail_fragment_container, mFragments[Constants.FRAG_ALBUM_PHOTOS], AlbumListFragment.TAG).commit()
            for (i in 0..mFragments.size.minus(1)) {
                mFragments[i]?.retainInstance = true
            }
        } else {
                mFragments[Constants.FRAG_ALBUM_LIST] = supportFragmentManager.findFragmentByTag(AlbumPhotosFragment.TAG) as FacebookFragment
                mFragments[Constants.FRAG_ALBUM_PHOTOS] = supportFragmentManager.findFragmentByTag(AlbumListFragment.TAG) as FacebookFragment
        }

        if (Constants.ACT_VIEW_ALBUM == intent?.action) {
            switchToFragment(Constants.FRAG_ALBUM_PHOTOS)
        } else {
            switchToFragment(Constants.FRAG_ALBUM_LIST)
        }

        mHandler = UIHandler(this)
        FacebookManager.getInstance()?.mHandler = mHandler

        initToolBar()

        updateLoginUI(isLoggedIn())

        if (isLoggedIn()) {
            getUserProfile()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        this.intent = intent
        if (Constants.ACT_VIEW_ALBUM == intent?.action) {
            switchToFragment(Constants.FRAG_ALBUM_PHOTOS)
        } else {
            switchToFragment(Constants.FRAG_ALBUM_LIST)
        }
    }

    override fun onResume() {
        super.onResume()
        updateLoginUI(isLoggedIn())
    }

    override fun onDestroy() {
        FacebookManager.getInstance()?.mHandler = null
        AlbumManager.release()
        PhotosManager.release()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mCallbackManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        mMenuLogout = menu?.findItem(R.id.action_logout)
        mMenuLogout?.isVisible = isLoggedIn()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var menuId = item?.itemId
        when (menuId) {
            R.id.action_logout -> {
                FacebookManager.getInstance()?.logout()
                switchToFragment(Constants.FRAG_ALBUM_LIST)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (mFragIdx == Constants.FRAG_ALBUM_PHOTOS) {
            switchToFragment(Constants.FRAG_ALBUM_LIST)
        } else {
            super.onBackPressed()
        }
    }

    private fun switchToFragment(idx : Int) {
        if (idx >= Constants.FRAG_ALBUM_LIST && idx <= Constants.FRAG_ALBUM_PHOTOS) {
            mFragIdx = idx
            for (i in 0..mFragContainers.size.minus(1)) {
                if (i == idx) {
                    mFragContainers[i]?.visibility = View.VISIBLE
                    mFragments[i]?.onVisible()
                } else {
                    mFragContainers[i]?.visibility = View.GONE
                }
            }
            updateToolBarTitle()
        }
    }

    private fun updateToolBarTitle() {
        when (mFragIdx) {
            Constants.FRAG_ALBUM_LIST -> {
                mToolbar?.title = FacebookManager.getInstance()?.mMeProfile?.mName + "'s albums"
            }
            Constants.FRAG_ALBUM_PHOTOS -> {
                var albumId = (mFragments[Constants.FRAG_ALBUM_PHOTOS] as AlbumPhotosFragment).mAlbumId
                mToolbar?.title = AlbumManager.getInstance()?.getData(albumId)?.mName
            }
        }
    }

    private fun initToolBar() {
        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(mToolbar)
    }

    private fun updateLoginUI(isLoggedIn : Boolean) {
        if (isLoggedIn) {
            mLoginContainer?.visibility = View.GONE
            mMainUiContainer?.visibility = View.VISIBLE
        } else {
            mLoginContainer?.visibility = View.VISIBLE
            mMainUiContainer?.visibility = View.GONE
        }
        mMenuLogout?.isVisible = isLoggedIn
    }

    private fun isLoggedIn() : Boolean {
        return FacebookManager.getInstance()?.isLoggedIn() == true
    }

    private fun getUserProfile() {
        // Start to request user profile
        val parameters = Bundle()
        parameters.putString("fields", "id, name, link, cover, picture")
        // Request login
        mMeProfileRequest = FacebookManager.createGraphRequest(parameters, mMeProfileCallback)
        mMeProfileRequest?.executeAsync()
    }
}