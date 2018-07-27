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

        if (savedInstanceState == null) {
            addFragments()
        } else {
            mDisplayIdx = savedInstanceState.getInt(KEY_FRAG_IDX)
        }

        mPresenter = FragmentsPresenter(this)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        Log.d("MainActivity", "fragments count: ${supportFragmentManager.fragments.size}")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(KEY_FRAG_IDX, mDisplayIdx)
    }

    override fun switchFragment(from: Int, to: Int) {
        if (mDisplayIdx != to) {
            try {
                var fragmentFrom = supportFragmentManager.findFragmentByTag("fragment${from}")
                var fragmentTo = supportFragmentManager.findFragmentByTag("fragment${to}")
                var transaction = supportFragmentManager.beginTransaction()
                if (fragmentTo == null) {
                    var fragment = MyFragment()
                    fragment.mId = to
                    transaction.hide(fragmentFrom).add(R.id.fragment_container, fragment, to.toString()).commit()
                } else if (!fragmentTo.isAdded) {
                    transaction.hide(fragmentFrom).add(R.id.fragment_container, fragmentTo, to.toString()).commit()
                } else {
                    transaction.hide(fragmentFrom).show(fragmentTo).commit()
                }
                mDisplayIdx = to
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun addFragments() {
        var fragment1 = MyFragment()
        fragment1.mId = 0
        var fragment2 = MyFragment()
        fragment2.mId = 1
        var fragment3 = MyFragment()
        fragment3.mId = 2

        var transaction = supportFragmentManager.beginTransaction()

        // Add first fragment
        transaction.add(R.id.fragment_container, fragment1, "fragment${fragment1.mId}")
        // Add second fragment
        transaction.add(R.id.fragment_container, fragment2, "fragment${fragment2.mId}")
        // Add third fragment
        transaction.add(R.id.fragment_container, fragment3, "fragment${fragment3.mId}")

        // show second fragment & hide second fragment
        transaction.hide(fragment3).hide(fragment2).show(fragment1).commit()
        Log.d("MainActivity", "[addFragments] fragment count: ${supportFragmentManager.fragments.size}")

        mDisplayIdx = 0
    }
}
