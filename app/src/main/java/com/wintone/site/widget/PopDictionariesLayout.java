package com.wintone.site.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.wintone.site.R;
import com.wintone.site.networkmodel.DictionariesModel;
import com.wintone.site.ui.adapter.DictionariesAdapter;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * create by ths on 2020/7/3
 */
public class PopDictionariesLayout extends PopupWindow {

    private Context mContext;

    private View mMenuView;

    private EditText            editTextSearch;
    private RecyclerView        recyclerView;

    private DictionariesAdapter mDictionariesAdapter;

    private DictionariesListener mDictionariesListener;

    public PopDictionariesLayout(Context context){
        this.mContext = context;

        initView();

        initListener();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mMenuView = inflater.inflate(R.layout.pop_dictionaries_layout,null);
        editTextSearch = mMenuView.findViewById(R.id.editTextSearch);
        recyclerView = mMenuView.findViewById(R.id.recyclerView);

        this.setContentView(mMenuView);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.pop_down_to_up);
        ColorDrawable cd = new ColorDrawable(0x80000000);
        this.setBackgroundDrawable(cd);
    }

    private void initLayoutManager(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        mDictionariesAdapter = new DictionariesAdapter();

        mDictionariesAdapter.setOnItemClickListener(new DictionariesAdapter.OnItemClickListener() {
            @Override
            public void onClickItem(int position, DictionariesModel.ResultBean.RecordsBean recordsBean) {
                mDictionariesListener.selectDictionaries(position,recordsBean);
                dismiss();
            }
        });

        recyclerView.setAdapter(mDictionariesAdapter);
    }

    private void initListener() {
        initLayoutManager();

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDictionariesListener.searchDictionaries(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    public void setAdapterData(List<DictionariesModel.ResultBean.RecordsBean> recordsBeans){
        mDictionariesAdapter.submitList(recordsBeans);
    }

    public interface DictionariesListener{
        void searchDictionaries(String title);
        void selectDictionaries(int position,DictionariesModel.ResultBean.RecordsBean recordsBean);
    }

    public void setDictionariesListener(DictionariesListener listener){
        this.mDictionariesListener = listener;
    }
}
