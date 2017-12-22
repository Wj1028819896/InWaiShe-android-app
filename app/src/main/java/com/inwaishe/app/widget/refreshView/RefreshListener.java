package com.inwaishe.app.widget.refreshView;

/**
 * Created by WangJing on 2017/9/29.
 * 监听接口
 */

public interface RefreshListener {

    void onRefresh();
    void onLoadingMore();
    void onChange(int dx,int dy,int headerstate);
}
