package com.wintone.site;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import androidx.multidex.MultiDex;

public class SiteApplication extends Application {

    private static SiteApplication instance;

    public List<Activity> mActivities = new ArrayList<>();

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

    public void addActivity(Activity activity){
        if(!mActivities.contains(activity)){
            mActivities.add(activity);
        }
    }

    public void removeActivity(Activity activity){
        if(!mActivities.contains(activity)){
            mActivities.remove(activity);
        }
    }

    public void removeAllActivity(){
        for(Activity activity : mActivities){
            if(activity != null){
                Log.i("SiteApplication","the activity name = " + activity.getClass().getSimpleName());
                activity.finish();
            }
        }
    }
}
