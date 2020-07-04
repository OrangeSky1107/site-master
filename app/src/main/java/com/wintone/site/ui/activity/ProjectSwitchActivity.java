package com.wintone.site.ui.activity;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wintone.site.R;
import com.wintone.site.ui.base.activity.BaseActivity;
import com.wintone.site.utils.KeyboardUtils;
import com.wintone.site.widget.KeywordListView;

import butterknife.BindView;
import butterknife.OnClick;

public class ProjectSwitchActivity extends BaseActivity {

    @BindView(R.id.searchMaskView)  TextView searchMaskView;
    @BindView(R.id.editTextSearch)  EditText editTextSearch;
    @BindView(R.id.ll_keyword_view) LinearLayout ll_keyword_view;

    private String inputContent = "";

    private Handler mHandler;

    private KeywordListView mKeywordListView;

    @Override
    protected int getContentView() {
        return R.layout.activity_project_switch;
    }

    @Override
    protected void initView() {
        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    KeyboardUtils.hideSoftInput(editTextSearch);
                    return true;
                }
                return false;
            }
        });

        mKeywordListView = new KeywordListView(this, ll_keyword_view, new KeywordListView.OnShowKeyWordListener() {
            @Override
            public void isShowView(boolean isShow) {
                if (isShow) {
                    if (ll_keyword_view != null)
                        ll_keyword_view.setVisibility(View.VISIBLE);
                } else {
                    if (ll_keyword_view != null)
                        ll_keyword_view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSelectKeyWord(String content) {
            }

            @Override
            public void hideProgress() {
                mHUD.dismiss();
            }

            @Override
            public void selectProjectFinish() {
                finish();
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputContent = s.toString();
                mKeywordListView.getKeyWordList(inputContent);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        showInput();
    }

    private void showInput() {
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (editTextSearch != null)
                    KeyboardUtils.showSoftInput(editTextSearch);
            }
        }, 100);
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
        mKeywordListView.getKeyWordList("");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (KeyboardUtils.isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }

        try {
            if (getWindow().superDispatchTouchEvent(ev)) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return onTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
