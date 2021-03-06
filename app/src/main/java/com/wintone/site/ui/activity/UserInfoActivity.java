package com.wintone.site.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ToastUtils;
import com.wintone.site.R;
import com.wintone.site.network.NetService;
import com.wintone.site.network.NetWorkUtils;
import com.wintone.site.networkmodel.UpdateUserModel;
import com.wintone.site.ui.base.activity.BaseActivity;
import com.wintone.site.utils.Constant;
import com.wintone.site.utils.SPUtils;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class UserInfoActivity extends BaseActivity {

    @BindView(R.id.toolbar_title)       TextView toolbar_title;
    @BindView(R.id.account)             TextView accountLayout;
    @BindView(R.id.organization)        TextView organizationLayout;
    @BindView(R.id.nameTextView)        EditText nameTextView;

    @Override
    protected int getContentView() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void initView() {
        toolbar_title.setText("个人信息");

        mHUD.setDetailsLabel("保存中...");

        String nikeName = (String)SPUtils.getShare(this,Constant.NIKE_NAME,"");

        nameTextView.setHint(nikeName);
    }

    @OnClick({R.id.iv_back,R.id.save})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.save:
                mHUD.show();
                submit();
                break;
        }
    }

    private void submit(){
        String nikeName = nameTextView.getText().toString();

        if(TextUtils.isEmpty(nikeName)){
            ToastUtils.showShort("昵称必须输入!");
            return;
        }

        UpdateUserModel updateUserModel = new UpdateUserModel();
        String id = (String)SPUtils.getShare(this,Constant.USER_ID,"");
        updateUserModel.setId(id);
        updateUserModel.setNickName(nikeName);

        String token = (String)SPUtils.getShare(this,Constant.USER_TOKEN,"");
        NetWorkUtils.getInstance().createService(NetService.class)
                .postUpdateUserInfo(Constant.USER_UPDATE_URL,token,updateUserModel)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override public void onSubscribe(Disposable d) { }
                    @Override
                    public void onNext(ResponseBody value) {
                        try {
                            HashMap map =  JSON.parseObject(value.string(),HashMap.class);
                            Integer code = (Integer) map.get("code");
                            if(code == 1000){
                                SPUtils.putShare(UserInfoActivity.this,Constant.NIKE_NAME,nikeName);
                                ToastUtils.showShort("修改成功!");
                                finish();
                            }
                            mHUD.dismiss();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShort("修改失败:"+e.getMessage());
                    }
                    @Override public void onComplete() { }
                });
    }

    @Override
    protected void initData() {
        String userName = (String) SPUtils.getShare(this, Constant.USER_NAME,"");
        accountLayout.setText(userName);

        Integer user_Type = (Integer) SPUtils.getShare(this,Constant.USER_TYPE,5);

        String companyName = (String)SPUtils.getShare(this,Constant.COMPANY_NAME,"无所属公司");
        String projectName = (String)SPUtils.getShare(this,Constant.PROJECT_NAME,"无所属项目");

        switch (user_Type){
            case 0:
                //company
                organizationLayout.setText(companyName);
                break;
            case 1:
                //company
                organizationLayout.setText(companyName);
                break;
            case 2:
                organizationLayout.setText(projectName);
                //project
                break;
            case 3:
                organizationLayout.setText(projectName);
                break;
            default:
                organizationLayout.setText(companyName);
                break;
        }
    }
}
