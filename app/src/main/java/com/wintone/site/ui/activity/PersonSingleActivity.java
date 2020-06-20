package com.wintone.site.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.socks.library.KLog;
import com.wintone.site.R;
import com.wintone.site.network.NetService;
import com.wintone.site.network.NetWorkUtils;
import com.wintone.site.networkmodel.PersonSignalModel;
import com.wintone.site.ui.base.activity.BaseActivity;
import com.wintone.site.ui.fragment.PersonInfoFragment;
import com.wintone.site.ui.fragment.ProjectInfoFragment;
import com.wintone.site.utils.Constant;
import com.wintone.site.utils.SPUtils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PersonSingleActivity extends BaseActivity {

    @BindView(R.id.tablayout)     protected TabLayout tablayout;
    @BindView(R.id.tab_viewpager) protected ViewPager tabViewpager;
    @BindView(R.id.toolbar_title) TextView toolbar_title;

    private Fragment[] mFragmentArrays = new Fragment[2];
    private String[] mTabTitles = new String[2];

    private KProgressHUD mHUD;

    @Override
    protected int getContentView() {
        return R.layout.activity_person_single;
    }

    @Override
    protected void initView() {
        initProgress();

        toolbar_title.setText("详细信息");

        getIntentData();
    }

    @OnClick({R.id.iv_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void initTabLayoutAndFragment(PersonSignalModel value){
        mTabTitles[0] = "个人信息";
//        mTabTitles[1] = "资料签订";
        mTabTitles[1] = "所属项目";
        tablayout.setTabMode(TabLayout.MODE_FIXED);
        tablayout.setPadding(20,20,20,20);

        mFragmentArrays[0] = PersonInfoFragment.newInstance(value);
//        mFragmentArrays[1] = MaterialInfoTitleFragment.newInstance();
        mFragmentArrays[1] = ProjectInfoFragment.newInstance(value);

        PagerAdapter pagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        tabViewpager.setAdapter(pagerAdapter);
        //将ViewPager和TabLayout绑定
        tablayout.setupWithViewPager(tabViewpager);
    }

    private void getIntentData(){
        mHUD.show();
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String url = Constant.WORKERS_INFO_URL + id;
        String token = (String) SPUtils.getShare(this,Constant.USER_TOKEN,"");

        NetWorkUtils.getInstance().createService(NetService.class)
                .getSignalPersonInfo(url,token)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PersonSignalModel>() {
                    @Override public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(PersonSignalModel value) {
                        initTabLayoutAndFragment(value);
                        mHUD.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        KLog.i("look at response error message = " + e.getMessage());
                        mHUD.dismiss();
                    }

                    @Override public void onComplete() { }
                });
    }

    private void initProgress() {
        mHUD = KProgressHUD.create(PersonSingleActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDetailsLabel("加载中...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    @Override
    protected void initData() {

    }

    final class MyViewPagerAdapter extends FragmentPagerAdapter {
        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentArrays[position];
        }


        @Override
        public int getCount() {
            return mFragmentArrays.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];
        }
    }
}
