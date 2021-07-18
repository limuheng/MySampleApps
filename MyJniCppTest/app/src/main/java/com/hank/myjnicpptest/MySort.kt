package com.hank.myjnicpptest

class MySort() {
    companion object {
        val tag: String = "MySort"

        init {
            System.loadLibrary("NativeSort")
        }
    }

    private external fun createNativeSort(): Long
    private external fun nativeSelectionSort(obj: Long, array: IntArray)
    private external fun nativeBubbleSort(obj: Long, array: IntArray)
    private external fun nativeInsertionSort(obj: Long, array: IntArray)

    private var nativeInstance = 0L

    init {
        nativeInstance = createNativeSort()
    }

    fun selectionSort(array: IntArray) {
        nativeSelectionSort(nativeInstance, array)
    }

    fun bubbleSort(array: IntArray) {
        nativeBubbleSort(nativeInstance, array)
    }

    fun insertionSort(array: IntArray) {
        nativeInsertionSort(nativeInstance, array)
    }
}