package com.muheng.common

import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Muheng Li on 2018/7/10.
 */
open class BasePresenter {

    protected lateinit var mCompositeDisposable: CompositeDisposable

    open fun init() {
        mCompositeDisposable = CompositeDisposable()
    }

    open fun dispose() {
        if (!mCompositeDisposable.isDisposed) {
            mCompositeDisposable.clear()
        }
    }

}