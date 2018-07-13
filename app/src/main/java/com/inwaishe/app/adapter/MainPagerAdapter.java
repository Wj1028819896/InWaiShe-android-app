package com.inwaishe.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inwaishe.app.R;
import com.inwaishe.app.common.GlideUtils;
import com.inwaishe.app.entity.mainpage.Articlelnfo;
import com.inwaishe.app.entity.mainpage.BannerInfo;
import com.inwaishe.app.entity.mainpage.MainPageInfo;
import com.inwaishe.app.entity.mainpage.ShareInfo;
import com.inwaishe.app.framework.activitytrans.FromActivityTool;
import com.inwaishe.app.ui.ArcDetaileActivity;
import com.inwaishe.app.ui.PhotoCenterActivity;
import com.inwaishe.app.ui.display.DisplayActivity;
import com.inwaishe.app.viewmodel.PhotoCenterViewModel;
import com.inwaishe.app.widget.VerticalTextview;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class MainPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEWTYPE_VIEWPAGER = 0;//轮播
    public static final int VIEWTYPE_ENTRANCE = 1;//入口
    public static final int VIEWTYPE_WELFARE = 2;//福利
    public static final int VIEWTYPE_RECOMMANDS = 3;//推荐
    public static final int VIEWTYPE_HOTARTS = 4;//热门
    public static final int VIEWTYPE_LIST = 5;//文章List

    public static final int VIEWTYPE_TITLE = 10;//标题

    private Context mContext;
    private LayoutInflater mInflater;
    private MainPageInfo mainPageinfo;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView mRecyclerView;
    public MainPagerAdapter(RecyclerView recyclerView, Context context, MainPageInfo mainPageInfo){
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mainPageinfo = mainPageInfo;
        this.mRecyclerView = recyclerView;

        initRecyclerView();
    }

    private void initRecyclerView() {


        gridLayoutManager = new GridLayoutManager(mContext, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = mRecyclerView.getAdapter().getItemViewType(position);
                int spanSize = 1;
                switch (viewType) {
                    case MainPagerAdapter.VIEWTYPE_VIEWPAGER:
                        spanSize = 2;
                        break;
                    case MainPagerAdapter.VIEWTYPE_ENTRANCE:
                        spanSize = 2;
                        break;
                    case MainPagerAdapter.VIEWTYPE_WELFARE:
                        spanSize = 2;
                        break;
                    case MainPagerAdapter.VIEWTYPE_TITLE:
                        spanSize = 2;
                        break;
                    case MainPagerAdapter.VIEWTYPE_RECOMMANDS:
                        spanSize = 2;
                        break;
                    case MainPagerAdapter.VIEWTYPE_HOTARTS:
                        spanSize = 2;
                        break;
                    case MainPagerAdapter.VIEWTYPE_LIST:
                        spanSize = 2;
                        break;
                    default:
                        spanSize = 2;
                        break;
                }
                return spanSize;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        View itemView = null;
        switch (viewType){
            case VIEWTYPE_VIEWPAGER:
                itemView = mInflater.inflate(R.layout.item_banner,parent,false);
                viewHolder = new TopBannerViewHolder(itemView);
                break;
            case VIEWTYPE_WELFARE:
                itemView = mInflater.inflate(R.layout.item_welfarecontainer,parent,false);
                viewHolder = new WelFareContainerViewHolder(itemView);
                break;
            case VIEWTYPE_ENTRANCE:
                itemView = mInflater.inflate(R.layout.item_entrances,parent,false);
                viewHolder = new EntranceViewHolder(itemView);
                break;
            case VIEWTYPE_RECOMMANDS:
                itemView = mInflater.inflate(R.layout.item_recommandscontainer,parent,false);
                viewHolder = new RecommandContainerViewHolder(itemView);
                break;
            case VIEWTYPE_TITLE:
                itemView = mInflater.inflate(R.layout.item_title,parent,false);
                viewHolder = new TitleViewHolder(itemView);
                break;
            case VIEWTYPE_HOTARTS:
                itemView = mInflater.inflate(R.layout.item_hotartcontainer,parent,false);
                viewHolder = new HotArtViewHolder(itemView);
                break;
            case VIEWTYPE_LIST:
                itemView = mInflater.inflate(R.layout.item_envalute,parent,false);
                viewHolder = new ListViewHolder(itemView);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType){
            case VIEWTYPE_VIEWPAGER:
                ArrayList<String> imgurls = new ArrayList<String>();
                for(int i = 0; i <  mainPageinfo.bannerInfos.size(); i++){
                    imgurls.add(mainPageinfo.bannerInfos.get(i).imgUrl);
                }
                ((TopBannerViewHolder)holder).banner.setImages(imgurls).start().startAutoPlay();
                ((TopBannerViewHolder)holder).banner
                        .setOnBannerListener(new OnBannerListener() {
                            @Override
                            public void OnBannerClick(int position) {
                                BannerInfo info = mainPageinfo.bannerInfos.get(position);
                                Articlelnfo in = new Articlelnfo();
                                in.artSrc = info.src;
                                in.artTitle = info.desc;
                                in.artImageUrl = info.imgUrl;
                                Intent it = new Intent(mContext, ArcDetaileActivity.class);
                                it.putExtra("ARTICLE_INFO",in);
                                FromActivityTool.defaultLaucher((Activity)mContext,it);
                            }
                        });
                break;
            case VIEWTYPE_WELFARE:
                WelFareContainerViewHolder welFareContainerViewHolder = (WelFareContainerViewHolder) holder;
                if(welFareContainerViewHolder.ryv.getAdapter() == null){
                    WelFareAdapter adapter = new WelFareAdapter(mContext,mainPageinfo.shareInfos);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
                    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    welFareContainerViewHolder.ryv.setLayoutManager(linearLayoutManager);
                    welFareContainerViewHolder.ryv.setAdapter(adapter);
                }else{
                    welFareContainerViewHolder.ryv.getAdapter().notifyDataSetChanged();
                }
                break;
            case VIEWTYPE_RECOMMANDS:
                final RecommandContainerViewHolder viewHolder = (RecommandContainerViewHolder) holder;
                if(mainPageinfo.recommandsInfos.size() > 0){
                    final ShareInfo info  = mainPageinfo.recommandsInfos.get(0);
                    GlideUtils.disPlayUrl(mContext,info.imgUrl,viewHolder.ivmain);
                    viewHolder.tvmain.setText(info.title);
                    viewHolder.cvmain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Articlelnfo in = new Articlelnfo();
                            in.artSrc = info.src;
                            in.artTitle = info.title;
                            in.artImageUrl = info.imgUrl;
                            Intent it = new Intent(mContext, ArcDetaileActivity.class);
                            it.putExtra("ARTICLE_INFO",in);
                            FromActivityTool.laucherWithShareView((Activity)mContext,it,viewHolder.ivmain);
                        }
                    });
                    if(viewHolder.ryv.getAdapter() == null){
                        RecommandAdapter adapter = new RecommandAdapter(mContext,mainPageinfo.recommandsInfos);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
                        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        viewHolder.ryv.setLayoutManager(linearLayoutManager);
                        viewHolder.ryv.setAdapter(adapter);
                    }else{
                        viewHolder.ryv.getAdapter().notifyDataSetChanged();
                    }
                }
                break;
            case VIEWTYPE_HOTARTS:
                HotArtViewHolder hotArtViewHolder = (HotArtViewHolder) holder;
                if(mainPageinfo.hotatrsInfos.size() > 0) {
                    ArrayList<String> txtList = new ArrayList<>();
                    for(int i = 0; i < mainPageinfo.hotatrsInfos.size();i++){
                        txtList.add(mainPageinfo.hotatrsInfos.get(i).title);
                    }
                    hotArtViewHolder.tvsTitle.stopAutoScroll();
                    hotArtViewHolder.tvsTitle.setTextList(txtList);
                    hotArtViewHolder.tvsTitle.setText(15, 5, mContext.getResources().getColor(R.color.itemTextTitle));//设置属性,具体跟踪源码
                    hotArtViewHolder.tvsTitle.setTextStillTime(3000);//设置停留时长间隔
                    hotArtViewHolder.tvsTitle.setAnimTime(300);//设置进入和退出的时间间隔
                    hotArtViewHolder.tvsTitle.setOnItemClickListener(new VerticalTextview.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            ShareInfo info = mainPageinfo.hotatrsInfos.get(position);
                            Articlelnfo in = new Articlelnfo();
                            in.artSrc = info.src;
                            in.artTitle = info.title;
                            in.artImageUrl = info.imgUrl;
                            Intent it = new Intent(mContext, ArcDetaileActivity.class);
                            it.putExtra("ARTICLE_INFO",in);
                            FromActivityTool.defaultLaucher((Activity)mContext,it);
                        }
                    });
                    hotArtViewHolder.tvsTitle.startAutoScroll();
                }
                break;
            case VIEWTYPE_TITLE:
                if(position == 5){
                    //文章标题
                }else{

                }
                break;
            case VIEWTYPE_ENTRANCE:
                EntranceViewHolder entranceViewHolder = (EntranceViewHolder) holder;
                //图片中心
                entranceViewHolder.llPhotoCenter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(mContext, PhotoCenterActivity.class);
                        mContext.startActivity(it);
                    }
                });
                //闲置交易
                entranceViewHolder.llExcharge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,"模块建设中...",Toast.LENGTH_LONG).show();
                    }
                });
                //大字报
                entranceViewHolder.llNewsPager.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,"模块建设中...",Toast.LENGTH_LONG).show();
                    }
                });
                //更多
                entranceViewHolder.llMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,"模块建设中...",Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case VIEWTYPE_LIST:
                final ListViewHolder listViewHolder = (ListViewHolder)holder;
                int offset = 6;
                if(mainPageinfo.articleInfos.size() > 0){
                    if(mainPageinfo.articleInfos.size() > position - offset){
                        final Articlelnfo info = mainPageinfo.articleInfos.get(position - offset);
                        listViewHolder.tvArcTitle.setText(info.artTitle);
                        listViewHolder.tvArcDate.setText(info.artDate);
                        listViewHolder.tvArcDesc.setText(info.artDesc);
                        listViewHolder.tvArcAuthor.setText(info.artAuthor);
                        GlideUtils.disPlayUrl(mContext,info.artImageUrl,listViewHolder.ivArc);
                        GlideUtils.disPlayUrl(mContext,info.artAuthorAvter,listViewHolder.ivAvter);

                        listViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent it = new Intent(mContext, ArcDetaileActivity.class);
                                it.putExtra("ARTICLE_INFO",info);

                                FromActivityTool.laucherWithShareView((Activity)mContext,it,listViewHolder.ivArc);

                            }
                        });
                    }
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 1//轮播图
                + 1//快捷入口
                + 1//福利
                + 1//热门文章
                + 1//推荐
                + 1//文章标题
                + mainPageinfo.articleInfos.size();//文章
    }

    @Override
    public int getItemViewType(int position) {

        int viewType = 0;
        switch (position){
            case 0:
                viewType = VIEWTYPE_VIEWPAGER;
                break;
            case 1:
                viewType = VIEWTYPE_ENTRANCE;
                break;
            case 2:
                viewType = VIEWTYPE_WELFARE;
                break;
            case 3:
                viewType = VIEWTYPE_HOTARTS;
                break;
            case 4:
                viewType = VIEWTYPE_RECOMMANDS;
                break;
            case 5:
                viewType = VIEWTYPE_TITLE;
                break;
            default:
                viewType = VIEWTYPE_LIST;
                break;
        }
        return viewType;
    }


    private class TopBannerViewHolder extends RecyclerView.ViewHolder{
        private Banner banner;
        public TopBannerViewHolder(View itemView) {
            super(itemView);
            banner = (Banner) itemView.findViewById(R.id.banner);
            initBanner();
        }
        /**
         * 初始化Banner参数
         */
        private void initBanner() {
            banner.setImageLoader(new ImageLoader() {
                @Override
                public void displayImage(Context context, Object path, ImageView imageView) {
                    GlideUtils.disPlayUrl(context,path,imageView);
                }
            });
            banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
            banner.isAutoPlay(true);
            banner.setDelayTime(2000);
            banner.setBannerAnimation(Transformer.Default);
            banner.setIndicatorGravity(BannerConfig.CENTER);
        }
    }

    private class HotArtViewHolder extends RecyclerView.ViewHolder{

        VerticalTextview tvsTitle;
        public HotArtViewHolder(View itemView) {
            super(itemView);
            tvsTitle = (VerticalTextview) itemView.findViewById(R.id.tvshotarts);
        }
    }

    private class TitleViewHolder extends RecyclerView.ViewHolder{

        public TitleViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class WelFareContainerViewHolder extends RecyclerView.ViewHolder{

        RecyclerView ryv;
        public WelFareContainerViewHolder(View itemView) {
            super(itemView);
            ryv = (RecyclerView) itemView.findViewById(R.id.ryv_welfareContainer);
        }
    }

    private class RecommandContainerViewHolder extends RecyclerView.ViewHolder{

        RecyclerView ryv;
        ImageView ivmain;
        TextView tvmain;
        CardView cvmain;
        public RecommandContainerViewHolder(View itemView) {
            super(itemView);
            cvmain = (CardView) itemView.findViewById(R.id.cvmain);
            ivmain = (ImageView) itemView.findViewById(R.id.ivmain);
            tvmain = (TextView) itemView.findViewById(R.id.tvmain);
            ryv = (RecyclerView) itemView.findViewById(R.id.ryv_reCommandContainer);
        }
    }

    private class EntranceViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout llPhotoCenter;
        private LinearLayout llExcharge;
        private LinearLayout llNewsPager;
        private LinearLayout llMore;
        public EntranceViewHolder(View itemView) {
            super(itemView);
            llPhotoCenter = (LinearLayout) itemView.findViewById(R.id.llPhotoCenter);
            llExcharge = (LinearLayout) itemView.findViewById(R.id.llExcharge);
            llNewsPager = (LinearLayout) itemView.findViewById(R.id.llNewsPager);
            llMore = (LinearLayout) itemView.findViewById(R.id.llMore);
        }
    }

    private class ListViewHolder extends RecyclerView.ViewHolder{

        private TextView tvArcTitle,tvArcDesc,tvArcAuthor,tvArcDate;
        private ImageView ivArc,ivAvter;
        public ListViewHolder(View itemView) {
            super(itemView);

            tvArcAuthor = (TextView) itemView.findViewById(R.id.arcAuthor);
            tvArcTitle = (TextView) itemView.findViewById(R.id.arcTitle);
            tvArcDesc = (TextView) itemView.findViewById(R.id.arcDesc);
            tvArcDate = (TextView) itemView.findViewById(R.id.arcDate);
            ivArc = (ImageView) itemView.findViewById(R.id.arcImg);
            ivAvter = (ImageView) itemView.findViewById(R.id.arcAuthorAvter);

        }
    }

    /**
     * 福利数据适配器
     */
    private class WelFareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ArrayList<ShareInfo> mShareInfos;
        Context mContext;
        LayoutInflater mInflater;
        public WelFareAdapter(Context context, ArrayList<ShareInfo> shareInfos) {
            this.mContext = context;
            this.mShareInfos = shareInfos;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemv = mInflater.inflate(R.layout.item_welfare, viewGroup,false);
            ShareWellViewHolder holder = new ShareWellViewHolder(itemv);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            if (mShareInfos != null && mShareInfos.size() > position) {
                ShareWellViewHolder holder = (ShareWellViewHolder) viewHolder;
                holder.tvDesc.setText("" + mShareInfos.get(position).title);
                GlideUtils.disPlayUrl(mContext, mShareInfos.get(position).imgUrl, holder.ivDesc);
                final Articlelnfo articlelnfo = new Articlelnfo();
                articlelnfo.artSrc = mShareInfos.get(position).src;
                articlelnfo.commSrc = articlelnfo.artSrc;
                articlelnfo.artTitle = mShareInfos.get(position).title;
                articlelnfo.artImageUrl = mShareInfos.get(position).imgUrl;
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, DisplayActivity.class);
                        intent.putExtra("INFO",articlelnfo);
                        mContext.startActivity(intent);
                    }
                });
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mShareInfos.size();
        }
        private class ShareWellViewHolder extends RecyclerView.ViewHolder {
            TextView tvDesc;
            ImageView ivDesc;

            public ShareWellViewHolder(View itemView) {
                super(itemView);
                tvDesc = (TextView) itemView.findViewById(R.id.sharewelltxt);
                ivDesc = (ImageView) itemView.findViewById(R.id.sharewellimg);
            }
        }
    }

    /**
     * 推荐数据适配器
     */
    private class RecommandAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ArrayList<ShareInfo> mRecommandInfos;
        Context mContext;
        LayoutInflater mInflater;
        public RecommandAdapter(Context context, ArrayList<ShareInfo> Infos) {
            this.mContext = context;
            this.mRecommandInfos = Infos;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemv = mInflater.inflate(R.layout.item_recommand, viewGroup,false);
            RecommandViewHolder holder = new RecommandViewHolder(itemv);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            if (mRecommandInfos != null && mRecommandInfos.size() > position) {
                final RecommandViewHolder holder = (RecommandViewHolder) viewHolder;
                final ShareInfo info = mRecommandInfos.get(position);
                holder.tvDesc.setText("" + info.title);
                GlideUtils.disPlayUrl(mContext, info.imgUrl, holder.ivDesc);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Articlelnfo in = new Articlelnfo();
                        in.artSrc = info.src;
                        in.artTitle = info.title;
                        in.artImageUrl = info.imgUrl;
                        Intent it = new Intent(mContext, ArcDetaileActivity.class);
                        it.putExtra("ARTICLE_INFO",in);
                        FromActivityTool.laucherWithShareView((Activity)mContext,it,holder.ivDesc);

                    }
                });

            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mRecommandInfos.size();
        }
        private class RecommandViewHolder extends RecyclerView.ViewHolder {
            TextView tvDesc;
            ImageView ivDesc;

            public RecommandViewHolder(View itemView) {
                super(itemView);
                tvDesc = (TextView) itemView.findViewById(R.id.tv);
                ivDesc = (ImageView) itemView.findViewById(R.id.iv);
            }
        }
    }
}
