package com.example.mvplibrary1.mvp;

import android.os.Bundle;
import com.example.mvplibrary1.base.BaseActivity;
import com.example.mvplibrary1.base.BasePresenter;
import com.example.mvplibrary1.base.BaseView;


public abstract class MvpActivity<P extends BasePresenter> extends BaseActivity {
    protected P mPresent;

    @Override
    public void initBeforeView(Bundle savedInstanceState) {
        mPresent=createPresent();
        mPresent.attach((BaseView) this);
    }

    protected abstract P createPresent();

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresent.detach((BaseView) this);
    }
}

