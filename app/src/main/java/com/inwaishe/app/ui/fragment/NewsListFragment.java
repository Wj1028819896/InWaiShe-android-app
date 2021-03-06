package com.inwaishe.app.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.inwaishe.app.R;
import com.inwaishe.app.adapter.MainPagerAdapter;
import com.inwaishe.app.adapter.NewsListAdapter;
import com.inwaishe.app.base.LazyFragment;
import com.inwaishe.app.dbroom.NewsTypes;
import com.inwaishe.app.entity.mainpage.MainPageInfo;
import com.inwaishe.app.viewmodel.MainPagerViewModel;
import com.inwaishe.app.viewmodel.NewsPagerViewModel;

/**
 * Created by WangJing on 2017/8/14.
 * 评测列表页
 */

public class NewsListFragment extends LazyFragment {
    public static final String TAG = "NewsListFragment";
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    NewsPagerViewModel newsPagerViewModel;
    NewsListAdapter newsListAdapter;
    public NewsTypes Type;
    private GridLayoutManager gridLayoutManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = super.onCreateView(inflater, container, savedInstanceState);
        recyclerView = (RecyclerView) rootview.findViewById(R.id.rvNewsList);
        swipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.srL);
        return rootview;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_list;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isPrepared = true;
        Type = NewsListFragment.changeStringToType(getArguments().getString("TYPE"));

        Log.e(TAG,"finishCreateView()" + Type);

        newsPagerViewModel = ViewModelProviders.of(getActivity()).get(NewsPagerViewModel.class);
        newsPagerViewModel.init();
        newsPagerViewModel.getNewsPageInfoLiveData().getValue().get(Type)
                .observe(this, new Observer<MainPageInfo>(){
                    @Override
                    public void onChanged(@Nullable MainPageInfo mainPageInfo) {
                        Log.e(TAG,"onChanged" + Type);
                        if(newsListAdapter != null){
                            Log.e(TAG,"onChanged" + Type + " -> " + mainPageInfo.articleInfos.size());
                            newsListAdapter.setLoadingMore(false);
                            newsListAdapter.setLoadAll(mainPageInfo.isLoadAll);
                            newsListAdapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                            if(mainPageInfo.code < 0){
                                Toast.makeText(getActivity(),"" + mainPageInfo.msg,Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        newsListAdapter = new NewsListAdapter(recyclerView,getActivity(),newsPagerViewModel.getNewsPageInfoLiveData().getValue().get(Type).getValue());
        newsListAdapter.setOnLoadOrRefreshListener(new NewsListAdapter.OnLoadOrRefreshListener() {
            @Override
            public void onLoadMore() {
                loadData();
            }

            @Override
            public void onRefresh() {

            }
        });

        recyclerView.setAdapter(newsListAdapter);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        lazyLoad();
    }

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadDataForRefresh();
        }
    };
    @Override
    protected void onVisible() {
        super.onVisible();
        Log.e(TAG,"onVisible" + Type);
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        Log.e(TAG,"lazyLoad" + Type);
        super.lazyLoad();
        if(!isPrepared || !isVisible){
            return;
        }
        loadData();
        isPrepared = false;
    }

    @Override
    protected void loadData() {
        super.loadData();
        Log.e(TAG,"loadData" + Type);
        newsPagerViewModel.loadData(Type,false);
    }

    protected void loadDataForRefresh(){
        newsPagerViewModel.loadData(Type,true);
    }

    public static NewsListFragment getInStance(String type){
        NewsListFragment newsListFragment = new NewsListFragment();
        Bundle info = new Bundle();
        info.putString("TYPE",type);
        newsListFragment.setArguments(info);
        return newsListFragment;
    }

    public static NewsTypes changeStringToType(String str){

        NewsTypes type = NewsTypes.周边;
        switch (str){
            case "周边":
                type = NewsTypes.周边;
                break;
            case "手柄":
                type = NewsTypes.手柄;
                break;
            case "数码":
                type = NewsTypes.数码;
                break;
            case "活动":
                type = NewsTypes.活动;
                break;
            case "游戏":
                type = NewsTypes.游戏;
                break;
            case "键帽":
                type = NewsTypes.键帽;
                break;
            case "键盘":
                type = NewsTypes.键盘;
                break;
            case "音频":
                type = NewsTypes.音频;
                break;
            case "鼠垫":
                type = NewsTypes.鼠垫;
                break;
            case "鼠标":
                type = NewsTypes.鼠标;
                break;
        }
        return  type;
    }

    @Override
    public void onDestroyView() {
        Log.e(TAG,"onDestroyView()" + Type);
        super.onDestroyView();
    }
}
