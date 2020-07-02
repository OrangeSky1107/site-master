package com.wintone.site.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.msd.ocr.idcard.LibraryInitOCR;
import com.socks.library.KLog;
import com.wintone.site.R;
import com.wintone.site.ui.base.activity.BaseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;

public class IdCardInfoActivity extends BaseActivity{

    @BindView(R.id.nameTextView)      EditText nameTextView;
    @BindView(R.id.sexExplain)        EditText sexExplain;
    @BindView(R.id.nationExplain)     EditText nationExplain;
    @BindView(R.id.idNoExplain)       EditText idNoExplain;
    @BindView(R.id.birthdayExplain)   EditText birthdayExplain;
    @BindView(R.id.addressExplain)    EditText addressExplain;
    @BindView(R.id.idFrontImageView)  ImageView idFrontImageView;
    @BindView(R.id.idHeaderImageView) ImageView mCircleImageView;
    @BindView(R.id.toolbar_title)     TextView toolbarTitle;
    @BindView(R.id.toolbar_right)     ImageView toolbarRight;

    private String imgPath;

    private HashMap hashMap = null;

    private static final String[] LIBRARIES = new String[]{
            // 人脸相关
            "libarcsoft_face_engine.so",
            "libarcsoft_face.so",
            // 图像库相关
            "libarcsoft_image_util.so",
    };

    @Override
    protected int getContentView() {
        return R.layout.activity_id_card_info;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        if(bundle != null){
            hashMap = (HashMap) bundle.getSerializable("data");
        }

        toolbarTitle.setText("身份证信息");

        toolbarRight.setVisibility(View.VISIBLE);
        toolbarRight.setImageResource(R.drawable.reset_photo);

        operationView(hashMap);
    }

    private void operationView(HashMap hashMap){
        if(hashMap != null){
            nameTextView.setText(hashMap.get("name").toString());
            sexExplain.setText(hashMap.get("sex").toString());
            nationExplain.setText(hashMap.get("folk").toString());
            idNoExplain.setText(hashMap.get("num").toString());
            birthdayExplain.setText(hashMap.get("birt").toString());
            addressExplain.setText(hashMap.get("addr").toString());
            Glide.with(this).load(hashMap.get("imgPath")).into(idFrontImageView);
            imgPath = hashMap.get("headPath").toString();
            Glide.with(this).load(hashMap.get("headPath")).into(mCircleImageView);
        }
    }

    private HashMap extraInoutText(){
        String name = nameTextView.getText().toString();
        hashMap.put("name",name);
        String sex = sexExplain.getText().toString();
        hashMap.put("sex",sex);
        String nation = nationExplain.getText().toString();
        hashMap.put("folk",nation);
        String idNo = idNoExplain.getText().toString();
        hashMap.put("num",idNo);
        String birthday = birthdayExplain.getText().toString();
        hashMap.put("birt",birthday);
        String address = addressExplain.getText().toString();
        hashMap.put("addr",address);
        return hashMap;
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.next,R.id.toolbar_right})

    public void onClick(View view){
        switch(view.getId()){
            case R.id.next:{
                if(checkSoFile(LIBRARIES)){
                    Intent intent = new Intent(IdCardInfoActivity.this,FacePreViewActivity.class);
                    intent.putExtra("headPath",imgPath);
                    if(hashMap == null){
                        ToastUtils.showShort("请先拍摄身份证正面信息!");
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data",extraInoutText());
                    intent.putExtra("bundle",bundle);
                    startActivity(intent);
                    finish();
                }else{
                    KLog.i("can't find face so file");
                }
                break;
            }
            case R.id.toolbar_right:
                Bundle bundle = new Bundle();
                bundle.putBoolean("saveImage", true);  // 是否保存识别图片
                bundle.putBoolean("showSelect", true); // 是否显示选择图片
                bundle.putBoolean("showCamera", true); // 显示图片界面是否显示拍照(驾照选择图片识别率比扫描高)
                bundle.putInt("requestCode", 1);       // requestCode
                bundle.putInt("type", 0);              // 0身份证, 1驾驶证

                LibraryInitOCR.startScan(this, bundle);
                break;
        }
    }

    /**
     * 检查能否找到动态链接库，如果找不到，请修改工程配置
     *
     * @param libraries 需要的动态链接库
     * @return 动态库是否存在
     */
    private boolean checkSoFile(String[] libraries) {
        File dir = new File(getApplicationInfo().nativeLibraryDir);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return false;
        }
        List<String> libraryNameList = new ArrayList<>();
        for (File file : files) {
            libraryNameList.add(file.getName());
        }
        boolean exists = true;
        for (String library : libraries) {
            exists &= libraryNameList.contains(library);
        }
        return exists;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            String result = data.getStringExtra("OCRResult");
            HashMap hashMap = JSON.parseObject(result,HashMap.class);
            if(hashMap != null){
                nameTextView.setText(hashMap.get("name").toString());
                sexExplain.setText(hashMap.get("sex").toString());
                nationExplain.setText(hashMap.get("folk").toString());
                idNoExplain.setText(hashMap.get("num").toString());
                birthdayExplain.setText(hashMap.get("birt").toString());
                addressExplain.setText(hashMap.get("addr").toString());
                Glide.with(this).load(hashMap.get("imgPath")).into(idFrontImageView);
                imgPath = hashMap.get("headPath").toString();
                Glide.with(this).load(hashMap.get("headPath")).into(mCircleImageView);
            }
        }
    }
}
