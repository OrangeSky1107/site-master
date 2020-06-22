package com.wintone.site.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.bumptech.glide.Glide;
import com.wintone.site.R;
import com.wintone.site.ui.activity.AboutCompanyActivity;
import com.wintone.site.ui.activity.FeedBackActivity;
import com.wintone.site.ui.activity.ModifyPasswordActivity;
import com.wintone.site.ui.activity.SystemSettingActivity;
import com.wintone.site.ui.activity.UserInfoActivity;
import com.wintone.site.ui.base.fragment.BaseFragment;
import com.wintone.site.utils.Constant;
import com.wintone.site.utils.SPUtils;
import com.wintone.site.widget.CircleImageView;

import butterknife.BindView;
import butterknife.OnClick;


public class MineFragment extends BaseFragment {

    @BindView(R.id.mini_password) LinearLayout passwordLayout;
    @BindView(R.id.iv_back)       ImageView ivBack;
    @BindView(R.id.toolbar_title) TextView toolbarTitle;
    @BindView(R.id.headerPhoto)   CircleImageView headerPhoto;
    @BindView(R.id.mini_phone)    TextView mini_phone;
    @BindView(R.id.userExplain)   TextView userExplain;

    @Override
    protected int getContentView() {
        return R.layout.fragment_notifications;
    }

    @Override
    protected void initView(View view) {
        ivBack.setVisibility(View.GONE);

        toolbarTitle.setText("我的");

        String imgUrl = (String) SPUtils.getShare(getActivity(), Constant.FACE_URL,"");
        Glide.with(getActivity()).load(imgUrl).into(headerPhoto);

        String name = (String)SPUtils.getShare(getActivity(),Constant.USER_NAME,"");
        mini_phone.setText(name);

        Integer user_Type = (Integer) SPUtils.getShare(getActivity(),Constant.USER_TYPE,5);

        switch (user_Type){
            case 0:
                userExplain.setText("集团");
                break;
            case 1:
                userExplain.setText("企业");
                break;
            case 2:
                userExplain.setText("项目");
                break;
            case 3:
                userExplain.setText("参建单位");
                break;
            default:
                userExplain.setText("超级管理员");
                break;
        }
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

    @OnClick({R.id.mini_password,R.id.mini_ours,R.id.mini_feedback,R.id.mini_customer,R.id.cellAboutAccount,R.id.mini_setting,R.id.cellProject})
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
            case R.id.mini_setting:
                ActivityUtils.startActivity(new Intent(getActivity(), SystemSettingActivity.class));
                break;
            case R.id.cellProject:
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
                        dialog.dismiss();
                    }
                });

        builder.setPositiveButton("立即呼叫",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        Uri data = Uri.parse("tel:" + "028-82571111");
                        intent.setData(data);
                        startActivity(intent);
                    }
                });

        builder.setCancelable(false);

        builder.show();
    }

}