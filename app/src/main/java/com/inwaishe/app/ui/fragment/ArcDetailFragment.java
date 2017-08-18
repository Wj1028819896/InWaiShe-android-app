package com.inwaishe.app.ui.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.inwaishe.app.R;
import com.inwaishe.app.base.LazyFragment;
import com.inwaishe.app.viewmodel.ArcDetailViewModel;

import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2017/8/12 0012.
 *
 * 文章内容页面
 */

public class ArcDetailFragment extends LazyFragment {
    public static final String TAG = "ArcDetailFragment";
    private ArcDetailViewModel arcDetailViewModel;
    private WebView mWvDetail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mWvDetail = (WebView) rootView.findViewById(R.id.wvArcDetail);
        WebSettings ws = mWvDetail.getSettings();
        ws.setJavaScriptEnabled(false);
        ws.setPluginState(WebSettings.PluginState.OFF);
        mWvDetail.setWebViewClient(new WebViewClient());
        mWvDetail.setWebChromeClient(new WebChromeClient());
        return rootView;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_arcdetail;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        Log.e(TAG,"lazyLoad");
        super.lazyLoad();
        if(!isPrepared || !isVisible){
            return;
        }
        arcDetailViewModel = ViewModelProviders.of(getActivity()).get(ArcDetailViewModel.class);
        arcDetailViewModel.getDocumentMutableLiveData().observe(this, new Observer<Document>() {
            @Override
            public void onChanged(@Nullable Document document) {
                if(TextUtils.isEmpty(document.html())){
                    return;
                }
                long start = System.currentTimeMillis();
                String html = document.outerHtml();
                long end = System.currentTimeMillis();
                Log.e(TAG,"onChanged docment->" + "耗时间-》" + (end - start) + "ms");
                mWvDetail.loadData(html, "text/html; charset=UTF-8", "UTF-8");
                //mWvDetail.loadDataWithBaseURL("http://www.inwaishe.com/",html, "text/html; charset=UTF-8", "UTF-8",null);
            }
        });
        if(!"".equals(arcDetailViewModel.getDocumentMutableLiveData().getValue().html())){
            mWvDetail.loadData(arcDetailViewModel.getDocumentMutableLiveData().getValue().html(), "text/html; charset=UTF-8", null);
        }
        loadData();
        isPrepared = false;
    }

    @Override
    protected void loadData() {
        super.loadData();
        Log.e(TAG,"loadData");
        arcDetailViewModel.loadDataForArcDetailFragment();
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        lazyLoad();
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
    }
}
