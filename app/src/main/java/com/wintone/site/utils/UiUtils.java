package com.wintone.site.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.OptionPicker;

public class UiUtils {

    public static final String DateFormat_10 = "yyyy-MM-dd";
    public static final String DateFormat_8 = "yyyyMMdd";

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

    public static void showOptionPicker(Activity activity, String[] data, int selectedIndex, OptionPicker.OnOptionPickListener onOptionPickListener) {
        if (data == null || data.length == 0) {
            return;
        }
        if (selectedIndex < 0 || data.length <= selectedIndex) {
            selectedIndex = 0;
        }

        OptionPicker picker = new OptionPicker(activity, data);
        picker.setCanceledOnTouchOutside(true);
        picker.setSelectedIndex(selectedIndex);
        picker.setCycleDisable(true);
        if (onOptionPickListener != null) {
            picker.setOnOptionPickListener(onOptionPickListener);
        }
        picker.show();
    }

    public static void showOptionInfoPicker(Activity activity, List data, int selectedIndex, OptionPicker.OnOptionPickListener onOptionPickListener) {
        if (data == null || data.size() == 0) {
            return;
        }
        if (selectedIndex < 0 || data.size() <= selectedIndex) {
            selectedIndex = 0;
        }

        OptionPicker picker = new OptionPicker(activity, data);
        picker.setCanceledOnTouchOutside(true);
        picker.setSelectedIndex(selectedIndex);
        picker.setCycleDisable(true);
        if (onOptionPickListener != null) {
            picker.setOnOptionPickListener(onOptionPickListener);
        }
        picker.show();
    }

    public static void showYearMonthDayPicker(Activity activity, String selectedDate, DatePicker.OnYearMonthDayPickListener listener) {
        Calendar startCalendar = Calendar.getInstance();
        int startYear = startCalendar.get(Calendar.YEAR);

        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.setTime(dateFromString(selectedDate));
        int selectedYear = selectedCalendar.get(Calendar.YEAR);
        int selectedMonth = selectedCalendar.get(Calendar.MONTH) + 1;
        int selectedDay = selectedCalendar.get(Calendar.DAY_OF_MONTH);

        int minYear = Math.min(startYear, selectedYear);
        int maxYear = Math.max(startYear, selectedYear);

        final DatePicker picker = new DatePicker(activity);
        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(20);
        picker.setRangeStart(minYear - 5, 1, 1);
        picker.setRangeEnd(maxYear + 5, 12, 31);
        picker.setSelectedItem(selectedYear, selectedMonth, selectedDay);
        picker.setResetWhileWheel(false);
        picker.setOnDatePickListener(listener);
        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });
        picker.show();
    }

    public static Date dateFromString(String string) {
        if (StringUtils.isEmpty(string)) {
            return new Date();
        }

        String formatString = null;
        int length = StringUtils.length(string);
        if (length == 8) {
            formatString = DateFormat_8;
        } else if (length == 10) {
            formatString = DateFormat_10;
        }

        if (StringUtils.isEmpty(formatString)) {
            return new Date();
        }

        try {
            SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.getDefault());
            return format.parse(string);
        } catch (Exception ex) {
            // do nothing
        }

        return new Date();
    }
}
