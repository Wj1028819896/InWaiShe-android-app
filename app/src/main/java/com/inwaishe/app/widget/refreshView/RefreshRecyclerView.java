package com.inwaishe.app.widget.refreshView;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.inwaishe.app.R;


/**
 * Created by WangJing on 2017/9/22.
 *
 * 下拉刷新RecyleView
 */

public class RefreshRecyclerView extends RecyclerView {

    private String TAG = "RerfeshRecyclerView";
    //头部VIEW
    private RefrshHeaderViewComm HeaderViewComm;
    private int HeaderViewCount = 1;
    private int HeaderViewType = Integer.MAX_VALUE;
    private boolean isTop = false;//头部滑动到最顶（true: 开始拉伸）
    //底部VIEW
    private RefreshFooterViewComm FooterviewComm;
    private int FooterViewType = Integer.MAX_VALUE - 1;
    private int FooterViewCount = 1;
    private boolean isLast = false;//滑动到最底部（true: 开始加载更多）
    //包装Adapter
    private ProxyAdapter proxyAdapter;
    //Contxet
    private Context mContext;
    //
    int startY = 0,startX = 0;
    //阻尼系数
    float mDampRatio = 0.75f;
    //第一个可见的VIEW
    int firstVisiblePosition = -1;
    //是否正在刷新
    boolean isRrefreshing = false;
    //加载更多，所有加载完
    private boolean isLoadingMore = false;
    private boolean isLoadAll = false;
    private boolean isLoadMoreNetError = false;

    RefreshListener mRefreshListener;

    //普通手势监听
    GestureDetector mGestureDetector;

    public RefreshRecyclerView(Context context) {
        this(context,null);
    }

    public RefreshRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RefreshRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        //初始化默认HeaderView
        View HeaderView = LayoutInflater.from(mContext).inflate(R.layout.header_recylerview,null);
        HeaderViewComm = new RefrshHeaderViewComm() {
            @Override
            void changeHeaderViewByState(int state) {
                if(mRefreshListener != null){
                    mRefreshListener.onChange(0,0,state);
                }
                TextView tv = (TextView) getHeaderView().findViewById(R.id.tvState);
                AppCompatImageView iv = (AppCompatImageView) getHeaderView().findViewById(R.id.inwaishe);
                Drawable drawable = iv.getDrawable();
                String stateStr = "";
                switch (state){
                    case RefrshHeaderViewComm.STATE_NORMAL:
                        stateStr = "下拉刷新";
                        break;
                    case RefrshHeaderViewComm.STATE_ABLEREFRESH:
                        stateStr = "松开刷新";
                        break;
                    case RefrshHeaderViewComm.STATE_REFRESHING:
                        if(drawable instanceof Animatable){
                            if(!((Animatable) drawable).isRunning()){
                                ((Animatable) drawable).start();
                            }
                        }
                        stateStr = "正在刷新";
                        break;
                    case RefrshHeaderViewComm.STATE_STATE_HIDE:
                        if(drawable instanceof Animatable){
                            if(((Animatable) drawable).isRunning()){
                                ((Animatable) drawable).stop();
                            }
                        }
                        drawable = AnimatedVectorDrawableCompat
                                .create(mContext,R.drawable.animator_inwaishe);
                        iv.setImageDrawable(drawable);
                        stateStr = "下拉刷新";
                        break;
                }
                tv.setText(stateStr);
            }
            @Override
            void changeHeaderViewbyHeight(int HeaderViewHeight) {
            }
        };
        HeaderViewComm.setHeaderView(HeaderView);

        View FooterView = LayoutInflater.from(mContext).inflate(R.layout.item_footer_loadmore,null);
        FooterviewComm = new RefreshFooterViewComm() {
            @Override
            void changeViewByState(int state) {
                SpinKitView pb = (SpinKitView) getFooterView().findViewById(R.id.footer_pb);
                TextView tv = (TextView)getFooterView().findViewById(R.id.footer_txt);
                TextView netError = (TextView)getFooterView().findViewById(R.id.retryLoad);
                if(state == FooterviewComm.STATE_LOADALL){
                    pb.setVisibility(GONE);
                    tv.setVisibility(VISIBLE);
                    netError.setVisibility(GONE);
                }else if(state == FooterviewComm.STATE_NORMAL){
                    pb.setVisibility(VISIBLE);
                    tv.setVisibility(GONE);
                    netError.setVisibility(GONE);
                }else if(state == FooterviewComm.STATE_NETERROE){
                    pb.setVisibility(GONE);
                    tv.setVisibility(GONE);
                    netError.setVisibility(VISIBLE);
                }
                getFooterView().setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isLoadMoreNetError){
                            changeViewByState(FooterviewComm.STATE_NORMAL);
                            mRefreshListener.onLoadingMore();
                            isLoadMoreNetError = false;
                        }
                    }
                });
            }
        };
        FooterviewComm.setFooterView(FooterView);
        initListener();
    }

    public void setHeaderView(RefrshHeaderViewComm headerViewComm){
        this.HeaderViewComm = headerViewComm;
    }

    public View getHeaderView(){
        return this.HeaderViewComm.getHeaderView();
    }

    public void setFooterView(RefreshFooterViewComm footerViewComm){
        this.FooterviewComm = footerViewComm;
    }

    /**
     * 刷新开始
     */
    public void refreshingStart(){
        isRrefreshing = true;
    }
    /**
     * 刷新完成
     */
    public void refreshComplete(){
        isRrefreshing = false;
        HeaderViewComm.hideHeaderView(this,true);

    }
    /**
     * 加载更多开始
     */
    public void loadMoreStart(){
        isLoadingMore = true;
    }
    /**
     * 加载更多完成
     */
    public void loadMoreComplect(){
        isLoadingMore = false;
    }

    /**
     * 加载更多时网络是否异常
     * @param isNetError
     */
    public void setLoadMoreNetError(boolean isNetError){
        this.isLoadMoreNetError = isNetError;
    }
    /**
     * 设置是否加载完所有数据
     * @param isLoadAll
     */
    public void setLoadAll(boolean isLoadAll){
        this.isLoadAll = isLoadAll;
    }
    /**
     * 设置监听器
     * @param mRefreshListener
     */
    public void setRefreshListener(RefreshListener mRefreshListener) {
        this.mRefreshListener = mRefreshListener;
    }

    private void initListener() {
        this.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LayoutManager layoutManager = getLayoutManager();
                int lastVisibleItemPosition = 0;
                if(layoutManager instanceof LinearLayoutManager){
                    lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();
                }else if(layoutManager instanceof GridLayoutManager){
                    lastVisibleItemPosition = ((GridLayoutManager)layoutManager).findLastVisibleItemPosition();
                }else if(layoutManager instanceof StaggeredGridLayoutManager){
                    int[] lastInto = null;
                    lastVisibleItemPosition = ((StaggeredGridLayoutManager)layoutManager).findLastVisibleItemPositions(lastInto)[0];
                }
                if(lastVisibleItemPosition == (getAdapter().getItemCount() - 1)){
                    //footer 可见
                    if(!isLoadingMore
                            && !isRrefreshing
                            && mRefreshListener!=null
                            && !isLoadMoreNetError
                            && !isLoadAll){
                        isLoadingMore = true;
                        mRefreshListener.onLoadingMore();
                    }
                }
            }
        });
    }

    public int getFirstVisiblePosition(){
        return firstVisiblePosition;
    }

    public boolean isTop(){
        return isTop;
    }

    private void checkIstop() {
        LayoutManager ob = this.getLayoutManager();
        int position = 0;
        if(ob instanceof LinearLayoutManager){
            position = ((LinearLayoutManager)ob).findFirstVisibleItemPosition();
        }else if(ob instanceof GridLayoutManager){
            position = ((GridLayoutManager)ob).findFirstVisibleItemPosition();
        }else if(ob instanceof StaggeredGridLayoutManager){
            int[] firstInto = null;
            position = ((StaggeredGridLayoutManager)ob).findFirstVisibleItemPositions(firstInto)[0];
        }
        firstVisiblePosition = position;
        //Log.e(TAG,"first = " + position);
        if(position == 1 || position == 0){
            View firstVisiableChildView = ob.findViewByPosition(position);
            if(firstVisiableChildView.getTop() == 0){
                isTop = true;
                return;
            }
        }
        isTop = false;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
    }

    /**
     * 包装用户Adapter（代理模式），添加头尾 item
     * @param adapter 用户adapter
     */
    @Override
    public void setAdapter(Adapter adapter) {
        ProxyAdapter proxyAdapter = new ProxyAdapter(adapter);
        super.setAdapter(proxyAdapter);
        this.proxyAdapter = proxyAdapter;
        HeaderViewComm.setHeaderViewHeight(RefreshRecyclerView.this,0);
    }

    @Override
    public Adapter getAdapter() {
        return super.getAdapter();
    }

    /**
     * 代理 Adapter 类
     */
    private class ProxyAdapter extends RecyclerView.Adapter<ViewHolder>{

        private Adapter mAdapter;

        public ProxyAdapter(Adapter adapter){
            this.mAdapter = adapter;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            if(viewType == HeaderViewType){
                /**
                 * 头部ViewHolder
                 */
                return new HeaderViewHolder(HeaderViewComm.getHeaderView());
            }
            if(viewType == FooterViewType){
                /**
                 * 底部ViewHolder
                 */
                return new FooterViewHolder(FooterviewComm.getFooterView());
            }
            return mAdapter.onCreateViewHolder(viewGroup,viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            if(position < HeaderViewCount){

            }else if(position == (getItemCount() -1 )){
                if(isLoadAll){
                    FooterviewComm.changeViewByState(RefreshFooterViewComm.STATE_LOADALL);
                }else{
                    if(isLoadMoreNetError){
                        FooterviewComm.changeViewByState(RefreshFooterViewComm.STATE_NETERROE);
                    }else{
                        FooterviewComm.changeViewByState(RefreshFooterViewComm.STATE_NORMAL);
                    }
                }
            }else {
                mAdapter.onBindViewHolder(viewHolder,position - HeaderViewCount);
            }
        }

        @Override
        public int getItemCount() {
            /**
             *  头部ITEM+ 被代理ITEM
             */
            return HeaderViewCount + mAdapter.getItemCount() + FooterViewCount;
        }

        @Override
        public int getItemViewType(int position) {
            /**
             * 调用 被代理的 adapter 之前对 position 处理
             */
            if(position < HeaderViewCount){
                return HeaderViewType;
            }
            if(position == (getItemCount() - 1)){
                return FooterViewType;
            }
            return mAdapter.getItemViewType(position - HeaderViewCount);
        }

        private class HeaderViewHolder extends ViewHolder {
            public HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        private class FooterViewHolder extends RecyclerView.ViewHolder{
            public FooterViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        /**滚动状态**/
        Log.e(TAG,"onScrollStateChanged");
        super.onScrollStateChanged(state);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        /**滚动距离**/
        //Log.e(TAG,"onScrolled--> " + dy);
        super.onScrolled(dx, dy);
        checkIstop();
        if(mRefreshListener != null){
                mRefreshListener.onChange(dx,dy,HeaderViewComm.getCrxState());
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        //Log.e(TAG,"onScrollChanged");
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        boolean su = super.onInterceptTouchEvent(e);
        //Log.e(TAG,"onInterceptTouchEvent = " + su);
        return su;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean su = super.dispatchTouchEvent(ev);
        //Log.e(TAG,"dispatchTouchEvent = " + su);
        return su;
    }
    private boolean isOnTop() {
        if(HeaderViewComm == null)
            return false;
        if (HeaderViewComm.getHeaderView().getParent() != null) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(startY == 0){
            startY = (int) event.getRawY();
        }
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dsY = (int) (event.getRawY()) - startY;
                startY = (int) event.getRawY();
                if(isOnTop() && !isRrefreshing){
                    if(dsY > 0){
                        HeaderViewComm.setHeaderViewHeight(RefreshRecyclerView.this, HeaderViewComm.HeaderViewHeight + (int)(dsY * mDampRatio));
                    }else{
                        if (HeaderViewComm.getHeaderView().getMeasuredHeight() > 0){
                            HeaderViewComm.setHeaderViewHeight(
                                        RefreshRecyclerView.this,
                                        (HeaderViewComm.HeaderViewHeight + (int)(dsY/mDampRatio)) < 0
                                                ?
                                                0 :
                                                (HeaderViewComm.HeaderViewHeight + (int)(dsY/mDampRatio)));
                        }
                    }
                    if (HeaderViewComm.getHeaderView().getMeasuredHeight() > 0){
                        return false;
                    }
                }
                break;
            default:
                startY = 0;
                    if (HeaderViewComm.getHeaderView().getMeasuredHeight() > (HeaderViewComm.HeaderViewRefreshRadio * HeaderViewComm.OrignalHeaderViewHeight)) {
                        HeaderViewComm.hideHeaderView(RefreshRecyclerView.this, false);
                        if (mRefreshListener != null && !isRrefreshing) {
                            isRrefreshing = true;
                            mRefreshListener.onRefresh();
                        }
                    } else {
                        HeaderViewComm.hideHeaderView(RefreshRecyclerView.this, true);
                    }
                break;
        }
        return super.onTouchEvent(event);
    }
}
