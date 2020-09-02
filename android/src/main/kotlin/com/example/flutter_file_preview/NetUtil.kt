package com.example.flutter_file_preview

import android.content.Context
import android.net.ConnectivityManager


object NetUtil {
    /**
     * 没有网络
     */
    val NETWORK_NONE = -1

    /**
     * 移动网络
     */
    val NETWORK_MOBILE = 0

    /**
     * 无线网络
     */
    val NETWORK_WIFI = 1

    fun getNetWorkState(context: Context): Int {
        //得到连接管理器对象
        val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val activeNetworkInfo = connectivityManager
                    .activeNetworkInfo
            //如果网络连接，判断该网络类型
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                    return NETWORK_WIFI //wifi
                } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                    return NETWORK_MOBILE //mobile
                }
            } else {
                //网络异常
                return NETWORK_NONE
            }
        }
        return NETWORK_NONE
    }
}