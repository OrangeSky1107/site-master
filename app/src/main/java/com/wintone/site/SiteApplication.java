package com.wintone.site;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

public class SiteApplication extends Application {

    private static SiteApplication instance;

    public static SiteApplication getInstance(){
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
