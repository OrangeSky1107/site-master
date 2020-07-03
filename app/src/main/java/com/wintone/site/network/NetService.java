package com.wintone.site.network;

import com.wintone.site.networkmodel.AppVersionModel;
import com.wintone.site.networkmodel.ResponseModel;
import com.wintone.site.networkmodel.AttendanceRecord;
import com.wintone.site.networkmodel.ConstructionModel;
import com.wintone.site.networkmodel.DictionariesModel;
import com.wintone.site.networkmodel.FeedModelRequetModel;
import com.wintone.site.networkmodel.HomePagerModel;
import com.wintone.site.networkmodel.LoginModel;
import com.wintone.site.networkmodel.PersonDetailsModel;
import com.wintone.site.networkmodel.PersonSignalModel;
import com.wintone.site.networkmodel.ProjectModel;
import com.wintone.site.networkmodel.ProjectWorkers;
import com.wintone.site.networkmodel.RegisterInfoModel;
import com.wintone.site.networkmodel.TeamModel;
import com.wintone.site.networkmodel.UpdateUserModel;
import com.wintone.site.utils.bankinfo.BankModel;

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

    @POST
    Observable<ResponseModel> postUpdatePassword(
            @Header("token") String token,
            @Url String url,
            @Body Map<String,String> map
    );

    @GET
    Observable<BankModel> getBankInfoOfAilPay(
            @Url String url
    );

    @POST
    Observable<LoginModel> postLogin(
            @Url String url,
            @Body Map<String,String> map
    );

    @POST
    Observable<HomePagerModel> postHomePager(
            @Header("token") String token,
            @Url  String url,
            @Body Map<String,String> map
    );

    @POST
    Observable<ProjectModel> postProjectList(
            @Header("token") String token,
            @Url String url,
            @Body Map map
    );

    @POST
    Observable<ConstructionModel> postConstructionList(
            @Header("token") String token,
            @Url String url,
            @Body Map map
    );

    @POST
    Observable<TeamModel> postTeamList(
            @Header("token") String token,
            @Url String url,
            @Body Map map
    );

    @POST
    Observable<DictionariesModel> postHotDicList(
            @Header("token") String token,
            @Url String url,
            @Body Map map
    );

    @POST
    Observable<RegisterInfoModel> postWorkersSaveOrUpdate(
            @Header("token") String token,
            @Url String url,
            @Body ProjectWorkers data
    );

    @POST
    Observable<ResponseModel> postAttendanceRecord(
            @Header("token") String token,
            @Url String url,
            @Body AttendanceRecord data
    );

    @POST
    Observable<PersonDetailsModel> postWorkersPersonnelList(
            @Header("token") String token,
            @Url String url,
            @Body Map map
    );

    @POST
    Observable<ResponseBody> postLoginOut(
            @Url String url,
            @Header("token") String token
    );

    @GET
    Observable<PersonSignalModel> getSignalPersonInfo(
            @Url String url,
            @Header("token") String token
    );

    @GET
    Observable<AppVersionModel> getAppVersionInfo(
            @Url String url,
            @Header("token") String token
    );

    @POST
    Observable<ResponseBody> postFeedbackInfo(
            @Url String url,
            @Header("token") String token,
            @Body FeedModelRequetModel feedModelRequetModel
    );

    @POST
    Observable<ResponseBody> postUpdateUserInfo(
            @Url String url,
            @Header("token") String token,
            @Body UpdateUserModel model
    );

    @POST
    Observable<ProjectModel> postSwitchProject(
            @Url String url,
            @Header("token") String token,
            @Body Map map
    );
}
