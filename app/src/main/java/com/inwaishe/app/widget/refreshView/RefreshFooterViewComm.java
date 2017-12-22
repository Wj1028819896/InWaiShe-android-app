package com.inwaishe.app.widget.refreshView;

import android.view.View;

/**
 * Created by WangJing on 2017/9/29.
 */

public abstract class RefreshFooterViewComm {

    private View FooterView;

    public static final int STATE_LOADALL = 0x0;
    public static final int STATE_NORMAL = 0x1;

    public View getFooterView(){
        return FooterView;
    }

    public void setFooterView(View footerView){
        this.FooterView = footerView;
    }

    abstract void changeViewByState(int state);
}
