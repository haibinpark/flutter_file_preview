package com.example.flutter_file_preview

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.example.flutter_file_preview.LoadFileModel
import com.example.flutter_file_preview.LogUtil
import com.tencent.smtt.sdk.TbsReaderView
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class X5FilePreviewView internal constructor(val context: Context, messenger: BinaryMessenger?, id: Int, params: Map<String?, Any?>?, var plugin: FlutterFilePreviewPlugin) : PlatformView, MethodChannel.MethodCallHandler, TbsReaderView.ReaderCallback {
    val TAG = "X5FileReaderView"
    private var methodChannel: MethodChannel?
    private var readerView: TbsReaderView?
    private val tempPath: String
    override fun onMethodCall(methodCall: MethodCall, result: MethodChannel.Result) {
        when (methodCall.method) {
            "openFile" -> {
                val filePath = (methodCall.arguments as Map<String?, String?>)["file"]
                if (isSupportFile(filePath) == true) {
                    if (filePath?.startsWith("http") == true){
                        downLoadFromNet(filePath)
                    }else{
                        openFile(filePath)
                    }
                    result.success(true)
                } else {
                    //    plugin.openFileByMiniQb((String) methodCall.arguments);
                    result.success(false)
                }
            }
            "canOpen" -> result.success(isSupportFile(methodCall.arguments as String))
        }
    }

    fun openFile(filePath: String?) {
        if (isSupportFile(filePath) == true) {
            //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
            val bsReaderTempFile = File(tempPath)
            if (!bsReaderTempFile.exists()) {
                bsReaderTempFile.mkdir()
            }
            //加载文件
            val localBundle = Bundle()
            localBundle.putString("filePath", filePath)
            localBundle.putBoolean("is_bar_show", false)
            localBundle.putBoolean("menu_show", false)
            localBundle.putBoolean("is_bar_animating", false)
            localBundle.putString("tempPath", tempPath)
            readerView?.openFile(localBundle)
        }
    }

    fun isSupportFile(filePath: String?): Boolean? {
        return readerView?.preOpen(getFileType(filePath), false)
    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    private fun getFileType(paramString: String?): String {
        var str = ""
        if (TextUtils.isEmpty(paramString)) {
            return str
        }
        val i = paramString!!.lastIndexOf('.')
        if (i <= -1) {
            return str
        }
        str = paramString.substring(i + 1)
        return str
    }

    override fun getView(): View {
        return readerView!!
    }

    override fun dispose() {
        Log.d("FileReader", "FileReader Close")
        readerView?.onStop()
        methodChannel!!.setMethodCallHandler(null)
        methodChannel = null
        readerView = null
    }

    override fun onCallBackAction(integer: Int, o: Any, o1: Any) {}

    init {
        tempPath = context.cacheDir.toString() + "/" + "TbsReaderTemp"
        methodChannel = MethodChannel(messenger, FlutterFilePreviewPlugin.channelName + "_" + id)
        methodChannel!!.setMethodCallHandler(this)
        //这里的Context需要Activity
        readerView = TbsReaderView(context, this)
        readerView?.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }



    private fun downLoadFromNet(url: String?) {

        //1.网络下载、存储路径、
        val cacheFile = getCacheFile(url)
        if (cacheFile.exists()) {
            if (cacheFile.length() <= 0) {
                LogUtil.d(TAG, "删除空文件！！")
                cacheFile.delete()
                return
            }
        }
        LoadFileModel.loadPdfFile(url, object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                LogUtil.d(TAG, "下载文件-->onResponse")
                var flag: Boolean
                var `is`: InputStream? = null
                val buf = ByteArray(2048)
                var len = 0
                var fos: FileOutputStream? = null
                try {
                    val responseBody = response.body()
                    `is` = responseBody!!.byteStream()
                    val total = responseBody.contentLength()
                    val file1 = getCacheDir(url)
                    if (!file1.exists()) {
                        file1.mkdirs()
                        LogUtil.d(TAG, "创建缓存目录： $file1")
                    }


                    //fileN : /storage/emulated/0/pdf/kauibao20170821040512.pdf
                    val fileN = getCacheFile(url) //new File(getCacheDir(url), getFileName(url))
                    LogUtil.d(TAG, "创建缓存文件： $fileN")
                    if (!fileN.exists()) {
                        val mkdir = fileN.createNewFile()
                    }
                    fos = FileOutputStream(fileN)
                    var sum: Long = 0
                    while (`is`.read(buf).also { len = it } != -1) {
                        fos.write(buf, 0, len)
                        sum += len.toLong()
                        val progress = (sum * 1.0f / total * 100).toInt()
                        LogUtil.d(TAG, "写入缓存文件" + fileN.name + "进度: " + progress)
                    }
                    fos.flush()
                    LogUtil.d(TAG, "文件下载成功,准备展示文件。")
                    //2.ACache记录文件的有效期
                    displayFile(fileN)
                } catch (e: Exception) {
                    LogUtil.d(TAG, "文件下载异常 = $e")
                } finally {
                    try {
                        `is`?.close()
                    } catch (e: IOException) {
                    }
                    try {
                        fos?.close()
                    } catch (e: IOException) {
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                LogUtil.d(TAG, "文件下载失败${t.message}")
                val file = getCacheFile(url)
                if (!file.exists()) {
                    LogUtil.d(TAG, "删除下载失败文件")
                    file.delete()
                }
            }
        })
    }

    private fun displayFile(mFile: File?) {
        if (mFile != null && !TextUtils.isEmpty(mFile.toString())) {
            //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
            val bsReaderTemp = "/storage/emulated/0/TbsReaderTemp"
            val bsReaderTempFile = File(bsReaderTemp)
            if (!bsReaderTempFile.exists()) {
                LogUtil.d("准备创建/storage/emulated/0/TbsReaderTemp！！")
                val mkdir = bsReaderTempFile.mkdir()
                if (!mkdir) {
                    LogUtil.e("创建/storage/emulated/0/TbsReaderTemp失败！！！！！")
                }
            }
//            //加载文件
//            val params = HashMap<String, String>()
//            params["style"] = "1"
//            params["local"] = "true"
//            QbSdk.openFileReader(this@FileDisplayActivity, mFile.toString(), params) {
//                LogUtil.d(it)
//            }
            val tempPath = Environment.getExternalStorageDirectory().toString() + "/" + "TbsReaderTemp"
            val localBundle = Bundle()
            localBundle.putString("filePath", mFile.toString())
            localBundle.putBoolean("is_bar_show", false)
            localBundle.putBoolean("menu_show", false)
            localBundle.putBoolean("is_bar_animating", false)
            localBundle.putString("tempPath", tempPath)
            if (readerView == null){
                readerView = TbsReaderView(context,TbsReaderView.ReaderCallback { p0, p1, p2 -> })
            }
            val preOpen = readerView?.preOpen(getFileType(mFile.toString()), false)
            if (preOpen == true) {
                readerView?.openFile(localBundle)
            }
        } else {
            LogUtil.e("文件路径无效！")
        }
    }


    /***
     * 获取缓存目录
     *
     * @param url
     * @return
     */
    private fun getCacheDir(url: String?): File {
        return File(Environment.getExternalStorageDirectory().absolutePath + "/007/")
    }

    /***
     * 绝对路径获取缓存文件
     *
     * @param url
     * @return
     */
    private fun getCacheFile(url: String?): File {
        val cacheFile = File(Environment.getExternalStorageDirectory().absolutePath + "/007/"
                + getFileName(url))
        LogUtil.d(TAG, "缓存文件 = $cacheFile")
        return cacheFile
    }

    /***
     * 根据链接获取文件名（带类型的），具有唯一性
     *
     * @param url
     * @return
     */
    private fun getFileName(url: String?): String {
        return Md5Tool.hashKey(url).toString() + "." + getFileType(url)
    }



}