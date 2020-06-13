package com.yizhitong.site.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.yizhitong.site.R;
import com.yizhitong.site.ui.activity.ModifyPasswordActivity;
import com.yizhitong.site.ui.base.fragment.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;


public class MineFragment extends BaseFragment {

    @BindView(R.id.mini_password) LinearLayout passwordLayout;
    @BindView(R.id.iv_back)       ImageView ivBack;
    @BindView(R.id.toolbar_title) TextView toolbarTitle;

    @Override
    protected int getContentView() {
        return R.layout.fragment_notifications;
    }

    @Override
    protected void initView(View view) {
        ivBack.setVisibility(View.GONE);

        toolbarTitle.setText("我的");
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void loadData() {

    }

    @OnClick({R.id.mini_password})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.mini_password:
                ActivityUtils.startActivity(new Intent(getActivity(), ModifyPasswordActivity.class));
                break;
        }
    }
}