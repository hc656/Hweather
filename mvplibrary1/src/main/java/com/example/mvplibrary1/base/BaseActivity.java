package com.example.mvplibrary1.base;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mvplibrary1.BaseApplication;
import com.example.mvplibrary1.R;
import com.example.mvplibrary1.kit.KnifeKit;
import butterknife.Unbinder;


public abstract class BaseActivity extends AppCompatActivity implements UiCallBack {

    protected Activity context;
    private Unbinder unbinder;
    private Dialog mDialog;//加载弹窗
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBeforeView(savedInstanceState);
        this.context = this;
        //添加继承这个BaseActivity的Activity
        BaseApplication.getActivityManager().addActivity(this);
        if (getLayoutId() > 0) {
            setContentView(getLayoutId());
            unbinder = KnifeKit.bind(this);
        }
        initData(savedInstanceState);
    }

    @Override
    public void initBeforeView(Bundle savedInstanceState) {
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //弹窗出现
    public void showLoadingDialog(){
        if (mDialog == null) {
            mDialog = new Dialog(context, R.style.loading_dialog);
        }
        mDialog.setContentView(R.layout.dialog_loading);
        mDialog.setCancelable(false);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.show();
    }
    //弹窗消失
    public void dismissLoadingDialog(){
        mDialog.dismiss();
        if (mDialog != null) {
        }
        mDialog = null;
    }
}

