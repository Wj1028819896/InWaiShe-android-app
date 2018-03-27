package com.inwaishe.app.widget.horizontallListView;

import android.view.MotionEvent;

/**
 * Created by WangJing on 2017/12/25.
 * 横向滚动手势监听接口
 */

public interface HorizonOnGestureListener {

    boolean onHorizonScroll(MotionEvent e1,MotionEvent e2);
}
