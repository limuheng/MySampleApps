package com.muheng.fotograb.presenter

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.muheng.common.BasePresenter
import com.muheng.facebook.Photo
import com.muheng.fotograb.DownloadUtils
import com.muheng.fotograb.R
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

    lateinit var mPhoto: Photo
    private val mView = WeakReference<IPhotoView>(photoView)

    var mTouchListener = object : View.OnTouchListener {
        private var draggingId = MotionEvent.INVALID_POINTER_ID
        private var lastTouchX: Float = 0f
        private var lastTouchY: Float = 0f

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {

            when (event?.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (event.pointerCount == 1) {
                        draggingId = event.actionIndex
                        lastTouchX = event.getX(event.actionIndex)
                        lastTouchY = event.getY(event.actionIndex)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (draggingId == event.actionIndex) {
                        var dx = (event.getX(event.actionIndex) - lastTouchX)
                        var dy = (event.getY(event.actionIndex) - lastTouchY)

                        mView.get()?.onMotionMove(dx, dy)

                        // Do not remeber x/y to lastTouchX/Y
                    }
                }
                MotionEvent.ACTION_UP -> {
                    draggingId = MotionEvent.INVALID_POINTER_ID;
                }
                MotionEvent.ACTION_CANCEL -> {
                    draggingId = MotionEvent.INVALID_POINTER_ID;
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    if (draggingId == event.getPointerId(event.actionIndex)) {
                        draggingId = MotionEvent.INVALID_POINTER_ID
                    }
                }
            }

            return true
        }
    }

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
                        is Event.ScaleEvent -> {
                            mView.get()?.onScale(event.scaleFactor)
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

    fun showPhoto() {
        if (mPhoto.webp_images.isNotEmpty()) {
            mView.get()?.showPhoto(mPhoto.webp_images[0].source)
        } else {
            mView.get()?.onNoData()
        }
    }

    fun sharePhoto(): Boolean {
        if (mPhoto.webp_images.isNotEmpty()) {
            mView.get()?.onShareClicked(mPhoto.name, mPhoto.webp_images[0].source)
            return true
        }
        return false
    }

    fun save() {
        if (mPhoto.images.isNotEmpty()) {
            Thread(Runnable {
                val url = mPhoto.images[0].source
                var fileName = DownloadUtils.extractPhotoName(url)
                DownloadUtils.downloadPhoto(fileName, url)
            }).start()
        } else {
            mView.get()?.onSaveFailed()
        }
    }
}