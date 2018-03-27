package com.inwaishe.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.inwaishe.app.R;

/**
 * Created by WangJing on 2018/1/16.
 * 加载和网络异常View
 */

public class LoadingNetErrorView extends RelativeLayout{

    public LoadingNetErrorView(Context context) {
        this(context,null);
    }

    public LoadingNetErrorView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingNetErrorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private Context mContext;
    private View mView;
    private LinearLayout mLoadingView;
    private LinearLayout mNetErrorView;
    private Button mBtnRetry;
    /**
     * 初始化View
     * @param context
     */
    private void initView(Context context) {
        mContext = context;
        mView = View.inflate(context, R.layout.loadingneterrorview, this);
        mLoadingView = (LinearLayout) mView.findViewById(R.id.loadingview);
        mNetErrorView = (LinearLayout) mView.findViewById(R.id.neterrorview);
        mBtnRetry = (Button) mView.findViewById(R.id.btnRetry);
    }

    public void setLoadingViewShown(){
        mLoadingView.setVisibility(VISIBLE);
        mNetErrorView.setVisibility(GONE);
    }

    public void setNetErrorViewShown(){
        mLoadingView.setVisibility(GONE);
        mNetErrorView.setVisibility(VISIBLE);
    }

    public void setOnBtnRetryClickListener(View.OnClickListener onBtnRetryClickListener){
        mBtnRetry.setOnClickListener(onBtnRetryClickListener);
    }

}
