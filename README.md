# TakePhoto

>最近先来无事把以前项目中用到的比较常用的功能抽取出来整理成一个demo，以后用到的时候直接拿来用，也希望能帮到其他人。整理的可能不太好，还望各位指正。

##TakePhoto 介绍

TakePhoto是用来从相册或文件或相机中获取图片的封装类库。

##TakePhoto 功能

 - 支持通过相机拍照获取图片
 - 支持从相册选择图片
 - 支持从文件选择图片
 - 支持图片裁剪（可选）
 - 支持裁剪后图片的格式、大小设置
 - 支持Android7.0
 - 不支持Android6.0动态权限（使用前请先进行权限申请）
 
##演示

![这里写图片描述](http://img.blog.csdn.net/20170711144200514?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY2hlbmdmdTExNg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
![这里写图片描述](http://img.blog.csdn.net/20170707133030703?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY2hlbmdmdTExNg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
![这里写图片描述](http://img.blog.csdn.net/20170711144351957?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY2hlbmdmdTExNg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
![这里写图片描述](http://img.blog.csdn.net/20170711144234292?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY2hlbmdmdTExNg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
![这里写图片描述](http://img.blog.csdn.net/20170707133117711?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY2hlbmdmdTExNg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)


##使用说明

TakePhoto使用比较简单，只需两步即可。


**第一步：选择相册或相机或文件获取**

- 从相册获取图片
```
TakePhoto.album(this)//从相册获取
         .setCrop(true)//是否裁剪图片
         .setAspectX(1)//裁剪框的比例
         .setAspectY(1)
         .setOutputX(300)//裁剪后输出图片的尺寸大小
         .setOutputY(300)
         .setOutputFormat(Bitmap.CompressFormat.JPEG.toString())//裁剪后输出图片的格式
         .start();//开始
``` 
 - 从相机获取图片
```
TakePhoto.camera(this)//拍照获取
         .setCrop(true)//是否裁剪图片
         .setAspectX(1)//裁剪框的比例
         .setAspectY(1)
         .setOutputX(300)//裁剪后输出图片的尺寸大小
         .setOutputY(300)
         .setOutputFormat(Bitmap.CompressFormat.JPEG.toString())//裁剪后输出图片的格式
         .start();//开始
```
 - 从文件获取图片
```
TakePhoto.document(this)//从文件获取
         .setCrop(true)//是否裁剪图片
         .setAspectX(1)//裁剪框的比例
         .setAspectY(1)
         .setOutputX(300)//裁剪后输出图片的尺寸大小
         .setOutputY(300)
         .setOutputFormat(Bitmap.CompressFormat.JPEG.toString())//裁剪后输出图片的格式
         .start();//开始
```
**第二步：重写onActivityResult方法，并回调结果**
```
 @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TakePhoto.onActivityResult(requestCode, resultCode, data, new ResultListener() {
            @Override
            public void onSuccess(String imagPath) {//成功
                ToastUtil.showShortToast(MainActivity.this, imagPath);
            }
            @Override
            public void onCancel() {//取消
            }
            @Override
            public void onFailure() {//失败
            }
        });
        super.onActivityResult(requestCode, resultCode, data);
    }
```

##注意：

此库没有对6.0动态权限进行申请，使用前一定要先申请权限，github有很多6.0权限申请的库，这里推荐严大的[AndPermission](https://github.com/yanzhenjie/AndPermission)，使用非常方便。


该项目参考了：

 - https://github.com/yanzhenjie/Album
 - https://github.com/jeasonlzy/ImagePicker
 - https://github.com/crazycodeboy/TakePhoto

*本人水平有限，如有错误，欢迎指正！*







