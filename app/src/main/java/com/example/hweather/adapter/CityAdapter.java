package com.example.hweather.adapter;

import androidx.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.hweather.R;
import com.example.hweather.bean.CityResponse;
import java.util.List;

public class CityAdapter extends BaseQuickAdapter<CityResponse.CityBean, BaseViewHolder> {
    public CityAdapter(int layoutResId, @Nullable List<CityResponse.CityBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CityResponse.CityBean item) {
        helper.setText(R.id.tv_city,item.getName());//市名称
        helper.addOnClickListener(R.id.item_city);//点击事件  点击进入区/县列表
    }
}

