//
//  FilePreviewFactory.swift
//  file_preview
//
//  Created by haibin on 2020/8/13.
//

import UIKit

class FilePreviewFactory: NSObject,FlutterPlatformViewFactory {
    func create(withFrame frame: CGRect, viewIdentifier viewId: Int64, arguments args: Any?) -> FlutterPlatformView {
        return FilePreviewView.init(withFrame: frame, viewIdentifier: viewId, arguments: args, binaryMessenger: _messenger!)
    }
    
    
    var _messenger : FlutterBinaryMessenger?
    
    init(messenger : FlutterBinaryMessenger) {
        super.init()
        self._messenger = messenger
    }
    
//    func create(withFrame frame: CGRect, viewIdentifier viewId: Int64, arguments args: Any?) -> FlutterPlatformView {
//        return FilePreviewView.init(withFrame: frame, viewIdentifier: viewId, arguments: args, binaryMessenger: _messenger!)
//    }
    
    func createArgsCodec() -> FlutterMessageCodec & NSObjectProtocol {
           return FlutterStandardMessageCodec.sharedInstance()
    }
    
}
