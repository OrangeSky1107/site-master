package com.wintone.site.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ActivityUtils;
import com.bumptech.glide.Glide;
import com.socks.library.KLog;
import com.wintone.site.R;
import com.wintone.site.network.NetService;
import com.wintone.site.network.NetWorkUtils;
import com.wintone.site.ui.base.activity.BaseActivity;
import com.wintone.site.utils.bankinfo.BankInfo;
import com.wintone.site.utils.bankinfo.BankModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BankInfoActivity extends BaseActivity {

    @BindView(R.id.officeTextView)     TextView officeTextView;
    @BindView(R.id.nameTextView)       TextView nameTextView;
    @BindView(R.id.idFrontImageView)   ImageView idFrontImageView;
    @BindView(R.id.openCamera)         TextView openCamera;
    @BindView(R.id.toolbar_title)      TextView toolbarTitle;
    @BindView(R.id.toolbar_right)      ImageView toolbarRight;
    @BindView(R.id.nextOperation)      Button nextOperation;

    private HashMap bankInfoMap;

    private HashMap dataMap = null;

    private static final String[] LIBRARIES = new String[]{
            "libAndroidBankCard.so"
    };

    @Override
    protected int getContentView() {
        return R.layout.activity_bank_info;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        if(bundle != null){
            dataMap = (HashMap) bundle.getSerializable("data");

            Log.i("IdCardBackInfoActivity","look at map data = " + JSON.toJSONString(dataMap));
        }

        toolbarTitle.setText("银行卡信息");

        toolbarRight.setVisibility(View.VISIBLE);
        toolbarRight.setImageResource(R.drawable.reset_photo);
    }

    @Override
    protected void initData() {
        if(bankInfoMap != null) return;
        bankInfoMap = BankInfo.getInstance().transformMap();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bankInfoMap.clear();
        bankInfoMap = null;
    }

    @OnClick({R.id.openCamera,R.id.nextOperation,R.id.iv_back,R.id.toolbar_right})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.openCamera:
                openBankCamera();
                break;
            case R.id.nextOperation:
                Intent intent = new Intent(BankInfoActivity.this,PersonInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("data",dataMap);
                intent.putExtra("bundle",bundle);
                ActivityUtils.startActivity(intent);
                finish();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.toolbar_right:
                openBankCamera();
                break;
        }
    }

    public void openBankCamera(){
        if(checkSoFile(LIBRARIES)){
            Intent intent = new Intent(BankInfoActivity.this,ScanCamera.class);
            startActivityForResult(intent,1);
        }else{
            KLog.i("can't find bankcard so file");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            int[] picR = data.getIntArrayExtra("PicR");
            char[] StringR = data.getCharArrayExtra("StringR");
            String bankImg = data.getStringExtra("imagePath");

            officeTextView.setText(String.valueOf(StringR));
            Glide.with(this).load(bankImg).into(idFrontImageView);
            openCamera.setVisibility(View.GONE);

            String bankCard = String.valueOf(StringR).replaceAll(" ","");
            String disposeBank = disposeChars(bankCard);
            getBankNetworkInfo(disposeBank);

            dataMap.put("bankCard",disposeBank);
            dataMap.put("bankImg",bankImg);
        }
    }

    private String disposeChars(String value){
        char[] chars = value.toCharArray();
        StringBuffer buffer = new StringBuffer();
        try{
            for(int i=0;i<chars.length;i++){
                int currentNumber = Integer.valueOf(String.valueOf(chars[i]));
                if(currentNumber < 10){
                    buffer.append(currentNumber);
                    Log.i("BankInfoActivity","current chars true = " +chars[i] );
                }else{
                    Log.i("BankInfoActivity","current chars false = " +chars[i] );
                }
            }
        }catch (Exception e){
            Log.i("BankInfoActivity","error message = " +e.getMessage().toString() );
            e.printStackTrace();
        }
        return buffer.toString();
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

    private void getBankNetworkInfo(String bankCard){
        String url = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo=";
        url+=bankCard;
        url+="&cardBinCheck=true";
        Log.i("BankInfoActivity","look at url = " + url);
        NetWorkUtils.getInstance().createService(NetService.class)
                .getBankInfoOfAilPay(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BankModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BankModel value) {
                        dispose(value.getBank());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("BankInfoActivity","look at error message = " + e.getMessage().toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void dispose(String bankIdentification){
        String bankName = bankInfoMap.get(bankIdentification).toString();
        nameTextView.setText(bankName);
        dataMap.put("bankName",bankName);
        nextOperation.setEnabled(true);
    }
}
