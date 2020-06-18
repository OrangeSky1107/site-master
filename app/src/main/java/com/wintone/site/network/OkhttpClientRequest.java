package com.wintone.site.network;

/**
 * create by ths on 2020/6/18
 */
public interface OkhttpClientRequest {

    void responseFailure(String errorMessage);

    void responseSuccess(String successMessage);
}
