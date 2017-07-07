package com.cf.takephotolibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;


public class ImageUtil {

	/**
	 * 检查SD卡是否挂载
	 * @return
	 */
	public static boolean checkSDCardAvailable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 创建头像存储目录
	 * @param dirPath
	 * @return
     */
	public static File createDir(String dirPath) {
		File dir = new File(Environment.getExternalStorageDirectory() + "/" + dirPath + "/");
		if (!dir.exists()) {
			// 创建照片的存储目录
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * 根据Uri获取图片路径
	 * @param act
	 * @param uri
     * @return
     */
	public static String getPathFromUri(Activity act, Uri uri) {
		try {
			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = act.getContentResolver().query(uri, projection, null,null,null);
			int column_index = cursor.getColumnIndex(projection[0]);
			cursor.moveToFirst();
			return cursor.getString(column_index);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 拍照裁剪
	 * @param act
	 * @param imagUri
	 * @param outputUri
	 * @param aspectX
	 * @param aspectY
	 * @param outputX
	 * @param outputY
     * @param outputFormat
     */
	public static void cropPhoto(Activity act,Uri imagUri, Uri outputUri, int aspectX, int aspectY, int outputX, int outputY, String outputFormat) {
		try {
			Intent intent = new Intent("com.android.camera.action.CROP", null);
			intent.setDataAndType(imagUri, "image/*");
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
			act.startActivityForResult(intent, TakePhoto.REQUEST_CODE_CROP);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 使用ContentProvider通过uri获取原始图片 
	 * @param uri
	 * @param mContext
	 * @return
	 */
	public static Bitmap getBitmapFromUri(Uri uri, Context mContext) {
		try {
			// 读取uri所在的图片
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 读取照片exif信息中的旋转角度<br/>
	 * http://www.eoeandroid.com/thread-196978-1-1.html
	 * @param path 照片路径
	 * @return角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;

	}

	/** 旋转图片一定角度
	 * rotaingImageView
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		//创建操作图片是用的matrix对象
		Matrix matrix = new Matrix();
		// 旋转图片 动作
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * 重新指定图片大小
	 *
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}

	/**
	 * 将图片变为圆角
	 *
	 * @param bitmap
	 *            原Bitmap图片
	 * @param pixels
	 *            图片圆角的弧度(单位:像素(px))
	 * @return 带有圆角的图片(Bitmap 类型)
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 将图片转化为圆形头像 
	 *
	 * @Title: toRoundBitmap
	 * @throws
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;

			left = 0;
			top = 0;
			right = width;
			bottom = width;

			height = width;

			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;

			float clip = (width - height) / 2;

			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;

			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);// 设置画笔无锯齿

		canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas

		// 以下有两种方法画圆,drawRounRect和drawCircle
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
		// canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
		canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

		return output;
	}

}
