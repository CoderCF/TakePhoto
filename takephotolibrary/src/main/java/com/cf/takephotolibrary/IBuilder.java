package com.cf.takephotolibrary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * 描    述：
 * 创建日期：2017/7/6 13:14
 * 作    者：Chengfu
 * 邮    箱：
 * 备    注：
 */
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
    /**图片格式*/
    protected String outputFormat = Bitmap.CompressFormat.JPEG.toString();
    /**裁剪之后的输出路径*/
    protected File outputFile = new File(Environment.getExternalStorageDirectory(), "temp_crop.jpg");
    /**拍照图片的临时路径*/
    protected File tempFile = new File(Environment.getExternalStorageDirectory(), "temp_photo.jpg");

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
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data, ResultListener listener) {
        switch (requestCode){
            case TakePhoto.REQUEST_CODE_CAMERA://拍照
                    if (resultCode == RESULT_OK) {
                        //判断裁剪图片
                        if(isCrop){
                            //判断Android版本是否是Android7.0以上
                            Uri outputUri;
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                                outputUri = FileProvider.getUriForFile(mActivity, ProviderUtil.getFileProviderName(mActivity), outputFile);
                            } else {
                                outputUri = Uri.fromFile(outputFile);
                            }
                            ImageUtil.cropPhoto(mActivity, Uri.fromFile(tempFile), outputUri, aspectX, aspectY, outputX, outputY, outputFormat);
                        } else {
                            if(listener != null){
                                listener.onSuccess(tempFile.getAbsolutePath());
                            }
                        }
                    } else if (resultCode == RESULT_CANCELED) {
                        // 用户取消了图像捕获
                        ToastUtil.showShortToast(mActivity, "您取消了拍照！");
                        if(listener != null){
                            listener.onCancel();
                        }
                    } else {
                        // 图像捕获失败，提示用户
                        ToastUtil.showShortToast(mActivity, "拍照失败！");
                        if(listener != null){
                            listener.onFailure();
                        }
                    }
                break;
            case TakePhoto.REQUEST_CODE_ALBUM://从相册获取图片
                if (resultCode == RESULT_OK && data!=null) {
                    Uri uri = data.getData();
                    if(uri != null){
                        //判断是否裁剪图片
                        if(isCrop){
                            //判断Android版本是否是Android7.0以上
                            Uri outputUri;
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                                outputUri = FileProvider.getUriForFile(mActivity, ProviderUtil.getFileProviderName(mActivity), outputFile);
                            } else {
                                outputUri = Uri.fromFile(outputFile);
                            }
                            ImageUtil.cropPhoto(mActivity, data.getData(), outputUri, aspectX, aspectY, outputX, outputY, outputFormat);
                        } else {
                            if(listener != null){
                                listener.onSuccess(ImageUtil.getPathFromUri(mActivity, data.getData()));
                            }
                        }
                    }
                }else if (resultCode == RESULT_CANCELED) {
                    // 用户取消了选择
                    ToastUtil.showShortToast(mActivity, "您取消了选择图片！");
                    if(listener != null){
                        listener.onCancel();
                    }
                } else {
                    ToastUtil.showShortToast(mActivity, "获取图片失败！");
                    if(listener != null){
                        listener.onFailure();
                    }
                }
                break;
            case TakePhoto.REQUEST_CODE_CROP://裁剪
                if (resultCode == RESULT_OK) {
                    if(listener != null){
                        listener.onSuccess(outputFile.getAbsolutePath());
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    // 用户取消了图像捕获
                    ToastUtil.showShortToast(mActivity, "您取消了裁剪！");
                    if(listener != null){
                        listener.onCancel();
                    }
                } else {
                    // 图像捕获失败，提示用户
                    ToastUtil.showShortToast(mActivity, "裁剪失败！");
                    if(listener != null){
                        listener.onFailure();
                    }
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

}
