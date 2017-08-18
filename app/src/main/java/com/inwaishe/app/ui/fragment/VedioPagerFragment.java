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
import com.inwaishe.app.adapter.VedioListAdapter;
import com.inwaishe.app.base.LazyFragment;
import com.inwaishe.app.entity.mainpage.MainPageInfo;
import com.inwaishe.app.viewmodel.VedioPagerViewModel;

/**
 * Created by Administrator on 2017/8/16 0016.
 *
 */

public class VedioPagerFragment extends LazyFragment{
    public static final String TAG = "VedioPagerFragment";
    RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    VedioPagerViewModel vedioPagerViewModel;
    VedioListAdapter vedioListAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        initView(rootView);
        return rootView;

    }

    private void initView(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvVedioList);

    }

    private void initRecyclerView() {

        gridLayoutManager = new GridLayoutManager(getActivity(),2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_vediopager;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isPrepared = true;
        isVisible = true;
        initRecyclerView();
        vedioPagerViewModel = ViewModelProviders.of(getActivity()).get(VedioPagerViewModel.class);
        vedioPagerViewModel.init();
        vedioPagerViewModel.getMainPageInfoLiveData().observe(this, new Observer<MainPageInfo>() {
            @Override
            public void onChanged(@Nullable MainPageInfo mainPageInfo) {
                if(vedioListAdapter != null){
                    vedioListAdapter.notifyDataSetChanged();
                }
            }
        });

        vedioListAdapter = new VedioListAdapter(getActivity(),vedioPagerViewModel.getMainPageInfoLiveData().getValue());
        recyclerView.setAdapter(vedioListAdapter);
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
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
        vedioPagerViewModel.loadData();
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        lazyLoad();
    }
}