package com.hank.myjnitest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.hank.nativelibtest.NativeLib

class MainActivity : AppCompatActivity() {

    private val nativeSort: NativeSort? = NativeSort.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        var array = intArrayOf(3, 2, 6, 8, 5, 1, 9, 7)
        Log.d(NativeSort.tag, "Array before selection sort: ${array.contentToString()}")
        nativeSort?.selectionSort(array)
        Log.d(NativeSort.tag, "Array after selection sort: ${array.contentToString()}")

        array = intArrayOf(3, 2, 6, 8, 5, 1, 9, 7)
        Log.d(NativeSort.tag, "Array before bubble sort: ${array.contentToString()}")
        nativeSort?.bubbleSort(array)
        Log.d(NativeSort.tag, "Array after bubble sort: ${array.contentToString()}")

        array = intArrayOf(3, 2, 6, 8, 5, 1, 9, 7)
        Log.d(NativeSort.tag, "Array before insertion sort: ${array.contentToString()}")
        nativeSort?.insertionSort(array)
        Log.d(NativeSort.tag, "Array after insertion sort: ${array.contentToString()}")

        val nativeLib = NativeLib()
        nativeLib.sayHello()
    }
}
