package com.example.hweather.api;

import com.example.hweather.bean.AirNowResponse;
import com.example.hweather.bean.BiYingImgResponse;
import com.example.hweather.bean.DailyResponse;
import com.example.hweather.bean.HourlyResponse;
import com.example.hweather.bean.LifestyleResponse;
import com.example.hweather.bean.NewSearchCityResponse;
import com.example.hweather.bean.NowResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {


    @GET("/HPImageArchive.aspx?format=js&idx=0&n=1")
    Call<BiYingImgResponse> biying();


    @GET("/v7/weather/now?key=37b820c0417a47e590b60827d38823f9")
    Call<NowResponse> nowWeather(@Query("location") String location);


    @GET("/v7/weather/{type}?key=37b820c0417a47e590b60827d38823f9")
    Call<DailyResponse> dailyWeather(@Path("type") String type,@Query("location") String location);


    @GET("/v7/weather/24h?key=37b820c0417a47e590b60827d38823f9")
    Call<HourlyResponse> hourlyWeather(@Query("location") String location);


    @GET("/v7/air/now?key=37b820c0417a47e590b60827d38823f9")
    Call<AirNowResponse> airNowWeather(@Query("location") String location);


    @GET("/v7/indices/1d?key=37b820c0417a47e590b60827d38823f9")
    Call<LifestyleResponse> Lifestyle(@Query("type") String type,
                                      @Query("location") String location);


    @GET("/v2/city/lookup?key=37b820c0417a47e590b60827d38823f9&range=cn")
    Call<NewSearchCityResponse> newSearchCity(@Query("location") String location);
}

