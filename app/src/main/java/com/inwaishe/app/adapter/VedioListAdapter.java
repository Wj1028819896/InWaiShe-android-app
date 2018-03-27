package com.inwaishe.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fivehundredpx.greedolayout.GreedoLayoutManager;
import com.fivehundredpx.greedolayout.GreedoLayoutSizeCalculator;
import com.fivehundredpx.greedolayout.GreedoSpacingItemDecoration;
import com.github.ybq.android.spinkit.SpinKitView;
import com.inwaishe.app.R;
import com.inwaishe.app.common.AppUtils;
import com.inwaishe.app.common.GlideUtils;
import com.inwaishe.app.entity.mainpage.Articlelnfo;
import com.inwaishe.app.entity.mainpage.MainPageInfo;
import com.inwaishe.app.framework.activitytrans.FromActivityTool;
import com.inwaishe.app.ui.ArcDetaileActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.util.Random;

/**
 * Created by Administrator on 2017/8/15 0015.
 */

public class VedioListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements GreedoLayoutSizeCalculator.SizeCalculatorDelegate {

    private Context mContext;
    private LayoutInflater mInflater;
    private MainPageInfo mainPageinfo;

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private GreedoLayoutManager greedoLayoutManager;
    private OnLoadOrRefreshListener onLoadOrRefreshListener;
    private boolean isLoadingMore = false;
    private boolean isLoadAll = false;

    public static final int VIEWTYPE_LIST = 1;//文章List
    public static final int VIEWTYPE_FOOTER = 2;//加载更多

    private double[] aspectRatios = {2d,1.5d,0.5d,1d};

    public VedioListAdapter(RecyclerView recyclerView,Context context, MainPageInfo mainPageInfo){
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mainPageinfo = mainPageInfo;
        this.recyclerView = recyclerView;

        initRecyclerView();
    }

    public void setLoadingMore(boolean loadingMore) {
        isLoadingMore = loadingMore;
    }

    public void setLoadAll(boolean loadAll) {
        isLoadAll = loadAll;
    }

    private void initRecyclerView() {

        gridLayoutManager = new GridLayoutManager(mContext,2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 2;
            }
        });

        //greedoLayoutManager = new GreedoLayoutManager(this);
        //greedoLayoutManager.setMaxRowHeight(AppUtils.dip2px(mContext,150));
        //greedoLayoutManager.setAutoMeasureEnabled(true);
        //int spacing = AppUtils.dip2px(mContext, 4);
        //recyclerView.addItemDecoration(new GreedoSpacingItemDecoration(spacing));
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPosition = gridLayoutManager.findLastVisibleItemPosition();
                if(lastVisibleItemPosition == (getItemCount() - 1)){
                    //footer 可见
                    if(!isLoadingMore
                            && onLoadOrRefreshListener!=null
                            && !isLoadAll){
                        isLoadingMore = true;
                        onLoadOrRefreshListener.onLoadMore();
                    }
                }
            }
        });
    }

    @Override
    public double aspectRatioForIndex(int position) {
        Random rand = new Random();
        int index = rand.nextInt(aspectRatios.length);
        return aspectRatios[index];
    }

    public interface OnLoadOrRefreshListener{
        void onLoadMore();
        void onRefresh();
    }

    public void setOnLoadOrRefreshListener(OnLoadOrRefreshListener onLoadOrRefreshListener){
        this.onLoadOrRefreshListener = onLoadOrRefreshListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View itemView = null;
        switch (viewType){
            case VIEWTYPE_LIST:
                itemView = mInflater.inflate(R.layout.item_vedio,parent,false);
                viewHolder = new VedioListAdapter.ListViewHolder(itemView);
                break;
            case VIEWTYPE_FOOTER:
                itemView = mInflater.inflate(R.layout.item_footer_loadmore,parent,false);
                viewHolder = new FooterViewHolder(itemView);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        if(viewType == VIEWTYPE_LIST){
            final VedioListAdapter.ListViewHolder listViewHolder = (VedioListAdapter.ListViewHolder)holder;
            if(mainPageinfo.articleInfos.size() > 0){
                if(mainPageinfo.articleInfos.size() > position){
                    final Articlelnfo info = mainPageinfo.articleInfos.get(position);
                    listViewHolder.tvArcTitle.setText(info.artTitle);
                    listViewHolder.tvArcPlaytimes.setText("" + info.vedioPlayNum);
                    listViewHolder.tvArcCommnums.setText("" + info.usrCommNum);
                    listViewHolder.tvArcAuthor.setText(info.artAuthor);

                    GlideUtils.disPlayUrl(mContext,info.artImageUrl,listViewHolder.ivArc);

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
        }else{
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            if(isLoadAll){
                footerViewHolder.textView.setVisibility(View.VISIBLE);
                footerViewHolder.spinKitView.setVisibility(View.GONE);
            }else{
                footerViewHolder.textView.setVisibility(View.GONE);
                footerViewHolder.spinKitView.setVisibility(View.VISIBLE);
            }
        }


    }

    @Override
    public int getItemCount() {
        return mainPageinfo.articleInfos.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == (getItemCount() - 1)){
            return VIEWTYPE_FOOTER;
        }
        return VIEWTYPE_LIST;
    }

    private class ListViewHolder extends RecyclerView.ViewHolder{

        private TextView tvArcTitle,tvArcAuthor;
        private AppCompatTextView tvArcPlaytimes,tvArcCommnums;
        private ImageView ivArc;
        public ListViewHolder(View itemView) {
            super(itemView);

            tvArcAuthor = (TextView) itemView.findViewById(R.id.arcAuthor);
            tvArcTitle = (TextView) itemView.findViewById(R.id.arcTitle);
            tvArcPlaytimes = (AppCompatTextView) itemView.findViewById(R.id.arcPlaytimes);
            tvArcCommnums = (AppCompatTextView) itemView.findViewById(R.id.arcCommnums);
            ivArc = (ImageView) itemView.findViewById(R.id.arcImg);


            VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat
                    .create(mContext.getResources(),
                            R.drawable.ic_ondemand_video_black_24dp,
                            mContext.getTheme());

            vectorDrawableCompat.setBounds(0, 0, vectorDrawableCompat.getMinimumWidth(), vectorDrawableCompat.getMinimumHeight());
            tvArcPlaytimes.setCompoundDrawables(vectorDrawableCompat, null, null, null);

            vectorDrawableCompat = VectorDrawableCompat
                    .create(mContext.getResources(),
                            R.drawable.ic_comment_black_24dp,
                            mContext.getTheme());

            vectorDrawableCompat.setBounds(0, 0, vectorDrawableCompat.getMinimumWidth(), vectorDrawableCompat.getMinimumHeight());
            tvArcCommnums.setCompoundDrawables(vectorDrawableCompat, null, null, null);


        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder{

        private SpinKitView spinKitView;
        private TextView textView;
        public FooterViewHolder(View itemView) {
            super(itemView);
            spinKitView = (SpinKitView) itemView.findViewById(R.id.footer_pb);
            textView = (TextView) itemView.findViewById(R.id.footer_txt);
            if(isLoadAll){
                textView.setVisibility(View.VISIBLE);
                spinKitView.setVisibility(View.GONE);
            }else{
                textView.setVisibility(View.GONE);
                spinKitView.setVisibility(View.VISIBLE);
            }
        }
    }
}
