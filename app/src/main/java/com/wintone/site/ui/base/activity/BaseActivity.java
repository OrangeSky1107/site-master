package com.wintone.site.ui.base.activity;

import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;

import com.blankj.utilcode.util.ToastUtils;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.wintone.site.R;
import com.wintone.site.SiteApplication;
import com.wintone.site.receiver.NetListenerReceiver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity implements NetListenerReceiver.NetChangeListener {

    private   Unbinder m;
    protected KProgressHUD mHUD;
    private   NetListenerReceiver mReceiver;

    private long lastClickTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        SiteApplication.getInstance().addActivity(this);
        m = ButterKnife.bind(this);
        initProgress();
        initView();
        registerCustomBroadcast();
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
        unregisterReceiver(mReceiver);
        SiteApplication.getInstance().removeActivity(this);
    }

    protected boolean preventDoubleClick() {
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return true;
        }
        lastClickTime = SystemClock.elapsedRealtime();
        return false;
    }

    private void registerCustomBroadcast(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            mReceiver = new NetListenerReceiver();
            mReceiver.setNetChangeListener(this);
            registerReceiver(mReceiver, filter);
        }
    }

    @Override
    public void onChangeListener(int status) {
        switch (status){
            case -1:
                //没有网络
                ToastUtils.setGravity(Gravity.CENTER_VERTICAL,-1,-1);
                ToastUtils.setBgColor(getResources().getColor(R.color.person_dark_gray_color));
                ToastUtils.showLong("当前无可用网络!");
                if(null != mHUD && mHUD.isShowing()){
                    Log.i("BaseActivity","onChangeListener");
                    mHUD.dismiss();
                }
                break;
            case  0:
                //移动网络
                break;
            case  1:
                //无线网络
                break;
        }
    }

    protected abstract int getContentView();

    protected abstract void initView();

    protected abstract void initData();
}
