package com.inwaishe.app.ui.display;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.inwaishe.app.R;
import com.inwaishe.app.base.BaseActivity;
import com.inwaishe.app.common.AppUtils;
import com.inwaishe.app.common.CommonData;
import com.inwaishe.app.common.DialogUtils;
import com.inwaishe.app.entity.mainpage.Articlelnfo;
import com.inwaishe.app.framework.arch.bus.XBus;
import com.inwaishe.app.framework.arch.bus.XBusObserver;
import com.inwaishe.app.framework.emotionkeyboard.EmotionKeyBoardMainDialogFragment;
import com.inwaishe.app.framework.webviewstyleselector.StyleSelectDialogFragment;
import com.inwaishe.app.http.OkCookieJar;
import com.inwaishe.app.http.OkHttpUtils;
import com.inwaishe.app.ui.ArcDetaileActivity;
import com.inwaishe.app.ui.UsrCommDetailActivity;
import com.inwaishe.app.ui.fragment.SocialShareDialogFragment;
import com.inwaishe.app.viewmodel.DisplayViewModel;
import com.inwaishe.app.widget.NestedScrollWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
@SuppressLint("JavascriptInterface")
public class DisplayActivity extends BaseActivity implements LifecycleRegistryOwner,EmotionKeyBoardMainDialogFragment.DialogFragmentDataCallback {
    public static final String TAG = "DisplayActivity";
    private Articlelnfo mArticleInfo;
    private NestedScrollWebView mWebView;
    private TextView mTvTitle;
    private ImageView mIvBack;
    private ImageView mIvSize;
    private TextView mTvReadNum;
    private TextView mTvReplyNum;
    private RelativeLayout mRlReply;
    private RelativeLayout mRlShare;
    private LinearLayout mLlTopBar;
    private Context THIS;

    private DisplayViewModel mViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        THIS = this;
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mWebView = (NestedScrollWebView) findViewById(R.id.wvDetail);
        mTvTitle = (TextView) findViewById(R.id.tvTitle);
        mIvBack = (ImageView) findViewById(R.id.ivBack);
        mIvSize = (ImageView) findViewById(R.id.ivSetting);
        mTvReadNum = (TextView) findViewById(R.id.tvReadNum);
        mRlReply = (RelativeLayout) findViewById(R.id.rlReply);
        mRlShare = (RelativeLayout) findViewById(R.id.rlShare);
        mTvReplyNum = (TextView) findViewById(R.id.tvReplyNum);
        mLlTopBar = (LinearLayout) findViewById(R.id.topTitleBar);
        setWebView();
    }
    private void setWebView() {
        WebSettings ws = mWebView.getSettings();
        ws.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JSInterface(),"inwaishe");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            //ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        }
        //ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        ws.setPluginState(WebSettings.PluginState.ON);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        ws.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient());
        if(Build.VERSION.SDK_INT > 20){
            mWebView.setTransitionGroup(true);
        }
        mWebView.setBackgroundColor(0);
        XBus.getInstance().observe(this, ArcDetaileActivity.EVENT_FONTSIZE_CHANGE, new XBusObserver<Integer>() {
            @Override
            public void onCall(Integer fontSize) {
                float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP
                        ,fontSize,getResources().getDisplayMetrics());
                mWebView.getSettings().setDefaultFontSize((int) size);
            }
        });
    }

    public class JSInterface{
        /**
         * webView 回调App 显示大图
         * @param position
         * @param urls
         */
        @JavascriptInterface
        public void showImage(String position,String urls){
            Intent it = new Intent(DisplayActivity.this,ImageShowActivity.class);
            it.putExtra(CommonData.PIC_INDEX,Integer.valueOf(position));
            try {
                JSONArray array = new JSONArray(urls);
                String[] imgs = new String[array.length()];
                for(int i = 0;i < array.length();i++){
                    imgs[i] = (String) array.get(i);
                }
                it.putExtra(CommonData.PIC_URLS,imgs);

                int artReadTimes = mViewModel.getArticlelnfoMutableLiveData().getValue().artReadTimes;
                int usrCommNum = mViewModel.getArticlelnfoMutableLiveData().getValue().usrCommNum;
                String artTitle = mArticleInfo.artTitle;
                String artAuthor = mArticleInfo.artAuthor;
                it.putExtra(CommonData.PIC_ARTTITLE,artTitle);
                it.putExtra(CommonData.PIC_ARTREADNUM,artReadTimes);
                it.putExtra(CommonData.PIC_ARTCOMMENTNUM,usrCommNum);
                it.putExtra(CommonData.PIC_ARTAUTHOR,artAuthor);

                DisplayActivity.this.startActivity(it);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void initData() {
        mArticleInfo = (Articlelnfo) getIntent().getSerializableExtra("INFO");
        mViewModel = ViewModelProviders.of(this).get(DisplayViewModel.class);
        mArticleInfo.usrCommNum = 10;
        mViewModel.init(mArticleInfo);

        mTvTitle.setText(mArticleInfo.artTitle);

        loadWebView();
    }

    private void loadWebView() {
        mViewModel.getDocumentMutableLiveData().observe(this, new Observer<Document>() {
            @Override
            public void onChanged(@Nullable Document document) {
                if(TextUtils.isEmpty(document.html())){
                    return;
                }
                /********/
                mTvReadNum.setText("" + mViewModel.getArticlelnfoMutableLiveData().getValue().artReadTimes);
                mTvReplyNum.setText("" + mViewModel.getArticlelnfoMutableLiveData().getValue().usrCommNum);
                /********/
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

                    mWebView.loadUrl("file:///" + file.getAbsolutePath());
                    WebSettings ws = mWebView.getSettings();
                    int fontSize = (int) getResources().getDimension(R.dimen.default_small_text_size);
                    ws.setDefaultFontSize(fontSize);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //mWvDetail.loadData(html, "text/html; charset=UTF-8", "UTF-8");
            }
        });

        mViewModel.loadDataForArcDetailFragment();
    }

    private void initEvent() {
        mRlShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialShareDialogFragment socialShareDialogFragment = new SocialShareDialogFragment();
                socialShareDialogFragment.setEventCallBack(new SocialShareDialogFragment.EventCallBack() {
                    @Override
                    public void callBack(int eventCode) {
                        switch (eventCode){
                            case SocialShareDialogFragment.EVENT_COPYLIK:
                                AppUtils.copyToClipeBoard(THIS,"" + mArticleInfo.artSrc );
                                Toast.makeText(THIS,"成功复制到粘贴板",Toast.LENGTH_SHORT).show();
                                break;
                            case SocialShareDialogFragment.EVENT_SHAREWXCIRCLE:
                                AppUtils.shareWebPageToWxCircle(THIS,mWebView,mArticleInfo.artTitle,mArticleInfo.artSrc,mArticleInfo.artDesc);
                                break;
                            case SocialShareDialogFragment.EVENT_SHAREWXFRIENDS:
                                AppUtils.shareWebPageToWxFriends(THIS,mWebView,mArticleInfo.artTitle,mArticleInfo.artSrc,mArticleInfo.artDesc);
                                break;
                        }
                    }
                });
                socialShareDialogFragment.show(getFragmentManager(),"SocialShareDialogFragment");
            }
        });

        mIvSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(THIS,"字体设置",Toast.LENGTH_SHORT).show();
                StyleSelectDialogFragment styleSelectDialogFragment = new StyleSelectDialogFragment();
                styleSelectDialogFragment.show(getSupportFragmentManager(),"StyleSelectDialogFragment");
                styleSelectDialogFragment.setfontSizeChangeListener(new StyleSelectDialogFragment.FontSizeChangeListener() {
                    @Override
                    public void onChanged(int position, String scaleWord) {
                        int fontSize = 8;
                        switch (position){
                            case 0:
                                fontSize = 12;
                                break;
                            case 1:
                                fontSize = 16;
                                break;
                            case 2:
                                fontSize = 20;
                                break;
                            case 3:
                                fontSize = 24;
                                break;
                        }
                        float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP
                                ,fontSize,getResources().getDisplayMetrics());
                        mWebView.getSettings().setDefaultFontSize((int) size);
                    }
                });
            }
        });

        mRlReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayCommentDialogFragment displayCommentDialogFragment = new DisplayCommentDialogFragment();
                displayCommentDialogFragment.show(getSupportFragmentManager(),"DisplayCommentDialogFragment");
            }
        });

        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mWebView.setOnScrollChangedCallback(new NestedScrollWebView.OnScrollChangedCallback() {
            boolean isHide = false;
            @Override
            public void onScroll(int dx, int dy) {
                if(dy > 100 && !isHide){
                    Animation slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                            0.0f, Animation.RELATIVE_TO_SELF, -1.2f);
                    slide.setDuration(400);
                    slide.setFillAfter(true);
                    slide.setFillEnabled(true);
                    mLlTopBar.startAnimation(slide);
                    isHide = true;
                }else if(dy < -100 && isHide){
                    Animation slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                            -1.2f, Animation.RELATIVE_TO_SELF, 0.0f);
                    slide.setDuration(400);
                    slide.setFillAfter(true);
                    slide.setFillEnabled(true);
                    mLlTopBar.startAnimation(slide);
                    isHide = false;
                }
            }
        });

    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return mLifecycleRegistry;
    }

    LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);

    @Override
    public String getCommentText() {
        return "";
    }

    @Override
    public void setCommentText(String commentTextTemp) {
        if(TextUtils.isEmpty(commentTextTemp)){
            return;
        }else if(commentTextTemp.length() < 10){
            Toast.makeText(this,"回复不能少于10个字符请重新输入",Toast.LENGTH_SHORT).show();
            return;
        }
        DialogUtils.showProgressDialog(this,"正在回复..");
        addCommentForArtical(commentTextTemp);
    }

    /**
     * 添加评论给文章
     * @param commentTextTemp
     */
    private void addCommentForArtical(String commentTextTemp) {
        OkHttpClient ok = OkHttpUtils.make(this).okHttpClient;
        //获取需要提交的CookieStr
        StringBuilder cookieStr = new StringBuilder();
        //从缓存中获取Cookie
        List<Cookie> cookies = ((OkCookieJar)ok.cookieJar()).getLoginCookies("www.inwaishe.com");
        //将Cookie数据弄成一行
        for(Cookie cookie : cookies){
            cookieStr.append(cookie.name()).append("=").append(cookie.value()+";");
        }
        System.out.println(cookieStr.toString());

        FormBody.Builder formbuilder = new FormBody.Builder();

        Articlelnfo.ReplyFrom fastpostform = mViewModel.getArticlelnfoMutableLiveData().getValue().replyFrom;
        fastpostform.message = commentTextTemp;

        formbuilder.add("message",fastpostform.message);
        formbuilder.add("posttime",fastpostform.posttime);
        formbuilder.add("formhash",fastpostform.formhash);
        formbuilder.add("usesig",fastpostform.usesig);
        formbuilder.add("subject",fastpostform.subject);

        RequestBody requestBody = formbuilder.build();

        Request request = new Request.Builder()
                .url(fastpostform.action)
                .header("Cookie",cookieStr.toString())
                .header("Referer",mViewModel.getArticlelnfoMutableLiveData().getValue().artSrc)
                .post(requestBody).build();

        ok.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DisplayActivity.this,"添加失败",Toast.LENGTH_SHORT).show();
                        DialogUtils.close();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogUtils.close();
                    }
                });
                if (response.isSuccessful()){
                    String back = response.body().string();
                    int a = 1;
                    XBus.getInstance().post(UsrCommDetailActivity.EVENT_REPLY_SUCCESS,"success");
                }
            }
        });
    }
}
