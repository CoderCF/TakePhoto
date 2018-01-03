package com.cf.takephotolibrary;

import android.app.Activity;
import android.content.Intent;

public class AlbumBuilder extends IBuilder<AlbumBuilder> {

    public AlbumBuilder(Activity activity) {
        this.mActivity = activity;
        mIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mIntent.setType("image/*");
        isCamera = false;
    }

    @Override
    public void start() {
        mActivity.startActivityForResult(mIntent, TakePhoto.REQUEST_CODE_ALBUM);
    }

}
