package com.yang.network;

public interface HttpRequestListener {

    void onResponse(String result);//成功

    void onErrorResponse(String error);//失败
}

