package com.muheng.photoviewer.presenter

import com.muheng.facebook.Page

/**
 * Created by Muheng Li on 2018/7/13.
 */
interface IPageView<T> {
    fun onNoData()
    fun onPage(page: Page<T>, isNext: Boolean = true)
    fun onItemClicked(item: T)
    fun onError(error: Throwable)
    fun appendPage(page: Page<T>)
    fun insertPage(page: Page<T>)
}