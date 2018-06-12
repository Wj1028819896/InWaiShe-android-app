package com.inwaishe.app.ui.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.inwaishe.app.R;
import com.inwaishe.app.adapter.MainPagerAdapter;
import com.inwaishe.app.base.LazyFragment;
import com.inwaishe.app.common.AppUtils;
import com.inwaishe.app.entity.mainpage.MainPageInfo;
import com.inwaishe.app.entity.mainpage.UserInfo;
import com.inwaishe.app.framework.arch.bus.XBus;
import com.inwaishe.app.framework.arch.bus.XBusObserver;
import com.inwaishe.app.ui.MainActivity;
import com.inwaishe.app.ui.SearchResultActivity;
import com.inwaishe.app.viewmodel.MainPagerViewModel;
import com.inwaishe.app.widget.LoadingNetErrorView;
import com.inwaishe.app.widget.TitleItemDecoration;
import com.inwaishe.app.widget.refreshView.RefreshListener;
import com.inwaishe.app.widget.refreshView.RefreshRecyclerView;
import com.inwaishe.app.widget.refreshView.RefrshHeaderViewComm;


/**
 * Created by Administrator on 2017/8/8 0008.
 * 主页
 */

public class MainPagerFragment extends LazyFragment {

    public static final String TAG = "MainPagerFragment";
    private RefreshRecyclerView mRecyclerView;
    private MainPagerAdapter mainPagerAdapter;
    private MainPagerViewModel mainPagerViewModel;
    private View topViewBg01,topViewBg02,topViewBottomLine;
    private FrameLayout topBar;
    private LinearLayout searchBar;
    private LoadingNetErrorView mLoadingNetErrorView;
    private float bannerTop = 0;//banner top距离
    private float bannerHeight = 0;
    private int orignalSearchBarWidth = 0;
    ValueAnimator animator;
    private boolean isExpaned = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = super.onCreateView(inflater, container, savedInstanceState);
        mRecyclerView = (RefreshRecyclerView) rootview.findViewById(R.id.rvMainPager);
        topViewBg01 = rootview.findViewById(R.id.vTopbg01);
        topViewBg02 = rootview.findViewById(R.id.vTopbg02);
        topViewBottomLine = rootview.findViewById(R.id.vTopBottomLine);
        topBar = (FrameLayout) rootview.findViewById(R.id.topBar);
        searchBar = (LinearLayout) rootview.findViewById(R.id.searchBar);
        mLoadingNetErrorView = (LoadingNetErrorView) rootview.findViewById(R.id.loadingneterrorview);
        Log.e("001","MainPagerFragment onCreateView");
        return rootview;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_home_pager;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isFirstInitLiveData = true;
        isPrepared = true;
        isVisible = true;//通过 FragmentTransaction 的 hide 无法触发setUserVisibleHint，此处直接设置TRUE
        if(mainPagerAdapter != null){
            isPrepared = false;
        }
        lazyLoad();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        if(!isPrepared || !isVisible){
            return;
        }
        mainPagerViewModel = ViewModelProviders.of(this).get(MainPagerViewModel.class);
        mainPagerViewModel.init();
        mainPagerViewModel.getMainPageInfoLiveData().observe(this, new Observer<MainPageInfo>() {
            @Override
            public void onChanged(@Nullable MainPageInfo mainPageInfo) {
                if(mainPageInfo != null && !isFirstInitLiveData){
                    if(mainPageInfo.code > 0){
                        //网络请求成功
                        if(mainPageInfo.isRefreshBack){
                            mLoadingNetErrorView.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                        }
                        mRecyclerView.loadMoreComplect();
                        mRecyclerView.setLoadAll(mainPageInfo.isLoadAll);
                        mRecyclerView.setLoadMoreNetError(false);
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        mRecyclerView.refreshComplete();

                    }else{
                        if(mainPageInfo.isRefreshBack){
                            mRecyclerView.setLoadMoreNetError(false);
                            if(!mRecyclerView.isShown()){
                                mLoadingNetErrorView.setNetErrorViewShown();
                            }else{
                                mLoadingNetErrorView.setVisibility(View.GONE);
                                mRecyclerView.setVisibility(View.VISIBLE);
                            }
                        }else{
                            //footview 改变
                            mRecyclerView.setLoadMoreNetError(true);
                        }
                        //网络请求失败
                        Toast.makeText(getActivity(),"" + mainPageInfo.msg,Toast.LENGTH_SHORT).show();
                        mRecyclerView.loadMoreComplect();
                        mRecyclerView.setLoadAll(mainPageInfo.isLoadAll);
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        mRecyclerView.refreshComplete();
                    }
                }
                isFirstInitLiveData = false;
            }
        });
        mainPagerAdapter = new MainPagerAdapter(mRecyclerView,getActivity(),mainPagerViewModel.getMainPageInfoLiveData().getValue());
        mRecyclerView.setAdapter(mainPagerAdapter);
        mRecyclerView.setRefreshListener(new RefreshListener() {
            @Override
            public void onRefresh() {
                mainPagerViewModel.loadData();
            }

            @Override
            public void onLoadingMore() {
                mainPagerViewModel.loadDataMore();
            }

            @Override
            public void onChange(int dx, int dy, int headerstate) {
                changeTopBarViewByDy(headerstate);
            }
        });
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), SearchResultActivity.class));
            }
        });
        loadData();
        isPrepared = false;
    }

    private void changeTopBarViewByDy(int headerstate) {
        if(mRecyclerView.getFirstVisiblePosition() == 1){
            //banner 可见
            bannerHeight = mRecyclerView.getChildAt(0).getHeight()==0.0f?1.0f:mRecyclerView.getChildAt(0).getHeight();
            bannerTop = mRecyclerView.getChildAt(0).getTop();
        }else if(mRecyclerView.getFirstVisiblePosition() == 0){
            bannerHeight = 1;
            bannerTop = 0;
        }else {
            bannerHeight = 1;
            bannerTop = -1;
        }
        Log.e(TAG,"top:" + bannerTop + "  H:" + bannerHeight);
        float alpha = 1.0f * (-bannerTop)/bannerHeight;
        if(alpha < 0.0f){
            alpha = 0.0f;
        }else if(alpha > 1.0f){
            alpha = 1.0f;
        }
        if(alpha == 1.0f){
            if(!isExpaned){
                AppUtils.setStatusBarLightModel(getActivity(),true);
                expandSearchBar();
                isExpaned = true;
            }
        }else if(alpha == 0.0f){
            if(isExpaned){
                AppUtils.setStatusBarLightModel(getActivity(),false);
                collapSearchBar();
                isExpaned = false;
            }
        }
        topViewBg01.setAlpha(alpha);
        topViewBg02.setAlpha(alpha);
        topViewBottomLine.setAlpha(alpha);

        switch (headerstate){
            case RefrshHeaderViewComm.STATE_STATE_HIDE:
                topBar.setVisibility(View.VISIBLE);
                break;
            default:
                topBar.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 压缩SearchBar
     */
    private void collapSearchBar() {
        int searchBarCrxWidth = searchBar.getWidth();
        int fromWidth = searchBarCrxWidth;
        if(orignalSearchBarWidth == 0){
            orignalSearchBarWidth = (int) (topBar.getWidth()/3.5);
        }
        int toWidth = orignalSearchBarWidth;

        if(animator != null && animator.isRunning()){
            animator.cancel();
        }
        animator = ValueAnimator
                .ofInt(fromWidth,toWidth)
                .setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int width = (int) animation.getAnimatedValue();
                searchBar.getLayoutParams().width = width;
                searchBar.requestLayout();
                topBar.requestLayout();
            }
        });
        animator.start();
    }

    /**
     * 展开SearchBar
     */
    private void expandSearchBar() {
        int topBarWidth = topBar.getWidth();
        int leftMargin = ((RelativeLayout.LayoutParams)searchBar.getLayoutParams()).leftMargin;
        int rightMargin = ((RelativeLayout.LayoutParams)searchBar.getLayoutParams()).rightMargin;
        if(orignalSearchBarWidth == 0){
            orignalSearchBarWidth = searchBar.getWidth();
        }
        int fromWidth = orignalSearchBarWidth;
        int toWidth = topBarWidth - leftMargin - rightMargin;

        if(animator != null && animator.isRunning()){
            animator.cancel();
        }
        animator = ValueAnimator
                .ofInt(fromWidth,toWidth)
                .setDuration(300);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int width = (int) animation.getAnimatedValue();
                searchBar.getLayoutParams().width = width;
                searchBar.requestLayout();
                topBar.requestLayout();
            }
        });
        animator.start();
    }

    @Override
    protected void loadData() {
        super.loadData();
        mLoadingNetErrorView.setVisibility(View.VISIBLE);
        mLoadingNetErrorView.setLoadingViewShown();
        mRecyclerView.setVisibility(View.GONE);
        mRecyclerView.refreshingStart();
        mainPagerViewModel.loadData();
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        lazyLoad();
    }
}