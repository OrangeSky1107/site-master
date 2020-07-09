package com.scwang.smartrefresh.layout.listener;


import com.scwang.smartrefresh.layout.api.RefreshLayout;

import androidx.annotation.NonNull;

/**
 * 刷新监听器
 * Created by scwang on 2017/5/26.
 */
public interface OnRefreshListener {
    void onRefresh(@NonNull RefreshLayout refreshLayout);
}
