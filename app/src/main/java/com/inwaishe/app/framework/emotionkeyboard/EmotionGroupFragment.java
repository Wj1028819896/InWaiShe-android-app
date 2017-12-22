package com.inwaishe.app.framework.emotionkeyboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inwaishe.app.R;
import com.inwaishe.app.framework.emotionkeyboard.model.Emotion;
import com.inwaishe.app.framework.emotionkeyboard.adapter.EmotionFragmentPagerAdapter;
import com.inwaishe.app.framework.emotionkeyboard.model.EmotionGroupCofig;
import com.inwaishe.app.framework.emotionkeyboard.widget.BezierRoundView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by WangJing on 2017/12/7.
 * 一个表情组展示Fragment
 */

public class EmotionGroupFragment extends Fragment{

    private EmotionGroupCofig mEmotionCof;
    private ArrayList<Emotion> mEmotions;
    private View mRootView;
    private ViewPager mViewPager;
    private int mPagerNum = 0;
    private EmotionFragmentPagerAdapter mAdapter;
    private BezierRoundView mPointIndicator;
    private Context mContext;
    public static EmotionGroupFragment newInstance(EmotionGroupCofig emotionGroupCofig){
        EmotionGroupFragment fragment = new EmotionGroupFragment();
        Bundle bd = new Bundle();
        bd.putParcelable("EMOTIONGROUPCONFIG",emotionGroupCofig);
        fragment.setArguments(bd);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* 获得表情组数据 */
        mEmotionCof = getArguments().getParcelable("EMOTIONGROUPCONFIG");
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_emotiongroup,container,false);
        initView(mRootView);
        initData();
        initEvent();
        return mRootView;
    }

    private void initEvent() {

    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream is = null;
                    if(!TextUtils.isEmpty(mEmotionCof.assetfile)){
                        is = mContext.getAssets().open(mEmotionCof.assetfile);
                    }else if(!TextUtils.isEmpty(mEmotionCof.filepath)){
                        is = new FileInputStream(mEmotionCof.filepath);
                    }
                    mEmotions = parseXml(is);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                             /* 表情页数 */
                            mPagerNum = mEmotions.size()/20 + (mEmotions.size()%20==0?0:1);
                            FragmentManager fm = getChildFragmentManager();
                            mAdapter = new EmotionFragmentPagerAdapter(fm,mPagerNum,mEmotions);
                            mViewPager.setAdapter(mAdapter);
                            mViewPager.setCurrentItem(0);
                            mPointIndicator.attach2ViewPage(mViewPager);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initView(View mRootView) {
        mViewPager = (ViewPager) mRootView.findViewById(R.id.vpEmotionSelect);
        mPointIndicator = (BezierRoundView) mRootView.findViewById(R.id.pointIndicator);
    }

    /**
     * 解析表情数据
     * @param is
     * @return
     */
    protected ArrayList<Emotion> parseXml(InputStream is) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        ArrayList<Emotion> emotions = new ArrayList<>();
        try {
            // ❷Ⅱ获得DocumentBuilder
            DocumentBuilder builder = factory.newDocumentBuilder();
            // ❸Ⅲ--获得文档对象--
            Document doc = builder.parse(is);
            // ❹Ⅳ获得根元素
            Element element = doc.getDocumentElement();
            NodeList nodeList = element.getChildNodes();

            for(int i = 0; i < nodeList.getLength(); i++){
                Node emotionNode = nodeList.item(i);
                NodeList list = emotionNode.getChildNodes();
                Emotion emotion = new Emotion();
                for(int dex = 0;dex < list.getLength();dex++ ){
                    if("code".equals(list.item(dex).getNodeName())){
                        emotion.code = list.item(dex).getTextContent();
                    }else if("resid".equals(list.item(dex).getNodeName())){
                        emotion.name = list.item(dex).getTextContent();
                        Field f = (Field) R.drawable.class.getDeclaredField(emotion.name);
                        int j = f.getInt(R.drawable.class);
                        emotion.resId = j;
                    }else if("msg".equals(list.item(dex).getNodeName())){
                        emotion.msg = list.item(dex).getTextContent();
                    }else if("filepath".equals(list.item(dex).getNodeName())){
                        emotion.filepath = list.item(dex).getTextContent();
                    }else if("url".equals(list.item(dex).getNodeName())){
                        emotion.url = list.item(dex).getTextContent();
                    }
                }
                emotions.add(emotion);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return emotions;
    }
}
