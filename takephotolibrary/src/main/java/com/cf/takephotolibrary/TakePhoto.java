package com.cf.takephotolibrary;

import android.app.Activity;
import android.content.Intent;

/**
 * 描    述：
 * 创建日期：2017/7/6 13:03
 * 作    者：Chengfu
 * 邮    箱：
 * 备    注：
 */
public class TakePhoto {
    public static final int REQUEST_CODE_ALBUM = 10001;//相册
    public static final int REQUEST_CODE_CAMERA = 10002;//拍照
    public static final int REQUEST_CODE_CROP = 10003;//裁剪

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

    public static void onActivityResult(int requestCode, int resultCode, Intent data, ResultListener listener) {
        if(sBuilder != null){
            sBuilder.onActivityResult(requestCode, resultCode, data, listener);
        }
    }

}
