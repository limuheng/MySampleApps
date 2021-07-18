package com.hank.myjnitest

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class NativeSort private constructor() {
    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("NativeSort")
        }

        val tag: String = "NativeSort"

        private val sLocker = ReentrantLock()

        private var sInstance: NativeSort? = null

        fun getInstance(): NativeSort? {
            sLocker.withLock {
                if (sInstance == null) {
                    sInstance = NativeSort()
                }
                return sInstance
            }
        }
    }

    private external fun nativeSelectionSort(array: IntArray)
    private external fun nativeBubbleSort(array: IntArray)
    private external fun nativeInsertionSort(array: IntArray)

    fun selectionSort(array: IntArray) {
        nativeSelectionSort(array)
    }

    fun bubbleSort(array: IntArray) {
        nativeBubbleSort(array)
    }

    fun insertionSort(array: IntArray) {
        nativeInsertionSort(array)
    }
}