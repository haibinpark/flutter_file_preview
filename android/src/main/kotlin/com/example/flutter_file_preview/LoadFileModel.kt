package com.example.flutter_file_preview

import android.text.TextUtils
import okhttp3.ResponseBody
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by 12457 on 2017/8/21.
 */
object LoadFileModel {
    fun loadPdfFile(url: String?, callback: Callback<ResponseBody?>?) {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://www.baidu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val mLoadFileApi = retrofit.create(LoadFileApi::class.java)
        if (!TextUtils.isEmpty(url)) {
            val call = mLoadFileApi.loadPdfFile(url)
            call!!.enqueue(callback)
        }
    }
}