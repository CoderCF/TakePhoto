package com.cf.takephotolibrary;

import android.app.Activity;
import android.content.Intent;


public class DocumentBuilder extends IBuilder<DocumentBuilder> {

    public DocumentBuilder(Activity activity) {
        this.mActivity = activity;
        mIntent = new Intent(Intent.ACTION_GET_CONTENT);
        mIntent.setType("image/*");
        isCamera = false;
    }

    @Override
    public void start() {
        mActivity.startActivityForResult(mIntent, TakePhoto.REQUEST_CODE_DOCUMENT);
    }

}
