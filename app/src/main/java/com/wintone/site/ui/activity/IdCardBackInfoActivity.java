package com.wintone.site.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.msd.ocr.idcard.LibraryInitOCR;
import com.wintone.site.R;
import com.wintone.site.ui.base.activity.BaseActivity;

import java.util.HashMap;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;

public class IdCardBackInfoActivity extends BaseActivity {

    @BindView(R.id.officeTextView)   EditText officeTextView;
    @BindView(R.id.nameTextView)     EditText nameTextView;
    @BindView(R.id.idFrontImageView) ImageView idFrontImageView;
    @BindView(R.id.openCamera)       TextView openCamera;
    @BindView(R.id.toolbar_title)    TextView toolbarTitle;
    @BindView(R.id.toolbar_right)    ImageView toolbarRight;
    @BindView(R.id.nextOperation)    Button nextOperation;

    private HashMap dataMap = null;

    @Override
    protected int getContentView() {
        return R.layout.activity_id_card_back_info;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        if(bundle != null){
            dataMap = (HashMap) bundle.getSerializable("data");
        }

        toolbarTitle.setText("身份证背面");

        toolbarRight.setVisibility(View.VISIBLE);
        toolbarRight.setImageResource(R.drawable.reset_photo);
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.idFrontImageView,R.id.nextOperation,R.id.iv_back,R.id.toolbar_right})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.idFrontImageView:
                if(preventDoubleClick()){
                    return;
                }
                openCamera();
                break;

            case R.id.nextOperation:
                //是否需要采集银行信息
                if(preventDoubleClick()){
                    return;
                }
                Intent intent = new Intent(IdCardBackInfoActivity.this,BankInfoActivity.class);
                Bundle bankBundle = new Bundle();
                bankBundle.putSerializable("data",dataMap);
                intent.putExtra("bundle",bankBundle);
                ActivityUtils.startActivity(intent);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.toolbar_right:
                if(preventDoubleClick()){
                    return;
                }
                openCamera();
                break;
        }
    }

    private void openCamera(){
        Bundle bundle = new Bundle();
        bundle.putBoolean("saveImage", true);  // 是否保存识别图片
        bundle.putBoolean("showSelect", true); // 是否显示选择图片
        bundle.putBoolean("showCamera", true); // 显示图片界面是否显示拍照(驾照选择图片识别率比扫描高)
        bundle.putInt("requestCode", 1);       // requestCode
        bundle.putInt("type", 0);              // 0身份证, 1驾驶证

        LibraryInitOCR.startScan(IdCardBackInfoActivity.this, bundle);
    }

    private void showPhoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(IdCardBackInfoActivity.this);
        builder.setTitle("温馨提示!");
        builder.setMessage("是否需要采集银行卡信息!");

        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.setPositiveButton("确认",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityUtils.startActivity(new Intent(IdCardBackInfoActivity.this,BankInfoActivity.class));
                    }
                });

        builder.setCancelable(false);

        builder.show();
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

            String valid = hashMap.get("valid").toString();
            if(valid.length() < 10){
                ToastUtils.showShort("图片有误,请重新拍摄!");
                return;
            }

            officeTextView.setText(hashMap.get("issue").toString());
            nameTextView.setText(hashMap.get("valid").toString());
            Glide.with(this).load(hashMap.get("imgPath").toString()).into(idFrontImageView);
            openCamera.setVisibility(View.GONE);
            dataMap.put("issue",officeTextView.getText().toString());
            dataMap.put("valid",nameTextView.getText().toString());
            dataMap.put("backImagePath",hashMap.get("imgPath").toString());

            nextOperation.setEnabled(true);
        }
    }
}
