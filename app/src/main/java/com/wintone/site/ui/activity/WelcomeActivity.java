package com.wintone.site.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.blankj.utilcode.util.ActivityUtils;
import com.wintone.site.R;
import com.wintone.site.utils.Constant;
import com.wintone.site.utils.SPUtils;

import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class WelcomeActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);
    }

    @Override
    protected void onStart() {
        super.onStart();
        postDelay();
    }

    private void postDelay() {
        Observable.timer(3, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long value) {
                        if(!checkToken()){
                            ActivityUtils.startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
                            finish();
                        }else{
                            ActivityUtils.startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                            finish();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private boolean checkToken(){
        if(SPUtils.containsShare(WelcomeActivity.this,Constant.USER_TOKEN)){
            return true;
        }else{
            return false;
        }
    }
}
