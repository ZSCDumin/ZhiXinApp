package com.zscdumin.zhixinapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ZSCDumin
 */
public class CompressPhotoUtils {

	private List<String> fileList = new ArrayList<>();

	public void compressPhoto(List<String> list, CompressCallBack callBack) {
		CompressTask task = new CompressTask(list, callBack);
		task.execute();
	}

	class CompressTask extends AsyncTask<Void, Integer, Integer> {
		private List<String> list;
		private CompressCallBack callBack;

		CompressTask(List<String> list, CompressCallBack callBack) {
			this.list = list;
			this.callBack = callBack;
		}

		/**
		 * 运行在UI线程中，在调用doInBackground()之前执行
		 */
		@Override
		protected void onPreExecute() {
			Log.i("TAG", "压缩处理中....");
		}

		/**
		 * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 */
		@Override
		protected Integer doInBackground(Void... params) {
			for (int i = 0; i < list.size(); i++) {
				Bitmap bitmap = getBitmap(list.get(i));
				String path = saveBitmap(bitmap, list.get(i));
				fileList.add(path);
			}
			return null;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		protected void onPostExecute(Integer integer) {
			callBack.success(fileList);
		}

		/**
		 * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
		}
	}

	/**
	 * 从sd卡获取压缩图片bitmap
	 */
	public static Bitmap getBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		// 计算缩放比
		options.inSampleSize = calculateInSampleSize(options, 480, 800);
		// 完整解析图片返回bitmap
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
	                                        int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	/**
	 * 保存bitmap到内存卡
	 */
	public static String saveBitmap(Bitmap bmp, String picPath) {
		File file = new File(Environment.getExternalStorageDirectory().getPath() + "/tempFiles/");
		String path = null;
		if (!file.exists()) {
			file.mkdirs();
		}
		try {
			int len = picPath.split("/").length;
			String picName = picPath.split("/")[len - 1];
			Log.i("picName", picName);
			path = file.getPath() + "/" + picName;
			FileOutputStream fileOutputStream = new FileOutputStream(path);
			bmp.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

	public interface CompressCallBack {
		void success(List<String> list);
	}
}


