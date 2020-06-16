package com.wintone.site.network;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 */

public interface NetService {

//    @GET("api/AmountStates/GetAmountStates")
//    Observable<AmounStates> firstAmountGet(
//            @Header("Authorization") String lang
//    );
//
//    @GET("api/RptInfo/GetRptByCode?code=ContactDelivery")
//    Observable<ResponseBody> firstWebViewUrl(
//            @Header("Authorization") String lang
//    );
//
//    //GetCategoriesByCode
//    @GET("api/RptCategory/GetCategoriesByCode")
//    Observable<List<RptCategory>> executeStrGet(
//            @Header("Authorization") String lang
//    );
//
//    @GET
//    Observable<ResponseBody> executeGetData(
//            @Url String url,
//            @Header("Authorization") String lang
//    );
//
//    @GET
//    Observable<ResponseBody> customziedCode(
//            @Url String url,
//            @Header("Authorization") String lang
//    );
//
//    @GET
//    Observable<List<RptInfo>> executePostFrom(
//            @Url String url,
//            @Header("Authorization") String lang
//    );
//
//    @POST
//    @FormUrlEncoded
//    Observable<HResult> executeLogin(
//            @Url String url,
//            @FieldMap Map<String,String> map
//    );
//
//    @POST
//    @FormUrlEncoded
//    Observable<HResult> postLogin(
//            @Url String url,
//            @Field("account") String account,
//            @Field("pwd") String password,
//            @Field("mobileSerialNo") String serialNo
//    );
//
//    @GET("api/RptCustomization/GetFavoriteRpts")
//    Observable<ResponseBody> customziChinrd(
//            @Header("Authorization") String lang
//    );
//
//    @GET
//    Observable<ResponseBody> cancelCustomChilder(
//            @Url String url,
//            @Header("Authorization") String lang
//    );
//
//    @GET("/api/RptCustomization/GetFrequentVisitRpts?count=10")
//    Observable<ResponseBody> latelyLookCall(
//            @Header("Authorization") String lang
//    );
//
//    @GET("/api/APPVersion/GetLastVersion?clientType=android")
//    Observable<ResponseBody> detectionUpdate(
//    );

    @POST
    Observable<ResponseBody> postUpdatePassword(
            @Header("token") String token,
            @Url String url,
            @Body Map<String,String> map
    );

    @GET
    Observable<ResponseBody> getBankInfoOfAilPay(
            @Url String url
    );
}
