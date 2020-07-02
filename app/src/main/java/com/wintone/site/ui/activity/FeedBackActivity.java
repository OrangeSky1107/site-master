package com.wintone.site.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.wintone.site.R;
import com.wintone.site.network.NetService;
import com.wintone.site.network.NetWorkUtils;
import com.wintone.site.network.OkHttpUtil;
import com.wintone.site.network.OkhttpClientRequest;
import com.wintone.site.networkmodel.FeedModelRequetModel;
import com.wintone.site.utils.Constant;
import com.wintone.site.utils.CustomImageUtils;
import com.wintone.site.utils.SPUtils;
import com.wintone.site.widget.PopWindowLayout;

import org.devio.takephoto.app.TakePhotoActivity;
import org.devio.takephoto.model.TResult;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class FeedBackActivity extends TakePhotoActivity implements PopWindowLayout.OpenWindowListener, PopupWindow.OnDismissListener {

    @BindView(R.id.toolbar_title)  TextView toolbar_title;
    @BindView(R.id.submit)         Button submit;
    @BindView(R.id.feedBackLayout) LinearLayout feedBackLayout;
    @BindView(R.id.feedbackImage)  ImageView feedbackImage;
    @BindView(R.id.feedbackText)   EditText feedbackText;
    @BindView(R.id.phone)          EditText phone;
    @BindView(R.id.email)          EditText email;

    private KProgressHUD mHUD;
    private PopWindowLayout mPopWindowLayout;

    private String imgPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ButterKnife.bind(this);
        initView();
    }

    protected void initView() {
        toolbar_title.setText("用户反馈");
        initProgress();
    }

    private void initProgress() {
        mHUD = KProgressHUD.create(FeedBackActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDetailsLabel("反馈中...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    @OnClick({R.id.iv_back,R.id.feedbackImage,R.id.submit})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.feedbackImage:
                popLayout();
                break;
            case R.id.submit:
                mHUD.show();
                submitFeedback();
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mPopWindowLayout != null && mPopWindowLayout.isShowing()) {
            mPopWindowLayout.dismiss();
            mPopWindowLayout = null;
            setWindowAttributes(1f);
        }
        return super.onTouchEvent(event);
    }

    private void popLayout(){
        if(mPopWindowLayout == null){
            mPopWindowLayout = new PopWindowLayout(this);
            mPopWindowLayout.setOpenWindowListener(this);
            mPopWindowLayout.setOnDismissListener(this);
            setWindowAttributes(0.5f);
            mPopWindowLayout.showAtLocation(feedBackLayout,
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }else{
            setWindowAttributes(0.5f);
            mPopWindowLayout.showAtLocation(feedBackLayout,
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    private void setWindowAttributes(float color){
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = color;
        getWindow().setAttributes(lp);
    }

    private void submitFeedback(){
        String feedback = feedbackText.getText().toString();

        if(TextUtils.isEmpty(feedback)){
           ToastUtils.showShort("提交的问题还没有输入!");
           return;
        }

        String token = (String) SPUtils.getShare(this,Constant.USER_TOKEN,"");

        OkHttpUtil.getInstance().uploadTopPost(Constant.USER_UPLOAD_URL, token, imgPath, new OkhttpClientRequest() {
            @Override
            public void responseFailure(String errorMessage) {
                ToastUtils.showShort("问题图片上传失败:"+errorMessage);
                mHUD.dismiss();
            }

            @Override
            public void responseSuccess(String successMessage) {
                HashMap hashMap = JSON.parseObject(successMessage,HashMap.class);
                String imagePath = hashMap.get("result").toString();
                submitProblem(token,imagePath);
            }
        });
    }

    private void submitProblem(String token,String imgPath){
        FeedModelRequetModel feedModelRequetModel = new FeedModelRequetModel();
        feedModelRequetModel.setPhone(phone.getText().toString());
        feedModelRequetModel.setEmail(email.getText().toString());
        feedModelRequetModel.setUserId((String)SPUtils.getShare(this,Constant.USER_ID,""));
        feedModelRequetModel.setFilePath(imgPath);
        feedModelRequetModel.setProblem(feedbackText.getText().toString());

        NetWorkUtils.getInstance().createService(NetService.class)
                .postFeedbackInfo(Constant.FEEDBACK_URL,token,feedModelRequetModel)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override public void onSubscribe(Disposable d) {}
                    @Override
                    public void onNext(ResponseBody value) {
                        mHUD.dismiss();
                        ToastUtils.showShort("问题反馈成功!");
                        finish();
                    }
                    @Override
                    public void onError(Throwable e) {
                        mHUD.dismiss();
                    }
                    @Override public void onComplete() { }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPopWindowLayout != null){
            mPopWindowLayout.dismiss();
            mPopWindowLayout = null;
        }
    }

    @Override
    public void fromLocalImage() {
        CustomImageUtils.selectLocalImage(getTakePhoto());
    }

    @Override
    public void fromPhotoImage() {
        CustomImageUtils.selectPickerPhotoImage(getTakePhoto());
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        setWindowAttributes(1f);
        String imgPath = result.getImage().getCompressPath();
        Glide.with(this).load(imgPath).into(feedbackImage);
        this.imgPath = imgPath;
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
        setWindowAttributes(1f);
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        setWindowAttributes(1f);
        ToastUtils.showShort("图片选择失败:"+msg);
    }

    @Override
    public void onDismiss() {
        setWindowAttributes(1f);
    }
}
