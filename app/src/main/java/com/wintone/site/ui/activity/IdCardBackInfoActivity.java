package com.wintone.site.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ActivityUtils;
import com.bumptech.glide.Glide;
import com.msd.ocr.idcard.LibraryInitOCR;
import com.wintone.site.R;
import com.wintone.site.ui.base.activity.BaseActivity;

import java.util.HashMap;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;

public class IdCardBackInfoActivity extends BaseActivity {

    @BindView(R.id.officeTextView)   TextView officeTextView;
    @BindView(R.id.nameTextView)     TextView nameTextView;
    @BindView(R.id.idFrontImageView) ImageView idFrontImageView;
    @BindView(R.id.openCamera)       TextView openCamera;

    @Override
    protected int getContentView() {
        return R.layout.activity_id_card_back_info;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.openCamera,R.id.nextOperation})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.openCamera:
                Bundle bundle = new Bundle();
                bundle.putBoolean("saveImage", true);  // 是否保存识别图片
                bundle.putBoolean("showSelect", true); // 是否显示选择图片
                bundle.putBoolean("showCamera", true); // 显示图片界面是否显示拍照(驾照选择图片识别率比扫描高)
                bundle.putInt("requestCode", 1);       // requestCode
                bundle.putInt("type", 0);              // 0身份证, 1驾驶证

                LibraryInitOCR.startScan(IdCardBackInfoActivity.this, bundle);
                break;

            case R.id.nextOperation:
                //是否需要采集银行信息
                ActivityUtils.startActivity(new Intent(IdCardBackInfoActivity.this,BankInfoActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LibraryInitOCR.closeDecode();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            String result = data.getStringExtra("OCRResult");
            HashMap hashMap = JSON.parseObject(result,HashMap.class);
            officeTextView.setText(hashMap.get("issue").toString());
            nameTextView.setText(hashMap.get("valid").toString());
            Glide.with(this).load(hashMap.get("imgPath").toString()).into(idFrontImageView);
            openCamera.setVisibility(View.GONE);
//            try {
//                JSONObject jo = new JSONObject(result);
//                StringBuffer sb = new StringBuffer();
//                sb.append(String.format("正面 = %s\n", jo.opt("type")));
//                sb.append(String.format("姓名 = %s\n", jo.opt("name")));
//                sb.append(String.format("性别 = %s\n", jo.opt("sex")));
//                sb.append(String.format("民族 = %s\n", jo.opt("folk")));
//                sb.append(String.format("日期 = %s\n", jo.opt("birt")));
//                sb.append(String.format("号码 = %s\n", jo.opt("num")));
//                sb.append(String.format("住址 = %s\n", jo.opt("addr")));
//                sb.append(String.format("签发机关 = %s\n", jo.opt("issue")));
//                sb.append(String.format("有效期限 = %s\n", jo.opt("valid")));
//                sb.append(String.format("整体照片 = %s\n", jo.opt("imgPath")));
//                sb.append(String.format("头像路径 = %s\n", jo.opt("headPath")));
//                sb.append("\n驾照专属字段\n");
//                sb.append(String.format("国家 = %s\n", jo.opt("nation")));
//                sb.append(String.format("初始领证 = %s\n", jo.opt("startTime")));
//                sb.append(String.format("准驾车型 = %s\n", jo.opt("drivingType")));
//                sb.append(String.format("有效期限 = %s\n", jo.opt("registerDate")));
//                Log.i("MainActivity","look at current message = " + sb.toString());
//            } catch (JSONException e) {
//                KLog.i("look at error message = " + e.getMessage().toString());
//                e.printStackTrace();
//            }
        }
    }
}
