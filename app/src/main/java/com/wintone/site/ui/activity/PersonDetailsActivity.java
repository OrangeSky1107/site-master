package com.wintone.site.ui.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.wintone.site.R;
import com.wintone.site.network.NetService;
import com.wintone.site.network.NetWorkUtils;
import com.wintone.site.networkmodel.PersonDetailsModel;
import com.wintone.site.ui.adapter.PersonDetailsAdapter;
import com.wintone.site.ui.base.activity.BaseActivity;
import com.wintone.site.utils.Constant;
import com.wintone.site.utils.SPUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PersonDetailsActivity extends BaseActivity {

    @BindView(R.id.gridView)       GridView mGridView;
    @BindView(R.id.tips)           TextView tips;
    @BindView(R.id.toolbar_title)  TextView toolbarTitle;
    @BindView(R.id.refreshLayout)  SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.materialHeader) MaterialHeader mMaterialHeader;

    private PersonDetailsAdapter adapter;

    private KProgressHUD mHUD;

    @Override
    protected int getContentView() {
        return R.layout.activity_person_details;
    }

    @Override
    protected void initView() {
        toolbarTitle.setText("人员信息");

        initProgress();

        initListener();
    }

    private void initListener() {
        mRefreshLayout.setPrimaryColorsId(R.color.home_num_color,R.color.white);
        mMaterialHeader.setProgressBackgroundColorSchemeResource(R.color.home_num_color);
        mMaterialHeader.setColorSchemeResources(R.color.white);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(1500);
                initData();
            }
        });
    }

    private void initProgress() {
        mHUD = KProgressHUD.create(PersonDetailsActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDetailsLabel("加载中...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    @OnClick({R.id.iv_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    protected void initData() {
        mHUD.show();

        String token = (String) SPUtils.getShare(PersonDetailsActivity.this, Constant.USER_TOKEN,"");

        Map map = new HashMap();
        map.put("loginName", (String) SPUtils.getShare(this,Constant.USER_NAME,""));
        map.put("page",1);
        map.put("rows",100000);

        NetWorkUtils.getInstance().createService(NetService.class)
                .postWorkersPersonnelList(token,Constant.WORKERS_PERSONNEL_LIST,map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PersonDetailsModel>() {
                    @Override public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(PersonDetailsModel value) {
                        fillToAdapter(value.getResult().getRecords());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("PersonDetailsActivity","look at message body = " + e.getMessage().toString());
                    }

                    @Override public void onComplete() {}
                });
    }

    private void fillToAdapter(List<PersonDetailsModel.ResultBean.RecordsBean> list){
        adapter = new PersonDetailsAdapter(this, list);
        mGridView.setAdapter(adapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(PersonDetailsActivity.this,PersonSingleActivity.class);
                intent.putExtra("id",list.get(position).getId());
                ActivityUtils.startActivity(intent);
            }
        });

        mHUD.dismiss();
    }
}
