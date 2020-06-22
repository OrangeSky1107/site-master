package com.wintone.site.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
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

    private AppVersionModel mVersionModel;

    @Override
    protected int getContentView() {
        return R.layout.activity_about_company;
    }

    @Override
    protected void initView() {
        currentVersion.setText(AppUtils.getAppVersionName());

        toolbar_title.setText("关于我们");

        mHUD.setDetailsLabel("加载中...");
    }

    @Override
    protected void initData() {
        mHUD.show();

        String token = (String) SPUtils.getShare(AboutCompanyActivity.this,Constant.USER_TOKEN,"");

        String url = Constant.CHECK_VERSION_URL + "1" + "/V"+AppUtils.getAppVersionName();

        NetWorkUtils.getInstance().createService(NetService.class)
                .getAppVersionInfo(url,token)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppVersionModel>() {
                    @Override public void onSubscribe(Disposable d) { }

                    @Override
                    public void onNext(AppVersionModel value) {
                        mVersionModel = value;
                        mHUD.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShort("更新日志错误:"+e.getMessage());
                        mHUD.dismiss();
                    }

                    @Override public void onComplete() { }
                });
    }

    @OnClick({R.id.iv_back,R.id.update,R.id.update_history})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.update:
                if(mVersionModel.getResult().getIsUpdate() == 0){
                    ToastUtils.showShort("当前已经是最新版本了");
                }else{
                    //开始更新APK
                }
                break;
            case R.id.update_history:
                showPhoneDialog(mVersionModel.getResult().getContent());
                break;
        }
    }

    private void showPhoneDialog(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("更新日志");
        builder.setMessage(content);

        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.setCancelable(false);

        builder.show();
    }

}
