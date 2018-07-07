package com.muheng.fragmentswitch

import java.lang.ref.WeakReference

/**
 * Created by limuh on 2018/7/6.
 */
class FragmentsPresenter(private val view : WeakReference<IFragmentsHolder>) {
    fun switch(from: Int, to: Int) {
        view.get()?.switchFragment(from, to)
    }
}