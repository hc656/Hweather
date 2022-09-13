package com.example.mvplibrary1.net;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    //https://devapi.qweather.com/v7/weather/now?key=37b820c0417a47e590b60827d38823f9&location=101010100
    public static String BASE_URL = null;//地址

    private static String urlType(int type){
        switch (type){
            case 0://和风天气
                BASE_URL = "https://devapi.qweather.com";
                break;
            case 1://必应每日一图
                BASE_URL = "https://cn.bing.com";
                break;
            case 2:
                BASE_URL = "https://geoapi.qweather.com";
                break;
        }
        return BASE_URL;
    }

    //创建服务  参数就是API服务
    public static <T> T createService(Class<T> serviceClass,int type) {

        //创建OkHttpClient构建器对象
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

        //设置请求超时的时间，这里是10秒
        okHttpClientBuilder.connectTimeout(10000, TimeUnit.MILLISECONDS);

        //消息拦截器  因为有时候接口不同在排错的时候 需要先从接口的响应中做分析。利用了消息拦截器可以清楚的看到接口返回的所有内容
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

        //setlevel用来设置日志打印的级别，共包括了四个级别：NONE,BASIC,HEADER,BODY
        //BASEIC:请求/响应行
        //HEADER:请求/响应行 + 头
        //BODY:请求/响应航 + 头 + 体
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //为OkHttp添加消息拦截器
        okHttpClientBuilder.addInterceptor(httpLoggingInterceptor);

        //在Retrofit中设置httpclient
        Retrofit retrofit = new Retrofit.Builder().baseUrl(urlType(type))
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClientBuilder.build())
                .build();
        return retrofit.create(serviceClass);
    }

}

