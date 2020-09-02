import 'package:flutter/material.dart';
import 'package:flutter_file_preview/flutter_file_preview.dart';

class FilePreviewPage extends StatefulWidget {
  final String filePath;

  FilePreviewPage({Key key, this.filePath}) : super(key: key);

  @override
  _FilePreviewPageState createState() => _FilePreviewPageState();
}

class _FilePreviewPageState extends State<FilePreviewPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("文档显示"),
      ),
      body: FilePreviewView(
        filePath: widget.filePath,
      ),
//      body: Container(),
    );
  }
}
