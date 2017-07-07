package com.cf.takephotolibrary;

import android.app.Activity;
import android.content.Intent;

/**
 * 描    述：
 * 创建日期：2017/7/6 13:30
 * 作    者：Chengfu
 * 邮    箱：
 * 备    注：
 */
public class AlbumBuilder extends IBuilder<AlbumBuilder> {

    public AlbumBuilder(Activity activity) {
        this.mActivity = activity;
        mIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        isCamera = false;
    }

    @Override
    public void start() {
        mIntent.setType("image/*");
        mIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mActivity.startActivityForResult(mIntent, TakePhoto.REQUEST_CODE_ALBUM);

    }

}
