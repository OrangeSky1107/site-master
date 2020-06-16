package com.wintone.site.ui.base.view;

public interface BaseView {

    void showProgress();

    void hideProgress();

    void showError(String msg);

    void loadData();
}
