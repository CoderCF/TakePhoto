package com.cf.takephoto;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cf.takephotolibrary.ResultListener;
import com.cf.takephotolibrary.TakePhoto;
import com.cf.takephotolibrary.ToastUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION_OTHER = 200;
    private static final int REQUEST_CODE_SETTING = 300;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    TakePhoto.camera(MainActivity.this)
                            .setCrop(false)
                            .start();
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

    /**
     * 相册
     * @param view
     */
    public void onPicture(View view){
        TakePhoto.album(this)
                .setCrop(true)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TakePhoto.onActivityResult(requestCode, resultCode, data, new ResultListener() {
            @Override
            public void onSuccess(String imagPath) {
                ToastUtil.showShortToast(MainActivity.this, imagPath);
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
