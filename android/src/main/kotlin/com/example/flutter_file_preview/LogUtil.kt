package com.example.flutter_file_preview

import android.text.TextUtils
import android.util.Log

object LogUtil {
    private const val LOG_TAG = "superFileLog"
    private const val DEBUG = true
    fun e(log: String) {
        if (DEBUG && !TextUtils.isEmpty(log)) Log.e(LOG_TAG, "" + log)
    }

    fun i(log: String?) {
        if (DEBUG && !TextUtils.isEmpty(log)) Log.i(LOG_TAG, log)
    }

    fun i(tag: String?, log: String?) {
        if (DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(log)) Log.i(tag, log)
    }

    fun d(tag: String?, log: String?) {
        if (DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(log)) Log.d(tag, log)
    }

    fun d(log: String?) {
        if (DEBUG && !TextUtils.isEmpty(log)) Log.d(LOG_TAG, log)
    }

    fun e(tag: String?, log: String?) {
        if (DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(log)) Log.e(tag, log)
    }
}