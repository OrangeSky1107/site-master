package com.yizhitong.site.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankj.utilcode.util.ActivityUtils;
import com.yizhitong.site.R;
import com.yizhitong.site.permissions.EasyPermission;
import com.yizhitong.site.utils.UiUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class WelcomeActivity extends AppCompatActivity implements EasyPermission.PermissionCallback{

    private String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private boolean isHavePermission = true;

    private final int REQUEST_PERMISS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);
        ImageView ic = findViewById(R.id.ic_logo);
        Drawable drawable = getResources().getDrawable(R.drawable.logo);
        UiUtils.circularBitmap(ic, drawable);
        UiUtils.hideNavKey(this);

        requestPermission();
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
                        if(checkToken()){
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
        return false;
    }

    private void requestPermission(){
        if(EasyPermission.hasPermissions(this, permissions)){
            isHavePermission = true;
        }else{
            EasyPermission.with(this)
                    .rationale(getString(com.msd.ocr.idcard.R.string.rationale_camera))
                    .addRequestCode(REQUEST_PERMISS)
                    .permissions(permissions)
                    .request();
        }
    }

    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        isHavePermission = true;
    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        Toast.makeText(this, "请打开权限,不然有关功能使用不了", Toast.LENGTH_SHORT).show();
    }
}
