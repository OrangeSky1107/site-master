package com.wintone.site.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.ImageUtils;

public class UiUtils {


    public static void hideNavKey(Context context) {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = ((Activity) context).getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = ((Activity) context).getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
    public static void circularBitmap(ImageView imageView, Drawable drawable){
        Bitmap bitmap = ImageUtils.drawable2Bitmap(drawable);
        Bitmap mbitmap = ImageUtils.toRoundCorner(bitmap, 20);
        imageView.setImageBitmap(mbitmap);
    }
}
