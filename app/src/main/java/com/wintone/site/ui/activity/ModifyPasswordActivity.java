package com.wintone.site.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.socks.library.KLog;
import com.wintone.site.R;
import com.wintone.site.network.NetService;
import com.wintone.site.network.NetWorkUtils;
import com.wintone.site.networkmodel.ResponseModel;
import com.wintone.site.ui.base.activity.BaseActivity;
import com.wintone.site.utils.Constant;
import com.wintone.site.utils.SPUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ModifyPasswordActivity extends BaseActivity {

    @BindView(R.id.toolbar_title)    TextView toolbarTitle;

    @BindView(R.id.et_old_password)  EditText etOldPassword;
    @BindView(R.id.et_new_password)  EditText etNewPassword;
    @BindView(R.id.et_confirm_password) EditText etConfirmPassword;
    @BindView(R.id.btn_login)   Button btnLogin;

    private KProgressHUD mHUD;

    @Override
    protected int getContentView() {
        return R.layout.activity_modify_password;
    }

    @Override
    protected void initView() {
        toolbarTitle.setText("修改密码");

        initProgress();
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.iv_back,R.id.btn_login})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_login:
                mHUD.show();
                submit();
                break;
        }
    }

    private void submit(){
        String token = (String) SPUtils.getShare(this,Constant.USER_TOKEN,"");
        String loginName = (String) SPUtils.getShare(this,Constant.USER_NAME,"");
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword) || StringUtils.isEmpty(confirmPassword)) {
            ToastUtils.showShort("密码不能为空");
            mHUD.dismiss();
            return;
        }

        if (!StringUtils.equals(newPassword, confirmPassword)) {
            ToastUtils.showShort("两次密码不一致");
            mHUD.dismiss();
            return;
        }

        Map<String,String> stringMap = new HashMap<>();
        stringMap.put("loginName",loginName);
        stringMap.put("oldPassword",oldPassword);
        stringMap.put("newPassword",newPassword);
        stringMap.put("confirmPassword",confirmPassword);

        NetWorkUtils.getInstance().createService(NetService.class)
                .postUpdatePassword(token,Constant.UPDATE_PASSWORD_URL,stringMap)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseModel>() {
                    @Override public void onSubscribe(Disposable d) { }
                    @Override
                    public void onNext(ResponseModel value) {
                        if(value.getCode() == 1000){
                            ToastUtils.showShort(value.getMessage());
                            finish();
                        }else{
                            ToastUtils.showShort(value.getMessage());
                        }
                        mHUD.dismiss();
                    }
                    @Override
                    public void onError(Throwable e) {
                        KLog.i("look at error message = " + e.getMessage().toString());
                        mHUD.dismiss();
                    }
                    @Override public void onComplete() {}
                });
    }

    private void initProgress() {
        mHUD = KProgressHUD.create(ModifyPasswordActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDetailsLabel("修改中...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }
}
