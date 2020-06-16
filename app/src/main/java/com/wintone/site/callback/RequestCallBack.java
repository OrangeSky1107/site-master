package com.wintone.site.callback;


public interface RequestCallBack<T> {

    void onStart(T data);//请求前

    void onSuccess(T data); //请求成功

    void onError(String errorMsg, boolean pullToRefresh); //请求失败

    void onCompleted();//请求完成

    //下载
    void onProgress(long downSize, long fileSize);

    void downloadSuccess(String path, String fileName, long fileSize);
}
