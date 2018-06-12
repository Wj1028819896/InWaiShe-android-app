package com.inwaishe.app.ui;

import android.animation.Animator;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.PowerManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dou361.ijkplayer.bean.VideoijkBean;
import com.dou361.ijkplayer.listener.OnShowThumbnailListener;
import com.dou361.ijkplayer.widget.PlayStateParams;
import com.dou361.ijkplayer.widget.PlayerView;
import com.flyco.tablayout.SlidingTabLayout;
import com.inwaishe.app.R;
import com.inwaishe.app.base.AppBarLayoutOverScrollViewBehavior;
import com.inwaishe.app.base.AppBarStateChangeListener;
import com.inwaishe.app.base.BaseActivity;
import com.inwaishe.app.base.LazyFragment;
import com.inwaishe.app.common.AppUtils;
import com.inwaishe.app.common.DialogUtils;
import com.inwaishe.app.common.GlideUtils;
import com.inwaishe.app.common.MediaUtils;
import com.inwaishe.app.common.XAnimatorListener;
import com.inwaishe.app.dataprovider.DataProvider;
import com.inwaishe.app.dataprovider.BiliVedioDataProvider;
import com.inwaishe.app.entity.mainpage.Articlelnfo;
import com.inwaishe.app.entity.video.BiliVideoInfo;
import com.inwaishe.app.framework.activitytrans.EnterActivityTool;
import com.inwaishe.app.framework.arch.bus.XBus;
import com.inwaishe.app.framework.arch.bus.XBusObserver;
import com.inwaishe.app.framework.arch.bus.XBusThreadModel;
import com.inwaishe.app.framework.webviewstyleselector.StyleSelectDialogFragment;
import com.inwaishe.app.http.OkCookieJar;
import com.inwaishe.app.media.MediaPlayerActivity;
import com.inwaishe.app.http.OkHttpUtils;
import com.inwaishe.app.ui.fragment.ArcCommFragment;
import com.inwaishe.app.ui.fragment.ArcDetailFragment;
import com.inwaishe.app.framework.emotionkeyboard.EmotionKeyBoardMainDialogFragment;
import com.inwaishe.app.ui.fragment.SocialShareDialogFragment;
import com.inwaishe.app.viewmodel.ArcDetailViewModel;
import com.inwaishe.app.widget.CircularAnim;
import com.inwaishe.app.widget.RipperSeziView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED;
import static android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL;

public class ArcDetaileActivity extends BaseActivity implements EmotionKeyBoardMainDialogFragment.DialogFragmentDataCallback,LifecycleRegistryOwner{

    private AppBarLayout mAppBarLayout;
    public static String EVENT_FONTSIZE_CHANGE = "EVENT_FONTSIZE_CHANGE";
    public static String EVENT_PLAYBUTTON_SETVISIBLEORNO = "EVENT_PLAYBUTTON_SETVISIBLEORNO";
    private Toolbar mToolbar;
    private ActionMenuView mActionMenuView;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Articlelnfo mArticleInfo;
    private ImageView mIvImgBg;
    private ViewPager mVpContent;
    private FloatingActionButton mFbPlay;
    private SlidingTabLayout slidingTabLayout;
    private ArcDetailViewModel arcDetailViewModel;
    private LazyFragment mArcDetailFragment;
    private LazyFragment mArcCommFragment;
    private ArrayList<LazyFragment> lazyFragmentArrayList;
    private ArcDetailPagerAdapter arcDetailPagerAdapter;
    private String arcType = "";//文章类型
    private Button mBtnShare;
    private TextView mToolBarTitle;

    private TextView mBtnAddComm;
    private AppBarStateChangeListener.State crxState;

    private View rootView;
    private PlayerView player;
    private Context mContext;
    private PowerManager.WakeLock wakeLock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().from(this).inflate(R.layout.activity_arc_detaile, null);
        setContentView(rootView);
        /**虚拟按键的隐藏方法*/
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                //比较Activity根布局与当前布局的大小
                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
                if (heightDiff > 100) {
                    //大小超过100时，一般为显示虚拟键盘事件
                    rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                } else {
                    //大小小于100时，为不显示虚拟键盘或虚拟键盘隐藏
                    rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                }
            }
        });

        /**常亮*/
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "liveTAG");
        wakeLock.acquire();

        mContext = this;
        initView();
        initToobar();
        initData();
        initEvent();

        EnterActivityTool.getEnterTransition(this,mIvImgBg,savedInstanceState)
                .setAnimatorDuration(500)
                .setTimeInterpolator(new DecelerateInterpolator())
                .startAnimate();
    }

    private void initToobar() {
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.CollapsingToolbarLayout);
        mToolbar = (Toolbar) findViewById(R.id.toolBar);
        mToolBarTitle = (TextView) findViewById(R.id.toolBarTitle);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.AppBarLayout);
        mBtnAddComm = (TextView) findViewById(R.id.edit_comment);
        mFbPlay = (FloatingActionButton) findViewById(R.id.fbtnPlay);
        setSupportActionBar(mToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }

        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener(mCollapsingToolbarLayout) {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                crxState = state;
                switch (state){
                    case EXPANDED:
                        break;
                    case COLLAPSED:
                        break;
                    case IDLE:
                        break;
                    case SHOWNSCRIM:
                        if(player != null){
                            //播放画面时 播放图标不在出现
                            break;
                        }
                        mFbPlay.animate()
                                .setDuration(250)
                                .setInterpolator(new AccelerateInterpolator())
                                .scaleX(1.0F)
                                .scaleY(1.0F);
                        break;
                    case HIDESCRIM:
                        mFbPlay.animate()
                                .setDuration(250)
                                .setInterpolator(new AccelerateInterpolator())
                                .scaleX(0.0F)
                                .scaleY(0.0F);
                        break;
                }
            }
        });
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.holo_blue_dark));

        mActionMenuView.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.toolbar_menu_font){
                    Toast.makeText(ArcDetaileActivity.this,"字体设置",Toast.LENGTH_SHORT).show();
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
                            XBus.getInstance().post(EVENT_FONTSIZE_CHANGE,Integer.valueOf(fontSize));
                        }
                    });
                }
                return true;
            }
        });
    }

    private void initView() {

        mIvImgBg = (ImageView) findViewById(R.id.ivTopImgBg);
        mVpContent = (ViewPager) findViewById(R.id.vpContent);
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabSlide);
        mBtnShare = (Button) findViewById(R.id.btnShare);

        mActionMenuView = (ActionMenuView) findViewById(R.id.amv_toolbar);



    }

    private void initData() {

        mArticleInfo = (Articlelnfo) getIntent().getSerializableExtra("ARTICLE_INFO");
        arcType = mArticleInfo.arcType;
        GlideUtils.disPlayUrl(this,mArticleInfo.artImageUrl,mIvImgBg);
//        mAppBarLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                GlideUtils.disPlayUrlBackground(ArcDetaileActivity.this,mArticleInfo.artImageUrl,mAppBarLayout);
//                return true;
//            }
//        });
        mCollapsingToolbarLayout.setTitle("");
        mToolbar.setTitle("");
        mToolBarTitle.setText("" + mArticleInfo.artTitle);

        arcDetailViewModel = ViewModelProviders.of(this).get(ArcDetailViewModel.class);
        arcDetailViewModel.init(mArticleInfo);
        lazyFragmentArrayList = new ArrayList<>();
        mArcDetailFragment = new ArcDetailFragment();
        mArcCommFragment = new ArcCommFragment();
        lazyFragmentArrayList.add(mArcDetailFragment);
        lazyFragmentArrayList.add(mArcCommFragment);

        arcDetailPagerAdapter = new ArcDetailPagerAdapter(getSupportFragmentManager());
        mVpContent.setAdapter(arcDetailPagerAdapter);

        slidingTabLayout.setViewPager(mVpContent);


        XBus.getInstance().observe(this, EVENT_PLAYBUTTON_SETVISIBLEORNO, new XBusObserver() {
            @Override
            public void onCall(Object var) {
                changePlayButtonVisible();
            }
        }).runOn(XBusThreadModel.mainThread());
    }

    /**
     * 改变播放按钮是否显示
     */
    public void changePlayButtonVisible(){
        arcType = mArticleInfo.arcType;
        if(arcType.equals("视频")){
            mFbPlay.setVisibility(View.VISIBLE);
        }else{
            mFbPlay.setVisibility(View.GONE);
        }
    }

    @Override
    public String getCommentText() {
        return "";
    }

    @Override
    public void setCommentText(String commentTextTemp) {
        //评论返回
        if(!TextUtils.isEmpty(commentTextTemp)){
            DialogUtils.showProgressDialog(this,"正在添加评论...");
            addCommentForArtical(commentTextTemp);
        }
    }

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

        Articlelnfo.ReplyFrom cform = arcDetailViewModel.getArticlelnfoMutableLiveData().getValue().replyFrom;

        cform.message = commentTextTemp;
        formbuilder.add("referer",cform.referer);
        formbuilder.add("portal_referer",cform.portal_referer);
        formbuilder.add("aid",cform.aid);
        formbuilder.add("id",cform.id);
        formbuilder.add("idtype",cform.idtype);
        formbuilder.add("formhash",cform.formhash);
        formbuilder.add("commentsubmit",cform.commentsubmit);
        formbuilder.add("commentsubmit_btn",cform.commentsubmit_btn);
        formbuilder.add("replysubmit",cform.replysubmit);
        formbuilder.add("message",cform.message);

        RequestBody requestBody = formbuilder.build();

        Request request = new Request.Builder()
                .url(cform.action)
                .header("Cookie",cookieStr.toString())
                .header("Referer",arcDetailViewModel.getArticlelnfoMutableLiveData().getValue().artSrc)
                .post(requestBody).build();

        ok.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ArcDetaileActivity.this,"添加失败",Toast.LENGTH_SHORT).show();
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

    private class ArcDetailPagerAdapter extends FragmentPagerAdapter{

        private String[] mTitles = new String[]{"详情","评论"};

        public ArcDetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return lazyFragmentArrayList.get(position);
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }


    private void initEvent() {

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                return false;
            }
        });

        mFbPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if("视频".equals(arcDetailViewModel.getArticlelnfoMutableLiveData().getValue().arcType)
                        && !TextUtils.isEmpty(arcDetailViewModel.getArticlelnfoMutableLiveData().getValue().avid)){
                    final CharSequence[] charSequences;
                    final int values[];
                    if(!TextUtils.isEmpty(mArticleInfo.youku)){
                        charSequences = new CharSequence[]{"Bili站(推荐)","优酷"};
                        values = new int[]{MediaPlayerActivity.TYPE_IJKPLAYER,MediaPlayerActivity.TYPE_YOUKU_WEBVIEW};
                    }else{
                        playVideoByBili(arcDetailViewModel.getArticlelnfoMutableLiveData().getValue().avid);
                        return;
                    }
                    AlertDialog.Builder builder= new AlertDialog.Builder(ArcDetaileActivity.this);
                    builder.setTitle("视频来源")
                            .setIcon(R.mipmap.logo)
                            .setItems(charSequences, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which == 1){
                                        Intent it = new Intent(ArcDetaileActivity.this,MediaPlayerActivity.class);
                                        it.putExtra("YOUKU",arcDetailViewModel.getArticlelnfoMutableLiveData().getValue().youku);
                                        it.putExtra("AID",arcDetailViewModel.getArticlelnfoMutableLiveData().getValue().avid);
                                        it.putExtra(MediaPlayerActivity.MEDIA_TYPE,values[which]);
                                        ArcDetaileActivity.this.startActivity(it);
                                    }else{
                                        playVideoByBili(arcDetailViewModel.getArticlelnfoMutableLiveData().getValue().avid);
                                    }
                                }
                            }).show();

                }

            }
        });


        mBtnAddComm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DataProvider.isNeedLogin(ArcDetaileActivity.this)){
                    return;
                }
                EmotionKeyBoardMainDialogFragment emotionKeyBoardMainDialogFragment = new EmotionKeyBoardMainDialogFragment();
                Bundle data = new Bundle();
                data.putString("hint","添加评论...");
                emotionKeyBoardMainDialogFragment.setArguments(data);
                emotionKeyBoardMainDialogFragment.show(getSupportFragmentManager(), "CommentDialogFragment");
            }
        });

        mBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialShareDialogFragment socialShareDialogFragment = new SocialShareDialogFragment();
                socialShareDialogFragment.setEventCallBack(new SocialShareDialogFragment.EventCallBack() {
                    @Override
                    public void callBack(int eventCode) {
                        switch (eventCode){
                            case SocialShareDialogFragment.EVENT_COPYLIK:
                                AppUtils.copyToClipeBoard(ArcDetaileActivity.this,"" + mArticleInfo.artSrc );
                                Toast.makeText(ArcDetaileActivity.this,"成功复制到粘贴板",Toast.LENGTH_SHORT).show();
                                break;
                            case SocialShareDialogFragment.EVENT_SHAREWXCIRCLE:
                                AppUtils.shareWebPageToWxCircle(ArcDetaileActivity.this,mIvImgBg,mArticleInfo.artTitle,mArticleInfo.artSrc,mArticleInfo.artDesc);
                                break;
                            case SocialShareDialogFragment.EVENT_SHAREWXFRIENDS:
                                AppUtils.shareWebPageToWxFriends(ArcDetaileActivity.this,mIvImgBg,mArticleInfo.artTitle,mArticleInfo.artSrc,mArticleInfo.artDesc);
                                break;
                        }
                    }
                });
                socialShareDialogFragment.show(getFragmentManager(),"SocialShareDialogFragment");
            }
        });
    }

    private void playVideoByBili(final String avid) {
        final AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) mCollapsingToolbarLayout.getLayoutParams();
        layoutParams.setScrollFlags(0);
        final CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        params.setBehavior(new AppBarLayoutOverScrollViewBehavior(false));

        mAppBarLayout.setExpanded(true,true);
        final Thread task = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final BiliVideoInfo biliVideoInfo = BiliVedioDataProvider.getFirstVideo(avid);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rootView.findViewById(R.id.app_video_box).setVisibility(View.VISIBLE);
                            player = new PlayerView(ArcDetaileActivity.this,rootView){

                                @Override
                                public PlayerView startPlay() {
                                    //播放画面不可滑出屏幕
                                    layoutParams.setScrollFlags(0);
                                    //AppBarLayout behavior 不拦截事件，防止声音亮度手势被拦截
                                    params.setBehavior(new AppBarLayoutOverScrollViewBehavior(false));
                                    return super.startPlay();
                                }

                                @Override
                                public PlayerView pausePlay() {
                                    layoutParams.setScrollFlags(SCROLL_FLAG_SCROLL|SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
                                    params.setBehavior(new AppBarLayoutOverScrollViewBehavior(true));
                                    return super.pausePlay();
                                }

                                @Override
                                public PlayerView stopPlay() {
                                    layoutParams.setScrollFlags(SCROLL_FLAG_SCROLL|SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
                                    params.setBehavior(new AppBarLayoutOverScrollViewBehavior(true));
                                    return super.stopPlay();
                                }
                                @Override
                                public PlayerView onConfigurationChanged(Configuration newConfig) {
                                    if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                                        //隐藏干扰控件 显示topBar
                                        hideHideTopBar(false);
                                        mFbPlay.setVisibility(View.GONE);
                                        rootView.findViewById(R.id.comment_bar).setVisibility(View.GONE);
                                    } else {
                                        hideHideTopBar(true);
                                        mFbPlay.setVisibility(View.VISIBLE);
                                        rootView.findViewById(R.id.comment_bar).setVisibility(View.VISIBLE);
                                    }
                                    return super.onConfigurationChanged(newConfig);
                                }

                                @Override
                                public PlayerView operatorPanl() {
                                    if(getBottonBarView().getVisibility() != View.VISIBLE){
                                        mToolbar.setVisibility(View.VISIBLE);
                                    }else{
                                        mToolbar.setVisibility(View.GONE);
                                    }
                                    if(getTopBarView().getVisibility() == View.VISIBLE){
                                        mToolbar.setVisibility(View.GONE);
                                    }
                                    return super.operatorPanl();
                                }

                                @Override
                                public PlayerView toggleProcessDurationOrientation() {
                                    hideSteam(getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                    return setProcessDurationOrientation(getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ? PlayStateParams.PROCESS_PORTRAIT : PlayStateParams.PROCESS_LANDSCAPE);
                                }

                                @Override
                                public PlayerView setPlaySource(List<VideoijkBean> list) {
                                    return super.setPlaySource(list);
                                }
                            }
                                    .setTitle("" + biliVideoInfo.title)
                                    .setScaleType(PlayStateParams.fitparent)
                                    .setProcessDurationOrientation(PlayStateParams.PROCESS_PORTRAIT)
                                    .forbidTouch(false)
                                    .hideSteam(true)
                                    .setUseAndroidMediaplayer(true)
                                    .hideCenterPlayer(false)
                                    .hideHideTopBar(true)
                                    .showThumbnail(new OnShowThumbnailListener() {
                                        @Override
                                        public void onShowThumbnail(ImageView ivThumbnail) {
                                            GlideUtils.disPlayUrl(mContext,mArticleInfo.artImageUrl,ivThumbnail);
                                        }
                                    })
                                    .setPlaySource(biliVideoInfo.url).startPlay();
                                    player.setBrightness(50);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            rootView.findViewById(R.id.ripperView).setVisibility(View.VISIBLE);
            final RipperSeziView ripperSeziView = ((RipperSeziView)rootView.findViewById(R.id.ripperView));
            ripperSeziView.setAnimatorListener(new RipperSeziView.RAnimatorListener() {
                @Override
                public void onAnimationEnd() {
                    task.start();
                    ripperSeziView.setVisibility(View.GONE);
                }
                @Override
                public void onAnimationStart() {
                    //mFbPlay.animate().setDuration(250).scaleY(0.0F).scaleX(0.0F);
                }
            });
            ripperSeziView.start();
            mFbPlay.animate().setDuration(250).scaleY(0.0F).scaleX(0.0F);
        }else{
            CircularAnim
                    .show(rootView.findViewById(R.id.app_video_box))
                    .triggerView(mFbPlay)
                    .animateViewParent(mIvImgBg)
                    .duration(1000)
                    .animaterListener(new XAnimatorListener(){
                        @Override
                        public void onAnimationStartX(Animator animation) {
                            super.onAnimationStartX(animation);
                            mFbPlay.animate()
                                    .setDuration(250)
                                    .scaleX(0.0F)
                                    .scaleY(0.0F);
                        }
                    })
                    .go(new CircularAnim.OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd() {
                            task.start();
                        }
                    });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
        /**demo的内容，恢复系统其它媒体的状态*/
        MediaUtils.muteAudioFocus(mContext, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
        /**demo的内容，暂停系统其它媒体的状态*/
        MediaUtils.muteAudioFocus(mContext, false);
        /**demo的内容，激活设备常亮状态*/
        if (wakeLock != null) {
            wakeLock.acquire();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (player != null) {
            player.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        if (player != null && player.onBackPressed()) {
            return;
        }
        super.onBackPressed();
        /**demo的内容，恢复设备亮度状态*/
        if (wakeLock != null) {
            wakeLock.release();
        }
    }

    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);
    @Override
    public LifecycleRegistry getLifecycle() {
        return mRegistry;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.arcdetail_menu,mActionMenuView.getMenu());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e("onOptionsItemSelected","onOptionsItemSelected->id = " + item.getItemId());
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
