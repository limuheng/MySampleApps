package com.muheng.fragmentswitch

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by limuh on 2018/7/6.
 */
class MyFragment : Fragment() {

    private var mTextView: TextView? = null

    var mId: Int = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        var rootView = inflater?.inflate(R.layout.layout_fragment, container, false)
        mTextView = rootView?.findViewById(R.id.message)
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTextView?.text = "Fragment $mId"
    }
}