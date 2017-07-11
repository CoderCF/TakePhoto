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
public class DocumentBuilder extends IBuilder<DocumentBuilder> {

    public DocumentBuilder(Activity activity) {
        this.mActivity = activity;
        mIntent = new Intent(Intent.ACTION_GET_CONTENT);
        isCamera = false;
    }

    @Override
    public void start() {
        mIntent.setType("image/*");
        mActivity.startActivityForResult(mIntent, TakePhoto.REQUEST_CODE_DOCUMENT);
    }

}
