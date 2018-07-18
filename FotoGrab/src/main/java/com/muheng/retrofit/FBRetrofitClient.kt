package com.muheng.retrofit

import com.muheng.facebook.IFacebookApi
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Muheng Li on 2018/7/10.
 */
class FBRetrofitClient private constructor() {

    companion object {
        private val instance: FBRetrofitClient by lazy { FBRetrofitClient() }

        fun get(): IFacebookApi {
            return instance.retrofit.create(IFacebookApi::class.java)
        }
    }

    private var retrofit =
            Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(IFacebookApi.FB_API_BASE_RUL)
                    .build()
}