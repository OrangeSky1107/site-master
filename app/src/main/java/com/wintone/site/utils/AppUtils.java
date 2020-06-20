package com.wintone.site.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.wintone.site.SiteApplication;

import java.util.UUID;

/**
 * create by ths on 2020/6/19
 */
public class AppUtils {

    //获取手机的唯一标识
    @SuppressLint("MissingPermission")
   public static String getPhoneSign(){

       StringBuilder deviceId = new StringBuilder();
       // 渠道标志
       try {
           //IMEI（imei）
           TelephonyManager tm = (TelephonyManager) SiteApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
           String imei = tm.getDeviceId();
             if(!TextUtils.isEmpty(imei)){
                 deviceId.append(imei);
                 return deviceId.toString();
             }
             //序列号（sn）
           String sn = tm.getSimSerialNumber();
             if(!TextUtils.isEmpty(sn)){
                 deviceId.append(sn);
                 return deviceId.toString();
             }
             //如果上面都没有， 则生成一个id：随机码
             String uuid = getUUID();
             if(!TextUtils.isEmpty(uuid)){
                 deviceId.append("id");
                 deviceId.append(uuid);
                 return deviceId.toString();
             }
            } catch (Exception e) {
               e.printStackTrace();
               deviceId.append("id").append(getUUID());
            }
            return deviceId.toString();
   }

    public static String getUUID(){
        String uuid = "";
        SharedPreferences mShare = SiteApplication.getInstance().getSharedPreferences("uuid",SiteApplication.getInstance().MODE_PRIVATE);
        if(mShare != null){
            uuid = mShare.getString("uuid", "");
        }
        if(TextUtils.isEmpty(uuid)){
            uuid = UUID.randomUUID().toString();
            mShare.edit().putString("uuid",uuid).commit();
        }
        return uuid;
    }
}
