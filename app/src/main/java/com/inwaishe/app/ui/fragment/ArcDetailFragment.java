package com.inwaishe.app.ui.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.inwaishe.app.R;
import com.inwaishe.app.base.LazyFragment;
import com.inwaishe.app.common.CommonData;
import com.inwaishe.app.entity.mainpage.UserInfo;
import com.inwaishe.app.framework.arch.bus.XBus;
import com.inwaishe.app.framework.arch.bus.XBusObserver;
import com.inwaishe.app.ui.ArcDetaileActivity;
import com.inwaishe.app.viewmodel.ArcDetailViewModel;
import com.inwaishe.app.widget.NestedScrollWebView;

import org.jsoup.nodes.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/8/12 0012.
 *
 * 文章内容页面
 */

public class ArcDetailFragment extends LazyFragment {
    public static final String TAG = "ArcDetailFragment";
    private ArcDetailViewModel arcDetailViewModel;
    private NestedScrollWebView mWvDetail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mWvDetail = (NestedScrollWebView) rootView.findViewById(R.id.wvArcDetail);
        WebSettings ws = mWvDetail.getSettings();
        ws.setJavaScriptEnabled(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            //ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        }
        //ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        ws.setPluginState(WebSettings.PluginState.ON);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        ws.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWvDetail.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        mWvDetail.setWebChromeClient(new WebChromeClient());
        if(Build.VERSION.SDK_INT > 20){
            mWvDetail.setTransitionGroup(true);
        }
        mWvDetail.setBackgroundColor(0);
        XBus.getInstance().observe(this, ArcDetaileActivity.EVENT_FONTSIZE_CHANGE, new XBusObserver<Integer>() {
            @Override
            public void onCall(Integer fontSize) {
                float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP
                        ,fontSize,getResources().getDisplayMetrics());
                mWvDetail.getSettings().setDefaultFontSize((int) size);
            }
        });
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

                XBus.getInstance().post(ArcDetaileActivity.EVENT_PLAYBUTTON_SETVISIBLEORNO,null);
                if(TextUtils.isEmpty(document.html())){
                    return;
                }
                long start = System.currentTimeMillis();
                String html = document.outerHtml();
                long end = System.currentTimeMillis();
                Log.e(TAG,"onChanged docment->" + html + "耗时间-》" + (end - start) + "ms");


                FileWriter writer;
                try {
                    String basePath = CommonData.SAVE_BASE_DIR;
                    if(!new File(basePath).exists()){
                        new File(basePath).mkdirs();
                    }
                    File file = new File(basePath,"index01.html");
                    if(!file.exists()){
                        file.createNewFile();
                    }
                    writer = new FileWriter(file.getAbsolutePath());
                    writer.write(html);
                    writer.flush();
                    writer.close();

                    mWvDetail.loadUrl("file:///" + file.getAbsolutePath());
                    WebSettings ws = mWvDetail.getSettings();
                    int fontSize = (int) getResources().getDimension(R.dimen.default_small_text_size);
                    ws.setDefaultFontSize(fontSize);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //mWvDetail.loadData(html, "text/html; charset=UTF-8", "UTF-8");
            }
        });
        if(!"".equals(arcDetailViewModel.getDocumentMutableLiveData().getValue().html())){
            //mWvDetail.loadData(arcDetailViewModel.getDocumentMutableLiveData().getValue().html(), "text/html; charset=UTF-8", null);
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

    @Override
    public void onDestroyView() {
        mWvDetail.destroy();
        super.onDestroyView();
    }
}
