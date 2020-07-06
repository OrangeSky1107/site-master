package com.wintone.site;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.enums.RuntimeABI;
import com.wintone.site.utils.Constant;
import com.wintone.site.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class SiteApplication extends Application {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

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
        activeEngine();
    }

    public void activeEngine() {
        Integer exitsEngine = (Integer) SPUtils.getShare(instance.getApplicationContext(),Constant.FACE_ENGINE,0);
        if(exitsEngine == 0){
            Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                public void subscribe(ObservableEmitter<Integer> emitter) {
                    RuntimeABI runtimeABI = FaceEngine.getRuntimeABI();
                    Log.i( "SiteApplication","SiteApplication subscribe: getRuntimeABI() " + runtimeABI);

                    int activeCode = FaceEngine.activeOnline(instance, Constant.APP_ID, Constant.SDK_KEY);
                    emitter.onNext(activeCode);
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {}

                        @Override
                        public void onNext(Integer activeCode) {
                            if (activeCode == ErrorInfo.MOK) {
                                Log.i("SiteApplication","SiteApplication active is success ");
                                SPUtils.putShare(instance.getApplicationContext(),Constant.FACE_ENGINE,1);
                            } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                                Log.i("SiteApplication","SiteApplication active is ALREADY ");
                            } else {
                                Log.i("SiteApplication","SiteApplication active is failed code = " + activeCode);
                            }
//                        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
//                        int res = FaceEngine.getActiveFileInfo(instance, activeFileInfo);
//                        if (res == ErrorInfo.MOK) {
//                            Log.i("SiteApplication",activeFileInfo.toString());
//                        }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i("SiteApplication","look at error message = " + e.getMessage().toString());
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }else{
            Log.i("SiteApplication","face engine is ALREADY");
        }
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
                activity.finish();
            }
        }
    }
}
