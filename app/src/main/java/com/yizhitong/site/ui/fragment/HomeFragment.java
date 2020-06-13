package com.yizhitong.site.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.msd.ocr.idcard.LibraryInitOCR;
import com.yizhitong.site.R;
import com.yizhitong.site.ui.base.fragment.BaseFragment;
import com.yizhitong.site.utils.CalendarUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeFragment extends BaseFragment {

    @BindView(R.id.weekTv) TextView currentWeek;
    @BindView(R.id.dateTv) TextView currentDate;

    @Override
    protected int getContentView() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View view) {
        currentWeek.setText(CalendarUtil.transformDate(0));
        currentDate.setText(CalendarUtil.transformDate(1));
    }

    @Override
    public void onStart() {
        super.onStart();
        LibraryInitOCR.initOCR(getActivity());
    }

    @OnClick({R.id.real_name_registration})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.real_name_registration:
                registerPersonInfo();
                break;
        }
    }

    private void registerPersonInfo(){
        Bundle bundle = new Bundle();
        bundle.putBoolean("saveImage", true);  // 是否保存识别图片
        bundle.putBoolean("showSelect", true); // 是否显示选择图片
        bundle.putBoolean("showCamera", true); // 显示图片界面是否显示拍照(驾照选择图片识别率比扫描高)
        bundle.putInt("requestCode", 1);       // requestCode
        bundle.putInt("type", 0);              // 0身份证, 1驾驶证

        LibraryInitOCR.startScan(getActivity(), bundle);
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
}