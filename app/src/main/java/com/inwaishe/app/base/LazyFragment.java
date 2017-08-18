package com.inwaishe.app.base;

import android.arch.lifecycle.LifecycleFragment;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by WangJing on 2017/8/7.
 * Fragment 懒加载基类
 * LifeCycle
 */

public abstract class LazyFragment extends LifecycleFragment{

    private View mRootView;
    protected boolean isVisible;//fragment 是否可见
    protected boolean isPrepared;//是否View初始化完成
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getLayoutResId(),container,false);
        return mRootView;
    }
    @LayoutRes
    protected abstract int getLayoutResId();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        finishCreateView(savedInstanceState);
    }
    /**
     * 初始化views
     *
     * @param state
     */
    public abstract void finishCreateView(Bundle state);

    /**
     * fragment显示时才加载数据
     */
    protected void onVisible() {
    }

    /**
     * fragment懒加载方法
     */
    protected void lazyLoad() {

    }

    /**
     * fragment隐藏
     */
    protected void onInvisible() {
    }

    /**
     * 加载数据
     */
    protected void loadData() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            //可见
            isVisible = true;
            onVisible();
        }else{
            //不可见
            isVisible = false;
            onInvisible();
        }
    }
}
