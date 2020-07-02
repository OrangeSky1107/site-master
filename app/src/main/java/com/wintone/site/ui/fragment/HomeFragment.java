package com.wintone.site.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.msd.ocr.idcard.LibraryInitOCR;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.socks.library.KLog;
import com.wintone.site.R;
import com.wintone.site.SiteApplication;
import com.wintone.site.network.NetService;
import com.wintone.site.network.NetWorkUtils;
import com.wintone.site.networkmodel.HomePagerModel;
import com.wintone.site.permissions.EasyPermission;
import com.wintone.site.ui.activity.FaceActionActivity;
import com.wintone.site.ui.activity.PersonDetailsActivity;
import com.wintone.site.ui.base.fragment.BaseFragment;
import com.wintone.site.utils.CalendarUtil;
import com.wintone.site.utils.Constant;
import com.wintone.site.utils.SPUtils;
import com.wintone.site.utils.UiUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.OptionPicker;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends BaseFragment implements EasyPermission.PermissionCallback{

    @BindView(R.id.weekTv)                  TextView currentWeek;
    @BindView(R.id.dateTv)                  TextView currentDate;
    @BindView(R.id.workerAttencePercentage) TextView workerAttencePercentage;
    @BindView(R.id.workerTotalNum)          TextView workerTotalNum;
    @BindView(R.id.workerPresentNum)        TextView workerPresentNum;
    @BindView(R.id.todayAttenceWorkerNum)   TextView todayAttenceWorkerNum;
    @BindView(R.id.managerPercentage)       TextView managerPercentage;
    @BindView(R.id.totalManager)            TextView totalManager;
    @BindView(R.id.managerPresent)          TextView managerPresent;
    @BindView(R.id.todayAttenceManagerNum)  TextView todayAttenceManagerNum;
    @BindView(R.id.refreshLayout)           SmartRefreshLayout mSmartRefreshLayout;

    private KProgressHUD mHUD;

    private int selectorType = -1;

    @Override
    protected int getContentView() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View view) {
        currentWeek.setText(CalendarUtil.transformDate(0));
        currentDate.setText(CalendarUtil.transformDate(1));
        initProgress();

        initListener();
    }

    private void initProgress() {
        mHUD = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDetailsLabel("加载中...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    private void initListener(){
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadData();
                refreshLayout.finishRefresh(1500);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();
    }

    @OnClick({R.id.real_name_registration,R.id.personnel_info,R.id.face_attendance})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.real_name_registration:
                selectorType = 2;
                checkRegisterPermissions();
                break;
            case R.id.personnel_info:
                ActivityUtils.startActivity(new Intent(getActivity(), PersonDetailsActivity.class));
                break;
            case R.id.face_attendance:
                selectorType = 1;
                checkFacePermissions();
                break;
        }
    }

    private void attendanceWorkers(){
        Integer userType = (Integer) SPUtils.getShare(getActivity(),Constant.USER_TYPE,5);
        if(userType == 0 || userType == 1 || userType == 3){
            ToastUtils.showShort("该账号权限过高,不支持实名登记!");
            return;
        }
        registerPersonInfo();
    }

    private void faceAttendanceFunction(){
        Integer userType = (Integer) SPUtils.getShare(getActivity(),Constant.USER_TYPE,5);
        if(userType == 0 || userType == 1){
            ToastUtils.showShort("该账号权限过高,不支持考勤!");
            return;
        }

        UiUtils.showOptionPicker(getActivity(), getResources().getStringArray(R.array.attend_options), 0, new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                Intent intent = new Intent(getActivity(), FaceActionActivity.class);
                if (index == 0) {
                    intent.putExtra("commute", "in");
                } else {
                    intent.putExtra("commute", "out");
                }
                startActivity(intent);
            }
        });
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
    public void onPause() {
        super.onPause();
        LibraryInitOCR.closeDecode();
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
        mHUD.show();
        Map<String,String> stringMap = new HashMap<>();
        stringMap.put("loginName", (String) SPUtils.getShare(getActivity(),Constant.USER_NAME,""));
        stringMap.put("projectsId",(String)SPUtils.getShare(getActivity(),Constant.PROJECT_ID,""));
        String token = (String)SPUtils.getShare(getActivity(),Constant.USER_TOKEN,"");
        NetWorkUtils.getInstance().createService(NetService.class)
                .postHomePager(token,Constant.HOME_PAGER_URL,stringMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HomePagerModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HomePagerModel value) {
                        KLog.i("look at response body message = " + JSON.toJSONString(value));
                        fillView(value);
                        mHUD.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mHUD.dismiss();
                        KLog.i("look at error message = " + e.getMessage().toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void fillView(HomePagerModel homePagerModel){
        if(homePagerModel.getResult().getLaborSituation() <= 0){
            workerAttencePercentage.setText("0%");
        }else{
            double attence = homePagerModel.getResult().getLaborSituation() * 100;
            workerAttencePercentage.setText(attence + "%");
        }

        workerTotalNum.setText(homePagerModel.getResult().getLaborCount()+"");

        workerPresentNum.setText(homePagerModel.getResult().getBePresentLaborCount()+"");

        todayAttenceWorkerNum.setText(homePagerModel.getResult().getAttendanceLaborCount()+"");

        if(homePagerModel.getResult().getAdminSituation() <= 0){
            managerPercentage.setText("0%");
        }else{
            double attence = homePagerModel.getResult().getAdminSituation() * 100;
            managerPercentage.setText(attence + "%");
        }

        totalManager.setText(homePagerModel.getResult().getAdminIstrationCount()+"");

        managerPresent.setText(homePagerModel.getResult().getBePresentAdminIstrationCount()+"");

        todayAttenceManagerNum.setText(homePagerModel.getResult().getAttendanceAdminCount()+"");
    }

    private void checkFacePermissions(){
        requestPermission();
    }

    private void checkRegisterPermissions(){
        requestPermission();
    }

    private String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
    };

    private final int REQUEST_PERMISSIONS = 2;

    private void requestPermission(){
        if(EasyPermission.hasPermissions(getActivity(), permissions)){
            if(selectorType == 1){
                faceAttendanceFunction();
            }else{
                LibraryInitOCR.initOCR(getActivity());
                attendanceWorkers();
            }
        }else{
            EasyPermission.with(this)
                    .rationale(getString(com.msd.ocr.idcard.R.string.rationale_camera))
                    .addRequestCode(REQUEST_PERMISSIONS)
                    .permissions(permissions)
                    .request();
        }
    }

    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        SiteApplication.getInstance().activeEngine();
        if(selectorType == 1){
            faceAttendanceFunction();
        }else{
            LibraryInitOCR.initOCR(getActivity());
            attendanceWorkers();
        }
    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        ToastUtils.showShort("不打开权限,部分功能将使用不了!");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermission.onRequestPermissionsResult(this,REQUEST_PERMISSIONS,permissions,grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EasyPermission.SETTINGS_REQ_CODE){
            if(EasyPermission.hasPermissions(getActivity(), permissions)){
                SiteApplication.getInstance().activeEngine();
                if(selectorType == 1){
                    faceAttendanceFunction();
                }else{
                    LibraryInitOCR.initOCR(getActivity());
                    attendanceWorkers();
                }
            }else{
                EasyPermission.with(this)
                        .rationale(getString(com.msd.ocr.idcard.R.string.rationale_camera))
                        .addRequestCode(REQUEST_PERMISSIONS)
                        .permissions(permissions)
                        .request();
            }
        }
    }
}