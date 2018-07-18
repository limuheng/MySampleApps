package com.muheng.fotograb.presenter

import com.facebook.AccessToken
import com.facebook.AccessTokenTracker
import com.facebook.login.LoginManager
import com.muheng.common.BasePresenter
import com.muheng.facebook.IFacebookApi
import com.muheng.facebook.FBManager
import com.muheng.retrofit.FBRetrofitClient
import com.muheng.retrofit.Parameter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference

/**
 * Created by Muheng Li on 2018/7/10.
 */
class FBPresenter(fbView: IFBView) : BasePresenter() {

    companion object {
        const val TAG = "FBPresenter"
        const val REQ_ME_FIELDS = "id,name,picture"
    }

    private val mView = WeakReference<IFBView>(fbView)

    private var mAccessTokenTracker: AccessTokenTracker

    init {
        mAccessTokenTracker = object : AccessTokenTracker() {
            override fun onCurrentAccessTokenChanged(
                    oldAccessToken: AccessToken?, currentAccessToken: AccessToken?) {
                // User logged out
                if (currentAccessToken == null) {
                    mView.get()?.onLogOut()
                }
            }
        }
    }

    fun reqMeProfile() {
        // Start to request user profile
        var parameter = Parameter(IFacebookApi.FIELDS, REQ_ME_FIELDS)
        var paramList = ArrayList<Parameter>()
        paramList.add(parameter)

        mCompositeDisposable.add(
                FBRetrofitClient.get()
                        .meProfile(IFacebookApi.getParamsMap(paramList))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            FBManager.get()?.mUser = result
                            mView.get()?.onMeProfile(result)
                        }, { error ->
                            error.printStackTrace()
                            mView.get()?.onError(error)
                        })
        )
    }

    fun logout() {
        LoginManager.getInstance()?.logOut()
    }
}