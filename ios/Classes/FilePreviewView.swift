//
//  FilePreviewView.swift
//  file_preview
//
//  Created by haibin on 2020/8/13.
//

import UIKit
import WebKit

class FilePreviewView: NSObject,FlutterPlatformView,DownloadDelegate {
    func downloaded(path: String) {
        self.openFile(filePath: path)
    }

    var _webView: FilePreviewWebKit?
    var downloader:DownLoader?
    
    init(withFrame frame: CGRect, viewIdentifier viewId: Int64, arguments args: Any?,binaryMessenger messenger: FlutterBinaryMessenger) {
        super.init()
        let channelName1 = channelName+"_" + String(viewId)
        let channel = FlutterMethodChannel.init(name: channelName1, binaryMessenger: messenger)
        downloader = DownLoader(delegate: self)
        channel.setMethodCallHandler { (call, result) in
            if call.method == "openFile" {
                var path = (call.arguments as! Dictionary<String,String>)["file"] as! String
                if isSupportOpen(fileType: fileType(filePath: path)){
                    if(path.starts(with: "http")){
                        self.downloader?.downLoader(url: NSURL(string: path)!)
                        path = self.downloader?.downLoadFilePath() as! String
                        self.openFile(filePath: path)
                    }else{
                        self.openFile(filePath: path)
                    }
                    result(true)
                }else{
                    result(false)
                }
                return
            }
        }
        
        self._webView = FilePreviewWebKit.init(frame: frame)
    }
    
    func view() -> UIView {
        return self._webView!
    }
    
    func openFile(filePath:String){
        let url = URL.init(fileURLWithPath: filePath)
        
        if #available(iOS 9.0, *) {
            _webView?.loadFileURL(url, allowingReadAccessTo: url)
        } else {
            let request = URLRequest.init(url: url)
            _webView?.load(request)
        }
    }
}
