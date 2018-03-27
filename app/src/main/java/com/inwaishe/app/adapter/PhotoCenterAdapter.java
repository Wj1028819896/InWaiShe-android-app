package com.inwaishe.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.inwaishe.app.R;
import com.inwaishe.app.common.GlideUtils;
import com.inwaishe.app.entity.mainpage.Articlelnfo;
import com.inwaishe.app.entity.photocenter.AlbumInfo;
import com.inwaishe.app.entity.photocenter.PhotoCenterBackInfo;
import com.inwaishe.app.ui.display.DisplayActivity;

import java.util.zip.Inflater;

/**
 * Created by WangJing on 2018/2/26.
 */

public class PhotoCenterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private RecyclerView recyclerView;
    private PhotoCenterBackInfo photoCenterBackInfo;
    private LayoutInflater mInflater;
    public static final int VIEWTYPE_LIST = 1;//文章List
    public static final int VIEWTYPE_FOOTER = 2;//加载更多
    private boolean isLoadingMore = false;
    private OnLoadOrRefreshListener onLoadOrRefreshListener;
    private boolean isLoadAll = false;
    private StaggeredGridLayoutManager layoutmanager;
    public PhotoCenterAdapter(RecyclerView recyclerView, Context context, PhotoCenterBackInfo photoCenterBackInfo){
        this.mContext = context;
        this.recyclerView = recyclerView;
        this.photoCenterBackInfo = photoCenterBackInfo;
        this.mInflater = LayoutInflater.from(context);

        initRecyclerView();
    }

    private void initRecyclerView() {

        layoutmanager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        layoutmanager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutmanager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] lastPositions = new int[layoutmanager.getSpanCount()];
                layoutmanager.findLastVisibleItemPositions(lastPositions);
                int lastVisibleItemPosition = findMax(lastPositions);
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

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public interface OnLoadOrRefreshListener{
        void onLoadMore();
        void onRefresh();
    }

    public void setOnLoadOrRefreshListener(OnLoadOrRefreshListener onLoadOrRefreshListener){
        this.onLoadOrRefreshListener = onLoadOrRefreshListener;
    }

    public void setLoadingMore(boolean loadingMore) {
        isLoadingMore = loadingMore;
    }

    public void setLoadAll(boolean loadAll) {
        isLoadAll = loadAll;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View itemView = null;
        switch (viewType){
            case VIEWTYPE_LIST:
                itemView = mInflater.inflate(R.layout.item_album,parent,false);
                viewHolder = new PhotoAlbumViewHolder(itemView);
                break;
            case VIEWTYPE_FOOTER:
                itemView = mInflater.inflate(R.layout.item_footer_loadmore,parent,false);
                viewHolder = new FooterViewHolder(itemView);
                StaggeredGridLayoutManager.LayoutParams layoutParams =
                        new StaggeredGridLayoutManager.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setFullSpan(true);
                viewHolder.itemView.setLayoutParams(layoutParams);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        switch(viewType){
            case VIEWTYPE_LIST:
                final AlbumInfo info = photoCenterBackInfo.abList.get(position);
                PhotoAlbumViewHolder pVh = (PhotoAlbumViewHolder) viewHolder;
                GlideUtils.disPlayUrlAutoWh(mContext,info.coverImgSrc, pVh.ivCover);
                pVh.tvAuthor.setText("" + info.author);
                pVh.tvTitle.setText("" + info.title);
                pVh.tvCommentsNum.setText("" + info.commentsNum);

                pVh.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Articlelnfo articlelnfo = new Articlelnfo();
                        articlelnfo.artSrc = info.artSrc;
                        articlelnfo.usrCommNum = info.commentsNum;
                        articlelnfo.commSrc = articlelnfo.artSrc;
                        articlelnfo.artTitle = info.title;
                        articlelnfo.artAuthor = info.author;
                        Intent intent = new Intent(mContext, DisplayActivity.class);
                        intent.putExtra("INFO",articlelnfo);
                        mContext.startActivity(intent);
                    }
                });
                break;
            case VIEWTYPE_FOOTER:
                FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
                if(isLoadAll){
                    footerViewHolder.textView.setVisibility(View.VISIBLE);
                    footerViewHolder.spinKitView.setVisibility(View.GONE);
                }else{
                    footerViewHolder.textView.setVisibility(View.GONE);
                    footerViewHolder.spinKitView.setVisibility(View.VISIBLE);
                }
                break;
        }

    }

    private class PhotoAlbumViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivCover;
        private TextView tvTitle;
        private TextView tvAuthor;
        private TextView tvCommentsNum;
        public PhotoAlbumViewHolder(View itemView) {
            super(itemView);
            ivCover = (ImageView) itemView.findViewById(R.id.ivCover);
            tvAuthor  = (TextView) itemView.findViewById(R.id.tvAuthor);
            tvTitle  = (TextView) itemView.findViewById(R.id.tvTitle);
            tvCommentsNum  = (TextView) itemView.findViewById(R.id.tvCommentsNum);
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

    @Override
    public int getItemCount() {
        return photoCenterBackInfo.abList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == (getItemCount() - 1)){
            return VIEWTYPE_FOOTER;
        }
        return VIEWTYPE_LIST;
    }

}
