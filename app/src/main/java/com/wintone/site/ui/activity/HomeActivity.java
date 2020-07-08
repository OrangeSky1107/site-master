package com.wintone.site.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wintone.site.R;
import com.wintone.site.network.NetService;
import com.wintone.site.network.NetWorkUtils;
import com.wintone.site.networkmodel.AppVersionModel;
import com.wintone.site.ui.base.activity.BaseActivity;
import com.wintone.site.utils.Constant;
import com.wintone.site.utils.SPUtils;

import java.io.Serializable;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeActivity extends BaseActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
////        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
////                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
////               .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
////        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);
//    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    protected void initData() {
        checkCurrentVersion();
    }

    private void checkCurrentVersion(){
        String token = (String) SPUtils.getShare(this, Constant.USER_TOKEN,"");

        String url = Constant.CHECK_VERSION_URL + "1" + "/V"+AppUtils.getAppVersionName();

        NetWorkUtils.getInstance().createService(NetService.class)
                .getAppVersionInfo(url,token)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppVersionModel>() {
                    @Override public void onSubscribe(Disposable d) { }

                    @Override
                    public void onNext(AppVersionModel value) {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override public void onComplete() { }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            String result = data.getStringExtra("OCRResult");
            HashMap hashMap = JSON.parseObject(result,HashMap.class);
            Intent intent = new Intent(this,IdCardInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data",(Serializable)hashMap);
            intent.putExtra("bundle",bundle);
            ActivityUtils.startActivity(intent);

//            try {
//                JSONObject jo = new JSONObject(result);
//                StringBuffer sb = new StringBuffer();
//                sb.append(String.format("正面 = %s\n", jo.opt("type")));
//                sb.append(String.format("姓名 = %s\n", jo.opt("name")));
//                sb.append(String.format("性别 = %s\n", jo.opt("sex")));
//                sb.append(String.format("民族 = %s\n", jo.opt("folk")));
//                sb.append(String.format("日期 = %s\n", jo.opt("birt")));
//                sb.append(String.format("号码 = %s\n", jo.opt("num")));
//                sb.append(String.format("住址 = %s\n", jo.opt("addr")));
//                sb.append(String.format("签发机关 = %s\n", jo.opt("issue")));
//                sb.append(String.format("有效期限 = %s\n", jo.opt("valid")));
//                sb.append(String.format("整体照片 = %s\n", jo.opt("imgPath")));
//                sb.append(String.format("头像路径 = %s\n", jo.opt("headPath")));
//                sb.append("\n驾照专属字段\n");
//                sb.append(String.format("国家 = %s\n", jo.opt("nation")));
//                sb.append(String.format("初始领证 = %s\n", jo.opt("startTime")));
//                sb.append(String.format("准驾车型 = %s\n", jo.opt("drivingType")));
//                sb.append(String.format("有效期限 = %s\n", jo.opt("registerDate")));
//                Log.i("MainActivity","look at current message = " + sb.toString());
//            } catch (JSONException e) {
//                KLog.i("look at error message = " + e.getMessage().toString());
//                e.printStackTrace();
//            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SPUtils.removeShare(this,Constant.SHOW_SWITCH_PROJECT);
    }
}
