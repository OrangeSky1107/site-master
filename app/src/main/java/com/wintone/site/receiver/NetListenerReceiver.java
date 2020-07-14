package com.wintone.site.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.wintone.site.network.OkHttpUtil;

/**
 * create by ths on 2020/7/14
 * # 全局监听网络变化 广播
 */
public class NetListenerReceiver extends BroadcastReceiver {

    private NetChangeListener mNetChangeListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            int netWorkState = OkHttpUtil.getNetWorkState(context);
            if (mNetChangeListener != null) {
                mNetChangeListener.onChangeListener(netWorkState);
            }
        }
    }

    public void setNetChangeListener(NetChangeListener netChangeListener){
        this.mNetChangeListener = netChangeListener;
    }

    public interface NetChangeListener {
        void onChangeListener(int status);
    }
}
