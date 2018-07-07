package com.muheng.fragmentswitch

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference

/**
 * Created by limuh on 2018/7/6.
 */
class MainActivity : AppCompatActivity(), IFragmentsHolder {

    private val KEY_FRAG_IDX = "KEY_FRAG_IDX"

    private var mFragments: Array<MyFragment> = arrayOf(MyFragment(), MyFragment(), MyFragment())

    private var mDisplayIdx: Int = 0

    private lateinit var mPresenter: FragmentsPresenter

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                mPresenter.switch(mDisplayIdx, 0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                mPresenter.switch(mDisplayIdx, 1)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                mPresenter.switch(mDisplayIdx, 2)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(
                    R.id.fragment_container, mFragments[0]).commit()
            mDisplayIdx = 0
        } else {
            var idx = savedInstanceState.getInt(KEY_FRAG_IDX)
            if (idx in 0 until mFragments.size) {
                mDisplayIdx = idx
            } else {
                mDisplayIdx = 0
            }
        }

        mPresenter = FragmentsPresenter(WeakReference<IFragmentsHolder>(this))
    }

    override fun onPostResume() {
        super.onPostResume()
        initFragments()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(KEY_FRAG_IDX, mDisplayIdx)
    }

    override fun switchFragment(from: Int, to: Int) {
        if (mDisplayIdx != to) {
            try {
                var transaction = supportFragmentManager.beginTransaction()
                if (!mFragments[to].isAdded) {
                    transaction.hide(mFragments[from]).add(R.id.fragment_container, mFragments[to]).commit()
                } else {
                    transaction.hide(mFragments[from]).show(mFragments[to]).commit()
                }
                mDisplayIdx = to
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initFragments() {
        for (i in 0 until mFragments.size) {
            mFragments[i].mId = i
        }
    }
}
