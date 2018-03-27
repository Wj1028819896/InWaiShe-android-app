package com.inwaishe.app.widget.horizontallListView;

import android.Manifest;
import android.content.Context;
import android.view.MotionEvent;

/**
 * Created by WangJing on 2017/12/25.
 */

public class HorizonScrollGestureDetector {



    float downX,downY;
    float moveX,moveY;
    MotionEvent downEvent;
    MotionEvent moveEvent;
    Context mContext;
    HorizonOnGestureListener mListener;
    boolean isScrolling = false;

    /**
     * constractor
     * @param con
     * @param listener
     */
    public HorizonScrollGestureDetector(Context con,HorizonOnGestureListener listener){
        this.mContext = con;
        this.mListener = listener;
    }
    /**
     * 处理事件 进行事件判断
     * 单 x轴位移大于Y轴时，认为是横向滑动
     * @param ev
     * @return
     */
    public boolean onTouchEvent(MotionEvent ev){
        final int action = ev.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                downEvent = ev;
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = ev.getX();
                moveY = ev.getY();
                moveEvent = ev;
                break;
            case MotionEvent.ACTION_UP:
                reset();
                break;
        }
        boolean handler = cal();
        return handler;
    }

    private boolean cal() {
        float distanceX = Math.abs(downX - moveY);
        float distanceY = Math.abs(downY - moveY);
        if(distanceX > distanceY || isScrolling){
            if(mListener != null){
                isScrolling = true;
                return mListener.onHorizonScroll(downEvent,moveEvent);
            }
            return false;
        }
        return false;
    }

    /**
     * 重置
     */
    private void reset() {
        isScrolling = false;
    }
}
