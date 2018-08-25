package com.zscdumin.zhixinapp.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * 文件工具类，用于处理缓存
 */
public class FileUtils {
    public static File getDisCacheDir(Context context, String data) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + data);
    }


}
