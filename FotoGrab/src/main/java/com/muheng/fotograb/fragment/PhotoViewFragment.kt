package com.muheng.fotograb.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.muheng.facebook.Photo
import com.muheng.fotograb.R
import com.muheng.fotograb.presenter.AlbumsPresenter
import com.muheng.fotograb.presenter.IPhotoView
import com.muheng.fotograb.presenter.PhotoViewPresenter
import com.muheng.fotograb.utils.Constants
import com.muheng.rxjava.Event
import com.muheng.rxjava.RxEventBus
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.functions.Consumer

/**
 * Created by Muheng Li on 2018/7/14.
 */
class PhotoViewFragment : Fragment(), IPhotoView {

    companion object {
        const val TAG = "PhotoViewFragment"
    }

    private lateinit var mPhoto: Photo

    protected open lateinit var mPresenter: PhotoViewPresenter

    private var mImageView: ImageView? = null
    private var mNoData: TextView? = null
    private var mFabs = Array<FloatingActionButton?>(PhotoViewPresenter.FAB_2.plus(1), { i -> null })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPresenter = PhotoViewPresenter(this)
        mPresenter.init()
        mPresenter.registerRxBus()

        if (savedInstanceState != null) {
            mPhoto = savedInstanceState.getParcelable(Constants.EXTRA_PHOTO)
        } else {
            mPhoto = activity?.intent?.getParcelableExtra(Constants.EXTRA_PHOTO) ?: Photo()
        }
    }

    override fun onDestroy() {
        mPresenter.unregisterRxBus()
        mPresenter.dispose()
        super.onDestroy()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.fragment_photo, container, false)

        mImageView = rootView?.findViewById(R.id.photo)
        mNoData = rootView?.findViewById(R.id.no_data)

        mFabs[PhotoViewPresenter.FAB_MAIN] = rootView?.findViewById(R.id.fab_main)
        mFabs[PhotoViewPresenter.FAB_1] = rootView?.findViewById(R.id.fab_1)
        mFabs[PhotoViewPresenter.FAB_2] = rootView?.findViewById(R.id.fab_2)
        //mFabs[PhotoViewPresenter.FAB_3] = rootView?.findViewById(R.id.fab_3)

        for (i in 0 until mFabs.size) {
            mFabs[i]?.setOnClickListener {
                RxEventBus.get()?.send(Event.FabClicked(it.id))
            }
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mPhoto.webp_images.isNotEmpty()) {
            mPresenter.showPhoto(mPhoto.webp_images[0].source)
        } else {
            onNoData()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(Constants.EXTRA_PHOTO, mPhoto)
    }

    override fun showPhoto(path: String) {
        Glide.with(context).load(path).placeholder(R.drawable.image_coming_soon).into(mImageView)
    }

    override fun onFabClicked(id: Int) {
        when (id) {
            R.id.fab_1 -> {
                sharePhoto()
            }
            R.id.fab_2 -> {
                requestPermission()
            }
            R.id.fab_3 -> {
                Toast.makeText(context, "fab 2 was clicked", Toast.LENGTH_SHORT).show()
            }
        }
        fabDisplayControll()
    }

    override fun onShareClicked(name: String, path: String) {
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.type = "text/plain"
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)

        // Add data to the intent, the receiving app will decide what to do with it.
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, name)
        sendIntent.putExtra(Intent.EXTRA_TEXT, path)

        startActivity(Intent.createChooser(sendIntent, resources.getString(R.string.title_share_link)))
    }

    override fun onNoData() {
        mNoData?.visibility = View.VISIBLE
    }

    override fun onSaveSuccessfaul(path: String) {
        Toast.makeText(context, resources.getString(R.string.msg_download_completed) +
                path, Toast.LENGTH_SHORT).show()
    }

    override fun onSaveFailed(msg: String?) {
        Log.d(TAG, "onSaveFailed:$msg")
        var simpleMsg = resources.getString(R.string.msg_download_failed)
        var error = if (msg != null) simpleMsg + ":\n$msg" else simpleMsg
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionGranted(permission: String) {
        if (Manifest.permission.WRITE_EXTERNAL_STORAGE == permission) {
            savePhoto()
        } else {
            Log.e(TAG, "Unknown permission: $permission")
        }
    }

    override fun requestPermission() {
        if (activity != null) {
            var rxPermission = RxPermissions(activity as Activity)
            rxPermission.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(Consumer<Permission>() {
                        if (it.granted) {
                            RxEventBus.get()?.send(Event.PermissionGranted(it.name))
                        } else {
                            Toast.makeText(context, resources.getString(R.string.msg_operation_cancel), Toast.LENGTH_SHORT).show()
                        }
                    })
        }
    }

    private fun fabDisplayControll() {
        if (mFabs.size > 1) {
            if (mFabs[PhotoViewPresenter.FAB_1]?.visibility == View.GONE) {
                for (i in 1 until mFabs.size) {
                    mFabs[i]?.visibility = View.VISIBLE
                }
            } else {
                for (i in 1 until mFabs.size) {
                    mFabs[i]?.visibility = View.GONE
                }
            }
        }
    }

    private fun sharePhoto() {
        if (mPhoto.webp_images.isNotEmpty()) {
            mPresenter.sharePhoto(mPhoto.name, mPhoto.webp_images[0].source)
        } else {
            Toast.makeText(activity, resources.getString(R.string.msg_unable_to_share), Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePhoto() {
        Toast.makeText(context, "Save photo: ${mPhoto.id}", Toast.LENGTH_SHORT).show()
        if (mPhoto.images.isNotEmpty()) {
            mPresenter.save(mPhoto.images[0].source)
        } else {
            onSaveFailed()
        }
    }

}