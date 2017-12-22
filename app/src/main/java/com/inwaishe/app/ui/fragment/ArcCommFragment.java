package com.inwaishe.app.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inwaishe.app.R;
import com.inwaishe.app.adapter.CommonAdapter;
import com.inwaishe.app.base.LazyFragment;
import com.inwaishe.app.entity.mainpage.ArcCommInfo;
import com.inwaishe.app.framework.arch.bus.XBus;
import com.inwaishe.app.framework.arch.bus.XBusObserver;
import com.inwaishe.app.framework.arch.bus.XBusThreadModel;
import com.inwaishe.app.ui.UsrCommDetailActivity;
import com.inwaishe.app.viewmodel.ArcDetailViewModel;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/12 0012.
 * 文章评论页面
 */

public class ArcCommFragment extends LazyFragment {

    private RecyclerView mRecycleView;
    private LinearLayoutManager linearLayoutManager;
    private CommonAdapter commonAdapter;
    private ArcDetailViewModel arcDetailViewModel;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mRecycleView = (RecyclerView) rootView.findViewById(R.id.rcArcComm);
        return rootView;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_arccomm;
    }

    @Override
    public void finishCreateView(Bundle state) {

        isPrepared = true;

        initRecycleView();
        arcDetailViewModel = ViewModelProviders.of(getActivity()).get(ArcDetailViewModel.class);
        commonAdapter = new CommonAdapter(getActivity(),arcDetailViewModel.getArccommMutableLiveData().getValue());
        mRecycleView.setAdapter(commonAdapter);
        arcDetailViewModel.getArccommMutableLiveData().observe(this, new Observer<ArrayList<ArcCommInfo>>() {
            @Override
            public void onChanged(@Nullable ArrayList<ArcCommInfo> arcCommInfos) {
                commonAdapter.notifyDataSetChanged();
            }
        });

        lazyLoad();
        /**
         * 接收来自回复成功的事件
         */
        XBus.getInstance().observe(this, UsrCommDetailActivity.EVENT_REPLY_SUCCESS, new XBusObserver() {
            @Override
            public void onCall(Object var) {
                isPrepared = true;
                lazyLoad();
            }
        }).runOn(XBusThreadModel.mainThread());

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
        arcDetailViewModel.loadDataForArcCommFragment();
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        lazyLoad();
    }

    private void initRecycleView() {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecycleView.setLayoutManager(linearLayoutManager);

    }
}
