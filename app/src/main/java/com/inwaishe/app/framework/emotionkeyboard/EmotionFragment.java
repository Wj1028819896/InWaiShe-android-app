package com.inwaishe.app.framework.emotionkeyboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inwaishe.app.R;
import com.inwaishe.app.framework.emotionkeyboard.model.Emotion;
import com.inwaishe.app.framework.emotionkeyboard.adapter.EmotionAdapter;

import java.util.ArrayList;

/**
 * Created by WangJing on 2017/12/7.
 *
 */

public class EmotionFragment extends Fragment{

    private ArrayList<Emotion> mEmotions;
    private View mRootView;
    private RecyclerView mRvEmotions;
    private EmotionAdapter mAdapter;
    private Context mContext;

    public static EmotionFragment newInstance(ArrayList<Emotion> emotions){
        EmotionFragment fragment = new EmotionFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("EMOTIONS",emotions);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* 获得表情数据 */
        mEmotions = getArguments().getParcelableArrayList("EMOTIONS");
        //添加退格键
        Emotion emotion = new Emotion();
        emotion.code = "[del]";
        emotion.resId = R.drawable.del;
        emotion.msg = "退格";
        mEmotions.add(emotion);
        //
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_emotiongrid,container,false);
        initView(mRootView);
        initData();
        initEvent();
        return mRootView;
    }

    private void initView(View mRootView) {

        mRvEmotions = (RecyclerView) mRootView.findViewById(R.id.rvEmotionList);

    }

    private void initData() {
        mAdapter = new EmotionAdapter(mContext,mEmotions);
        GridLayoutManager g = new GridLayoutManager(mContext,7);
        mRvEmotions.setLayoutManager(g);
        mRvEmotions.setAdapter(mAdapter);
    }

    private void initEvent() {

    }
}
