package com.wintone.site.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.wintone.site.SiteApplication;
import com.wintone.site.networkmodel.AttendacedofUpload;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import androidx.annotation.NonNull;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**

 */

public class OkHttpUtil {

    private static final long CACHE_MAX_AGE = 60 * 1;

    private static final long CACHE_STALE_SEC = 60 * 60 * 24;

    private static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;

    private static final String CACHE_CONTROL_AGE = "max-age="+CACHE_MAX_AGE;

    /**
     * 没有网络
     */
    private static final int NETWORK_NONE = -1;
    /**
     * 移动网络
     */
    private static final int NETWORK_MOBILE = 0;
    /**
     * 无线网络
     */
    private static final int NETWORK_WIFI = 1;

    private CustomX509TrustManager sCustomX509TrustManager;

    public static OkHttpUtil getInstance(){
       return new OkHttpUtil();
    }

    public OkHttpClient getOkHttpClient(){
        Cache cache = new Cache(new File(SiteApplication.getInstance().getCacheDir().getAbsolutePath(),"HttpCache"),1024 * 1024 * 10);
        return new OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .sslSocketFactory(createSSLSocketFactory())
                .addInterceptor(mLoginInterceptor)
                .addNetworkInterceptor(mRewriteCacheControlInterceptor)
                .addInterceptor(mRewriteCacheControlInterceptor)
                .build();
    }

    /**
     * 解决okhttp请求https 证书如果是用来测试的(不可信任)的情况下  支持https网站是CA机构颁发的证书，默认情况下是可以信任的，否则是不可信任的。
     * 添加 OkHttp中忽略SSL验证
     * javax.net.ssl.SSLHandshakeException:
     *     java.security.cert.CertPathValidatorException:
     *         Trust anchor for certification path not found.
     * @return
     */
    private SSLSocketFactory createSSLSocketFactory(){
        SSLSocketFactory sslSocketFactory = null;
        try{
            sCustomX509TrustManager = new CustomX509TrustManager();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null,new TrustManager[]{sCustomX509TrustManager},new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        }catch (Exception e){
            e.printStackTrace();
        }
        return sslSocketFactory;
    }

    public static class CustomX509TrustManager implements X509TrustManager{
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public static boolean isNetWorkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) SiteApplication.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null){
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()){
                if(networkInfo.getState() == NetworkInfo.State.CONNECTED){
                    return true;
                }
            }
        }
        return false;
    }

    public static int getNetWorkState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                return NETWORK_WIFI;//wifi
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                return NETWORK_MOBILE;//mobile
            }
        } else {
            return NETWORK_NONE;
        }
        return NETWORK_NONE;
    }

    private final Interceptor mLoginInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            long start = System.nanoTime();

            Log.i("AddLogInterceptor:", String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            long end = System.nanoTime();

            Response response = chain.proceed(request);

            return response;
        }
    };

    private final Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!isNetWorkAvailable()) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }

            Response originalResponse = chain.proceed(request);

            if (isNetWorkAvailable()) {
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
            } else {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_STALE_SEC)
                        .removeHeader("Pragma")
                        .build();
            }
        }
    };

    public void uploadTopPost(String url,String token, String pathName , final OkhttpClientRequest mClient){
        MultipartBody.Builder builder = new MultipartBody.Builder();

        builder.addFormDataPart("file", pathName,
                RequestBody.create(MediaType.parse("media/type"), new File(pathName)));

        RequestBody body = builder.build();

        Request request = new Request.Builder().url(url).addHeader("token",token).post(body).build();

        Call mCall = getOkHttpClient().newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mClient.responseFailure(e.getMessage().toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mClient.responseSuccess(response.body().string());
            }
        });
    }

    public void uploadAndAttendance(AttendacedofUpload attendacedofUpload,String url, String token, String pathName, final OkhttpClientRequest mClient){
        MultipartBody.Builder builder = new MultipartBody.Builder();

        builder.addFormDataPart("file", pathName,
                RequestBody.create(MediaType.parse("media/type"), new File(pathName)));
        builder.addFormDataPart("projectId",attendacedofUpload.getProjectId());
        builder.addFormDataPart("constructionId",attendacedofUpload.getConstructionId());
        builder.addFormDataPart("direction",attendacedofUpload.getDirection());
        builder.addFormDataPart("way",attendacedofUpload.getWay());
        builder.addFormDataPart("deviceType",attendacedofUpload.getDeviceType());
        builder.addFormDataPart("deviceSn",attendacedofUpload.getDeviceSn());

        RequestBody body = builder.build();

        Request request = new Request.Builder().url(url).addHeader("token",token).post(body).build();

        Call mCall = getOkHttpClient().newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mClient.responseFailure(e.getMessage().toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mClient.responseSuccess(response.body().string());
            }
        });
    }

    @NonNull
    public static String getCacheControl(){
        return isNetWorkAvailable() ? CACHE_CONTROL_AGE : CACHE_CONTROL_CACHE ;
    }
}
