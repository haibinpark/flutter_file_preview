package com.example.flutter_file_preview

import android.content.Context
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.NonNull;
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.TbsListener

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.util.*

/** FlutterFilePreviewPlugin */
class FlutterFilePreviewPlugin: MethodChannel.MethodCallHandler, FlutterPlugin, ActivityAware {
  val TAG = "FilePreviewPlugin"
  private var x5LoadStatus = -1 // -1 未加载状态  5 成功 10 失败
  private var ctx: Context? = null
  private var methodChannel: MethodChannel? = null
  private var netBroadcastReceiver: NetBroadcastReceiver? = null
  private var pluginBinding: FlutterPlugin.FlutterPluginBinding? = null
  private var preInitCallback: QbSdkPreInitCallback? = null
  private var mainHandler: Handler? = Handler(Looper.getMainLooper(), Handler.Callback { msg ->
    if (msg.what == 100) {
      if (methodChannel != null) {
        methodChannel!!.invokeMethod("onLoad", isLoadX5)
      }
    }
    false
  })

  private fun init(context: Context, messenger: BinaryMessenger) {
    ctx = context
    methodChannel = MethodChannel(messenger, channelName)
    methodChannel!!.setMethodCallHandler(this)
    // initX5(context);
    netBroadcastRegister(context)
  }

  private fun onDestory() {
    Log.e("FileReader", "销毁")
    if (netBroadcastReceiver != null && ctx != null) {
      ctx!!.unregisterReceiver(netBroadcastReceiver)
    }
    preInitCallback = null
    ctx = null
    mainHandler!!.removeCallbacksAndMessages(null)
    mainHandler = null
    methodChannel = null
    pluginBinding = null
  }

  fun netBroadcastRegister(context: Context) {
    //实例化IntentFilter对象
    val filter = IntentFilter()
    filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
    netBroadcastReceiver = NetBroadcastReceiver(object : NetBroadcastReceiver.NetChangeListener {
      override fun onChangeListener(status: Int) {
        // -1 没有网络
        if (x5LoadStatus != 5 && status != -1) {
          initX5(context)
        }
      }
    })
    //注册广播接收
    context.registerReceiver(netBroadcastReceiver, filter)
  }

  fun initX5(context: Context?) {
    Log.e("FileReader", "初始化X5")
    if (!QbSdk.canLoadX5(context)) {
      //重要
      QbSdk.reset(context)
    }
    preInitCallback = QbSdkPreInitCallback()
    // 在调用TBS初始化、创建WebView之前进行如下配置，以开启优化方案
    val map = HashMap<String, Any>()
    map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
    map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
    QbSdk.initTbsSettings(map)
    QbSdk.setNeedInitX5FirstTime(true)
    QbSdk.setDownloadWithoutWifi(true)
    QbSdk.setTbsListener(object : TbsListener {
      override fun onDownloadFinish(i: Int) {
        Log.e("FileReader", "TBS下载完成")
      }

      override fun onInstallFinish(i: Int) {
        Log.e("FileReader", "TBS安装完成")
      }

      override fun onDownloadProgress(i: Int) {
        Log.e("FileReader", "TBS下载进度:$i")
      }
    })
    QbSdk.initX5Environment(context, preInitCallback)
  }

  override fun onMethodCall(methodCall: MethodCall, result: MethodChannel.Result) {
    if ("isLoad" == methodCall.method) {
      result.success(isLoadX5)
    } else if ("openFileByMiniQb" == methodCall.method) {
      val filePath = methodCall.arguments as String
      result.success(openFileByMiniQb(filePath))
    }
  }

  fun openFileByMiniQb(filePath: String?): Boolean {
    if (ctx != null) {
      val params = HashMap<String, String>()
      params["style"] = "1"
      params["local"] = "false"
      QbSdk.openFileReader(ctx, filePath, params) { s -> Log.d("FileReader", "openFileReader->$s") }
    }
    return true
  }

  private fun onX5LoadComplete() {
    mainHandler!!.sendEmptyMessage(100)
  }

  val isLoadX5: Int
    get() {
      if (ctx != null && QbSdk.canLoadX5(ctx)) {
        x5LoadStatus = 5
      }
      return x5LoadStatus
    }

  override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    Log.e("FileReader", "onAttachedToEngine")
    pluginBinding = binding
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    Log.e("FileReader", "onDetachedFromEngine")
    onDestory()
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    Log.e("FileReader", "onAttachedToActivity")
    init(pluginBinding!!.applicationContext, pluginBinding!!.binaryMessenger)
    pluginBinding!!.platformViewRegistry.registerViewFactory("FilePreview", X5FilePreviewFactory(pluginBinding!!.binaryMessenger, binding.activity, this))
  }

  override fun onDetachedFromActivityForConfigChanges() {
    Log.e("FileReader", "onDetachedFromActivityForConfigChanges")
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    Log.e("FileReader", "onReattachedToActivityForConfigChanges")
  }

  override fun onDetachedFromActivity() {
    Log.e("FileReader", "onDetachedFromActivity")
  }

  internal inner class QbSdkPreInitCallback : QbSdk.PreInitCallback {
    override fun onCoreInitFinished() {
      Log.e("FileReader", "TBS内核初始化结束")
    }

    override fun onViewInitFinished(b: Boolean) {
      if (ctx == null) {
        return
      }
      if (b) {
        x5LoadStatus = 5
        Log.e("FileReader", "TBS内核初始化成功" + "--" + QbSdk.canLoadX5(ctx))
      } else {
        x5LoadStatus = 10
        resetQbSdkInit()
        Log.e("FileReader", "TBS内核初始化失败" + "--" + QbSdk.canLoadX5(ctx))
      }
      onX5LoadComplete()
    }
  }

  ///反射 重置初始化状态(没网情况下加载失败)
  private fun resetQbSdkInit() {
    try {
      val field = QbSdk::class.java.getDeclaredField("s")
      field.isAccessible = true
      field.setBoolean(null, false)
    } catch (e: NoSuchFieldException) {
      e.printStackTrace()
    } catch (e: IllegalAccessException) {
      e.printStackTrace()
    }
  }

  companion object {
    const val channelName = "me.haibin.file_preview"
  }
}
