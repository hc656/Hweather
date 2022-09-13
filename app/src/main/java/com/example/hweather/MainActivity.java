package com.example.hweather;


import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.hweather.adapter.AreaAdapter;
import com.example.hweather.adapter.CityAdapter;
import com.example.hweather.adapter.DailyAdapter;
import com.example.hweather.adapter.HourlyAdapter;
import com.example.hweather.adapter.ProvinceAdapter;
import com.example.hweather.bean.AirNowResponse;
import com.example.hweather.bean.BiYingImgResponse;
import com.example.hweather.bean.CityResponse;
import com.example.hweather.bean.DailyResponse;
import com.example.hweather.bean.HourlyResponse;
import com.example.hweather.bean.LifestyleResponse;
import com.example.hweather.bean.NewSearchCityResponse;
import com.example.hweather.bean.NowResponse;
import com.example.hweather.contract.WeatherContract;
import com.example.hweather.utils.ToastUtils;
import com.example.hweather.utils.WeatherUtil;
import com.example.mvplibrary1.mvp.MvpActivity;
import com.example.mvplibrary1.utils.LiWindow;
import com.example.mvplibrary1.view.RoundProgressBar;
import com.example.mvplibrary1.view.WhiteWindmills;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tbruyelle.rxpermissions2.RxPermissions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Response;

import static com.example.mvplibrary1.utils.RecyclerViewAnimation.runLayoutAnimationRight;

public class MainActivity extends MvpActivity<WeatherContract.WeatherPresenter> implements WeatherContract.IWeatherView {
    @BindView(R.id.tv_info)
    TextView tvInfo;//天气状况
    @BindView(R.id.tv_temperature)
    TextView tvTemperature;//温度
    @BindView(R.id.tv_low_height)
    TextView tvLowHeight;//最高温和最低温
    @BindView(R.id.tv_city)
    TextView tvCity;//城市
    @BindView(R.id.tv_old_time)
    TextView tvOldTime;//最近更新时间
    @BindView(R.id.rv)
    RecyclerView rv;//天气预报显示列表
    @BindView(R.id.tv_comf)
    TextView tvComf;//舒适度
    @BindView(R.id.tv_trav)
    TextView tvTrav;//旅游指数
    @BindView(R.id.tv_sport)
    TextView tvSport;//运动指数
    @BindView(R.id.tv_cw)
    TextView tvCw;//洗车指数
    @BindView(R.id.tv_air)
    TextView tvAir;//空气指数
    @BindView(R.id.tv_drsg)
    TextView tvDrsg;//穿衣指数
    @BindView(R.id.tv_flu)
    TextView tvFlu;//感冒指数
    @BindView(R.id.ww_big)
    WhiteWindmills wwBig;//大风车
    @BindView(R.id.ww_small)
    WhiteWindmills wwSmall;//小风车
    @BindView(R.id.tv_wind_direction)
    TextView tvWindDirection;//风向
    @BindView(R.id.tv_wind_power)
    TextView tvWindPower;//风力
    @BindView(R.id.iv_city_select)
    ImageView ivCitySelect;//城市图标ID
    @BindView(R.id.bg)
    LinearLayout bg;//背景图
    @BindView(R.id.refresh)
    SmartRefreshLayout refresh;//刷新布局
    @BindView(R.id.iv_location)
    ImageView ivLocation;//定位图标
    @BindView(R.id.rv_hourly)
    RecyclerView rvHourly;//逐小时天气显示列表
    @BindView(R.id.rpb_aqi)
    RoundProgressBar rpbAqi;//污染指数圆环
    @BindView(R.id.tv_pm10)
    TextView tvPm10;//PM10
    @BindView(R.id.tv_pm25)
    TextView tvPm25;//PM2.5
    @BindView(R.id.tv_no2)
    TextView tvNo2;//二氧化氮
    @BindView(R.id.tv_so2)
    TextView tvSo2;//二氧化硫
    @BindView(R.id.tv_o3)
    TextView tvO3;//臭氧
    @BindView(R.id.tv_co)
    TextView tvCo;//一氧化碳
    private RxPermissions rxPermissions;//权限请求框架
    //定位器
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private static final String TAG = "hmc";
    private String district;
    private String city;//市 国控站点数据 用于请求空气质量
    private boolean flag = true;//图标显示标识,true显示，false不显示,只有定位的时候才为true,切换城市和常用城市都为false

    private List<String> list;//字符串列表
    private List<CityResponse> provinceList;//省列表数据
    private List<CityResponse.CityBean> citylist;//市列表数据
    private List<CityResponse.CityBean.AreaBean> arealist;//区/县列表数据
    ProvinceAdapter provinceAdapter;//省数据适配器
    CityAdapter cityAdapter;//市数据适配器
    AreaAdapter areaAdapter;//县/区数据适配器
    String provinceTitle;//标题
    LiWindow liWindow;//自定义弹窗
    private String locationId = null;//城市id，用于查询城市数据  V7版本 中 才有
    //V7 版本
    List<DailyResponse.DailyBean> dailyListV7 = new ArrayList<>();//天气预报数据列表
    DailyAdapter mAdapterDailyV7;//天气预报适配器
    List<HourlyResponse.HourlyBean> hourlyListV7 = new ArrayList<>();//逐小时天气预报数据列表
    HourlyAdapter mAdapterHourlyV7;//逐小时预报适配器


    private void initList() {

        //天气预报  7天
        mAdapterDailyV7 = new DailyAdapter(R.layout.item_weather_forecast_list, dailyListV7);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mAdapterDailyV7);
        //逐小时天气预报  24小时
        mAdapterHourlyV7 = new HourlyAdapter(R.layout.item_weather_hourly_list, hourlyListV7);
        LinearLayoutManager managerHourly = new LinearLayoutManager(context);
        managerHourly.setOrientation(RecyclerView.HORIZONTAL);//设置列表为横向
        rvHourly.setLayoutManager(managerHourly);
        rvHourly.setAdapter(mAdapterHourlyV7);
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        initList();
        LocationClient.setAgreePrivacy(true);//不加会因为隐私政策无法使用app
        rxPermissions = new RxPermissions(this);//实例化这个权限请求框架，否则会报错
        permissionVersion();//权限判断
    }


    //绑定布局文件
    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    //绑定Presenter
    @Override
    protected WeatherContract.WeatherPresenter createPresent() {
        return new WeatherContract.WeatherPresenter();
    }

    //权限判断
    private void permissionVersion() {
        if (Build.VERSION.SDK_INT >= 23) {//6.0或6.0以上
            //动态权限申请
            permissionsRequest();
        } else {//6.0以下
            ToastUtils.showShortToast(this, "你的版本在Android6.0以下，不需要动态申请权限。");
            startLocation();
        }
    }

    //动态权限申请
    private void permissionsRequest() {
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {//申请成功
                        startLocation();
                    } else {//申请失败
                        ToastUtils.showShortToast(this, "权限未开启");
                    }
                });
    }

    //定位
    private void startLocation() {
        //声明LocationClient类
        try {
            mLocationClient = new LocationClient(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();


        option.setIsNeedAddress(true);
        option.setNeedNewVersionRgc(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();

    }


    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //获取区/县
            district = location.getDistrict();
            city = location.getCity();
            Log.d(TAG, "onReceiveLocation: "+district);
            Toast.makeText(MainActivity.this,district,Toast.LENGTH_LONG).show();
            //在数据请求前放在在加载等待弹窗,返回结果后关闭
            showLoadingDialog();
            //获取区域id
            mPresent.newSearchCity(district);

            //下拉刷新
            refresh.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    mPresent.newSearchCity(district);
                }
            });
        }
    }


    @Override
    public void getNewSearchCityResult(Response<NewSearchCityResponse> response) {
        refresh.finishRefresh();//关闭刷新
        dismissLoadingDialog();//关闭弹窗
        if (mLocationClient != null) {
            mLocationClient.stop();//数据返回后关闭定位
        }
        if (response.body() != null) {
            if (response.body().getLocation() != null && response.body().getLocation().size() > 0) {
                tvCity.setText(response.body().getLocation().get(0).getName());//城市
                locationId = response.body().getLocation().get(0).getId();//城市Id
                Log.d("OkHttp", "getNewSearchCityResult: "+locationId);
                showLoadingDialog();
                mPresent.nowWeather(locationId);//查询实况天气
                mPresent.dailyWeather(locationId);//查询天气预报
                mPresent.hourlyWeather(locationId);//查询逐小时天气预报
                mPresent.airNowWeather(locationId);//空气质量
                mPresent.lifestyle(locationId);//生活指数
            } else {
                ToastUtils.showShortToast(context, "数据为空");
            }
        } else {
            tvCity.setText("查询城市失败");
        }
    }


    @Override
    public void getNowResult(Response<NowResponse> response) {
        dismissLoadingDialog();
        if (response.code() == 200) {//200则成功返回数据

            NowResponse data = response.body();
            if (data != null) {
                tvTemperature.setText(data.getNow().getTemp());//温度
                if (flag) {
                    ivLocation.setVisibility(View.VISIBLE);//显示定位图标
                } else {
                    ivLocation.setVisibility(View.GONE);//显示定位图标
                }
                tvInfo.setText(data.getNow().getText());//天气状况
                //"2022-08-27T14:35+08:00"
                String time = data.getUpdateTime().substring(11,16);
                tvOldTime.setText("上次更新时间：" + WeatherUtil.showTimeInfo(time) + time);
                tvWindDirection.setText("风向     " + data.getNow().getWindDir());//风向
                tvWindPower.setText("风力     " + data.getNow().getWindScale() + "级");//风力
                wwBig.startRotate();//大风车开始转动
                wwSmall.startRotate();//小风车开始转动
            } else {
                ToastUtils.showShortToast(context, "暂无实况天气数据");
            }
        }
    }


    @Override
    public void getDailyResult(Response<DailyResponse> response) {
        if (response.code() == 200) {

            List<DailyResponse.DailyBean> data = response.body().getDaily();
            if (data != null && data.size() > 0) {//判空处理
                tvLowHeight.setText(data.get(0).getTempMin() + " / " + data.get(0).getTempMax() + "℃");
                dailyListV7.clear();//添加数据之前先清除
                dailyListV7.addAll(data);//添加数据
                mAdapterDailyV7.notifyDataSetChanged();//刷新列表
            } else {
                ToastUtils.showShortToast(context, "天气预报数据为空");
            }
        }
    }


    @Override
    public void getHourlyResult(Response<HourlyResponse> response) {
        if(response.code() == 200){
            List<HourlyResponse.HourlyBean> data = response.body().getHourly();
            if(data != null && data.size()> 0){
                hourlyListV7.clear();
                hourlyListV7.addAll(data);
                mAdapterHourlyV7.notifyDataSetChanged();
            }else {
                ToastUtils.showShortToast(context, "逐小时预报数据为空");
            }
        }
    }


    @Override
    public void getAirNowResult(Response<AirNowResponse> response) {
        if(response.code() == 200){
            AirNowResponse.NowBean data = response.body().getNow();
            if(response.body().getNow() !=null){
                rpbAqi.setMaxProgress(300);//最大进度，用于计算
                rpbAqi.setMinText("0");//设置显示最小值
                rpbAqi.setMinTextSize(32f);
                rpbAqi.setMaxText("300");//设置显示最大值
                rpbAqi.setMaxTextSize(32f);
                rpbAqi.setProgress(Float.valueOf(data.getAqi()));//当前进度
                rpbAqi.setArcBgColor(getResources().getColor(R.color.arc_bg_color));//圆弧的颜色
                rpbAqi.setProgressColor(getResources().getColor(R.color.arc_progress_color));//进度圆弧的颜色
                rpbAqi.setFirstText(data.getCategory());//空气质量描述 取值范围：优，良，轻度污染，中度污染，重度污染，严重污染
                rpbAqi.setFirstTextSize(44f);//第一行文本的字体大小
                rpbAqi.setSecondText(data.getAqi());//空气质量值
                rpbAqi.setSecondTextSize(64f);//第二行文本的字体大小
                rpbAqi.setMinText("0");
                rpbAqi.setMinTextColor(getResources().getColor(R.color.arc_progress_color));

                tvPm10.setText(data.getPm10());//PM10
                tvPm25.setText(data.getPm2p5());//PM2.5
                tvNo2.setText(data.getNo2());//二氧化氮
                tvSo2.setText(data.getSo2());//二氧化硫
                tvO3.setText(data.getO3());//臭氧
                tvCo.setText(data.getCo());//一氧化碳
            }else {
                ToastUtils.showShortToast(context,"空气质量数据为空");
            }
        }
    }


    @Override
    public void getLifestyleResult(Response<LifestyleResponse> response) {
        if(response.code() == 200){
            List<LifestyleResponse.DailyBean> data = response.body().getDaily();
            for (int i = 0; i < data.size(); i++) {
                switch (data.get(i).getType()) {
                    case "8":
                        tvComf.setText("舒适度：" + data.get(i).getText());
                        break;
                    case "3":
                        tvDrsg.setText("穿衣指数：" + data.get(i).getText());
                        break;
                    case "9":
                        tvFlu.setText("感冒指数：" + data.get(i).getText());
                        break;
                    case "1":
                        tvSport.setText("运动指数：" + data.get(i).getText());
                        break;
                    case "6":
                        tvTrav.setText("旅游指数：" + data.get(i).getText());
                        break;
                    case "2":
                        tvCw.setText("洗车指数：" + data.get(i).getText());
                        break;
                    case "10":
                        tvAir.setText("空气指数：" + data.get(i).getText());
                        break;
                }
            }
        }
    }

    //获取必应每日一图返回
    @Override
    public void getBiYingResult(Response<BiYingImgResponse> response) {
        dismissLoadingDialog();
        if (response.body().getImages() != null) {
            //得到的图片地址是没有前缀的，所以加上前缀否则显示不出来
            String imgUrl = "http://cn.bing.com" + response.body().getImages().get(0).getUrl();
            Glide.with(context)
                    .asBitmap()
                    .load(imgUrl)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                            Drawable drawable = new BitmapDrawable(context.getResources(), resource);
                            bg.setBackground(drawable);
                        }
                    });
        } else {
            ToastUtils.showShortToast(context, "数据为空");
        }
    }

    //数据请求失败返回
    @Override
    public void getDataFailed() {
        refresh.finishRefresh();//关闭刷新
        dismissLoadingDialog();//关闭弹窗
        ToastUtils.showShortToast(context,"网络异常");//这里的context是框架中封装好的，等同于this
    }




    @Override
    public void onDestroy() {
        wwBig.stop();//停止大风车
        wwSmall.stop();//停止小风车
        super.onDestroy();
    }

    private void showCityWindow() {
        provinceList = new ArrayList<>();
        citylist = new ArrayList<>();
        arealist = new ArrayList<>();
        list = new ArrayList<>();
        liWindow = new LiWindow(context);
        final View view = LayoutInflater.from(context).inflate(R.layout.window_city_list, null);
        ImageView areaBack = (ImageView) view.findViewById(R.id.iv_back_area);
        ImageView cityBack = (ImageView) view.findViewById(R.id.iv_back_city);
        TextView windowTitle = (TextView) view.findViewById(R.id.tv_title);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        liWindow.showRightPopupWindow(view);
        initCityData(recyclerView,areaBack,cityBack,windowTitle);//加载城市列表数据

    }

    //点击事件
    @OnClick(R.id.iv_city_select)
    public void onViewClicked() {//显示城市弹窗
        showCityWindow();
    }


    private void initCityData(RecyclerView recyclerView,ImageView areaBack,ImageView cityBack,TextView windowTitle) {
        //初始化省数据 读取省数据并显示到列表中
        try {
            InputStream inputStream = getResources().getAssets().open("City.txt");//读取数据
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String lines = bufferedReader.readLine();
            while (lines != null) {
                stringBuffer.append(lines);
                lines = bufferedReader.readLine();
            }
            final JSONArray Data = new JSONArray(stringBuffer.toString());
            //循环这个文件数组、获取数组中每个省对象的名字
            for (int i = 0; i < Data.length(); i++) {
                JSONObject provinceJsonObject = Data.getJSONObject(i);
                String provinceName = provinceJsonObject.getString("name");
                CityResponse response = new CityResponse();
                response.setName(provinceName);
                provinceList.add(response);
            }

            //定义省份显示适配器
            provinceAdapter = new ProvinceAdapter(R.layout.item_city_list, provinceList);
            LinearLayoutManager manager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(provinceAdapter);
            provinceAdapter.notifyDataSetChanged();
            runLayoutAnimationRight(recyclerView);//动画展示
            provinceAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    try {
                        //返回上一级数据
                        cityBack.setVisibility(View.VISIBLE);
                        cityBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                recyclerView.setAdapter(provinceAdapter);
                                provinceAdapter.notifyDataSetChanged();
                                cityBack.setVisibility(View.GONE);
                                windowTitle.setText("中国");
                            }
                        });

                        //根据当前位置的省份所在的数组位置、获取城市的数组
                        JSONObject provinceObject = Data.getJSONObject(position);
                        windowTitle.setText(provinceList.get(position).getName());
                        provinceTitle = provinceList.get(position).getName();
                        final JSONArray cityArray = provinceObject.getJSONArray("city");

                        //更新列表数据
                        if (citylist != null) {
                            citylist.clear();
                        }

                        for (int i = 0; i < cityArray.length(); i++) {
                            JSONObject cityObj = cityArray.getJSONObject(i);
                            String cityName = cityObj.getString("name");
                            CityResponse.CityBean response = new CityResponse.CityBean();
                            response.setName(cityName);
                            citylist.add(response);
                        }

                        cityAdapter = new CityAdapter(R.layout.item_city_list, citylist);
                        LinearLayoutManager manager1 = new LinearLayoutManager(context);
                        recyclerView.setLayoutManager(manager1);
                        recyclerView.setAdapter(cityAdapter);
                        cityAdapter.notifyDataSetChanged();
                        runLayoutAnimationRight(recyclerView);

                        cityAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                            @Override
                            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                                try {
                                    //返回上一级数据
                                    areaBack.setVisibility(View.VISIBLE);
                                    areaBack.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            recyclerView.setAdapter(cityAdapter);
                                            cityAdapter.notifyDataSetChanged();
                                            areaBack.setVisibility(View.GONE);
                                            windowTitle.setText(provinceTitle);
                                            arealist.clear();
                                        }
                                    });
                                    //获取区/县的上级市，用于请求空气质量数据API接口
                                    city = citylist.get(position).getName();
                                    //根据当前城市数组位置 获取地区数据
                                    windowTitle.setText(citylist.get(position).getName());
                                    JSONObject cityJsonObj = cityArray.getJSONObject(position);
                                    JSONArray areaJsonArray = cityJsonObj.getJSONArray("area");
                                    if (arealist != null) {
                                        arealist.clear();
                                    }
                                    if(list != null){
                                        list.clear();
                                    }
                                    for (int i = 0; i < areaJsonArray.length(); i++) {
                                        list.add(areaJsonArray.getString(i));
                                    }
                                    Log.i("list", list.toString());
                                    for (int j = 0; j < list.size(); j++) {
                                        CityResponse.CityBean.AreaBean response = new CityResponse.CityBean.AreaBean();
                                        response.setName(list.get(j).toString());
                                        arealist.add(response);
                                    }
                                    areaAdapter = new AreaAdapter(R.layout.item_city_list, arealist);
                                    LinearLayoutManager manager2 = new LinearLayoutManager(context);

                                    recyclerView.setLayoutManager(manager2);
                                    recyclerView.setAdapter(areaAdapter);
                                    areaAdapter.notifyDataSetChanged();
                                    runLayoutAnimationRight(recyclerView);

                                    areaAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                                        @Override
                                        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                                            showLoadingDialog();
                                            district = arealist.get(position).getName();
                                            mPresent.newSearchCity(district);
                                            flag = false;//切换城市后不属于定位，因此位false
                                            liWindow.closePopupWindow();

                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}


