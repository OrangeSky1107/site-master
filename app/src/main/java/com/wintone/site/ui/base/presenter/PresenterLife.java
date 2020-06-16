package com.wintone.site.ui.base.presenter;

import com.wintone.site.ui.base.view.BaseView;

import androidx.annotation.NonNull;

public interface PresenterLife {

    void onCreate();

    void onBindView(@NonNull BaseView baseView);

    void onDestroy();
}
