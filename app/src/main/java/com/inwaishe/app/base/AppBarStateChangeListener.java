package com.inwaishe.app.base;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.Log;

import com.inwaishe.app.common.AppUtils;


/**
 * Created by Administrator on 2017/8/11 0011.
 * 折叠监听器
 */

public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener{
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    public AppBarStateChangeListener(CollapsingToolbarLayout mCollapsingToolbarLayout) {
        this.mCollapsingToolbarLayout = mCollapsingToolbarLayout;
    }

    public enum State {
        EXPANDED,
        COLLAPSED,
        IDLE,
        SHOWNSCRIM,//遮罩层显示
        HIDESCRIM//遮罩层隐藏
    }

    private State mCurrentState = State.IDLE;
    private State mCurrentScrimState = State.HIDESCRIM;

    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {

        if(mCollapsingToolbarLayout.getHeight() + i < mCollapsingToolbarLayout.getScrimVisibleHeightTrigger()){
            //分析源代码得出的判断条件
            if(mCurrentScrimState != State.HIDESCRIM){
                mCurrentScrimState = State.HIDESCRIM;
                onStateChanged(appBarLayout, State.HIDESCRIM);
            }
        }else{
            if(mCurrentScrimState != State.SHOWNSCRIM){
                mCurrentScrimState = State.SHOWNSCRIM;
                onStateChanged(appBarLayout, State.SHOWNSCRIM);
            }
        }
        if (i == 0) {
            if (mCurrentState != State.EXPANDED) {
                onStateChanged(appBarLayout, State.EXPANDED);
            }
            mCurrentState = State.EXPANDED;
        } else if (Math.abs(i) >= (appBarLayout.getTotalScrollRange())) {
            if (mCurrentState != State.COLLAPSED) {
                onStateChanged(appBarLayout, State.COLLAPSED);
            }
            mCurrentState = State.COLLAPSED;
        } else {
            if (mCurrentState != State.IDLE) {
                onStateChanged(appBarLayout, State.IDLE);
            }
            mCurrentState = State.IDLE;
        }
    }

    public abstract void onStateChanged(AppBarLayout appBarLayout, State state);
}
