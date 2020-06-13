package com.yizhitong.site.ui.base.presenter;

import com.yizhitong.site.ui.base.view.BaseView;

import androidx.annotation.NonNull;

public interface PresenterLife {

    void onCreate();

    void onBindView(@NonNull BaseView baseView);

    void onDestroy();
}
