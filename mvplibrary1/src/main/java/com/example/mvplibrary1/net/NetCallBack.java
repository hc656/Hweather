package com.example.mvplibrary1.net;


import android.util.Log;

import com.google.gson.Gson;
import com.example.mvplibrary1.base.BaseResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public abstract class NetCallBack<T> implements Callback<T> {//这里实现了retrofit2.Callback

    //访问成功回调
    @Override
    public void onResponse(Call<T> call, Response<T> response) {//数据返回
        if (response != null && response.body() != null && response.isSuccessful()) {
            if (response.code() == 404) {//404
                Log.e("Warn",response.message());
            }else if(response.code() == 500) {//500
                Log.e("Warn",response.message());
            } else {//无异常则返回数据
                onSuccess(call, response);
                Log.e("Warn","其他情况"+response.message());
            }
        } else {
            onFailed();
        }
    }

    //访问失败回调
    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFailed();
    }

    //数据返回
    public abstract void onSuccess(Call<T> call, Response<T> response);

    //失败异常
    public abstract void onFailed();


}

