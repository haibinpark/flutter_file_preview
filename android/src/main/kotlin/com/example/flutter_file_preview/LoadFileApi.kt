package com.example.flutter_file_preview

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Created by 12457 on 2017/8/21.
 */
interface LoadFileApi {
    @GET
    fun loadPdfFile(@Url fileUrl: String?): Call<ResponseBody?>?
}