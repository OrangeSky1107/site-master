package com.wintone.site.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.socks.library.KLog;
import com.wintone.site.R;
import com.wintone.site.ui.base.activity.BaseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class IdCardInfoActivity extends BaseActivity{

    @BindView(R.id.nameTextView)      TextView nameTextView;
    @BindView(R.id.sexExplain)        TextView sexExplain;
    @BindView(R.id.nationExplain)     TextView nationExplain;
    @BindView(R.id.idNoExplain)       TextView idNoExplain;
    @BindView(R.id.birthdayExplain)   TextView birthdayExplain;
    @BindView(R.id.addressExplain)    TextView addressExplain;
    @BindView(R.id.idFrontImageView)  ImageView idFrontImageView;
    @BindView(R.id.idHeaderImageView) ImageView mCircleImageView;

    private String imgPath;

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
        HashMap hashMap = null;
        if(bundle != null){
            hashMap = (HashMap) bundle.getSerializable("data");
        }
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

    @Override
    protected void initData() {

    }

    @OnClick({R.id.next})
    public void onClick(View view){
        switch(view.getId()){
            case R.id.next:{
                if(checkSoFile(LIBRARIES)){
                    Intent intent = new Intent(IdCardInfoActivity.this,FacePreViewActivity.class);
                    intent.putExtra("headPath",imgPath);
                    startActivity(intent);
                }else{
                    KLog.i("can't find face so file");
                }
                break;
            }
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
}
