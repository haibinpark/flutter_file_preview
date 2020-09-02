# flutter 文件浏览(`flutter file preview`)
这是一个Flutter的文件预览插件。Android使用的是TBS(腾讯浏览服务)，iOS使用的是原生WKWebView

## 支持文件类型
### iOS
```
docx,doc,xlsx,xls,pptx,ppt,pdf,txt,jpg,jpeg,png,在线文件
```
### Android
```
docx,doc,xlsx,xls,pptx,ppt,pdf,txt,在线文件
```
## 使用方法
### 导入
修改pubspec.yaml
```
flutter_file_preview:
    git:
        url: git://github.com/haibinpark/flutter_file_preview.git
```
### 使用
```
import 'package:flutter/material.dart';
import 'package:flutter_file_preview/file_preview_view.dart';

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

```

## 参考地址
在处理的过程中，参考了以下工程

- https://github.com/aliyoge/flutter_file_preview
- https://github.com/shingohu/flutter_filereader.git

## 注意事项
在iOS中的调试的时候，在`info.plist`文件中新增
```
io.flutter.embedded_views_preview bool YES
App Transport Security Settings
```

在`App Transport Security Settings Dictionary`添加子项
`Allow Arbitrary Loads bool YES`
