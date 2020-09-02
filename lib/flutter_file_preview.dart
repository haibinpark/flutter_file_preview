import 'dart:io';
import 'package:flutter/foundation.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'file_preview.dart';

class FilePreviewView extends StatefulWidget {
  final String filePath; //local path
  final Function(bool) openSuccess;
  final Widget loadingWidget;
  final Widget unSupportFileWidget;

  FilePreviewView(
      {Key key,
        this.filePath,
        this.openSuccess,
        this.loadingWidget,
        this.unSupportFileWidget})
      : super(key: key);

  @override
  _FileReaderViewState createState() => _FileReaderViewState();
}

class _FileReaderViewState extends State<FilePreviewView> {
  FilePreviewState _status = FilePreviewState.LOADING_ENGINE;
  String filePath;

  @override
  void initState() {
    super.initState();
    filePath = widget.filePath;
    if (!filePath.startsWith("http")) {
      File(filePath).exists().then((exists) {
        if (exists) {
          _checkOnLoad();
        } else {
          _setStatus(FilePreviewState.FILE_NOT_FOUND);
        }
      });
    } else {
      _checkOnLoad();
    }
  }

  _checkOnLoad() {
    FilePreview.engineLoadStatus((success) {
      if (success) {
        _setStatus(FilePreviewState.ENGINE_LOAD_SUCCESS);
      } else {
        _setStatus(FilePreviewState.ENGINE_LOAD_FAIL);
      }
    });
  }

  _setStatus(FilePreviewState status) {
    _status = status;
    if (mounted) {
      setState(() {});
    }
  }

  @override
  Widget build(BuildContext context) {
    if (Platform.isAndroid || Platform.isIOS) {
      if (_status == FilePreviewState.LOADING_ENGINE) {
        return _loadingWidget();
      } else if (_status == FilePreviewState.UNSUPPORT_FILE) {
        return _unSupportFile();
      } else if (_status == FilePreviewState.ENGINE_LOAD_SUCCESS) {
        if (Platform.isAndroid) {
          return _createAndroidView();
        } else {
          return _createIosView();
        }
      } else if (_status == FilePreviewState.ENGINE_LOAD_FAIL) {
        return _enginLoadFail();
      } else if (_status == FilePreviewState.FILE_NOT_FOUND) {
        return _fileNotFoundFile();
      } else {
        return _loadingWidget();
      }
    } else {
      return Center(child: Text("不支持的平台"));
    }
  }

  Widget _unSupportFile() {
    return widget.unSupportFileWidget ??
        Center(
          child: Text("不支持打开${_fileType(filePath)}类型的文件"),
        );
  }

  Widget _fileNotFoundFile() {
    return Center(
      child: Text("文件不存在"),
    );
  }

  Widget _enginLoadFail() {
    //最有可能是abi的问题,x5不支持64位的arm架构,所以需要abi过滤为armeabi 或者armv7a
    //还有可能第一次下载成功,但是加载不成功
    return Center(
      child: Text("引擎加载失败,请退出重试"),
    );
  }

  Widget _loadingWidget() {
    return widget.loadingWidget ??
        Center(
          child: CupertinoActivityIndicator(),
        );
  }

  Widget _createAndroidView() {
    return AndroidView(
        viewType: "FilePreview",
        onPlatformViewCreated: _onPlatformViewCreated,
        creationParamsCodec: StandardMessageCodec());
  }

  _onPlatformViewCreated(int platformViewId) {
    FilePreview.openFile(platformViewId,filePath, (success) {
      if (!success) {
        _setStatus(FilePreviewState.UNSUPPORT_FILE);
      }
      widget.openSuccess?.call(success);
    });
  }

  Widget _createIosView() {
    return UiKitView(
      viewType: "FilePreview",
      onPlatformViewCreated: _onPlatformViewCreated,
      creationParamsCodec: StandardMessageCodec(),
    );
  }

  String _fileType(String filePath) {
    if (filePath == null || filePath.isEmpty) {
      return "";
    }
    int i = filePath.lastIndexOf('.');
    if (i <= -1) {
      return "";
    }
    return filePath.substring(i + 1);
  }
}
