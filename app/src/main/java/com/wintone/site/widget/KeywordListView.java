package com.wintone.site.widget;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ToastUtils;
import com.socks.library.KLog;
import com.wintone.site.R;
import com.wintone.site.SiteApplication;
import com.wintone.site.network.NetService;
import com.wintone.site.network.NetWorkUtils;
import com.wintone.site.networkmodel.ProjectModel;
import com.wintone.site.ui.adapter.ProjectListAdapter;
import com.wintone.site.utils.Constant;
import com.wintone.site.utils.SPUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class KeywordListView {

    private Activity mActivity;
    private View view;
    private ViewGroup mViewGroup;
    RecyclerView rvSearchKeyword;

    private ProjectListAdapter mSearchKeywordAdapter;
    private List<String> mListData;

    private OnShowKeyWordListener monShowKeyWordListener;


    public KeywordListView(@NonNull Activity activity, ViewGroup mViewGroup, OnShowKeyWordListener monShowKeyWordListener) {
        this.mViewGroup = mViewGroup;
        mActivity = activity;
        this.monShowKeyWordListener = monShowKeyWordListener;
        init(activity);
    }

    private void init(Context mContext) {
        view = LayoutInflater.from(mContext).inflate(R.layout.search_switch_layout, null);
        mListData = new ArrayList<>();
        rvSearchKeyword = view.findViewById(R.id.rv_search_keyword);
        mSearchKeywordAdapter = new ProjectListAdapter();
        rvSearchKeyword.setLayoutManager(new LinearLayoutManager(mContext));
        rvSearchKeyword.setAdapter(mSearchKeywordAdapter);


        mSearchKeywordAdapter.setOnItemClickListener(new ProjectListAdapter.OnItemClickListener() {
            @Override
            public void onClickItem(int position, ProjectModel.ResultBean.RecordsBean recordsBean) {
                switchProjectData(recordsBean.getProjectName(),recordsBean.getId());
            }
        });
        rvSearchKeyword.setAdapter(mSearchKeywordAdapter);

        mViewGroup.addView(view);
    }

    public void getKeyWordList(String KeyWord){
        String token = (String) SPUtils.getShare(mActivity, Constant.USER_TOKEN,"");
        String loginName = (String)SPUtils.getShare(mActivity,Constant.USER_NAME,"");

        Map map = new HashMap();
        map.put("loginName",loginName);
        map.put("projectName",KeyWord);
        map.put("page",1);
        map.put("rows",10000);

        NetWorkUtils.getInstance().createService(NetService.class)
                .postSwitchProject(Constant.SWITCH_PROJECT_URL,token,map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ProjectModel>() {
                    @Override public void onSubscribe(Disposable d) { }

                    @Override
                    public void onNext(ProjectModel value) {
                        if(value.getResult().getRecords().size() > 0){
                            monShowKeyWordListener.isShowView(true);
                        }else{
                            monShowKeyWordListener.isShowView(false);
                        }
                        mSearchKeywordAdapter.submitList(value.getResult().getRecords());
                        monShowKeyWordListener.hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        monShowKeyWordListener.hideProgress();
                        ToastUtils.showShort("出现错误:"+e.getMessage());
                        KLog.i("look at response error message = " + e.getMessage());
                    }
                    @Override public void onComplete() { }
                });
    }

    private void switchProjectData(String projectName,String projectId){
        String token = (String) SPUtils.getShare(SiteApplication.getInstance(), Constant.USER_TOKEN,"");
        String loginName = (String)SPUtils.getShare(SiteApplication.getInstance(),Constant.USER_NAME,"");

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
                .subscribe(new Observer<ProjectModel>() {
                    @Override public void onSubscribe(Disposable d) { }

                    @Override
                    public void onNext(ProjectModel value) {
                        if(value != null){
                            monShowKeyWordListener.selectProjectFinish();
                            SPUtils.putShare(mActivity,Constant.SHOW_SWITCH_PROJECT,projectId);
                            ToastUtils.showShort("切换成功!");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShort("出现错误:"+e.getMessage());
                    }
                    @Override public void onComplete() { }
                });
    }


    public void resetview(){
        if (mListData != null && mSearchKeywordAdapter != null){
            mListData.clear();
//            mSearchKeywordAdapter.submitList(value.getResult().getRecords());
        }
    }

    public interface OnShowKeyWordListener {
        void isShowView(boolean isShow);

        void onSelectKeyWord(String content);

        void hideProgress();

        void selectProjectFinish();
    }


}
