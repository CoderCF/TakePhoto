package com.cf.takephotolibrary;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.List;

/**
 * 描    述：
 * 创建日期：2017/7/6 13:30
 * 作    者：Chengfu
 * 邮    箱：
 * 备    注：
 */
public class CameraBuilder extends IBuilder<CameraBuilder> {

    public CameraBuilder(Activity activity) {
        this.mActivity = activity;
        mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        isCamera = true;
        tempFile = new File(Environment.getExternalStorageDirectory(), "temp_photo.jpg");
    }

    @Override
    public void start() {
        if (ImageUtil.checkSDCardAvailable()) {  //判断是否有SD卡
            Uri uri;
            //判断Android版本是否是Android7.0以上
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                uri = FileProvider.getUriForFile(mActivity, ProviderUtil.getFileProviderName(mActivity), tempFile);
            } else {
                uri = Uri.fromFile(tempFile);
            }
            mIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            mIntent.putExtra("autofocus", true); // 自动对焦
            mIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//指定调用相机拍照后照片的临时储存路径
            mIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

            //授予"相机"保存文件的权限 针对API24+
            List<ResolveInfo> resInfoList = mActivity.getPackageManager().queryIntentActivities(mIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                mActivity.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            mActivity.startActivityForResult(mIntent, TakePhoto.REQUEST_CODE_CAMERA);
        } else{
            ToastUtil.showShortToast(mActivity, "请检查SD卡是否正常");
        }
    }


}
