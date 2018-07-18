package com.muheng.fotograb.presenter

import com.muheng.facebook.IFacebookApi
import com.muheng.facebook.Photo
import com.muheng.retrofit.FBRetrofitClient
import com.muheng.retrofit.Parameter
import com.muheng.rxjava.Event
import com.muheng.rxjava.IRxEventBus
import com.muheng.rxjava.RxEventBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

/**
 * Created by Muheng Li on 2018/7/10.
 */
class AlbumPhotosPresenter(photosView: IPageView<Photo>) : PagePresenter<Photo>(photosView) {

    companion object {
        const val TAG = "AlbumPhotosPresenter"
        const val PHOTOS_PER_PAGE = 20
        const val REQ_PHOTOS = "photos.limit(${PHOTOS_PER_PAGE}){name,link,id,picture,images,webp_images}"
    }

    override fun registerRxBus() {
        val subscribe = RxEventBus.get()!!.subscribe(
                Consumer { event ->
                    when (event) {
                        is Event.PhotoClicked -> {
                            mView.get()?.onItemClicked(event.photo)
                        }
                    }
                })

        mCompositeDisposable.add(subscribe)
    }

    // path is album ID
    override fun loadStartPage(path: String) {
        var parameter = Parameter(IFacebookApi.FIELDS, REQ_PHOTOS)
        var paramList = ArrayList<Parameter>()
        paramList.add(parameter)

        mCompositeDisposable.add(
                FBRetrofitClient.get()
                        .photos(path, IFacebookApi.getParamsMap(paramList))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            if (result.photos.data.isNotEmpty()) {
                                mView.get()?.onPage(result.photos)
                            } else {
                                mView.get()?.onNoData()
                            }
                        }, { error ->
                            error.printStackTrace()
                            mView.get()?.onError(error)
                        })
        )
    }

    override fun loadMore(url: String, isNext: Boolean) {
        if (url.isNotBlank()) {
            //Log.d("AlbumPhotosPresenter", "url:$url")
            mCompositeDisposable.add(
                    FBRetrofitClient.get()
                            .loadMorePhotos(url)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                mView.get()?.onPage(result, isNext)
                            }, { error ->
                                error.printStackTrace()
                                mView.get()?.onError(error)
                            })
            )
        }
    }
}