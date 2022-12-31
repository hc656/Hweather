package com.example.hweather.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.hweather.adapter.SearchCityAdapter;
import com.example.hweather.bean.NewSearchCityResponse;
import com.example.hweather.contract.SearchCityContract;
import com.example.hweather.eventbus.SearchCityEvent;
import com.example.hweather.utils.ToastUtils;
import com.example.mvplibrary1.mvp.MvpActivity;
import com.example.hweather.R;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import static  com.example.mvplibrary1.utils.RecyclerViewAnimation.runLayoutAnimationRight;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Response;


public class SearchCityActivity extends MvpActivity<SearchCityContract.SearchCityPresenter> implements SearchCityContract.ISearchCityView {
    @BindView(R.id.edit_query)
    AutoCompleteTextView editQuery;
    @BindView(R.id.iv_clear_search)
    ImageView ivClearSearch;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv)
    RecyclerView rv;
    List<NewSearchCityResponse.LocationBean> mList = new ArrayList<>();//数据源
    SearchCityAdapter mAdapter;//适配器
    private static final int FAST_CLICK_DELAY_TIME = 500;
    private static long lastClickTime;


    @Override
    public void initData(Bundle savedInstanceState) {
      //  Back(toolbar);
        initResultList();//初始化列表
        initEdit();//初始化输入框
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
                if(!isFastClick()) {
                    context.finish();
                }
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search_city;
    }


    @Override
    protected SearchCityContract.SearchCityPresenter createPresent() {
        return new SearchCityContract.SearchCityPresenter();
    }

    //搜索城市返回的结果数据
    @Override
    public void getSearchCityResult(Response<NewSearchCityResponse> response) {
        dismissLoadingDialog();

        if (response.isSuccessful()) {
            if (response.body().getLocation().size() > 0) {
                mList.clear();
                mList.addAll(response.body().getLocation());
                mAdapter.notifyDataSetChanged();
                runLayoutAnimationRight(rv);
            } else {
                Toast.makeText(context,"很抱歉，未找到相应的城市",Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(context,"很抱歉，发送消息异常",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getDataFailed() {
        dismissLoadingDialog();//关闭弹窗
        Toast.makeText(context,"网络异常",Toast.LENGTH_SHORT).show();
    }

    //初始化列表
    private void initResultList() {
        mAdapter = new SearchCityAdapter(R.layout.item_search_city_list, mList);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                //发送消息
                EventBus.getDefault().post(new SearchCityEvent(mList.get(position).getName(),
                        mList.get(position).getAdm2()));

                finish();
            }
        });
    }

    //初始化输入框
    private void initEdit() {
        editQuery.addTextChangedListener(textWatcher);//添加输入监听
        //监听软件键盘搜索按钮
        editQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String location = editQuery.getText().toString();
                    if (!TextUtils.isEmpty(location)) {
                        showLoadingDialog();
                        mPresent.searchCity(context, location);
                    } else {
                        ToastUtils.showShortToast(context, "请输入搜索关键词");
                    }
                }
                return false;
            }
        });
    }
    //输入监听
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals("")) {//输入后，显示清除按钮
                ivClearSearch.setVisibility(View.VISIBLE);
            } else {//隐藏按钮
                ivClearSearch.setVisibility(View.GONE);
            }
        }
    };

    //点击事件
    @OnClick(R.id.iv_clear_search)
    public void onViewClicked() {//清除输入框的内容
        ivClearSearch.setVisibility(View.GONE);//清除内容隐藏清除按钮
        editQuery.setText("");
    }

    // 两次点击间隔不能少于500ms
    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= FAST_CLICK_DELAY_TIME ) {
            flag = false;
        }
        lastClickTime = currentClickTime;

        return flag;
    }
}