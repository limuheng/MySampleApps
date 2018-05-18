package com.muheng.photoviewer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.muheng.photoviewer.fragment.FacebookFragment
import com.muheng.photoviewer.fragment.PhotoFragment

class PhotoActivity : AppCompatActivity() {
    var mFragment : FacebookFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        if (savedInstanceState == null) {
            mFragment = PhotoFragment()
            supportFragmentManager.beginTransaction().
                    add(R.id.fragment_container, mFragment, PhotoFragment.TAG).commit()
        } else {
            mFragment = supportFragmentManager.findFragmentByTag(PhotoFragment.TAG) as FacebookFragment
        }
    }
}