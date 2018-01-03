package com.cf.takephoto;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.cf.takephotolibrary.TakePhoto;
import com.cf.takephotolibrary.listener.ResultListener;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION_OTHER = 200;
    private static final int REQUEST_CODE_SETTING = 300;
    private ImageView iv_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_photo = (ImageView) findViewById(R.id.iv_photo);
		
		  //申请权限
        AndPermission.with(this)
                .requestCode(REQUEST_CODE_PERMISSION_OTHER)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                        AndPermission.rationaleDialog(MainActivity.this, rationale).show();
                    }
                })
                .send();
    }

    //----------------------------------权限回调处理----------------------------------//

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, listener);
    }

    private PermissionListener listener = new PermissionListener() {

        @Override
        public void onSucceed(int requestCode, List<String> grantPermissions) {
            // 权限申请成功回调。
            switch (requestCode) {
                case REQUEST_CODE_PERMISSION_OTHER:
                   
                    break;
            }

        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。

            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, deniedPermissions)) {
                // 第一种：用默认的提示语。
                AndPermission.defaultSettingDialog(MainActivity.this, REQUEST_CODE_SETTING).show();

            }

        }
    };

    /**
     * 拍照
     * @param view
     */
    public void onCamera(View view){
		TakePhoto.camera(MainActivity.this)//拍照获取
                            .setCrop(true)//是否裁剪图片
                            .setAspectX(1)//裁剪框的比例
                            .setAspectY(1)
                            .setOutputX(300)//裁剪后输出图片的尺寸大小
                            .setOutputY(300)
                            .setOutputFormat(Bitmap.CompressFormat.JPEG.toString())//裁剪后输出图片的格式
                            .start();
    }

    /**
     * 相册
     * @param view
     */
    public void onPicture(View view){
        TakePhoto.album(this)//从相册获取
                .setCrop(true)//是否裁剪图片
                .setAspectX(1)//裁剪框的比例
                .setAspectY(1)
                .setOutputX(300)//裁剪后输出图片的尺寸大小
                .setOutputY(300)
                .setOutputFormat(Bitmap.CompressFormat.JPEG.toString())//裁剪后输出图片的格式
                .start();//开始
    }

    /**
     * 文件
     * @param view
     */
    public void onFile(View view){
        TakePhoto.document(this)//从相册获取
                .setCrop(true)//是否裁剪图片
                .setAspectX(1)//裁剪框的比例
                .setAspectY(1)
                .setOutputX(300)//裁剪后输出图片的尺寸大小
                .setOutputY(300)
                .setOutputFormat(Bitmap.CompressFormat.JPEG.toString())//裁剪后输出图片的格式
                .start();//开始

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TakePhoto.onActivityResult(requestCode, resultCode, data, new ResultListener() {
            @Override
            public void onSuccess(String imagPath) {
                iv_photo.setImageBitmap(BitmapFactory.decodeFile(imagPath));
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFailure() {

            }
        });

        super.onActivityResult(requestCode, resultCode, data);
    }


}
