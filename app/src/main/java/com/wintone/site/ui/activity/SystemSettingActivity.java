package com.wintone.site.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.wintone.site.R;
import com.wintone.site.SiteApplication;
import com.wintone.site.network.NetService;
import com.wintone.site.network.NetWorkUtils;
import com.wintone.site.ui.base.activity.BaseActivity;
import com.wintone.site.utils.Constant;
import com.wintone.site.utils.SPUtils;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class SystemSettingActivity extends BaseActivity {

    @BindView(R.id.logout)        Button logout;
    @BindView(R.id.toolbar_title) TextView toolbar_title;

    @Override
    protected int getContentView() {
        return R.layout.activity_system_setting;
    }

    @Override
    protected void initView() {
        toolbar_title.setText("系统设置");
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.logout,R.id.iv_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.logout:
                mHUD.show();
                loginOutOfNetwork();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void loginOutOfNetwork(){

        String token = (String) SPUtils.getShare(SystemSettingActivity.this, Constant.USER_TOKEN,"");

        NetWorkUtils.getInstance().createService(NetService.class)
                .postLoginOut(Constant.USER_LOGINOUT,token)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(ResponseBody value) {
                            mHUD.dismiss();
                            loginOut();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mHUD.dismiss();
                    }

                    @Override public void onComplete() {}
                });
    }

    private void loginOut(){
        SPUtils.clearShare(SystemSettingActivity.this);
        SiteApplication.getInstance().removeAllActivity();
        ActivityUtils.startActivity(new Intent(SystemSettingActivity.this,LoginActivity.class));
    }
}
