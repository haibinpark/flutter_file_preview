package com.example.flutter_file_preview


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log

class NetBroadcastReceiver internal constructor(var listener: NetChangeListener?) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        // 如果相等的话就说明网络状态发生了变化
        Log.i("NetBroadcastReceiver", "NetBroadcastReceiver changed")
        if (intent.action != null && intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val netWorkState: Int = NetUtil.getNetWorkState(context)
            // 当网络发生变化，判断当前网络状态，并通过NetEvent回调当前网络状态
            if (listener != null) {
                listener!!.onChangeListener(netWorkState)
            }
        }
    }

    // 自定义接口
    interface NetChangeListener {
        fun onChangeListener(status: Int)
    }

}