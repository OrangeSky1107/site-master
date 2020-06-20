package com.wintone.site.ui.activity;

import android.view.View;
import android.widget.TextView;

import com.wintone.site.R;
import com.wintone.site.ui.base.activity.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class FeedBackActivity extends BaseActivity {

    @BindView(R.id.toolbar_title) TextView toolbar_title;

    @Override
    protected int getContentView() {
        return R.layout.activity_feed_back;
    }

    @Override
    protected void initView() {
        toolbar_title.setText("用户反馈");
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

    }
}
