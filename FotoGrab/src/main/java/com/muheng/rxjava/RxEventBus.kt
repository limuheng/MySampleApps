package com.muheng.rxjava

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * Created by Muheng Li on 2018/7/10.
 */
class RxEventBus {
    companion object {
        private var sEventBus: RxEventBus? = null

        fun get(): RxEventBus? {
            if (sEventBus == null) {
                sEventBus = RxEventBus()
            }
            return sEventBus
        }
    }

    private val mBus = PublishSubject.create<Any>()

    fun send(o: Any) {
        mBus.onNext(o)
    }

    fun subscribe(consumer: Consumer<Any>): Disposable {
        return mBus.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(consumer as Consumer<in Any>, Consumer<Throwable> {})
    }
}