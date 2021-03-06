#import "FlutterFilePreviewPlugin.h"
#if __has_include(<flutter_file_preview/flutter_file_preview-Swift.h>)
#import <flutter_file_preview/flutter_file_preview-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_file_preview-Swift.h"
#endif

@implementation FlutterFilePreviewPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterFilePreviewPlugin registerWithRegistrar:registrar];
}
@end
