package com.wintone.site.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.kyleduo.switchbutton.SwitchButton;
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
import com.wintone.site.widget.PopDictionariesLayout;

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

public class PersonInfoActivity extends BaseActivity implements PopupWindow.OnDismissListener,PopDictionariesLayout.DictionariesListener {

    private HashMap dataMap = null;

    @BindView(R.id.projectTextView) EditText projectTextView;
    @BindView(R.id.companyTextView) EditText companyTextView;
    @BindView(R.id.teamTextView)    EditText teamTextView;
    @BindView(R.id.workTypeTextView)EditText workTypeTextView;
    @BindView(R.id.entranceDateTextView) EditText entranceDateTextView;
    @BindView(R.id.phoneTextView)   EditText phoneTextView;
    @BindView(R.id.leaderSwitch)    SwitchButton leaderSwitch;
    @BindView(R.id.nextButton)      Button nextButton;

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

    private PopDictionariesLayout mDictionariesLayout;

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
        }

        initProgress();
    }

    private String project = "";
    private String team = "";
    private String date = "";
    private String phone = "";

    @Override
    protected void initData() {
        phoneTextView.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                phone = s.toString();
                listenerAllEditText(project,team,date,phone);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        workTypeTextView.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                team = s.toString();
                listenerAllEditText(project,team,date,phone);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        entranceDateTextView.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                date = s.toString();
                listenerAllEditText(project,team,date,phone);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        projectTextView.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                project = s.toString();
                listenerAllEditText(project,team,date,phone);
            }
            @Override public void afterTextChanged(Editable s) { }
        });
    }

    private void listenerAllEditText(String project,String team,String date,String phone){
        if(project.length() > 0 && team.length() > 0 && date.length() > 0 && phone.length() > 0){
            nextButton.setEnabled(true);
        }else{
            nextButton.setEnabled(false);
        }
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
                pullHotDicList("");
                break;
            case R.id.entranceDateTextView:
                openDatePicker();
                break;
            case R.id.leaderSwitch:
                if(leaderSwitch.isChecked()){
                    leaderFlag = 1;
                }else{
                    leaderFlag = 0;
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
                        mHUD.dismiss();
                    }
                    @Override public void onComplete() {}
                });
    }

    private boolean projectFlag = true;

    private void fillProject(List<ProjectModel.ResultBean.RecordsBean> recordsBeans){
        if(recordsBeans.size() == 0){
            ToastUtils.showShort("项目个数为0!");
            return;
        }
        List<String> projectName = new ArrayList<>();

        for (int i = 0; i < recordsBeans.size();i++){
            String title = recordsBeans.get(i).getProjectName();
            projectName.add(title);
        }

        UiUtils.showOptionInfoPicker(this, projectName, 0, new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                if(projectFlag){
                    //只记录第一次进来修改选择项目不控制,以后进来的都必须先清空一次
                    projectFlag = false;
                }else{
                    companyTextView.setText(null);
                    teamTextView.setText(null);
                    companyTextView.setHint("请选择所属分包商");
                    teamTextView.setHint("请选择班组");
                }
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
        String projectId =  mProjectModel.getResult().getRecords().get(projectIndex).getId();

        Map map = new HashMap();
        map.put("projectsId",projectId);
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
                        mHUD.dismiss();
                    }

                    @Override public void onComplete() {}
                });
    }

    private boolean companyFlag = true;

    private void fillCompanyStore(List<ConstructionModel.ResultBean.RecordsBean> recordsBeans){
        if(recordsBeans.size() == 0){
            ToastUtils.showShort("分包商个数为0!");
            return;
        }
        List<String> companyName = new ArrayList<>();

        for (int i = 0; i < recordsBeans.size();i++){
            String title = recordsBeans.get(i).getConstructionName();
            companyName.add(title);
        }

        UiUtils.showOptionInfoPicker(this, companyName, 0, new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                if(companyFlag){
                    companyFlag = false;
                }else{
                    teamTextView.setText(null);
                    teamTextView.setHint("请选择班组");
                }
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
                        mHUD.dismiss();
                    }

                    @Override public void onComplete() {}
                });
    }

    private void fillTeamList(List<TeamModel.ResultBean.RecordsBean> recordsBeans){
        if(recordsBeans.size() == 0){
            ToastUtils.showShort("班组个数为0!");
            return;
        }
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

    private void pullHotDicList(String dicName){
        String token = (String)SPUtils.getShare(PersonInfoActivity.this,Constant.USER_TOKEN,"");

        Map map = new HashMap();
        map.put("page",1);
        map.put("rows",100000);
        map.put("dicName",dicName);

        NetWorkUtils.getInstance().createService(NetService.class)
                .postHotDicList(token,Constant.DICTIONARIES_HOTDIC_URL,map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DictionariesModel>() {
                    @Override public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(DictionariesModel value) {
                        mDictionariesModel = value;
                        fillHotDicList(value.getResult().getRecords());
                        mHUD.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mHUD.dismiss();
                    }

                    @Override public void onComplete() {}
                });
    }

    private void fillHotDicList(List<DictionariesModel.ResultBean.RecordsBean> recordsBeans){
        if(recordsBeans.size() == 0){
            ToastUtils.showShort("工种个数为0!");
            return;
        }
        popLayout(recordsBeans);
    }

    private void popLayout(List<DictionariesModel.ResultBean.RecordsBean> recordsBeans){
        if(mDictionariesLayout == null){
            mDictionariesLayout = new PopDictionariesLayout(this);
            mDictionariesLayout.setDictionariesListener(this);
            mDictionariesLayout.setOnDismissListener(this);
            setWindowAttributes(0.5f);
            mDictionariesLayout.showAtLocation(nextButton,
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            mDictionariesLayout.setAdapterData(recordsBeans);
        }else{
            setWindowAttributes(0.5f);
            mDictionariesLayout.showAtLocation(nextButton,
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            mDictionariesLayout.setAdapterData(recordsBeans);
        }
    }

    private void setWindowAttributes(float color){
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = color;
        getWindow().setAttributes(lp);
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
        if(!RegexUtils.isMobileExact(phone)){
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
                ToastUtils.showShort("身份证上传失败!");
                mHUD.dismiss();
            }

            @Override
            public void responseSuccess(String successMessage) {
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
                ToastUtils.showShort("注册人脸图上传失败!");
                mHUD.dismiss();
            }

            @Override
            public void responseSuccess(String successMessage) {
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
                ToastUtils.showShort("身份证背面图上传失败!");
                mHUD.dismiss();
            }

            @Override
            public void responseSuccess(String successMessage) {
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
                    ToastUtils.showShort("身份证背面图上传失败!");
                    mHUD.dismiss();
                }

                @Override
                public void responseSuccess(String successMessage) {
                    HashMap hashMap = JSON.parseObject(successMessage,HashMap.class);
                    String cloudBankPath = hashMap.get("result").toString();
                    uploadFaceImage(cloudIdCardPath,cloudHeadPath,cloudIdCardBackPath,cloudBankPath);
                }
            });
        }else{
            uploadFaceImage(cloudIdCardPath,cloudHeadPath,cloudIdCardBackPath,"");
        }
    }

    private void uploadFaceImage(String cloudIdCardPath,String cloudHeadPath,String cloudIdCardBackPath,String cloudBankPath){
        String faceUrlPath = dataMap.get("faceImg").toString();
        String token = (String)SPUtils.getShare(PersonInfoActivity.this,Constant.USER_TOKEN,"");
        OkHttpUtil.getInstance().uploadTopPost(Constant.USER_UPLOAD_URL,token, faceUrlPath, new OkhttpClientRequest() {
            @Override
            public void responseFailure(String errorMessage) {
                ToastUtils.showShort("人脸实时图片上传失败!");
                mHUD.dismiss();
            }

            @Override
            public void responseSuccess(String successMessage) {
                HashMap hashMap = JSON.parseObject(successMessage,HashMap.class);
                String faceUrl = hashMap.get("result").toString();
                submit(cloudIdCardPath,cloudHeadPath,cloudIdCardBackPath,cloudBankPath,faceUrl);
            }
        });
    }

    private void submit(String cloudIdCardPath,String cloudHeadPath,String cloudIdCardBackPath,String cloudBankPath,String faceUrl){
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
        projectWorkers.setEmpNaticeplace(cloudHeadPath);
        projectWorkers.setFaceUrl(faceUrl);
        projectWorkers.setIdphotoScan(cloudIdCardPath);
        projectWorkers.setIdphotoScan2(cloudIdCardBackPath);

        String token = (String)SPUtils.getShare(PersonInfoActivity.this,Constant.USER_TOKEN,"");

        NetWorkUtils.getInstance().createService(NetService.class)
                .postWorkersSaveOrUpdate(token,Constant.WORKERS_SAVEORUPDATE_URL,projectWorkers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RegisterInfoModel>() {
                    @Override public void onSubscribe(Disposable d) { }

                    @Override
                    public void onNext(RegisterInfoModel value) {
                        disposeRegister(value);
                        mHUD.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mHUD.dismiss();
                    }

                    @Override public void onComplete() { }
                });
    }

    private void disposeRegister(RegisterInfoModel value){
        if(value.getCode() == 1000){
            ToastUtils.showShort("注册成功!");
        }else if(value.getCode() == 1002){
            ToastUtils.showShort("该人员已经注册过了!");
        }else {
            ToastUtils.showShort("系统错误!");
        }
        ActivityUtils.startActivity(new Intent(PersonInfoActivity.this,HomeActivity.class));
    }

    @Override
    public void onDismiss() {
        setWindowAttributes(1f);
    }


    @Override
    public void searchDictionaries(String title) {
        pullHotDicList(title);
    }

    @Override
    public void selectDictionaries(int position, DictionariesModel.ResultBean.RecordsBean recordsBean) {
        hotDicIndex = position;
        workTypeTextView.setText(recordsBean.getTitle());
    }
}
