package com.wintone.site.ui.base.activity;

import android.os.Bundle;

import com.wintone.site.SiteApplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {

    private Unbinder m;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        SiteApplication.getInstance().addActivity(this);
        m = ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m.unbind();
        m = null;
        SiteApplication.getInstance().removeActivity(this);
    }

    protected abstract int getContentView();

    protected abstract void initView();

    protected abstract void initData();
}
