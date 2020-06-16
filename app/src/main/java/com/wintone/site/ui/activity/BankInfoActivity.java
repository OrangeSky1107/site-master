package com.wintone.site.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.socks.library.KLog;
import com.wintone.site.R;
import com.wintone.site.network.NetService;
import com.wintone.site.network.NetWorkUtils;
import com.wintone.site.ui.base.activity.BaseActivity;
import com.wintone.site.utils.bankinfo.BankInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class BankInfoActivity extends BaseActivity {

    @BindView(R.id.officeTextView) TextView officeTextView;
    @BindView(R.id.nameTextView)   TextView nameTextView;
    @BindView(R.id.idFrontImageView)ImageView idFrontImageView;
    @BindView(R.id.openCamera)      TextView openCamera;

    private static final String[] LIBRARIES = new String[]{
            "libAndroidBankCard.so"
    };

    @Override
    protected int getContentView() {
        return R.layout.activity_bank_info;
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
                if(checkSoFile(LIBRARIES)){
                   Intent intent = new Intent(BankInfoActivity.this,ScanCamera.class);
                   startActivityForResult(intent,1);
                }else{
                    KLog.i("can't find bankcard so file");
                }
                break;
            case R.id.nextOperation:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("BankInfoActivity","onActivityResult");
        if(requestCode == 1 && resultCode == RESULT_OK){
            int[] picR = data.getIntArrayExtra("PicR");
            char[] StringR = data.getCharArrayExtra("StringR");
            officeTextView.setText(String.valueOf(StringR).replaceAll(" ",""));
            Bitmap bitmap = Bitmap.createBitmap(picR, 400, 80, Bitmap.Config.RGB_565);
            idFrontImageView.setImageBitmap(bitmap);
            openCamera.setVisibility(View.GONE);
            setBankInfo(String.valueOf(StringR).replaceAll(" ",""));
            getBankNetworkInfo(String.valueOf(StringR).replaceAll(" ",""));
        }
    }

    private void setBankInfo(String str){
        String disposeChar = str.substring(0,5);
        Log.i("BankInfoActivity","look at dispose char = " + disposeChar);
        char[] chars = str.toCharArray();
        String name = BankInfo.getNameOfBank(chars, 0);
        nameTextView.setText(name);
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
        char[] chars = bankCard.toCharArray();
        StringBuffer buffer = new StringBuffer();
        for(int i=0;i<chars.length;i++){
            if(isChinese(chars[i])){

            }else{
                buffer.append(chars[i]);
                Log.i("BankInfoActivity","look at chars = "+ i +"="+ chars[i]);
            }
        }
        Log.i("BankInfoActivity","look chars = " + buffer);

        Log.i("BankInfoActivity","look at chars = " + bankCard);
        String url = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo=";
        url+=bankCard;
        url+="&cardBinCheck=true";
        Log.i("BankInfoActivity","look at url = " + url);
        NetWorkUtils.getInstance().createService(NetService.class)
                .getBankInfoOfAilPay(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody value) {
                        try {
                            Log.i("BankInfoActivity","look at response body = " + value.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }
}
