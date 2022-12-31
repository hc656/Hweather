package com.example.hweather.contract;

import android.content.Context;
import android.util.Log;

import com.example.hweather.api.ApiService;
import com.example.hweather.bean.NewSearchCityResponse;
import com.example.mvplibrary1.base.BasePresenter;
import com.example.mvplibrary1.base.BaseView;
import com.example.mvplibrary1.net.NetCallBack;
import com.example.mvplibrary1.net.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Response;

public class SearchCityContract {

    public static class SearchCityPresenter extends BasePresenter<ISearchCityView> {

        /**
         * 搜索城市
         * @param context
         * @param location
         */
        public void searchCity(final Context context, String location) {
            ApiService service = ServiceGenerator.createService(ApiService.class, 2);//指明访问的地址
            service.newSearchCity(location).enqueue(new NetCallBack<NewSearchCityResponse>() {
                @Override
                public void onSuccess(Call<NewSearchCityResponse> call, Response<NewSearchCityResponse> response) {
                    if(getView() != null){
                        Log.d("hmc", "onSuccess: "+response.body().getLocation().get(0).getName());
                        getView().getSearchCityResult(response);
                        Log.d("hmc", "onSuccess: "+response.body().getLocation().get(0).getName());
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

    public interface ISearchCityView extends BaseView {
        //查询城市返回数据
        void getSearchCityResult(Response<NewSearchCityResponse> response);
        //错误返回
        void getDataFailed();
    }
}

