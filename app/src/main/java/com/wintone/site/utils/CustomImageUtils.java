package com.wintone.site.utils;

import android.net.Uri;
import android.os.Environment;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.compress.CompressConfig;
import org.devio.takephoto.model.CropOptions;
import org.devio.takephoto.model.TakePhotoOptions;

import java.io.File;

/**
 * create by ths on 2020/6/22
 */
public class CustomImageUtils {

    //选择本地图片上传
    public static void selectLocalImage(TakePhoto takePhoto){
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri imageUri = Uri.fromFile(file);

        configCompress(takePhoto);

        configTakePhotoOption(takePhoto);

        takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions());
    }

    //选择拍照上传
    public static void selectPickerPhotoImage(TakePhoto takePhoto){
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri imageUri = Uri.fromFile(file);

        configCompress(takePhoto);

        configTakePhotoOption(takePhoto);

        takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());
    }

    private static void configCompress(TakePhoto takePhoto){
        int maxSize = 102400;
        int width = 800;
        int height = 800;

        boolean showProgressBar = true;
        boolean enableRawFile = true;

        CompressConfig config;

        config = new CompressConfig.Builder().setMaxSize(maxSize)
                .setMaxPixel(width >= height ? width : height)
                .enableReserveRaw(enableRawFile)
                .create();

        takePhoto.onEnableCompress(config, showProgressBar);
    }

    private static void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        builder.setWithOwnGallery(true);
        takePhoto.setTakePhotoOptions(builder.create());
    }

    private static CropOptions getCropOptions() {
        int height = 800;
        int width = 800;
        boolean withWonCrop = false;

        CropOptions.Builder builder = new CropOptions.Builder();
        builder.setOutputX(width).setOutputY(height);
        builder.setWithOwnCrop(withWonCrop);

        return builder.create();
    }
}
