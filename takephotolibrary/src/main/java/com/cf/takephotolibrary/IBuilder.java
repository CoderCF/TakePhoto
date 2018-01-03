package com.cf.takephotolibrary;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.cf.takephotolibrary.listener.ResultListener;
import com.cf.takephotolibrary.utils.FileUtil;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public abstract class IBuilder<T extends IBuilder> {

    /**是否裁剪图片*/
    protected boolean isCrop = false;
    /**是否拍照*/
    protected boolean isCamera = false;
    /**裁剪框的比例*/
    protected int aspectX = 1;
    protected int aspectY = 1;
    /**裁剪后输出图片的尺寸大小*/
    protected int outputX = 300;
    protected int outputY = 300;
    /**裁剪后输出图片的格式*/
    protected String outputFormat = Bitmap.CompressFormat.JPEG.toString();
    /**裁剪之后的输出路径*/
    protected File outputFile = new File(Environment.getExternalStorageDirectory(), "temp_crop.jpg");
    /**拍照图片的临时路径*/
    protected File tempFile;

    protected Activity mActivity;
    protected Intent mIntent;

    /**
     * 是否裁剪
     * @param isCrop
     * @return
     */
    public T setCrop(boolean isCrop) {
        this.isCrop = isCrop;
        return (T) this;
    }

    /**
     * 裁剪框的比例
     * @param aspectX
     * @return
     */
    public T setAspectX(int aspectX) {
        this.aspectX = aspectX;
        return (T) this;
    }

    public T setAspectY(int aspectY) {
        this.aspectY = aspectY;
        return (T) this;
    }

    /**
     * 裁剪后输出图片的尺寸大小
     * @param outputX
     * @return
     */
    public T setOutputX(int outputX) {
        this.outputX = outputX;
        return (T) this;
    }

    public T setOutputY(int outputY) {
        this.outputY = outputY;
        return (T) this;
    }

    /**
     * 图片格式
     * @param outputFormat
     * @return
     */
    public T setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
        return (T) this;
    }

    /**跳转到拍照还是相册界面，由子类实现*/
    public abstract void start();

    /**
     * 处理拍照或从相册选择的图片或裁剪的结果
     * @param requestCode
     * @param resultCode
     * @param data
     * @param listener
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data, ResultListener listener) {
        switch (requestCode){
            case TakePhoto.REQUEST_CODE_CAMERA://拍照
                if (resultCode == RESULT_OK) {
                    //是否裁剪
                    if(isCrop){
                        Uri imageUri = FileUtil.getUri(mActivity, tempFile);
                        Uri outputUri = FileUtil.getUri(mActivity, outputFile);
                        //裁剪图片
                        cropPhoto(imageUri, outputUri);
                    } else {
                        if(listener != null){
                            listener.onSuccess(tempFile.getAbsolutePath());
                        }
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    // 用户取消了图像捕获
                    Toast.makeText(mActivity, "您取消了拍照！", Toast.LENGTH_SHORT).show();
                    if(listener != null){
                        listener.onCancel();
                    }
                } else {
                    // 图像捕获失败，提示用户
                    Toast.makeText(mActivity, "拍照失败！", Toast.LENGTH_SHORT).show();
                    if(listener != null){
                        listener.onFailure();
                    }
                }
                break;
            case TakePhoto.REQUEST_CODE_ALBUM://从相册获取图片
                if (resultCode == RESULT_OK && data!=null) {
                    Uri uri = data.getData();
                    if(uri != null){
                        String imagePath = FileUtil.getPathFromUriBeforeKitKat(mActivity, uri);
                        //是否裁剪
                        if(isCrop){
                            Uri imageUri = FileUtil.getUri(mActivity, new File(imagePath));
                            Uri outputUri = FileUtil.getUri(mActivity, outputFile);
                            //裁剪图片
                            cropPhoto(imageUri, outputUri);
                        } else {
                            if(listener != null){
                                listener.onSuccess(imagePath);
                            }
                        }
                    }
                }else if (resultCode == RESULT_CANCELED) {
                    // 用户取消了选择
                    Toast.makeText(mActivity, "您取消了选择图片！", Toast.LENGTH_SHORT).show();
                    if(listener != null){
                        listener.onCancel();
                    }
                } else {
                    Toast.makeText(mActivity, "获取图片失败！", Toast.LENGTH_SHORT).show();
                    if(listener != null){
                        listener.onFailure();
                    }
                }
                break;
            case TakePhoto.REQUEST_CODE_DOCUMENT://从文件获取图片
                if (resultCode == RESULT_OK && data!=null) {
                    Uri uri = data.getData();
                    if(uri != null){
                        String imagePath;
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                            imagePath = FileUtil.getPathFromUriOnKitKat(mActivity, uri);
                        } else {
                            imagePath = FileUtil.getPathFromUriBeforeKitKat(mActivity, uri);
                        }
                        //是否裁剪
                        if(isCrop){
                            Uri imageUri = FileUtil.getUri(mActivity, new File(imagePath));
                            Uri outputUri = FileUtil.getUri(mActivity, outputFile);
                            //裁剪图片
                            cropPhoto(imageUri, outputUri);
                        } else {
                            if(listener != null){
                                listener.onSuccess(imagePath);
                            }
                        }
                    }
                }else if (resultCode == RESULT_CANCELED) {
                    // 用户取消了选择
                    Toast.makeText(mActivity, "您取消了选择图片！", Toast.LENGTH_SHORT).show();
                    if(listener != null){
                        listener.onCancel();
                    }
                } else {
                    Toast.makeText(mActivity, "获取图片失败！", Toast.LENGTH_SHORT).show();
                    if(listener != null){
                        listener.onFailure();
                    }
                }
                break;
            case Crop.REQUEST_CROP://第三方裁剪
            case TakePhoto.REQUEST_CODE_CROP://裁剪
                try {
                    if (resultCode == RESULT_OK) {
                        if(listener != null){
                            listener.onSuccess(outputFile.getAbsolutePath());
                        }
                    } else if (resultCode == RESULT_CANCELED) {
                        // 用户取消了图像捕获
                        Toast.makeText(mActivity, "您取消了裁剪！", Toast.LENGTH_SHORT).show();
                        if(listener != null){
                            listener.onCancel();
                        }
                    } else {
                        // 图像捕获失败，提示用户
                        Toast.makeText(mActivity, "裁剪失败！", Toast.LENGTH_SHORT).show();
                        if(listener != null){
                            listener.onFailure();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mActivity, "裁剪失败！", Toast.LENGTH_SHORT).show();
                }
                //删除拍照的临时文件
                if(isCamera){
                    if(tempFile != null){
                        tempFile.delete();
                    }
                }
                break;
        }
    }

    /**
     * 裁剪图片
     * @param imageUri
     * @param outputUri
     */
    private void cropPhoto(Uri imageUri, Uri outputUri) {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(imageUri, "image/*");
            // crop为true是设置在开启的intent中设置显示的view可以剪裁
            intent.putExtra("crop", "true");
            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", aspectX);
            intent.putExtra("aspectY", aspectY);
            // outputX,outputY 是剪裁图片的宽高
            intent.putExtra("outputX", outputX);
            intent.putExtra("outputY", outputY);
            // 图片格式
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            intent.putExtra("outputFormat", outputFormat);
            intent.putExtra("return-data", false);// true:返回uri，false：不返回uri
            intent.putExtra("noFaceDetection", true);// 取消人脸识别

            //判断Android版本是否是Android7.0以上
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                //将存储图片的uri读写权限授权给剪裁工具应用 针对API24+
                List<ResolveInfo> resInfoList = mActivity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    mActivity.grantUriPermission(packageName, outputUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }
            //检查手机是否带有裁剪功能，如果没有，则使用第三方裁剪工具
            List list = mActivity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if(list.isEmpty()){//使用第三方裁剪
                Crop.of(imageUri, outputUri).withAspect(aspectX, aspectY).withMaxSize(outputX, outputY).start(mActivity);
            } else {
                mActivity.startActivityForResult(intent, TakePhoto.REQUEST_CODE_CROP);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mActivity, "裁剪功能不可用！", Toast.LENGTH_SHORT).show();
        }
    }

}
