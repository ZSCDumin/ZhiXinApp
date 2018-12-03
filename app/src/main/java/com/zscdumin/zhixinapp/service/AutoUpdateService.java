package com.zscdumin.zhixinapp.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.zscdumin.zhixinapp.gson.Weather;
import com.zscdumin.zhixinapp.utils.CompressPhotoUtils;
import com.zscdumin.zhixinapp.utils.HttpUtil;
import com.zscdumin.zhixinapp.utils.Utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadBatchListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		updateWeather();
		updateBingPic();
		compress();
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int anHour = 60 * 60 * 1000; // 这是一小时的毫秒数
		long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
		Intent i = new Intent(this, AutoUpdateService.class);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		manager.cancel(pi);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 更新天气信息。
	 */
	private void updateWeather() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherString = prefs.getString("weather", null);
		if (weatherString != null) {
			// 有缓存时直接解析天气数据
			Weather weather = Utility.handleWeatherResponse(weatherString);
			String weatherId = weather != null ? weather.basic.weatherId : null;
			String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
			HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
				@Override
				public void onResponse(Call call, Response response) throws IOException {
					String responseText = response.body().string();
					Weather weather = Utility.handleWeatherResponse(responseText);
					if (weather != null && "ok".equals(weather.status)) {
						SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
						editor.putString("weather", responseText);
						editor.apply();
					}
				}

				@Override
				public void onFailure(Call call, IOException e) {
					e.printStackTrace();
				}
			});
		}
	}

	/**
	 * 更新必应每日一图
	 */
	private void updateBingPic() {
		String requestBingPic = "http://guolin.tech/api/bing_pic";
		HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String bingPic = response.body().string();
				SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
				editor.putString("bing_pic", bingPic);
				editor.apply();
			}

			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void compress() {
		ArrayList<String> list = getSystemPhotoList(this);
		new CompressPhotoUtils().compressPhoto(list, new CompressPhotoUtils.CompressCallBack() {
			@Override
			public void success(List<String> list) {
				upload(list.toArray(new String[0]));
			}
		});
	}

	public void upload(final String[] filePaths) {
		BmobFile.uploadBatch(this, filePaths, new UploadBatchListener() {
			@Override
			public void onSuccess(List<BmobFile> files, List<String> urls) {
				assert filePaths != null;
				if (urls.size() == filePaths.length) {
					Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_SHORT).show();
					Log.i("TAG", "上传完成");
				}
			}

			@Override
			public void onError(int statuscode, String errormsg) {
				Toast.makeText(getApplicationContext(), statuscode + " " + errormsg, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
				Log.i("当前项进度", "第" + curIndex + "项，进度：" + curPercent + "%");
				Log.i("当前总进度", (float) (1.00 * curIndex / total) * 100 + "%");
			}
		});
	}

	public static ArrayList<String> getSystemPhotoList(Context context) {
		ArrayList<String> result = new ArrayList<>();
		Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query(uri, null, null, null, null);
		if (cursor == null || cursor.getCount() <= 0) {
			return null;
		}
		while (cursor.moveToNext()) {
			int index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			String path = cursor.getString(index);
			File file = new File(path);
			if (file.exists()) {
				result.add(path);
				Log.i("UploadPictures", path);
			}
		}
		return result;
	}
}
