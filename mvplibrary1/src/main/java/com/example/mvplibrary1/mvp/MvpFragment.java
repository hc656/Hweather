package com.example.mvplibrary1.mvp;

import android.os.Bundle;
import com.example.mvplibrary1.base.BaseFragment;
import com.example.mvplibrary1.base.BasePresenter;
import com.example.mvplibrary1.base.BaseView;


public abstract class MvpFragment<P extends BasePresenter> extends BaseFragment {
    protected P mPresent;
    @Override
    public void initBeforeView(Bundle savedInstanceState) {
        mPresent=createPresent();
        mPresent.attach((BaseView) this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mPresent!=null){
            mPresent.detach((BaseView) this);
        }
    }

    protected abstract P createPresent();

}

