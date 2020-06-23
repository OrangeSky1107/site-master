package com.wintone.site.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.Status;
import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.wintone.site.R;
import com.wintone.site.SiteApplication;
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
    @BindView(R.id.frameLayout)    FrameLayout mFrameLayout;
    @BindView(R.id.circle_loading_view) AnimatedCircleLoadingView mCircleLoadingView;

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
                    mFrameLayout.setVisibility(View.VISIBLE);
                    startUploaderUrl(mVersionModel.getResult().getDownloadUrl());
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

    public void startUploaderUrl(String downloaderUrl){

        int downloadOne = 0;

        if (Status.RUNNING == PRDownloader.getStatus(downloadOne)) {
            PRDownloader.pause(downloadOne);
            return;
        }

        if (Status.PAUSED == PRDownloader.getStatus(downloadOne)) {
            PRDownloader.resume(downloadOne);
            return;
        }

        final String installPath = com.wintone.site.utils.AppUtils.getRootDirPath(SiteApplication.getInstance());

        downloadOne = PRDownloader.download(downloaderUrl, installPath, Constant.APK_NAME)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        //开始
                        mCircleLoadingView.startDeterminate();
                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {
                        //暂停
                        mCircleLoadingView.stopOk();
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {
                        //取消
                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        //进度条
                        long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                        mCircleLoadingView.setPercent((int)progressPercent);
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        com.wintone.site.utils.AppUtils.installApk(AboutCompanyActivity.this,
                                installPath+"/"+Constant.APK_NAME);
                    }

                    @Override
                    public void onError(Error error) {
                        ToastUtils.showShort("更新出现错误:"+error.toString());
                    }
                });
    }


}
