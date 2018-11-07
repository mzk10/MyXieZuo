package com.meng.hui.android.xiezuo.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

/**
 * Created by mzk10 on 2017/7/21.
 */

public class Utils {

    private static final String TAG = "Utils";

    /**
     * 获取文字实际长度
     *
     * @param content
     * @return
     */
    public static int getStringAbsLength(String content) {
        if (content != null) {
            char[] chars = null;
            chars = content.toCharArray();
            int count = 0;
            for (char c : chars) {
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                    count++;
                } else if (c >= '0' && c <= '9') {
                    count++;
                } else if (c > 128) {
                    count++;
                }
            }
            return count;
        } else {
            return 0;
        }

    }

    public static void showToast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取在Manifest文件中指定的versionName
     * <p>
     * {@linkplain PackageInfo#versionName}
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取在Manifest文件中指定的versionCode
     * <p>
     * {@linkplain PackageInfo#versionCode}
     *
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }


    public static String getFilenameFromUrl(String url)
    {
        String name = url.substring(url.lastIndexOf("/")+1, url.length());
        return name;
    }



}