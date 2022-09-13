package com.example.hweather.contract;


import android.content.Context;

import com.example.hweather.api.ApiService;
import com.example.hweather.bean.AirNowResponse;
import com.example.hweather.bean.BiYingImgResponse;
import com.example.hweather.bean.DailyResponse;
import com.example.hweather.bean.HourlyResponse;
import com.example.hweather.bean.LifestyleResponse;
import com.example.hweather.bean.NewSearchCityResponse;
import com.example.hweather.bean.NowResponse;
import com.example.mvplibrary1.base.BasePresenter;
import com.example.mvplibrary1.base.BaseView;
import com.example.mvplibrary1.net.NetCallBack;
import com.example.mvplibrary1.net.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Response;


public class WeatherContract {

    public static class WeatherPresenter extends BasePresenter<IWeatherView> {


        public void newSearchCity(String location) {
            ApiService service = ServiceGenerator.createService(ApiService.class, 2);//指明访问的地址
            service.newSearchCity(location).enqueue(new NetCallBack<NewSearchCityResponse>() {
                @Override
                public void onSuccess(Call<NewSearchCityResponse> call, Response<NewSearchCityResponse> response) {
                    if(getView() != null){
                        getView().getNewSearchCityResult(response);
                    }
                }

                @Override
                public void onFailed() {
                    if(getView() != null){
                        getView().getDataFailed();
                    }
                }
            });
        }



        public void nowWeather(String location){//这个3 表示使用新的V7API访问地址
            ApiService service = ServiceGenerator.createService(ApiService.class,0);
            service.nowWeather(location).enqueue(new NetCallBack<NowResponse>() {
                @Override
                public void onSuccess(Call<NowResponse> call, Response<NowResponse> response) {
                    if(getView() != null){
                        getView().getNowResult(response);
                    }
                }

                @Override
                public void onFailed() {
                    if(getView() != null){
                        getView().getDataFailed();
                    }
                }
            });
        }


        public void dailyWeather(String location){//这个3 表示使用新的V7API访问地址
            ApiService service = ServiceGenerator.createService(ApiService.class,0);
            service.dailyWeather("7d",location).enqueue(new NetCallBack<DailyResponse>() {
                @Override
                public void onSuccess(Call<DailyResponse> call, Response<DailyResponse> response) {
                    if(getView() != null){
                        getView().getDailyResult(response);
                    }
                }

                @Override
                public void onFailed() {
                    if(getView() != null){
                        getView().getDataFailed();
                    }
                }
            });
        }


        public void hourlyWeather(String location){
            ApiService service = ServiceGenerator.createService(ApiService.class,0);
            service.hourlyWeather(location).enqueue(new NetCallBack<HourlyResponse>() {
                @Override
                public void onSuccess(Call<HourlyResponse> call, Response<HourlyResponse> response) {
                    if(getView() != null){
                        getView().getHourlyResult(response);
                    }
                }

                @Override
                public void onFailed() {
                    if(getView() != null){
                        getView().getDataFailed();
                    }
                }
            });
        }


        public void airNowWeather(String location){
            ApiService service = ServiceGenerator.createService(ApiService.class,0);
            service.airNowWeather(location).enqueue(new NetCallBack<AirNowResponse>() {
                @Override
                public void onSuccess(Call<AirNowResponse> call, Response<AirNowResponse> response) {
                    if(getView() != null){
                        getView().getAirNowResult(response);
                    }
                }

                @Override
                public void onFailed() {
                    if(getView() != null){
                        getView().getDataFailed();
                    }
                }
            });
        }


        public void lifestyle(String location){
            ApiService service = ServiceGenerator.createService(ApiService.class,0);
            service.Lifestyle("1,2,3,5,6,8,9,10",location).enqueue(new NetCallBack<LifestyleResponse>() {
                @Override
                public void onSuccess(Call<LifestyleResponse> call, Response<LifestyleResponse> response) {
                    if(getView() != null){
                        getView().getLifestyleResult(response);
                    }
                }

                @Override
                public void onFailed() {
                    if(getView() != null){
                        getView().getDataFailed();
                    }
                }
            });
        }



        public void biying(final Context context){
            ApiService service = ServiceGenerator.createService(ApiService.class,1);
            service.biying().enqueue(new NetCallBack<BiYingImgResponse>() {
                @Override
                public void onSuccess(Call<BiYingImgResponse> call, Response<BiYingImgResponse> response) {
                    if(getView() != null){
                        getView().getBiYingResult(response);
                    }
                }

                @Override
                public void onFailed() {
                    if(getView() != null){
                        getView().getDataFailed();
                    }
                }
            });
        }
    }

    public interface IWeatherView extends BaseView {

        //搜索城市返回城市id  通过id才能查下面的数据,否则会提示400  V7
        void getNewSearchCityResult(Response<NewSearchCityResponse> response);
        //实况天气
        void getNowResult(Response<NowResponse> response);
        //天气预报  7天
        void getDailyResult(Response<DailyResponse> response);
        //逐小时天气预报
        void getHourlyResult(Response<HourlyResponse> response);
        //空气质量
        void getAirNowResult(Response<AirNowResponse> response);
        //生活指数
        void getLifestyleResult(Response<LifestyleResponse> response);
        //获取每日一图返回
        void getBiYingResult(Response<BiYingImgResponse> response);
        void getDataFailed();
    }
}

