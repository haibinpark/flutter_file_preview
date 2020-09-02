package com.example.flutter_file_preview;

import android.content.Context;

import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;


public class X5FilePreviewFactory extends PlatformViewFactory {


    private final BinaryMessenger messenger;
    private FlutterFilePreviewPlugin plugin;
    private Context mContext;


    public X5FilePreviewFactory(BinaryMessenger messenger, Context context, FlutterFilePreviewPlugin plugin) {
        super(StandardMessageCodec.INSTANCE);
        this.messenger = messenger;
        this.mContext = context;
        this.plugin = plugin;

    }

    @Override
    public PlatformView create(Context context, int i, Object args) {
        Map<String, Object> params = (Map<String, Object>) args;

        return new X5FilePreviewView(mContext, messenger, i, params,plugin);
    }
}
