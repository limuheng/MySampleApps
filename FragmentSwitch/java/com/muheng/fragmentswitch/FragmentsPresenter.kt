package com.muheng.fragmentswitch

import java.lang.ref.WeakReference

/**
 * Created by limuh on 2018/7/6.
 */
class FragmentsPresenter(view: IFragmentsHolder) {

    private var mView = WeakReference<IFragmentsHolder>(view)

    fun switch(from: Int, to: Int) {
        mView.get()?.switchFragment(from, to)
    }
}