package com.inwaishe.app.media;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.dou361.ijkplayer.listener.OnShowThumbnailListener;
import com.dou361.ijkplayer.widget.PlayStateParams;
import com.dou361.ijkplayer.widget.PlayerView;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.inwaishe.app.R;
import com.inwaishe.app.net.DownLoad;
import com.inwaishe.app.net.DownloadUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class PlayerViewActivity extends Activity {
    PlayerView player;
    String avid = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        avid = getIntent().getStringExtra("AID");
        showPlayer();
        //showWebView();

    }
    private void getInfo(){

        setContentView(R.layout.fragment_arcdetail);
        new Thread(new Runnable() {
            @Override
            public void run() {

                String appkey="85eb6835b0a1034e";
                String secretkey = "2ad42749773c441109bdc0191257a664";

                try {
                    String cid = "19400215";
                    MessageDigest md5= MessageDigest.getInstance("MD5");
                    String signs = "appkey=" + appkey + "&cid=" + cid + secretkey;
                    byte[] bytes = signs.getBytes("UTF-8");
                    String sign_this = bytesToHex(md5.digest(bytes));
                    String url = "http://interface.bilibili.com/playurl?appkey=" + appkey + "&cid=" + cid + "&sign=" + sign_this;


                    url = "http://bilibili-service.daoapp.io/video/21633168?quailty=4&type=mp4";
                    OkHttpClient okHttpClient = initOkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .build();

                    Call call = okHttpClient.newCall(request);

                    Response response = call.execute();
                    String str = response.body().string();
                    System.out.println(str);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
    private void showPlayer(){

        setContentView(R.layout.simple_player_view_player);

        player = new PlayerView(this)
                .setTitle("什么")
                .setScaleType(PlayStateParams.fitparent)
                .hideMenu(true)
                .forbidTouch(false)
                .setPlayerRotation(270)
                .showThumbnail(new OnShowThumbnailListener() {
                    @Override
                    public void onShowThumbnail(ImageView ivThumbnail) {
                        Glide.with(PlayerViewActivity.this)
                                .load("http://pic2.nipic.com/20090413/406638_125424003_2.jpg")
                                .into(ivThumbnail);
                    }
                });


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String url = DownLoad.getFirstVideo(avid);
                    final String Path = "INWAISHE";
                    final String fileName = avid + ".mp4";

                    DownloadUtil.get().download(url,Path,fileName, new DownloadUtil.OnDownloadListener() {
                        boolean isPlay = false;
                        @Override
                        public void onDownloadSuccess() {

                        }
                        @Override
                        public void onDownloading(int progress) {

                            if(progress > 10 & !isPlay){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        isPlay = true;
                                        File file = new File(Environment.getExternalStorageDirectory() + File.separator + Path, fileName);
                                        String str = file.getAbsolutePath();
                                        player.setPlaySource(str).startPlay();
                                    }
                                });
                            }

                        }
                        @Override
                        public void onDownloadFailed() {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.stopPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stopPlay();
        player.onDestroy();
    }

    private void showWebView(){

        setContentView(R.layout.fragment_arcdetail);
        final WebView webView = (WebView) findViewById(R.id.wvArcDetail);

        WebSettings ws= webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setAllowFileAccess(true);
        ws.setDatabaseEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setSaveFormData(false);
        ws.setAppCacheEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        ws.setLoadWithOverviewMode(false);//<==== 一定要设置为false，不然有声音没图像
        ws.setUseWideViewPort(true);
        ws.setPluginState(WebSettings.PluginState.ON);
        ws.setBuiltInZoomControls(true);// 隐藏缩放按钮
        ws.setUseWideViewPort(true);// 可任意比例缩放
        ws.setLoadWithOverviewMode(true);// setUseWideViewPort方法设置webview推荐使用的窗口。setLoadWithOverviewMode方法是设置webview加载的页面的模式。
        ws.setSavePassword(true);
        ws.setSaveFormData(true);// 保存表单数据
        ws.setDomStorageEnabled(true);
        ws.setSupportMultipleWindows(true);// 新加

        if(Build.VERSION.SDK_INT > 21){
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        String UA = ws.getUserAgentString();
        Log.e("UA","" + UA);

        //headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36");

        //headers.put("","Mozilla/5.0 (Linux; U; Android 2.3.6; zh-cn; GT-S5660 Build/GINGERBREAD) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1 MicroMessenger/4.5.255");

        //ws.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36");




        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if(Build.VERSION.SDK_INT > 22){
                    Log.e("onReceivedError","" + error.getErrorCode() +" == == "  + error.getDescription());
                }else {
                    Log.e("onReceivedError","" + error.toString());
                }

            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                if(Build.VERSION.SDK_INT > 20){
                    Log.e("onReceivedError","" + errorResponse.getStatusCode() + " == == " + errorResponse.getReasonPhrase());
                }else{

                }

            }
        });




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(webView,true);
        }

        //String iframe = "<iframe height=498 width=510 src='http://player.youku.com/embed/XMjk1Nzg0NzA2MA==' frameborder=0 'allowfullscreen'></iframe>";

        //String iframe = "http://player.youku.com/embed/XMjk1Nzg0NzA2MA==";
        //final String iframe = "http://www.ibilibili.com/video/av13191243/";


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String url = DownLoad.getFirstVideo("12941219");
                    Log.e("VEDIO","" + url);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            webView.loadUrl(url);

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 二进制转十六进制
     *
     * @param bytes
     * @return
     */
    public  String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        // 把数组每一字节换成16进制连成md5字符串
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];

            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString().toUpperCase();
    }

    /**
     * 初始化OKHttpClient,设置缓存,设置超时时间,设置打印日志,设置UA拦截器
     */
    public OkHttpClient initOkHttpClient() {
        OkHttpClient mOkHttpClient = null;
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (mOkHttpClient == null) {
            synchronized (PlayerViewActivity.class) {
                if (mOkHttpClient == null) {
                    //设置Http缓存
                    Cache cache = new Cache(new File(PlayerViewActivity.this.getCacheDir(), "HttpCache"), 1024 * 1024 * 10);
                    mOkHttpClient = new OkHttpClient.Builder()
                            .cache(cache)
                            .addInterceptor(interceptor)
                            //.addNetworkInterceptor(new CacheInterceptor())
                            .addNetworkInterceptor(new StethoInterceptor())
                            .retryOnConnectionFailure(true)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .addInterceptor(new UserAgentInterceptor())
                            .followRedirects(true)
                            .build();
                }
            }
        }
        return mOkHttpClient;
    }


    /**
     * 添加UA拦截器，B站请求API需要加上UA才能正常使用
     */
    private static class UserAgentInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request requestWithUserAgent = originalRequest.newBuilder()
                    .removeHeader("User-Agent")
                    .addHeader("User-Agent", "OhMyBiliBili Android Client/2.1 (100332338@qq.com)")
                    .build();
            return chain.proceed(requestWithUserAgent);
        }
    }
}
