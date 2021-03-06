package com.cf.takephotolibrary;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.cf.takephotolibrary.utils.FileUtil;

import java.io.File;
import java.util.List;

public class CameraBuilder extends IBuilder<CameraBuilder> {

    public CameraBuilder(Activity activity) {
        this.mActivity = activity;
        mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        isCamera = true;
    }

    @Override
    public void start() {
        try {
            if (FileUtil.checkSDCardAvailable()) {  //判断是否有SD卡
                tempFile = new File(Environment.getExternalStorageDirectory(), "temp_photo.jpg");
                Uri uri = FileUtil.getUri(mActivity, tempFile);
                mIntent.putExtra("autofocus", true); // 自动对焦
                mIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//指定调用相机拍照后照片的临时储存路径
                mIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

                //判断Android版本是否是Android7.0以上
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    mIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    mIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    //将存储图片的uri读写权限授权给相机应用 针对API24+
                    List<ResolveInfo> resInfoList = mActivity.getPackageManager().queryIntentActivities(mIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        mActivity.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }

                //检查是否有相机功能
                List list = mActivity.getPackageManager().queryIntentActivities(mIntent, PackageManager.MATCH_DEFAULT_ONLY);
                if(list.isEmpty()){
                    Toast.makeText(mActivity, "请检查相机功能是否可用！", Toast.LENGTH_SHORT).show();
                } else {
                    mActivity.startActivityForResult(mIntent, TakePhoto.REQUEST_CODE_CAMERA);
                }
            } else{
                Toast.makeText(mActivity, "请检查SD卡是否正常！", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mActivity, "请检查相机功能是否可用！", Toast.LENGTH_SHORT).show();
        }
    }


}
