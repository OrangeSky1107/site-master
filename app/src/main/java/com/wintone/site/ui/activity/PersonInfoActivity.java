package com.wintone.site.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ToastUtils;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.kyleduo.switchbutton.SwitchButton;
import com.socks.library.KLog;
import com.wintone.site.R;
import com.wintone.site.network.NetService;
import com.wintone.site.network.NetWorkUtils;
import com.wintone.site.network.OkHttpUtil;
import com.wintone.site.network.OkhttpClientRequest;
import com.wintone.site.networkmodel.ConstructionModel;
import com.wintone.site.networkmodel.DictionariesModel;
import com.wintone.site.networkmodel.ProjectModel;
import com.wintone.site.networkmodel.ProjectWorkers;
import com.wintone.site.networkmodel.RegisterInfoModel;
import com.wintone.site.networkmodel.TeamModel;
import com.wintone.site.ui.base.activity.BaseActivity;
import com.wintone.site.utils.Constant;
import com.wintone.site.utils.SPUtils;
import com.wintone.site.utils.UiUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.OptionPicker;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PersonInfoActivity extends BaseActivity {

    private HashMap dataMap = null;

    @BindView(R.id.projectTextView) TextView projectTextView;
    @BindView(R.id.companyTextView) TextView companyTextView;
    @BindView(R.id.teamTextView)    TextView teamTextView;
    @BindView(R.id.workTypeTextView)TextView workTypeTextView;
    @BindView(R.id.entranceDateTextView) TextView entranceDateTextView;
    @BindView(R.id.phoneTextView)   EditText phoneTextView;
    @BindView(R.id.leaderSwitch)    SwitchButton leaderSwitch;

    private KProgressHUD mHUD;

    private ProjectModel mProjectModel = null;
    private ConstructionModel mConstructionModel = null;
    private TeamModel mTeamModel = null;
    private DictionariesModel mDictionariesModel = null;

    private int projectIndex;
    private int constructionIndex;
    private int teamIndex;
    private int hotDicIndex;

    private int leaderFlag;

    private String dateTime;

    @Override
    protected int getContentView() {
        return R.layout.activity_person_info;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        if(bundle != null){
            dataMap = (HashMap) bundle.getSerializable("data");

            Log.i("IdCardBackInfoActivity","look at map data = " + JSON.toJSONString(dataMap));
        }

        initProgress();
    }

    @Override
    protected void initData() {
        phoneTextView.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @OnClick({R.id.nextButton,R.id.projectTextView,R.id.companyTextView,R.id.teamTextView,R.id.workTypeTextView,R.id.entranceDateTextView,R.id.leaderSwitch})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.nextButton:
                mHUD.show();
                checkRegex();
                break;
            case R.id.projectTextView:
                mHUD.show();
                pullProjectList();
                break;
            case R.id.companyTextView:
                mHUD.show();
                pullCompanyStore();
                break;
            case R.id.teamTextView:
                mHUD.show();
                pullTeamList();
                break;
            case R.id.workTypeTextView:
                mHUD.show();
                pullHotDicList();
                break;
            case R.id.entranceDateTextView:
                openDatePicker();
                break;
            case R.id.leaderSwitch:
                if(leaderSwitch.isChecked()){
                    leaderFlag = 1;
                    Log.i("PersonInfoActivity","look at leader flag =  " + leaderFlag);
                }else{
                    leaderFlag = 0;
                    Log.i("PersonInfoActivity","look at leader flag =  " + leaderFlag);
                }
                break;
        }
    }

    private void pullProjectList(){
        String loginName = (String) SPUtils.getShare(PersonInfoActivity.this, Constant.USER_NAME,"");
        String token = (String)SPUtils.getShare(PersonInfoActivity.this,Constant.USER_TOKEN,"");

        Map map = new HashMap();
        map.put("loginName",loginName);
        map.put("page",1);
        map.put("rows",100000);

        NetWorkUtils.getInstance().createService(NetService.class)
                .postProjectList(token,Constant.PROJECT_LIST_URL,map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ProjectModel>() {
                    @Override public void onSubscribe(Disposable d) {}
                    @Override
                    public void onNext(ProjectModel value) {
                        mProjectModel = value;
                        fillProject(value.getResult().getRecords());
                        mHUD.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("PersonInfoActivity","look at error message = " + e.getMessage().toString());
                        mHUD.dismiss();
                    }
                    @Override public void onComplete() {}
                });
    }

    private void fillProject(List<ProjectModel.ResultBean.RecordsBean> recordsBeans){
        List<String> projectName = new ArrayList<>();

        for (int i = 0; i < recordsBeans.size();i++){
            String title = recordsBeans.get(i).getProjectName();
            projectName.add(title);
        }

        UiUtils.showOptionInfoPicker(this, projectName, 0, new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                projectIndex = index;
                projectTextView.setText(item);
            }
        });
    }

    private void pullCompanyStore(){
        if(mProjectModel == null){
            ToastUtils.showShort("请先选择项目!");
            mHUD.dismiss();
            return;
        }
        String token = (String)SPUtils.getShare(PersonInfoActivity.this,Constant.USER_TOKEN,"");
        String projecstId =  mProjectModel.getResult().getRecords().get(projectIndex).getId();

        Map map = new HashMap();
        map.put("projectsId",projecstId);
        map.put("page",1);
        map.put("rows",100000);

        NetWorkUtils.getInstance().createService(NetService.class)
                .postConstructionList(token,Constant.CONSTRUCTION_LIST_URL,map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ConstructionModel>() {
                    @Override public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(ConstructionModel value) {
                        mConstructionModel = value;
                        fillCompanyStore(value.getResult().getRecords());
                        mHUD.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("PersonInfoActivity","look at message error = " + e.getMessage().toString());
                        mHUD.dismiss();
                    }

                    @Override public void onComplete() {}
                });
    }

    private void fillCompanyStore(List<ConstructionModel.ResultBean.RecordsBean> recordsBeans){
        List<String> companyName = new ArrayList<>();

        for (int i = 0; i < recordsBeans.size();i++){
            String title = recordsBeans.get(i).getConstructionName();
            companyName.add(title);
        }

        UiUtils.showOptionInfoPicker(this, companyName, 0, new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                constructionIndex = index;
                companyTextView.setText(item);
            }
        });
    }

    private void pullTeamList(){
        if(mConstructionModel == null){
            ToastUtils.showShort("请先选择分包商!");
            mHUD.dismiss();
            return;
        }
        String token = (String)SPUtils.getShare(PersonInfoActivity.this,Constant.USER_TOKEN,"");
        String constructionId =  mConstructionModel.getResult().getRecords().get(constructionIndex).getId();

        Map map = new HashMap();
        map.put("constructionId",constructionId);
        map.put("page",1);
        map.put("rows",100000);

        NetWorkUtils.getInstance().createService(NetService.class)
                .postTeamList(token,Constant.TEAM_LIST_URL,map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TeamModel>() {
                    @Override public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(TeamModel value) {
                        mTeamModel = value;
                        fillTeamList(mTeamModel.getResult().getRecords());
                        mHUD.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("PersonInfoActivity","look at message team error = " + e.getMessage().toString());
                        mHUD.dismiss();
                    }

                    @Override public void onComplete() {}
                });
    }

    private void fillTeamList(List<TeamModel.ResultBean.RecordsBean> recordsBeans){
        List<String> teamName = new ArrayList<>();

        for (int i = 0; i < recordsBeans.size();i++){
            String title = recordsBeans.get(i).getTeamName();
            teamName.add(title);
        }

        UiUtils.showOptionInfoPicker(this, teamName, 0, new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                teamIndex = index;
                teamTextView.setText(item);
            }
        });
    }

    private void pullHotDicList(){
        String token = (String)SPUtils.getShare(PersonInfoActivity.this,Constant.USER_TOKEN,"");

        Map map = new HashMap();
        map.put("page",1);
        map.put("rows",100000);

        NetWorkUtils.getInstance().createService(NetService.class)
                .postHotDicList(token,Constant.DICTIONARIES_HOTDIC_URL,map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DictionariesModel>() {
                    @Override public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(DictionariesModel value) {
                        KLog.i("dic list = " + JSON.toJSONString(value));
                        mDictionariesModel = value;
                        fillHotDicList(value.getResult().getRecords());
                        mHUD.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("PersonInfoActivity","look at hot dic message error = " + e.getMessage().toString());
                        mHUD.dismiss();
                    }

                    @Override public void onComplete() {}
                });
    }

    private void fillHotDicList(List<DictionariesModel.ResultBean.RecordsBean> recordsBeans){
        List<String> teamName = new ArrayList<>();

        for (int i = 0; i < recordsBeans.size();i++){
            String title = recordsBeans.get(i).getTitle();
            teamName.add(title);
        }

        UiUtils.showOptionInfoPicker(this, teamName, 0, new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                hotDicIndex = index;
                workTypeTextView.setText(item);
            }
        });
    }

    private void openDatePicker(){
        UiUtils.showYearMonthDayPicker(this, "", new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                dateTime = year + "-" + month + "-" + day;
                entranceDateTextView.setText(dateTime);
            }
        });
    }

    private void initProgress() {
        mHUD = KProgressHUD.create(PersonInfoActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDetailsLabel("加载中...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }


    private void checkRegex(){
        String projectName = projectTextView.getText().toString();
        if(projectName.equals("请选择所选项目")){
            ToastUtils.showShort("项目必须选择!");
            mHUD.dismiss();
            return;
        }

        String workTypeName = workTypeTextView.getText().toString();
        if(workTypeName.equals("请选择工种")){
            ToastUtils.showShort("工种不能为空!");
            mHUD.dismiss();
            return;
        }

        String entranceDate = entranceDateTextView.getText().toString();
        if(entranceDate.equals("请选择进场日期")){
            ToastUtils.showShort("进场时间不能为空!");
            mHUD.dismiss();
            return;
        }

        String phone = phoneTextView.getText().toString();
        if(phone.length() < 11 && TextUtils.isEmpty(phone)){
            ToastUtils.showShort("手机格式不对!");
            mHUD.dismiss();
            return;
        }

        uploadIdCardImage();
    }

    private void uploadIdCardImage(){
        String token = (String)SPUtils.getShare(PersonInfoActivity.this,Constant.USER_TOKEN,"");
        String imgPath = dataMap.get("imgPath").toString();
        OkHttpUtil.getInstance().uploadTopPost(Constant.USER_UPLOAD_URL,token, imgPath, new OkhttpClientRequest() {
            @Override
            public void responseFailure(String errorMessage) {
                Log.i("PersonInfoActivity","look at image loader error = " + errorMessage);
                ToastUtils.showShort("身份证上传失败!");
                mHUD.dismiss();
            }

            @Override
            public void responseSuccess(String successMessage) {
                Log.i("PersonInfoActivity","look at uploadIdCardImage loader success = " + successMessage);
                HashMap hashMap = JSON.parseObject(successMessage,HashMap.class);
                String cloudIdCardPath = hashMap.get("result").toString();
                uploadHeadPath(cloudIdCardPath);
            }
        });
    }

    private void uploadHeadPath(String cloudIdCardPath){
        String token = (String)SPUtils.getShare(PersonInfoActivity.this,Constant.USER_TOKEN,"");
        String headPath = dataMap.get("headPath").toString();
        OkHttpUtil.getInstance().uploadTopPost(Constant.USER_UPLOAD_URL,token, headPath, new OkhttpClientRequest() {
            @Override
            public void responseFailure(String errorMessage) {
                Log.i("PersonInfoActivity","look at image loader error = " + errorMessage);
                ToastUtils.showShort("注册人脸图上传失败!");
                mHUD.dismiss();
            }

            @Override
            public void responseSuccess(String successMessage) {
                Log.i("PersonInfoActivity","look at uploadHeadPath loader success = " + successMessage);
                HashMap hashMap = JSON.parseObject(successMessage,HashMap.class);
                String cloudHeadPath = hashMap.get("result").toString();
                uploadIdCardBackImage(cloudIdCardPath,cloudHeadPath);
            }
        });
    }

    private void uploadIdCardBackImage(String cloudIdcardPath,String cloudheadPath){
        String token = (String)SPUtils.getShare(PersonInfoActivity.this,Constant.USER_TOKEN,"");
        String backIdCard = dataMap.get("backImagePath").toString();
        OkHttpUtil.getInstance().uploadTopPost(Constant.USER_UPLOAD_URL,token, backIdCard, new OkhttpClientRequest() {
            @Override
            public void responseFailure(String errorMessage) {
                Log.i("PersonInfoActivity","look at image loader error = " + errorMessage);
                ToastUtils.showShort("身份证背面图上传失败!");
                mHUD.dismiss();
            }

            @Override
            public void responseSuccess(String successMessage) {
                Log.i("PersonInfoActivity","look at uploadIdCardBackImage loader success = " + successMessage);
                HashMap hashMap = JSON.parseObject(successMessage,HashMap.class);
                String cloudIdCardBackPath = hashMap.get("result").toString();
                uploadBankImage(cloudIdcardPath,cloudheadPath,cloudIdCardBackPath);
            }
        });
    }

    private void uploadBankImage(String cloudIdCardPath,String cloudHeadPath,String cloudIdCardBackPath){
        String bankImagePath = dataMap.get("bankImg").toString();
        if(null != bankImagePath && bankImagePath.length() > 25){
            String token = (String)SPUtils.getShare(PersonInfoActivity.this,Constant.USER_TOKEN,"");
            OkHttpUtil.getInstance().uploadTopPost(Constant.USER_UPLOAD_URL,token, bankImagePath, new OkhttpClientRequest() {
                @Override
                public void responseFailure(String errorMessage) {
                    Log.i("PersonInfoActivity","look at image loader error = " + errorMessage);
                    ToastUtils.showShort("身份证背面图上传失败!");
                    mHUD.dismiss();
                }

                @Override
                public void responseSuccess(String successMessage) {
                    Log.i("PersonInfoActivity","look at uploadBankImage loader success = " + successMessage);
                    HashMap hashMap = JSON.parseObject(successMessage,HashMap.class);
                    String cloudBankPath = hashMap.get("result").toString();
                    submit(cloudIdCardPath,cloudHeadPath,cloudIdCardBackPath,cloudBankPath);
                }
            });
        }else{
            submit(cloudIdCardPath,cloudHeadPath,cloudIdCardBackPath,"");
        }
    }

    private void submit(String cloudIdCardPath,String cloudheadPath,String cloudIdCardBackPath,String cloudBankPath){
        ProjectWorkers projectWorkers = new ProjectWorkers();
        String projectName = projectTextView.getText().toString();

        String constructionName = companyTextView.getText().toString();
        if(!constructionName.equals("请选择所属分包商")){
            projectWorkers.setConstructionName(constructionName);
            projectWorkers.setConstructionId(mConstructionModel.getResult().getRecords().get(constructionIndex).getId());
        }

        String teamName = teamTextView.getText().toString();
        if(!teamName.equals("请选择班组")){
            projectWorkers.setTeamName(teamName);
            projectWorkers.setTeamId(mTeamModel.getResult().getRecords().get(teamIndex).getId());
        }

        String workTypeName = workTypeTextView.getText().toString();
        projectWorkers.setJobName(workTypeName);

        String entranceDate = entranceDateTextView.getText().toString();
        String phone = phoneTextView.getText().toString();

        projectWorkers.setEmpPhon(phone);
        projectWorkers.setProjectId(mProjectModel.getResult().getRecords().get(projectIndex).getId());
        projectWorkers.setProjectName(projectName);

        if(leaderFlag > 0){
            projectWorkers.setIsTeam(1);
        }else{
            projectWorkers.setIsTeam(0);
        }

        projectWorkers.setJobName(mDictionariesModel.getResult().getRecords().get(hotDicIndex).getTag());
        projectWorkers.setEmpName(dataMap.get("name").toString());
        projectWorkers.setIdCode(dataMap.get("num").toString());
        projectWorkers.setEmpSex(dataMap.get("sex").toString());
        projectWorkers.setEmpNation(dataMap.get("folk").toString());
        projectWorkers.setDateOfBirth(dataMap.get("birt").toString());
        projectWorkers.setIdAddress(dataMap.get("addr").toString());
        projectWorkers.setIdAgency(dataMap.get("issue").toString());
        projectWorkers.setIdValiddate(dataMap.get("valid").toString());


        if(dataMap.get("bankCard").toString().length() > 10){
            projectWorkers.setEmpCardnum(dataMap.get("bankCard").toString());
            projectWorkers.setEmpBankname(dataMap.get("bankName").toString());
            projectWorkers.setBankCardUrl(cloudBankPath);
        }

        projectWorkers.setStartTime(entranceDate);
        projectWorkers.setFaceUrl(cloudheadPath);
        projectWorkers.setIdphotoScan(cloudIdCardPath);
        projectWorkers.setIdphotoScan2(cloudIdCardBackPath);

        String token = (String)SPUtils.getShare(PersonInfoActivity.this,Constant.USER_TOKEN,"");

        String data = JSON.toJSONString(projectWorkers);

        Log.i("PersonInfoActivity","look at message data = " + data);

        NetWorkUtils.getInstance().createService(NetService.class)
                .postWorkersSaveOrUpdate(token,Constant.WORKERS_SAVEORUPDATE_URL,projectWorkers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RegisterInfoModel>() {
                    @Override public void onSubscribe(Disposable d) { }

                    @Override
                    public void onNext(RegisterInfoModel value) {
                        disposeRegister(value);
                        SPUtils.putShare(PersonInfoActivity.this,Constant.FACE_URL,projectWorkers.getFaceUrl());
                        mHUD.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("PersonInfoActivity","look at response error data = " + e.getMessage().toString());
                        mHUD.dismiss();
                    }

                    @Override public void onComplete() { }
                });
    }

    private void disposeRegister(RegisterInfoModel value){
        if(value.getCode() == 1000){
            ToastUtils.showShort("注册成功!");
            finish();
        }else{
            ToastUtils.showShort("注册失败,请联系管理员!");
        }
    }
}
