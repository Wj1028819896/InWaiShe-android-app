package com.inwaishe.app.ui.display;


import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.inwaishe.app.R;
import com.inwaishe.app.adapter.CommonAdapter;
import com.inwaishe.app.dataprovider.DataProvider;
import com.inwaishe.app.entity.mainpage.ArcCommInfo;
import com.inwaishe.app.framework.arch.bus.XBus;
import com.inwaishe.app.framework.arch.bus.XBusObserver;
import com.inwaishe.app.framework.arch.bus.XBusThreadModel;
import com.inwaishe.app.framework.emotionkeyboard.EmotionKeyBoardMainDialogFragment;
import com.inwaishe.app.ui.ArcDetaileActivity;
import com.inwaishe.app.ui.UsrCommDetailActivity;
import com.inwaishe.app.viewmodel.DisplayViewModel;
import com.inwaishe.app.widget.LoadingNetErrorView;

import java.util.ArrayList;

/**
 * Created by WangJing on 2018/2/23.
 */

public class DisplayCommentDialogFragment extends DialogFragment implements LifecycleRegistryOwner {

    private View rootView;
    private RecyclerView mRecycleView;
    private LinearLayoutManager linearLayoutManager;
    private CommonAdapter commonAdapter;
    private DisplayViewModel displayViewModel;
    private Context mContext;
    private LoadingNetErrorView mLoadingNetErrorViewl;
    private ImageView mIvBack;
    private TextView mBtnAddComm;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = inflater.inflate(R.layout.dialog_comments,container,false);
        initView(rootView);
        initData();
        initEvent();
        slideToUp(rootView);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    private void initView(View rootView) {
        mRecycleView = (RecyclerView) rootView.findViewById(R.id.rcArcComm);
        mLoadingNetErrorViewl = (LoadingNetErrorView) rootView.findViewById(R.id.loadingneterrorview);
        mLoadingNetErrorViewl.setVisibility(View.VISIBLE);
        mLoadingNetErrorViewl.setLoadingViewShown();
        mIvBack = (ImageView) rootView.findViewById(R.id.ivBack);
        mBtnAddComm = (TextView) rootView.findViewById(R.id.edit_comment);
        initRecycleView();
    }

    private void initRecycleView() {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecycleView.setLayoutManager(linearLayoutManager);
    }

    private void initData() {
        displayViewModel = ViewModelProviders.of(getActivity()).get(DisplayViewModel.class);
        commonAdapter = new CommonAdapter(getActivity(),displayViewModel.getArccommMutableLiveData().getValue());
        mRecycleView.setAdapter(commonAdapter);
        displayViewModel.getArccommMutableLiveData().observe(this, new Observer<ArrayList<ArcCommInfo>>() {
            @Override
            public void onChanged(@Nullable ArrayList<ArcCommInfo> arcCommInfos) {
                commonAdapter.notifyDataSetChanged();
                if(arcCommInfos.size() > 0){
                    mLoadingNetErrorViewl.setVisibility(View.GONE);
                }
            }
        });
        displayViewModel.loadDataForArcCommFragment();
    }

    private void initEvent() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mBtnAddComm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DataProvider.isNeedLogin(getActivity())){
                    return;
                }
                EmotionKeyBoardMainDialogFragment emotionKeyBoardMainDialogFragment = new EmotionKeyBoardMainDialogFragment();
                Bundle data = new Bundle();
                data.putString("hint","添加评论...");
                emotionKeyBoardMainDialogFragment.setArguments(data);
                emotionKeyBoardMainDialogFragment.show(getActivity().getSupportFragmentManager(), "CommentDialogFragment");
            }
        });

        /**
         * 接收来自回复成功的事件
         */
        XBus.getInstance().observe(this, UsrCommDetailActivity.EVENT_REPLY_SUCCESS, new XBusObserver() {
            @Override
            public void onCall(Object var) {
                displayViewModel.loadDataForArcCommFragment();
            }
        }).runOn(XBusThreadModel.mainThread());
    }


    @Override
    public void onStart() {
        super.onStart();
        /**
         * 设置无标题透明背景样式
         */
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void slideToUp(View view){
        Animation slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        slide.setDuration(400);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        view.startAnimation(slide);
        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return mLifecycleRegistry;
    }
    LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
}
