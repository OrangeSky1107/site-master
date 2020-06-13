package com.yizhitong.site.ui.base.presenter;

import com.yizhitong.site.callback.RequestCallBack;
import com.yizhitong.site.ui.base.view.BaseView;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;

public class BasePresenter <V extends BaseView,T> implements PresenterLife, RequestCallBack<T> {

    protected WeakReference<V> mView; //使用弱引用 避免内存泄露

    @Override
    public void onStart(T data) {

    }

    @Override
    public void onSuccess(T data) {
        mView.get().hideProgress();
    }

    @Override
    public void onError(String errorMsg, boolean pullToRefresh) {

    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onProgress(long downSize, long fileSize) {

    }

    @Override
    public void downloadSuccess(String path, String fileName, long fileSize) {

    }

    @Override
    public void onCreate() {

    }

    public V getView(){
        return mView==null ? null : mView.get();
    }

    @Override
    public void onBindView(@NonNull BaseView baseView) {
        mView = new WeakReference<V>((V) baseView);
    }

    @Override
    public void onDestroy() {
        if(mView != null){
            mView.clear();
            mView = null;
        }
    }
}
