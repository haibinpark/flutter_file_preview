import 'dart:convert';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_file_preview/file_preview.dart';
import 'package:path_provider/path_provider.dart';
import 'package:permission_handler/permission_handler.dart';

import 'file.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
//    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await FilePreview.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: HomePage(),
    );
  }
}

class HomePage extends StatefulWidget {
  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  String taskId;

  Map<String, String> iosfiles = {
    "docx": "assets/files/docx.docx", // IOS test
    "doc": "assets/files/doc.doc", // IOS test
    "xlsx": "assets/files/xlsx.xlsx", // IOS test
    "xls": "assets/files/xls.xls", // IOS test
    "pptx": "assets/files/pptx.pptx", // IOS test
    "ppt": "assets/files/ppt.ppt", // IOS test
    "pdf": "assets/files/pdf.pdf", // IOS test
    "txt": "assets/files/txt.txt", // IOS test
    "jpg": "assets/files/jpg.jpg", //
    "jpeg": "assets/files/jpeg1.jpeg", //
    "png": "assets/files/png.png", //
    "在线DOC": "http://res.imtt.qq.com/TES/HowToLoadX5Core.doc"
  };

  Map<String, String> androidfiles = {
    "docx": "assets/files/docx.docx", // android test
    "doc": "assets/files/doc.doc", // android test
    "xlsx": "assets/files/xlsx.xlsx", // android test
    "xls": "assets/files/xls.xls", // android test
    "pptx": "assets/files/pptx.pptx", // android test
    "ppt": "assets/files/ppt.ppt", // android test
    "pdf": "assets/files/pdf.pdf", // android test
    "txt": "assets/files/txt.txt",
    "在线DOC": "http://res.imtt.qq.com/TES/HowToLoadX5Core.doc" // android test
  };

  Map<String, String> files;

  @override
  void initState() {
    if (Platform.isAndroid) {
      files = androidfiles;
    } else {
      files = iosfiles;
    }
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("File Preview"),
      ),
      body: ListView.builder(
        itemBuilder: (ctx, index) {
          return item(
              files.keys.elementAt(index), files.values.elementAt(index));
        },
        itemCount: files.length,
      ),
    );
  }

  item(String type, String path) {
    return GestureDetector(
      onTap: () {
        onTap(type, path);
      },
      child: Container(
        alignment: Alignment.center,
        height: 50,
        margin: EdgeInsetsDirectional.only(bottom: 5),
        color: Colors.blue,
        child: Center(
          child: Text(
            type,
            style: TextStyle(color: Colors.white, fontSize: 20),
          ),
        ),
      ),
    );
  }

  onTap(String type, String assetPath) async {
    var filePath = assetPath;
    if (!assetPath.startsWith("http:")) {
      filePath = await fileLocalName(type, assetPath);
      if (!await File(filePath).exists()) {
        if (!await asset2Local(type, assetPath)) {
          return;
        }
      }
    }
    print(filePath);
    Navigator.of(context).push(MaterialPageRoute(builder: (ctx) {
      return FilePreviewPage(
        filePath: filePath,
      );
    }));
  }

  fileLocalName(String type, String assetPath) async {
    String dic = await _localSavedDir() + "/filereader/files/";
    return dic + base64.encode(utf8.encode(assetPath)) + "." + type;
  }

  fileExists(String type, String assetPath) async {
    String fileName = await fileLocalName(type, assetPath);
    if (await File(fileName).exists()) {
      return true;
    }
    return false;
  }

  asset2Local(String type, String assetPath) async {
    if (Platform.isAndroid) {
      if (await PermissionHandler()
              .checkPermissionStatus(PermissionGroup.storage) !=
          PermissionStatus.granted) {
        debugPrint("没有存储权限");
        return false;
      }
    }

    File file = File(await fileLocalName(type, assetPath));
    if (await fileExists(type, assetPath)) {
      await file.delete();
    }
    await file.create(recursive: true);
    //await file.create();
    debugPrint("文件路径->" + file.path);
    ByteData bd = await rootBundle.load(assetPath);
    await file.writeAsBytes(bd.buffer.asUint8List(), flush: true);
    return true;
  }

  _localSavedDir() async {
    Directory dic;
    if (defaultTargetPlatform == TargetPlatform.android) {
      dic = await getExternalStorageDirectory();
    } else if (defaultTargetPlatform == TargetPlatform.iOS) {
      dic = await getApplicationDocumentsDirectory();
    }
    return dic.path;
  }
}
