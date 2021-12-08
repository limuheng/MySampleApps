package com.hank.cameratestapp.utils

import android.util.Size
import java.util.*

class CompareSizesByArea: Comparator<Size> {

    companion object {
        private val comparator by lazy { CompareSizesByArea() }

        fun getLargestSize(sizes: Array<Size>): Size {
            return Collections.max(sizes.asList(), comparator)
        }

        fun getSmallestSize(sizes: Array<Size>): Size {
            return Collections.min(sizes.asList(), comparator)
        }
    }

    override fun compare(lhs: Size, rhs: Size): Int {
        // We cast here to ensure the multiplications won't overflow
        return java.lang.Long.signum(
            lhs.width.toLong() * lhs.height.toLong() - rhs.width.toLong() * rhs.height.toLong())
    }
}