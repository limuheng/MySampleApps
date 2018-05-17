package com.muheng.photoviewer

import android.os.Bundle
import android.os.Message
import android.view.*
import android.view.View.OnTouchListener
import android.view.GestureDetector.SimpleOnGestureListener
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.muheng.facebook.Photo
import com.muheng.photoviewer.utils.Constants
import com.muheng.photoviewer.utils.PhotosManager

class PhotoFragment : FacebookFragment() {
    companion object {
        val TAG: String = "PhotoFragment"
    }

    private var mPhotoId : String? = null
    private var mPhoto : ImageView? = null

    private var mGestureDetector : GestureDetector? = null
    private var mLastStartX : Float = Constants.ZERO_F

    private var mGestureListener = object : SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            var moveDistance = e2?.x?.minus(e1?.x ?: Constants.ZERO_F) ?: Constants.ZERO_F
            if (moveDistance > Constants.SWIPE_THRESHOLD) {
                if (mLastStartX == e1?.x) {
                    return true;
                }
                mLastStartX = e1?.x ?: Constants.ZERO_F
                // prvious photo
                if (mPhotoId != null) {
                    var prevPhoto = PhotosManager.getInstance()?.goPrevData()
                    mPhotoId = prevPhoto?.mId ?: mPhotoId
                    updateUI()
                }
            } else if (moveDistance < -Constants.SWIPE_THRESHOLD) {
                if (mLastStartX == e1?.x) {
                    return true;
                }
                mLastStartX = e1?.x ?: Constants.ZERO_F
                // next photo
                if (mPhotoId != null) {
                    var nextPhoto = PhotosManager.getInstance()?.goNextData()
                    mPhotoId = nextPhoto?.mId ?: mPhotoId
                    updateUI()
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }
    }

    private var mTouchListener = object : OnTouchListener {
        override fun onTouch(view: View?, event: MotionEvent?): Boolean {
            return mGestureDetector?.onTouchEvent(event) ?: false
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            mPhotoId = savedInstanceState.getString(Constants.EXTRA_PHOTO_ID)
        } else {
            mPhotoId = activity?.intent?.getStringExtra(Constants.EXTRA_PHOTO_ID)
        }

        // When created, calibrate the data index to make sure display correctly
        PhotosManager.getInstance()?.calibrateCurrIdx(mPhotoId)

        // TODO handler photo id is null
        updateUI()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.fragment_photo, container, false)
        mPhoto = rootView.findViewById(R.id.photo)
        mPhoto?.setOnTouchListener(mTouchListener)
        mGestureDetector = GestureDetector(activity, mGestureListener);
        return rootView
    }

    override fun handleMessage(msg: Message?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showProgress(idx: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideProgress(idx: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateUI() {
        if (mPhotoId != null) {
            var photo : Photo? = PhotosManager.getInstance()?.getData(mPhotoId as String)
            Glide.with(activity).load(photo?.mSource).placeholder(R.drawable.image_coming_soon).into(mPhoto)
        }
    }

    override fun executeRequest() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBackPressed(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onVisible() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}