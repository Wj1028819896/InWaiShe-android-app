package com.inwaishe.app.widget.refreshView;

import android.animation.ValueAnimator;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.inwaishe.app.R;

/**
 * Created by WangJing on 2017/9/29.
 * 头部View 可扩展插件设计
 */

public abstract class RefrshHeaderViewComm {

    private View HeaderView;
    private String TAG = "RefrshHeaderViewComm";

    public static final int STATE_STATE_HIDE = 0x0;
    public static final int STATE_NORMAL = 0x1;
    public static final int STATE_ABLEREFRESH = 0x2;
    public static final int STATE_REFRESHING = 0x3;

    public int HeaderViewHeight = 0;//动态高度
    public int OrignalHeaderViewHeight = 0;//原始状态高度

    public float HeaderViewRefreshRadio = 2.0f;//顶部VIEW 拉伸到可刷新的倍数

    public View getHeaderView() {
        return HeaderView;
    }

    private int crxState = STATE_STATE_HIDE;

    public int getCrxState(){
        return  crxState;
    }
    public void setHeaderView(View HeaderView){
        this.HeaderView = HeaderView;
        init();
    }

    private void init() {
        measureView(HeaderView);
        OrignalHeaderViewHeight = HeaderView.getMeasuredHeight();
    }

    abstract void changeHeaderViewByState(int state);
    /***
     * 设置头部HeaderView 的高度
     * @param height
     */
    public void setHeaderViewHeight(View parent,int height){
        if(HeaderView == null || HeaderView.getLayoutParams() == null){
            return;
        }
        HeaderView.getLayoutParams().height = height;
        HeaderViewHeight = height;

        if(crxState != STATE_REFRESHING){
            if(HeaderViewHeight < HeaderViewRefreshRadio*OrignalHeaderViewHeight){
                crxState = STATE_NORMAL;
            }else {
                crxState = STATE_ABLEREFRESH;
            }
        }
        if(HeaderViewHeight == 0){
            crxState = STATE_STATE_HIDE;
        }

        changeHeaderViewByState(crxState);
        changeHeaderViewbyHeight(HeaderViewHeight);
        parent.requestLayout();
    }

    /***
     * 根据高度改变HeaderView 样式
     * @param HeaderViewHeight
     */
    abstract void changeHeaderViewbyHeight(int HeaderViewHeight);

    /**
     * 通知父布局，占用的宽，高；
     * @param view
     */
    private void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(p);
        }
        int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int height;
        int tempHeight = p.height;
        if (tempHeight > 0) {
            height = View.MeasureSpec.makeMeasureSpec(tempHeight,
                    View.MeasureSpec.EXACTLY);
        } else {
            height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);
    }

    /**
     * 还原头部高度
     * @param isScrollTop 是否滚出屏幕
     */
    public void hideHeaderView(final View parent,boolean isScrollTop){
        int h = 0;
        if(!isScrollTop){
            h = OrignalHeaderViewHeight;
        }
        ValueAnimator animator = ValueAnimator.ofInt(
                HeaderView.getLayoutParams().height, h);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setHeaderViewHeight(parent,(int) animation.getAnimatedValue());
            }
        });
        if(!isScrollTop){
            crxState = STATE_REFRESHING;
        }else{
            crxState = STATE_STATE_HIDE;
        }
        changeHeaderViewByState(crxState);
        animator.start();
    }
}
