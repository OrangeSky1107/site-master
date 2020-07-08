package com.wintone.site.ui.base.activity;

import android.os.Bundle;
import android.os.SystemClock;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.wintone.site.SiteApplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {

    private   Unbinder m;
    protected KProgressHUD mHUD;

    private long lastClickTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        SiteApplication.getInstance().addActivity(this);
        m = ButterKnife.bind(this);
        initProgress();
        initView();
    }

    private void initProgress() {
        mHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDetailsLabel("加载中...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHUD.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m.unbind();
        m = null;
        mHUD.dismiss();
        mHUD = null;
        SiteApplication.getInstance().removeActivity(this);
    }

    protected boolean preventDoubleClick() {
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return true;
        }
        lastClickTime = SystemClock.elapsedRealtime();
        return false;
    }


    protected abstract int getContentView();

    protected abstract void initView();

    protected abstract void initData();
}
