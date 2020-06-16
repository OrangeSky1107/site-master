package com.wintone.site.ui.base.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wintone.site.ui.base.presenter.PresenterLife;
import com.wintone.site.ui.base.view.BaseView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment<P extends PresenterLife, V extends BaseView> extends Fragment
        implements BaseView{

    private Unbinder mUnbinder;
    protected P mPresenter;
    protected Bundle mBundle;

    protected View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mBundle = savedInstanceState.getBundle("bundle");
        } else {
            mBundle = getArguments() == null ? new Bundle() : getArguments();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mBundle != null) {
            outState.putBundle("bundle", mBundle);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView == null){
            rootView = inflater.inflate(getContentView(),container,false);
        }else{
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null){
                parent.removeView(rootView);
            }
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUnbinder = ButterKnife.bind(this,view);

        ButterKnife.bind(this,view);

        initView(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mUnbinder.unbind();

        if(mPresenter != null){
            mPresenter.onDestroy();
            mPresenter = null;
        }
        if(rootView!=null) rootView = null;
    }

    protected abstract int getContentView();

    protected abstract void initView(View view);
}
