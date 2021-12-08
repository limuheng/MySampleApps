package com.hank.cameratestapp.utils

typealias Callback<T> = (data: T?, throwable: Throwable?) -> Unit

const val API_TIMEOUT = 3000L
const val API_POLLING_DURATION = 500L