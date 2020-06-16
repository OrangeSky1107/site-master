package com.wintone.site.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;


public class PhoneUtils {

    /**
     * 获取屏幕高度
     * @param context 上下文对象
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    /**
     * 获取屏幕宽度
     * @param context 上下文对象
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    /**
     * 获取状态栏的高度
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取系统版本号
     * @param context
     * @return
     */
    public static String getVersionCode(Context context) {
        String versionCode = "";
        String packageName = context.getApplicationContext().getPackageName();
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName,
                PackageManager.GET_CONFIGURATIONS);
            versionCode = String.valueOf(info.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取系统版本名称
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        String packageName = context.getApplicationContext().getPackageName();
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName,
                PackageManager.GET_CONFIGURATIONS);
            versionName = String.valueOf(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * sp或者 dp 装换为 px
     */
    public static int dpToPx(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dpValue * scale);
    }
}
