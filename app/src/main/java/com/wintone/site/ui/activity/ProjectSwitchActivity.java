package com.wintone.site.ui.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.socks.library.KLog;
import com.wintone.site.R;
import com.wintone.site.network.NetService;
import com.wintone.site.network.NetWorkUtils;
import com.wintone.site.ui.adapter.ProjectListAdapter;
import com.wintone.site.ui.base.activity.BaseActivity;
import com.wintone.site.utils.Constant;
import com.wintone.site.utils.SPUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class ProjectSwitchActivity extends BaseActivity {

    @BindView(R.id.recyclerView)   RecyclerView recyclerView;
    @BindView(R.id.searchMaskView) RelativeLayout searchMaskView;
    @BindView(R.id.editTextSearch) EditText editTextSearch;

    private String inputContent = "";
    private ProjectListAdapter mListAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_project_switch;
    }

    @Override
    protected void initView() {
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputContent = s.toString();
                getProjectData(inputContent);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void initLayotuManager(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        mListAdapter = new ProjectListAdapter();
    }

    @Override
    protected void initData() {
        mHUD.show();
        String token = (String) SPUtils.getShare(this,Constant.USER_TOKEN,"");
        String loginName = (String)SPUtils.getShare(this,Constant.USER_NAME,"");

        Map map = new HashMap();
        map.put("loginName",loginName);
        map.put("page",1);
        map.put("rows",10000);

        NetWorkUtils.getInstance().createService(NetService.class)
                .postSwitchProject(Constant.SWITCH_PROJECT_URL,token,map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override public void onSubscribe(Disposable d) { }

                    @Override
                    public void onNext(ResponseBody value) {
                        try {
                            KLog.i("look at response body message = " + value.string());
                            mHUD.dismiss();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mHUD.dismiss();
                        KLog.i("look at response error message = " + e.getMessage());
                    }
                    @Override public void onComplete() { }
                });
    }

    private void getProjectData(String projectName){
        mHUD.show();
        String token = (String) SPUtils.getShare(this,Constant.USER_TOKEN,"");
        String loginName = (String)SPUtils.getShare(this,Constant.USER_NAME,"");

        Map map = new HashMap();
        map.put("loginName",loginName);
        map.put("projectName",projectName);
        map.put("page",1);
        map.put("rows",10000);

        NetWorkUtils.getInstance().createService(NetService.class)
                .postSwitchProject(Constant.SWITCH_PROJECT_URL,token,map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override public void onSubscribe(Disposable d) { }

                    @Override
                    public void onNext(ResponseBody value) {
                        try {
                            KLog.i("look at response body message = " + value.string());
                            mHUD.dismiss();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mHUD.dismiss();
                        KLog.i("look at response error message = " + e.getMessage());
                    }
                    @Override public void onComplete() { }
                });
    }
}
