import 'dart:async';

import 'package:flutter/services.dart';

class FilePreview {
  static const MethodChannel _channel =
      const MethodChannel('me.haibin.file_preview');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> openFile(int platformViewId,String fileName, Function(bool) onOpen) {
    Map<String, String> params = {"file": fileName};
    MethodChannel('me.haibin.file_preview'+"_$platformViewId")
        .invokeMethod('openFile', params)
        .then((value) => {onOpen?.call(value)});
  }

  static void engineLoadStatus(Function(bool) loadCallback) async {
    _channel.invokeMethod("isLoad").then((status) {
      if (status == 5) {
        loadCallback?.call(true);
      } else if (status == 10) {
        loadCallback?.call(false);
      } else if (status == -1) {
        _channel.setMethodCallHandler((call) {
          if (call.method == "onLoad") {
            int status = call.arguments;
            if (status == 5) {
              loadCallback?.call(true);
            } else if (status == 10) {
              loadCallback?.call(false);
            }
          }
          return;
        });
      }
    });
  }
}

enum FilePreviewState {
  LOADING_ENGINE, //loading engine
  ENGINE_LOAD_SUCCESS, //loading engine success
  ENGINE_LOAD_FAIL, //loading engine fail (only Android ,ios,Ignore)
  UNSUPPORT_FILE, // not support file type
  FILE_NOT_FOUND, //file not found
}
