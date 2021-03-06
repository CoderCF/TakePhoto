package com.cf.takephotolibrary;

import android.app.Activity;
import android.content.Intent;

import com.cf.takephotolibrary.listener.ResultListener;

public class TakePhoto {
    public static final int REQUEST_CODE_CAMERA = 10001;//拍照
    public static final int REQUEST_CODE_ALBUM = 10002;//相册
    public static final int REQUEST_CODE_DOCUMENT = 10003;//文件
    public static final int REQUEST_CODE_CROP = 10004;//裁剪

    private static IBuilder sBuilder = null;

    /**
     * 拍照
     * @param activity
     * @return
     */
    public static IBuilder camera(Activity activity){
        sBuilder = new CameraBuilder(activity);
        return sBuilder;
    }

    /**
     * 从相册获取
     * @param activity
     * @return
     */
    public static IBuilder album(Activity activity){
        sBuilder = new AlbumBuilder(activity);
        return sBuilder;
    }

    /**
     * 从相册获取
     * @param activity
     * @return
     */
    public static IBuilder document(Activity activity){
        sBuilder = new DocumentBuilder(activity);
        return sBuilder;
    }

    /**
     * 处理拍照或从相册选择的图片或裁剪的结果
     * @param requestCode
     * @param resultCode
     * @param data
     * @param listener
     */
    public static void onActivityResult(int requestCode, int resultCode, Intent data, ResultListener listener) {
        if(sBuilder != null){
            sBuilder.onActivityResult(requestCode, resultCode, data, listener);
        }
    }

}
