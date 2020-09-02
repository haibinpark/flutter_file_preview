import 'dart:async';

import 'package:flutter/services.dart';

class FlutterFilePreview {
  static const MethodChannel _channel =
      const MethodChannel('flutter_file_preview');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
