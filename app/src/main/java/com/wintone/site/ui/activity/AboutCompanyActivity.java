package com.wintone.site.ui.activity;

import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.AppUtils;
import com.socks.library.KLog;
import com.wintone.site.R;
import com.wintone.site.network.NetService;
import com.wintone.site.network.NetWorkUtils;
import com.wintone.site.networkmodel.AppVersionModel;
import com.wintone.site.ui.base.activity.BaseActivity;
import com.wintone.site.utils.Constant;
import com.wintone.site.utils.SPUtils;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AboutCompanyActivity extends BaseActivity {

    @BindView(R.id.currentVersion) TextView currentVersion;
    @BindView(R.id.toolbar_title)  TextView toolbar_title;

    @Override
    protected int getContentView() {
        return R.layout.activity_about_company;
    }

    @Override
    protected void initView() {
        currentVersion.setText(AppUtils.getAppVersionName());

        toolbar_title.setText("关于我们");
    }

    @Override
    protected void initData() {
        String token = (String) SPUtils.getShare(AboutCompanyActivity.this,Constant.USER_TOKEN,"");

        String url = Constant.CHECK_VERSION_URL + "1" ;

        NetWorkUtils.getInstance().createService(NetService.class)
                .getAppVersionInfo(url,token)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppVersionModel>() {
                    @Override public void onSubscribe(Disposable d) { }

                    @Override
                    public void onNext(AppVersionModel value) {
                        KLog.i("look at response body = " + JSON.toJSONString(value));

                    }

                    @Override
                    public void onError(Throwable e) {
                        KLog.i("look at response error message  = " + e.getMessage());
                    }

                    @Override public void onComplete() { }
                });
    }

    @OnClick({R.id.iv_back})
    public void onClick(View view){
        switch (R.id.iv_back){
            case R.id.iv_back:
                finish();
                break;
        }
    }

}
