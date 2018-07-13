package com.dou361.ijkplayer.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dou361.ijkplayer.utils.BiliDanmukuParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.Danmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.util.IOUtils;
import master.flame.danmaku.ui.widget.DanmakuTextureView;
import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * Created by WangJing on 2018/7/4.
 * 用于显示弹幕的布局
 *
 */

public class IjkDanmakuView extends FrameLayout {
    public IjkDanmakuView(@NonNull Context context) {
        this(context,null);
    }

    public IjkDanmakuView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public IjkDanmakuView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDanmakuView(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public IjkDanmakuView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initDanmakuView(context);
    }

    private DanmakuRenderView mDanmakuRenderView;//弹幕显示
    private DanmakuContext mDanmakuContext;//弹幕上下文
    private ILoader mDanmakuLoader;//弹幕加载器
    private BiliDanmukuParser mDanmakuParser;//xml 弹幕解析器
    private HashMap<Integer, Integer> maxLinesPair;// 弹幕最大行数
    private HashMap<Integer, Boolean> overlappingEnablePair;// 设置是否重叠
    private String mUrl;//弹幕地址

    private BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {

        private Drawable mDrawable;

        /**
         * 在弹幕显示前使用新的text,使用新的text
         * @param danmaku
         * @param fromWorkerThread 是否在工作(非UI)线程,在true的情况下可以做一些耗时操作(例如更新Span的drawblae或者其他IO操作)
         * @return 如果不需重置，直接返回danmaku.text
         */

        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
            if (danmaku.text instanceof Spanned) { // 根据你的条件检查是否需要需要更新弹幕
                // FIXME 这里只是简单启个线程来加载远程url图片，请使用你自己的异步线程池，最好加上你的缓存池
                new Thread() {

                    @Override
                    public void run() {
                        String url = "http://www.bilibili.com/favicon.ico";
                        InputStream inputStream = null;
                        Drawable drawable = mDrawable;
                        if (drawable == null) {
                            try {
                                URLConnection urlConnection = new URL(url).openConnection();
                                inputStream = urlConnection.getInputStream();
                                drawable = BitmapDrawable.createFromStream(inputStream, "bitmap");
                                mDrawable = drawable;
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                IOUtils.closeQuietly(inputStream);
                            }
                        }
                        if (drawable != null) {
                            drawable.setBounds(0, 0, 100, 100);
                            SpannableStringBuilder spannable = createSpannable(drawable);
                            danmaku.text = spannable;
                            if (mDanmakuRenderView != null) {
                                mDanmakuRenderView.invalidateDanmaku(danmaku, false);
                            }
                            return;
                        }
                    }
                }.start();
            }
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
        }
    };

    private DrawHandler.Callback mCallBack = new DrawHandler.Callback() {
        @Override
        public void prepared() {
            //mDanmakuRenderView.start();
        }

        @Override
        public void updateTimer(DanmakuTimer timer) {

        }

        @Override
        public void danmakuShown(BaseDanmaku danmaku) {

        }

        @Override
        public void drawingFinished() {

        }
    };
    /**
     * 创建图文混排模式
     * @param drawable
     * @return
     */
    private SpannableStringBuilder createSpannable(Drawable drawable) {
        String text = "bitmap";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        ImageSpan span = new ImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
        spannableStringBuilder.setSpan(span, 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append("图文混排");
        spannableStringBuilder.setSpan(new BackgroundColorSpan(Color.parseColor("#8A2233B1")), 0, spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableStringBuilder;
    }

    public float getDanmakuScaleTextSize() {
        return mDamakuScaleTextSize;
    }

    public float getDanmakuSpeed() {
        return mDamakuSpeed;
    }

    public float getDanmakuAplha() {
        return mDanmakuAplha;
    }

    public float getDanmakuStroken() {
        return mDanmakuStroken;
    }

    private float mDamakuScaleTextSize = 1.0f;
    /**
     * 设置弹幕字号
     * @param size 取值0.5 - 2.0
     * 0.5 代表一半大 2.0代表双倍大
     */
    public void setDanmakuSize(float size){
        this.mDamakuScaleTextSize = size;
        mDanmakuContext.setScaleTextSize(mDamakuScaleTextSize);
    }

    private float mDamakuSpeed = 1.0f;
    /**
     * 设置弹幕速度，速度决定弹幕显示时间长短
     * @param speed 0.3-2.0
     */
    public void setDanmakuSpeed(float speed){
        this.mDamakuSpeed = speed;
        mDanmakuContext.setScrollSpeedFactor(mDamakuSpeed);
    }

    private float mDanmakuAplha = 1.0f;
   /**
     * 设置弹幕透明度
     * @param aplha 取值0-1
     */
    public void setDanmakuAplha(float aplha){
        this.mDanmakuAplha = aplha;
        mDanmakuContext.setDanmakuTransparency(mDanmakuAplha);
    }

    private float mDanmakuStroken = 0.5f;
    /**
     * 设置弹幕描边宽度
     * @param stroken
     */
    public void setDanmakuStroken(float stroken){
        this.mDanmakuStroken = stroken;
        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN,mDanmakuStroken);
    }
    /**
     * 调整进度
     * @param ms
     */
    public void seekTo(long ms){
        if(ms == 0){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mDanmakuLoader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);
                        URL url = new URL(mUrl);//获得url对象
                        HttpURLConnection conn = (HttpURLConnection)url.openConnection();  //创建URLConnection连接
                        conn.setReadTimeout(5*1000);
                        conn.setRequestMethod("GET");
                        conn.setRequestProperty("Charset", "UTF-8");
                        InputStream inStream = conn.getInputStream();
                        mDanmakuLoader.load(new InflaterInputStream(inStream,new Inflater(true)));
                        mDanmakuParser = new BiliDanmukuParser();
                        IDataSource so = mDanmakuLoader.getDataSource();
                        mDanmakuParser.load(so);
                        mDanmakuRenderView.prepare(mDanmakuParser,mDanmakuContext);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }else{
            try {
                mDanmakuRenderView.seekTo(ms);
            }catch (Exception e){
                //mDanmakuRenderView.start();
            }
        }
    }
    /**
     * 继续播放
     */
    public void start(){
        if(mDanmakuRenderView.isPaused()){
            mDanmakuRenderView.resume();
        }else{
            mDanmakuRenderView.start();
        }
    }
    /**
     * 隐藏弹幕
     */
    public void hideDanmaku(){
        mDanmakuRenderView.hide();
    }

    /**
     * 显示弹幕
     */
    public void showDanmaku(){
        mDanmakuRenderView.show();
    }
    /**
     * 暂停
     */
    public void pause(){
        mDanmakuRenderView.pause();
    }

    /**
     * 释放资源
     */
    public void release(){
        mDanmakuRenderView.stop();
        mDanmakuRenderView.release();
        mDanmakuRenderView = null;
    }
    /**
     * 初始化弹幕布局
     * @param context
     */
    private void initDanmakuView(Context context) {

        mDanmakuRenderView = new DanmakuRenderView(context);
        FrameLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        mDanmakuRenderView.setLayoutParams(lp);
        addView(mDanmakuRenderView);

        mDanmakuContext = DanmakuContext.create();

        // 设置最大行数,从右向左滚动(有其它方向可选)
        maxLinesPair=new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL,6);

        // 设置是否禁止重叠
        overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_LR, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL,true);
        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN,mDanmakuStroken)
                .setDuplicateMergingEnabled(true)//是否启用合并重复弹幕
                .setScrollSpeedFactor(mDamakuSpeed)//设置弹幕滚动速度系数,只对滚动弹幕有效
                .setScaleTextSize(mDamakuScaleTextSize)//字体缩放比例大小
                .setCacheStuffer(new SpannedCacheStuffer(), mCacheStufferAdapter) // 图文混排使用SpannedCacheStuffer  设置缓存绘制填充器，
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);

        mDanmakuRenderView.setCallback(mCallBack);
        mDanmakuRenderView.showFPS(false);
        mDanmakuRenderView.enableDanmakuDrawingCache(true);

    }

    /**
     * 设置B站地址弹幕网络
     * （b站弹幕有压缩）
     * @param damakuUrl
     */
    public void setBilibiliDanmukuDataSource(final String damakuUrl){
        mUrl = damakuUrl;
    }
    /**
     * 渲染danmuview
     */
    private static class DanmakuRenderView extends DanmakuView {
        public DanmakuRenderView(Context context) {
            super(context);
        }
        public DanmakuRenderView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
        public DanmakuRenderView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }
    }
}
