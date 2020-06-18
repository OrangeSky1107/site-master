package com.wintone.site.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.wintone.site.R;
import com.wintone.site.ui.activity.AboutCompanyActivity;
import com.wintone.site.ui.activity.FeedBackActivity;
import com.wintone.site.ui.activity.ModifyPasswordActivity;
import com.wintone.site.ui.activity.UserInfoActivity;
import com.wintone.site.ui.base.fragment.BaseFragment;

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

    @OnClick({R.id.mini_password,R.id.mini_ours,R.id.mini_feedback,R.id.mini_customer,R.id.cellAboutAccount})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.mini_password:
                ActivityUtils.startActivity(new Intent(getActivity(), ModifyPasswordActivity.class));
                break;
            case R.id.mini_ours:
                ActivityUtils.startActivity(new Intent(getActivity(), AboutCompanyActivity.class));
                break;
            case R.id.mini_feedback:
                ActivityUtils.startActivity(new Intent(getActivity(), FeedBackActivity.class));
                break;
            case R.id.mini_customer:
                showPhoneDialog();
                break;
            case R.id.cellAboutAccount:
                ActivityUtils.startActivity(new Intent(getActivity(), UserInfoActivity.class));
                break;
        }
    }

    private void showPhoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("客服电话");
        builder.setMessage("028-82571111");

        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.setPositiveButton("立即呼叫",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.setCancelable(false);

        builder.show();
    }

}