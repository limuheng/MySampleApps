package com.muheng.fotograb.presenter

/**
 * Created by Muheng Li on 2018/7/14.
 */
interface IPhotoView {
    fun showPhoto(path: String)
    fun onFabClicked(id: Int)
    fun onShareClicked(name: String, path: String)
    fun onNoData()
    fun onSaveSuccessfaul(path: String)
    fun onSaveFailed(msg: String? = null)
    fun onPermissionGranted(permission: String)
    fun requestPermission()
    fun onScale(scaleFactor: Float)
    fun onMotionMove(dx: Float, dy: Float)
}