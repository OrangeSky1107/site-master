package com.yizhitong.site.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yizhitong.site.R;
import com.yizhitong.site.ui.base.activity.BaseActivity;

import java.util.HashMap;

import butterknife.BindView;

public class IdCardInfoActivity extends BaseActivity {

    @BindView(R.id.nameTextView)      TextView nameTextView;
    @BindView(R.id.sexExplain)        TextView sexExplain;
    @BindView(R.id.nationExplain)     TextView nationExplain;
    @BindView(R.id.idNoExplain)       TextView idNoExplain;
    @BindView(R.id.birthdayExplain)   TextView birthdayExplain;
    @BindView(R.id.addressExplain)    TextView addressExplain;
    @BindView(R.id.idFrontImageView)  ImageView idFrontImageView;
    @BindView(R.id.idHeaderImageView) ImageView mCircleImageView;

    @Override
    protected int getContentView() {
        return R.layout.activity_id_card_info;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();

        Bundle bundle = intent.getBundleExtra("bundle");

        HashMap hashMap = (HashMap) bundle.getSerializable("data");

        operationView(hashMap);
    }

    private void operationView(HashMap hashMap){
        nameTextView.setText(hashMap.get("name").toString());
        sexExplain.setText(hashMap.get("sex").toString());
        nationExplain.setText(hashMap.get("folk").toString());
        idNoExplain.setText(hashMap.get("num").toString());
        birthdayExplain.setText(hashMap.get("birt").toString());
        addressExplain.setText(hashMap.get("addr").toString());

        Glide.with(this).load(hashMap.get("imgPath")).into(idFrontImageView);

        Glide.with(this).load(hashMap.get("headPath")).into(mCircleImageView);
    }

    @Override
    protected void initData() {

    }
}
