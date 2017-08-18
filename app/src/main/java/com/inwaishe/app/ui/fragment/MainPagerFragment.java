package com.inwaishe.app.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inwaishe.app.R;
import com.inwaishe.app.adapter.MainPagerAdapter;
import com.inwaishe.app.base.LazyFragment;
import com.inwaishe.app.entity.mainpage.MainPageInfo;
import com.inwaishe.app.viewmodel.MainPagerViewModel;
import com.inwaishe.app.widget.TitleItemDecoration;


/**
 * Created by Administrator on 2017/8/8 0008.
 * 主页
 */

public class MainPagerFragment extends LazyFragment {

    public static final String TAG = "MainPagerFragment";
    private RecyclerView mRecyclerView;
    private MainPagerAdapter mainPagerAdapter;
    private GridLayoutManager gridLayoutManager;
    private MainPagerViewModel mainPagerViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = super.onCreateView(inflater, container, savedInstanceState);
        mRecyclerView = (RecyclerView) rootview.findViewById(R.id.rvMainPager);
        return rootview;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_home_pager;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isPrepared = true;
        isVisible = true;//通过 FragmentTransaction 的 hide 无法触发setUserVisibleHint，此处直接设置TRUE
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
        initRecyclerView();
        mainPagerViewModel = ViewModelProviders.of(this).get(MainPagerViewModel.class);
        mainPagerViewModel.init();
        mainPagerViewModel.getMainPageInfoLiveData().observe(this, new Observer<MainPageInfo>() {
            @Override
            public void onChanged(@Nullable MainPageInfo mainPageInfo) {
                mainPagerAdapter.notifyDataSetChanged();
            }
        });
        mainPagerAdapter = new MainPagerAdapter(getActivity(),mainPagerViewModel.getMainPageInfoLiveData().getValue());
        mRecyclerView.setAdapter(mainPagerAdapter);
        loadData();
        isPrepared = false;
    }

    @Override
    protected void loadData() {
        super.loadData();
        mainPagerViewModel.loadData();
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        lazyLoad();
    }

    private void initRecyclerView() {

        gridLayoutManager = new GridLayoutManager(getActivity(),2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = mainPagerAdapter.getItemViewType(position);
                int spanSize = 1;
                switch (viewType){
                    case MainPagerAdapter.VIEWTYPE_VIEWPAGER:
                        spanSize = 2;
                        break;
                    case MainPagerAdapter.VIEWTYPE_TITLE:
                        spanSize = 2;
                        break;
                    case MainPagerAdapter.VIEWTYPE_SHAREWELL:
                        spanSize = 1;
                        break;
                    case MainPagerAdapter.VIEWTYPE_ENTRANCE:
                        spanSize = 2;
                        break;
                    case MainPagerAdapter.VIEWTYPE_LIST:
                        spanSize = 2;
                        break;
                }
                return spanSize;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        TitleItemDecoration titleItemDecoration = new TitleItemDecoration(getContext(), mRecyclerView);
    }
}
