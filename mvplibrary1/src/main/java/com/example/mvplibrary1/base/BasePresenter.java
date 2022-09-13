package com.example.mvplibrary1.base;


import com.example.mvplibrary1.base.BaseView;

import java.lang.ref.WeakReference;


public class BasePresenter<V extends BaseView> {
    private WeakReference<V> mWeakReference;


    public void attach(V v){
        mWeakReference=new WeakReference<V>(v);
    }


    public void detach(V v){
        if (mWeakReference!=null){
            mWeakReference.clear();
            mWeakReference=null;
        }
    }


    public V getView(){
        if (mWeakReference!=null){
            return mWeakReference.get();
        }
        return null;
    }

}
