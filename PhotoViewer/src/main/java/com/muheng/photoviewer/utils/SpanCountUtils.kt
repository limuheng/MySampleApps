package com.muheng.photoviewer.utils

/**
 * Created by Muheng Li on 2018/7/18.
 */
class SpanCountUtils {

    companion object {

        private var sSpanCount: Int = Constants.SPAN_COUNT_NORMAL

        fun getSpanCount(): Int {
            return sSpanCount
        }

        fun calculateSpanCount() {
            var screenWidthDp = DimenUtils.getScreenWidthDp()
            sSpanCount = when {
                (screenWidthDp >= Constants.SW_XLARGE) -> Constants.SPAN_COUNT_XLARGE
                (screenWidthDp >= Constants.SW_LARGE) -> Constants.SPAN_COUNT_LARGE
                else -> Constants.SPAN_COUNT_NORMAL
            }
        }

        init {
            // Initialize span count of a row
            calculateSpanCount()
        }

    }

}