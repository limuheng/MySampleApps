package com.muheng.fotograb.presenter

import android.util.Log
import com.muheng.common.BasePresenter
import com.muheng.fotograb.DownloadUtils
import com.muheng.rxjava.Event
import com.muheng.rxjava.IRxEventBus
import com.muheng.rxjava.RxEventBus
import io.reactivex.functions.Consumer
import java.lang.ref.WeakReference

/**
 * Created by Muheng Li on 2018/7/14.
 */
class PhotoViewPresenter(photoView: IPhotoView) : BasePresenter(), IRxEventBus {

    companion object {
        const val TAG = "PhotoViewPresenter"

        const val FAB_MAIN = 0
        const val FAB_1 = 1
        const val FAB_2 = 2
        const val FAB_3 = 3
    }

    private val mView = WeakReference<IPhotoView>(photoView)

    override fun registerRxBus() {
        val subscribe = RxEventBus.get()!!.subscribe(
                Consumer { event ->
                    when (event) {
                        is Event.FabClicked -> {
                            mView.get()?.onFabClicked(event.id)
                        }
                        is Event.DownloadCompleted -> {
                            mView.get()?.onSaveSuccessfaul(event.path)
                        }
                        is Event.DownloadFailed -> {
                            mView.get()?.onSaveFailed(event.msg)
                        }
                        is Event.PermissionGranted -> {
                            mView.get()?.onPermissionGranted(event.permission)
                        }
                    }
                })

        mCompositeDisposable.add(subscribe)
    }

    override fun unregisterRxBus() {
        if (!mCompositeDisposable.isDisposed) {
            mCompositeDisposable.clear()
        }
    }

    fun showPhoto(path: String) {
        mView.get()?.showPhoto(path)
    }

    fun sharePhoto(name: String, path: String) {
        mView.get()?.onShareClicked(name, path)
    }

    fun save(url: String) {
        Thread(Runnable {
            var fileName = DownloadUtils.extractPhotoName(url)
            DownloadUtils.downloadPhoto(fileName, url)
        }).start()
    }
}