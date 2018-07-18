package com.muheng.fotograb.presenter

import com.muheng.facebook.MeProfile

/**
 * Created by Muheng Li on 2018/7/10.
 */
interface IFBView {
    fun onMeProfile(meProfile: MeProfile)
    fun onLogOut()
    fun onError(error: Throwable)
}