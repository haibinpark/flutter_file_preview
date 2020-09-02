//
//  FileTool.swift
//  file_preview
//
//  Created by haibin on 2020/8/14.
//

import UIKit

class FileTool: NSObject {

    
    // MARK:- 判断文件目录是否存在
    class func fileExists(filePath : String) -> Bool {
        
        if (filePath.count == 0) {
            return false
        }
        return FileManager.default.fileExists(atPath: filePath)
    }
    
    // MARK:- 获取文件的大小
    class func  fileSize(_ filePath : String) ->CLongLong{
    
        if !self.fileExists(filePath: filePath) {
            return 0
        }
        
        let fileInfo = try! FileManager.default.attributesOfItem(atPath: filePath)
        
        return fileInfo[FileAttributeKey.size] as! CLongLong
        
    }
    
    // MARK:- 移动文件
    class func moveFile(_ fromPath : String , _ toPath : String){
        
        if self.fileSize(fromPath)  == 0 {
            return
        }
        try! FileManager.default.moveItem(atPath: fromPath, toPath: toPath)
    }
    
    class func removeFile(_ filePath : String){
       try! FileManager.default.removeItem(atPath: filePath)
    }
  
}
