package com.wintone.site.ui.fragment;

import android.view.View;

import com.wintone.site.R;
import com.wintone.site.ui.base.fragment.BaseFragment;


public class PersonInfoFragment extends BaseFragment {


    private PersonInfoFragment() {
    }

    public static PersonInfoFragment newInstance() {
        PersonInfoFragment fragment = new PersonInfoFragment();
        return fragment;
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_person_info;
    }

    @Override
    protected void initView(View view) {

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
