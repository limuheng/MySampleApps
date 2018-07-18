package com.muheng.fotograb.presenter

import android.util.Log
import com.muheng.facebook.Album
import com.muheng.facebook.IFacebookApi
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
class AlbumsPresenter(pageView: IPageView<Album>) : PagePresenter<Album>(pageView) {

    companion object {
        const val TAG = "AlbumsPresenter"
        const val ALBUMS_PER_PAGE = 20
        const val REQ_ALBUMS = "albums.limit($ALBUMS_PER_PAGE){name,photo_count,photos.limit(1){webp_images},picture}"
    }

    override fun registerRxBus() {
        val subscribe = RxEventBus.get()!!.subscribe(
                Consumer { event ->
                    when (event) {
                        is Event.AlbumClicked -> {
                            mView.get()?.onItemClicked(event.album)
                        }
                    }
                })

        mCompositeDisposable.add(subscribe)
    }

    override fun loadStartPage(path: String) {
        // request album webp_images
        var parameter = Parameter(IFacebookApi.FIELDS, REQ_ALBUMS)
        var paramList = ArrayList<Parameter>()
        paramList.add(parameter)

        mCompositeDisposable.add(
                FBRetrofitClient.get()
                        .albums(IFacebookApi.getParamsMap(paramList))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            if (result.albums.data.isNotEmpty()) {
                                mView.get()?.onPage(result.albums)
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
            //Log.d("AlbumsPresenter", "url:$url")
            mCompositeDisposable.add(
                    FBRetrofitClient.get()
                            .loadMoreAlbums(url)
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