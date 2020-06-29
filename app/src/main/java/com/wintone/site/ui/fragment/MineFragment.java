package com.wintone.site.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.blankj.utilcode.util.ActivityUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.wintone.site.R;
import com.wintone.site.ui.activity.AboutCompanyActivity;
import com.wintone.site.ui.activity.FeedBackActivity;
import com.wintone.site.ui.activity.ModifyPasswordActivity;
import com.wintone.site.ui.activity.ProjectSwitchActivity;
import com.wintone.site.ui.activity.SystemSettingActivity;
import com.wintone.site.ui.activity.UserInfoActivity;
import com.wintone.site.ui.base.fragment.BaseFragment;
import com.wintone.site.utils.Constant;
import com.wintone.site.utils.SPUtils;
import com.wintone.site.utils.faceutils.ConfigUtil;
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

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.mine_default_avatar)
                .error(R.drawable.mine_default_avatar);
        Glide.with(getActivity()).load(imgUrl).apply(options).into(headerPhoto);

        String name = (String)SPUtils.getShare(getActivity(),Constant.USER_NAME,"");
        mini_phone.setText(name);

        Integer user_Type = (Integer) SPUtils.getShare(getActivity(),Constant.USER_TYPE,5);

        String companyName = (String)SPUtils.getShare(getActivity(),Constant.COMPANY_NAME,"无所属公司");
        String projectName = (String)SPUtils.getShare(getActivity(),Constant.PROJECT_NAME,"无所属项目");

        switch (user_Type){
            case 0:
                //company
                userExplain.setText(companyName);
                break;
            case 1:
                //company
                userExplain.setText(companyName);
                break;
            case 2:
                userExplain.setText(projectName);
                //project
                break;
            case 3:
                userExplain.setText(projectName);
                break;
            default:
                userExplain.setText(companyName);
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

    @OnClick({R.id.mini_password,R.id.mini_ours,R.id.mini_feedback,R.id.mini_customer,R.id.cellAboutAccount,R.id.mini_setting,R.id.cellProject
    ,R.id.mini_camera})
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
                ActivityUtils.startActivity(new Intent(getActivity(), ProjectSwitchActivity.class));
                break;
            case R.id.mini_camera:
                switchCameraDialog();
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

    private void switchCameraDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("切换摄像头");
        Integer integer = (Integer)SPUtils.getShare(getActivity(),Constant.CAMERA_SWITCH,1);
        if(integer == 1){
            builder.setMessage("是否要切换成后置摄像头");
        }else{
            builder.setMessage("是否要切换成前置摄像头");
        }

        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.setPositiveButton("确认",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int flag = 1;
                        if(integer == 1){
                            ConfigUtil.setFtOrient(getActivity(), DetectFaceOrientPriority.ASF_OP_ALL_OUT);
                            flag = 0;
                        }else{
                            ConfigUtil.setFtOrient(getActivity(),DetectFaceOrientPriority.ASF_OP_270_ONLY);
                            flag = 1;
                        }
                        SPUtils.putShare(getActivity(),Constant.CAMERA_SWITCH,flag);
                        dialog.dismiss();
                    }
                });

        builder.setCancelable(false);

        builder.show();
    }

}