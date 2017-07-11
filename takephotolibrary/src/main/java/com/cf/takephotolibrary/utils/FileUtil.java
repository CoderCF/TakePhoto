package com.cf.takephotolibrary.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
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
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;


public class FileUtil {

	/**
	 * 检查SD卡是否挂载
	 * @return
	 */
	public static boolean checkSDCardAvailable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 通过Uri和selection来获取真实的图片路径
	 * @param act
	 * @param uri
	 * @param selection
	 * @return
	 */
	private static String getPathFromUri(Activity act, Uri uri, String selection) {
		String path = null;
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = act.getContentResolver().query(uri, projection, selection,null,null);
		if(cursor != null){
			if(cursor.moveToFirst()){
				path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
			}
			cursor.close();
		}
		return path;
	}
	/**
	 * 根据Uri获取图片路径，专为Android4.4设计
	 * @param act
	 * @param uri
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getPathFromUriOnKitKat(Activity act, Uri uri) {
		/**
		 * uri=content://com.android.providers.media.documents/document/image%3A293502  4.4以后
		 * uri=file:///storage/emulated/0/temp_photo.jpg
		 * uri=content://media/external/images/media/193968
		 *
		 * uri=content://media/external/images/media/13   4.4以前
		 */
		String path = null;
		if (DocumentsContract.isDocumentUri(act, uri)) {
			// 如果是document类型的Uri，则通过document id处理
			String docId = DocumentsContract.getDocumentId(uri);
			if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
				String id = docId.split(":")[1]; // 解析出数字格式的id
				String selection = MediaStore.Images.Media._ID + "=" + id;
				path = getPathFromUri(act, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
			} else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
				Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
				path = getPathFromUri(act, contentUri, null);
			}
		} else if ("content".equalsIgnoreCase(uri.getScheme())) {
			// 如果是content类型的Uri，则使用普通方式处理
			path = getPathFromUri(act, uri, null);
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			// 如果是file类型的Uri，直接获取图片路径即可
			path = uri.getPath();
		}
		return path;
	}

	/**
	 * 根据Uri获取图片路径，Android4.4以前
	 * @param act
	 * @param uri
     * @return
     */
	public static String getPathFromUriBeforeKitKat(Activity act, Uri uri) {
		return getPathFromUri(act, uri, null);
	}

	/**
	 * 获取Uri
	 * @param context
	 * @param file
	 * @return
	 */
	public static Uri getUri(Context context, File file){
		//判断Android版本是否是Android7.0以上
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
			return FileProvider.getUriForFile(context, ProviderUtil.getFileProviderName(context), file);
		} else {
			return Uri.fromFile(file);
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
	 * 获取图片的旋转角度
	 * http://www.eoeandroid.com/thread-196978-1-1.html
	 * @param path 照片路径
	 * @return
	 */
	public static int getPictureDegree(String path) {
		int degree = 0;
		try {
			// 从指定路径下读取图片，并获取其EXIF信息
			ExifInterface exifInterface = new ExifInterface(path);
			// 获取图片的旋转信息
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
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

	/**
	 * 将图片按照指定的角度进行旋转
	 * @param bitmap
	 * @param degree
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, int degree) {
		//创建操作图片是用的matrix对象
		Matrix matrix = new Matrix();
		// 旋转图片 动作
		matrix.postRotate(degree);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		if (!bitmap.isRecycled()) {
			bitmap.recycle();
		}
		return resizedBitmap;
	}

	/**
	 * 重新指定图片大小
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
	 * @param bitmap 原Bitmap图片
	 * @param pixels 图片圆角的弧度(单位:像素(px))
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
