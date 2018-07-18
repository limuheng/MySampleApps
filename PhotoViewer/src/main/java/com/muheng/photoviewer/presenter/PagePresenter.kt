package com.muheng.photoviewer.presenter

import com.muheng.common.BasePresenter
import com.muheng.rxjava.IRxEventBus
import java.lang.ref.WeakReference

/**
 * Created by Muheng Li on 2018/7/10.
 */
open class PagePresenter<T>(pageView: IPageView<T>) : BasePresenter(), IRxEventBus {

    protected val mView = WeakReference<IPageView<T>>(pageView)

    override fun registerRxBus() {
        // TODO override with subscribing events
    }

    override fun unregisterRxBus() {
        if (!mCompositeDisposable.isDisposed) {
            mCompositeDisposable.clear()
        }
    }

    open fun loadStartPage(path: String = "") {}

    open fun loadMore(url: String, isNext: Boolean = true) {}
}