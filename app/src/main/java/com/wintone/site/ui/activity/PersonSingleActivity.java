package com.wintone.site.ui.activity;

import com.google.android.material.tabs.TabLayout;
import com.wintone.site.R;
import com.wintone.site.ui.base.activity.BaseActivity;
import com.wintone.site.ui.fragment.PersonInfoFragment;
import com.wintone.site.ui.fragment.ProjectInfoFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

public class PersonSingleActivity extends BaseActivity {

    @BindView(R.id.tablayout)
    protected TabLayout tablayout;
    @BindView(R.id.tab_viewpager)
    protected ViewPager tabViewpager;

    private Fragment[] mFragmentArrays = new Fragment[2];

    private String[] mTabTitles = new String[2];

    @Override
    protected int getContentView() {
        return R.layout.activity_person_single;
    }

    @Override
    protected void initView() {
        mTabTitles[0] = "个人信息";
//        mTabTitles[1] = "资料签订";
        mTabTitles[1] = "所属项目";
        tablayout.setTabMode(TabLayout.MODE_FIXED);
        //设置tablayout距离上下左右的距离
        tablayout.setPadding(20,20,20,20);
        mFragmentArrays[0] = PersonInfoFragment.newInstance();
//        mFragmentArrays[1] = MaterialInfoTitleFragment.newInstance();
        mFragmentArrays[1] = ProjectInfoFragment.newInstance();
        PagerAdapter pagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        tabViewpager.setAdapter(pagerAdapter);
        //将ViewPager和TabLayout绑定
        tablayout.setupWithViewPager(tabViewpager);
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
