package com.inwaishe.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.inwaishe.app.R;
import com.inwaishe.app.entity.mainpage.Articlelnfo;
import com.inwaishe.app.entity.mainpage.BannerInfo;
import com.inwaishe.app.entity.mainpage.MainPageInfo;
import com.inwaishe.app.entity.mainpage.ShareInfo;
import com.inwaishe.app.ui.ArcDetaileActivity;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class MainPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEWTYPE_VIEWPAGER = 0;//轮播
    public static final int VIEWTYPE_SHAREWELL = 1;//福利
    public static final int VIEWTYPE_ENTRANCE = 2;//入口
    public static final int VIEWTYPE_LIST = 3;//文章List
    public static final int VIEWTYPE_TITLE = 4;//标题

    private Context mContext;
    private LayoutInflater mInflater;
    private MainPageInfo mainPageinfo;
    public MainPagerAdapter(Context context, MainPageInfo mainPageInfo){
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mainPageinfo = mainPageInfo;
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
            case VIEWTYPE_TITLE:
                itemView = mInflater.inflate(R.layout.item_headtitle,parent,false);
                viewHolder = new HeadTitleViewHolder(itemView);
                break;
            case VIEWTYPE_SHAREWELL:
                itemView = mInflater.inflate(R.layout.item_sharewell,parent,false);
                viewHolder = new ShareWellViewHolder(itemView);
                break;
            case VIEWTYPE_ENTRANCE:
                itemView = mInflater.inflate(R.layout.item_entrances,parent,false);
                viewHolder = new EntranceViewHolder(itemView);
                break;
            case VIEWTYPE_LIST:
                itemView = mInflater.inflate(R.layout.item_envalute,parent,false);
                viewHolder = new ListViewHolder(itemView);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType){
            case VIEWTYPE_VIEWPAGER:
                ArrayList<String> imgurls = new ArrayList<String>();
                for(BannerInfo bannerInfo : mainPageinfo.bannerInfos){
                    imgurls.add(bannerInfo.imgUrl);
                }
                ((TopBannerViewHolder)holder).banner.setImages(imgurls).start().startAutoPlay();
                break;
            case VIEWTYPE_TITLE:

                break;
            case VIEWTYPE_SHAREWELL:
                ShareWellViewHolder shareWellViewHolder = (ShareWellViewHolder) holder;
                if(mainPageinfo.shareInfos.size() >= 4){
                    shareWellViewHolder.tvDesc.setText(""
                     + mainPageinfo.shareInfos.get(position - 3).title);
                    Glide.with(mContext).load(mainPageinfo.shareInfos.get(position - 3).imgUrl).into(shareWellViewHolder.ivDesc);
                }
                break;
            case VIEWTYPE_ENTRANCE:

                break;
            case VIEWTYPE_LIST:
                ListViewHolder listViewHolder = (ListViewHolder)holder;
                if(mainPageinfo.articleInfos.size() > 0){
                    if(mainPageinfo.articleInfos.size() > position - 7){
                        final Articlelnfo info = mainPageinfo.articleInfos.get(position - 7);
                        listViewHolder.tvArcTitle.setText(info.artTitle);
                        listViewHolder.tvArcDate.setText(info.artDate);
                        listViewHolder.tvArcDesc.setText(info.artDesc);
                        listViewHolder.tvArcAuthor.setText(info.artAuthor);
                        Glide.with(mContext).load(info.artImageUrl).into(listViewHolder.ivArc);

                        listViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent it = new Intent(mContext, ArcDetaileActivity.class);
                                it.putExtra("ARTICLE_INFO",info);
                                ((Activity)mContext).startActivity(it);

                            }
                        });
                    }
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 1 + 1 + 1 + 4 + mainPageinfo.articleInfos.size();
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
                viewType = VIEWTYPE_TITLE;
                break;
            default:
                viewType = VIEWTYPE_LIST;
                break;
        }
        if(position > 2 && position < 7){
            viewType = VIEWTYPE_SHAREWELL;
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
                    Glide.with(context).load(path).into(imageView);
                }
            });
            banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
            banner.isAutoPlay(true);
            banner.setDelayTime(2000);
            banner.setBannerAnimation(Transformer.Default);
            banner.setIndicatorGravity(BannerConfig.CENTER);
        }
    }

    private class HeadTitleViewHolder extends RecyclerView.ViewHolder{

        public HeadTitleViewHolder(View itemView) {

            super(itemView);
        }
    }

    private class ShareWellViewHolder extends RecyclerView.ViewHolder{
        TextView tvDesc;
        ImageView ivDesc;
        public ShareWellViewHolder(View itemView) {
            super(itemView);
            tvDesc = (TextView) itemView.findViewById(R.id.sharewelltxt);
            ivDesc = (ImageView) itemView.findViewById(R.id.sharewellimg);
        }
    }

    private class EntranceViewHolder extends RecyclerView.ViewHolder{
        public EntranceViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class ListViewHolder extends RecyclerView.ViewHolder{

        private TextView tvArcTitle,tvArcDesc,tvArcAuthor,tvArcDate;
        private ImageView ivArc;
        public ListViewHolder(View itemView) {
            super(itemView);

            tvArcAuthor = (TextView) itemView.findViewById(R.id.arcAuthor);
            tvArcTitle = (TextView) itemView.findViewById(R.id.arcTitle);
            tvArcDesc = (TextView) itemView.findViewById(R.id.arcDesc);
            tvArcDate = (TextView) itemView.findViewById(R.id.arcDate);
            ivArc = (ImageView) itemView.findViewById(R.id.arcImg);

        }
    }
}
