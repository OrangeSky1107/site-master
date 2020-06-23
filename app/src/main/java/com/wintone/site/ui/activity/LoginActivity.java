package com.wintone.site.ui.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.socks.library.KLog;
import com.wintone.site.R;
import com.wintone.site.network.NetService;
import com.wintone.site.network.NetWorkUtils;
import com.wintone.site.networkmodel.LoginModel;
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

public class LoginActivity extends BaseActivity {

    @BindView(R.id.et_userName)   EditText etUserName;
    @BindView(R.id.et_password)   EditText etPassword;
    @BindView(R.id.iv_unameClear) ImageView ivUserNameClear;
    @BindView(R.id.iv_pwdClear)   ImageView ivPwdClear;

    private KProgressHUD mHUD;

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {

        initProgress();

        etUserName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() > 0){
                    ivUserNameClear.setVisibility(View.VISIBLE);
                }else{
                    ivUserNameClear.setVisibility(View.INVISIBLE);
                }
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() > 0){
                    ivPwdClear.setVisibility(View.VISIBLE);
                }else{
                    ivPwdClear.setVisibility(View.INVISIBLE);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void initProgress() {
        mHUD = KProgressHUD.create(LoginActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDetailsLabel("登入中...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.btn_login,R.id.iv_unameClear,R.id.iv_pwdClear})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_login:
                login();
                break;
            case R.id.iv_unameClear:
                etUserName.setText("");
                break;
            case R.id.iv_pwdClear:
                etPassword.setText("");
                break;

        }
    }

    private void login(){
        mHUD.show();
        String userName = etUserName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if(checkAccount(userName,password)){
            Map<String, String> params = new HashMap<>();
            params.put("userName",userName);
            params.put("passWord",password);
            submit(params);
        }
    }

    public boolean checkAccount(String account, String password) {
        if (TextUtils.isEmpty(account)) {
            ToastUtils.showShort("请填写账号！");
            return false;
        } else if (account.length() < 1) {
            ToastUtils.showShort("账号格式不正确！");
            return false;
        } else if (TextUtils.isEmpty(password)) {
            ToastUtils.showShort("请输入密码！");
            return false;
        } else {
            return true;
        }
    }

    private void submit(Map<String,String> hashMap){
        NetWorkUtils.getInstance().createService(NetService.class)
                .postLogin(Constant.USER_LOGIN_URL,hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LoginModel value) {
                        KLog.i("look at response message = " + JSON.toJSONString(value));
                        saveUserInfo(value);
                        mHUD.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mHUD.dismiss();
                        KLog.i("look at login response error message  = " + e.getMessage().toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void saveUserInfo(LoginModel loginModel){
        if(loginModel.getCode() == 1000){
            SPUtils.putShare(this,Constant.USER_TOKEN,loginModel.getResult().getToken());
            SPUtils.putShare(this,Constant.USER_NAME,loginModel.getResult().getLoginName());
            SPUtils.putShare(this,Constant.CONSTRUCTION_ID,loginModel.getResult().getConstructionId());
            SPUtils.putShare(this,Constant.HEADER_IMAGE,loginModel.getResult().getIco());
            SPUtils.putShare(this,Constant.IS_MANAGER,loginModel.getResult().getIsManager());
            SPUtils.putShare(this,Constant.ORG_ID,loginModel.getResult().getOrgId());
            SPUtils.putShare(this,Constant.PROJECT_ID,loginModel.getResult().getProjectId());
            SPUtils.putShare(this,Constant.SEX,loginModel.getResult().getSex());
            SPUtils.putShare(this,Constant.STATUS,loginModel.getResult().getStatus());
            SPUtils.putShare(this,Constant.USER_TYPE,loginModel.getResult().getUserType());
            SPUtils.putShare(this,Constant.FACE_URL,loginModel.getResult().getFaceUrl());
            SPUtils.putShare(this,Constant.USER_ID,loginModel.getResult().getId());
            SPUtils.putShare(this,Constant.PROJECT_NAME,loginModel.getResult().getDisplayName());
            SPUtils.putShare(this,Constant.NIKE_NAME,loginModel.getResult().getNickName());

            ActivityUtils.startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }else{
            ToastUtils.showShort(loginModel.getCode() + "--- 错误信息:"+loginModel.getMessage());
        }
    }

}

