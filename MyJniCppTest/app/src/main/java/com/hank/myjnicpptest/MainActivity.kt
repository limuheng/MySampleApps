package com.hank.myjnicpptest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        val sort = MySort()
        var array = intArrayOf(3, 2, 6, 8, 5, 1, 9, 7)
        Log.d("MainActivity", "Array before selection sort: ${array.contentToString()}")
        sort.selectionSort(array)
        Log.d("MainActivity", "Array after selection sort: ${array.contentToString()}")

        array = intArrayOf(3, 2, 6, 8, 5, 1, 9, 7)
        Log.d("MainActivity", "Array before bubble sort: ${array.contentToString()}")
        sort.bubbleSort(array)
        Log.d("MainActivity", "Array after bubble sort: ${array.contentToString()}")

        array = intArrayOf(3, 2, 6, 8, 5, 1, 9, 7)
        Log.d("MainActivity", "Array before insertion sort: ${array.contentToString()}")
        sort.insertionSort(array)
        Log.d("MainActivity", "Array after insertion sort: ${array.contentToString()}")
    }
}
