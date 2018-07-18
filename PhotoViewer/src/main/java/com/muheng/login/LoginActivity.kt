package com.muheng.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.muheng.common.ToolbarActivity
import com.muheng.photoviewer.R
import com.muheng.photoviewer.utils.Constants
import com.muheng.photoviewer.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

/**
 * Created by  Muheng Li on 2018/7/7.
 */
class LoginActivity : ToolbarActivity(), IFBLoginView {

    private lateinit var mPresenter: FBLoginPresenter

    private var mCallbackManager: CallbackManager = CallbackManager.Factory.create()

    private var mFacebookCallback: FacebookCallback<LoginResult> = object : FacebookCallback<LoginResult> {
        override fun onSuccess(result: LoginResult?) {
            // Go to main activity
            startActivity(MainActivity::class.java)
        }

        override fun onCancel() {
            Toast.makeText(this@LoginActivity, R.string.cancel, Toast.LENGTH_SHORT).show()
        }

        override fun onError(error: FacebookException?) {
            Toast.makeText(this@LoginActivity, error?.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showToolbar(false)

        mPresenter = FBLoginPresenter(this)
        mPresenter.performLoginCheck()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mCallbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun initViews() {
        setContentView(R.layout.activity_login)

        login_button.setReadPermissions(Arrays.asList(Constants.PUBLIC_PROFILE, Constants.USER_PHOTOS))
        login_button.registerCallback(mCallbackManager, mFacebookCallback)
    }

    override fun onLoginCheck(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            // Transition to main activity
            startActivity(MainActivity::class.java)
        } else {
            initViews()
        }
    }
}