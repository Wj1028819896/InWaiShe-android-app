package com.inwaishe.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.inwaishe.app.R;
import com.inwaishe.app.common.CommonData;
import com.inwaishe.app.entity.SearchResBackInfo;
import com.inwaishe.app.entity.SearchResItemInfo;
import com.inwaishe.app.entity.mainpage.Articlelnfo;
import com.inwaishe.app.entity.photocenter.PhotoCenterBackInfo;
import com.inwaishe.app.framework.activitytrans.FromActivityTool;
import com.inwaishe.app.ui.ArcDetaileActivity;

/**
 * Created by WangJing on 2018/6/11.
 */

public class SearchResListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    private Context mContext;
    private RecyclerView recyclerView;
    private SearchResBackInfo searchResBackInfo;
    private LayoutInflater mInflater;
    public static final int VIEWTYPE_LIST = 1;//文章List
    public static final int VIEWTYPE_FOOTER = 2;//加载更多
    private boolean isLoadingMore = false;
    private boolean isLoadAll = false;
    private GridLayoutManager gridLayoutManager;
    private OnLoadOrRefreshListener onLoadOrRefreshListener;
    public SearchResListAdapter(RecyclerView recyclerView, Context context, SearchResBackInfo searchResBackInfo){
        this.mContext = context;
        this.recyclerView = recyclerView;
        this.searchResBackInfo = searchResBackInfo;
        this.mInflater = LayoutInflater.from(context);

        initRecyclerView();
    }

    public void setLoadingMore(boolean loadingMore) {
        isLoadingMore = loadingMore;
    }

    public interface OnLoadOrRefreshListener{
        void onLoadMore();
        void onRefresh();
    }

    public void setOnLoadOrRefreshListener(OnLoadOrRefreshListener onLoadOrRefreshListener){
        this.onLoadOrRefreshListener = onLoadOrRefreshListener;
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View itemView = null;
        switch (viewType){
            case VIEWTYPE_LIST:
                itemView = mInflater.inflate(R.layout.item_searchres,parent,false);
                viewHolder = new SearchResItemViewHolder(itemView);
                break;
            case VIEWTYPE_FOOTER:
                itemView = mInflater.inflate(R.layout.item_footer_loadmore,parent,false);
                viewHolder = new FooterViewHolder(itemView);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        switch(viewType) {
            case VIEWTYPE_LIST:
                SearchResItemViewHolder searchResItemviewHolder = (SearchResItemViewHolder) viewHolder;
                final SearchResItemInfo info = searchResBackInfo.list.get(position);

                searchResItemviewHolder.tvArcTitle.setText(info.artTitle);
                searchResItemviewHolder.tvArcDesc.setText(info.artDesc);
                searchResItemviewHolder.tvArcAuthor.setText(info.artAuthor);
                searchResItemviewHolder.tvArcData.setText("发表于"+ info.artDate);
                searchResItemviewHolder.tvArcReadnums.setText(info.artReadTimes + "查看");
                searchResItemviewHolder.tvArcCommonnums.setText(info.usrCommNum + "评论");

                searchResItemviewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Articlelnfo in = new Articlelnfo();
                        in.artSrc = info.artSrc;
                        in.artTitle = info.artTitle;
                        in.artImageUrl = CommonData.LOGO_3_1;
                        Intent it = new Intent(mContext, ArcDetaileActivity.class);
                        it.putExtra("ARTICLE_INFO",in);
                        FromActivityTool.defaultLaucher((Activity)mContext,it);
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

    private class SearchResItemViewHolder extends RecyclerView.ViewHolder{

        private TextView tvArcTitle;
        private TextView tvArcDesc;
        private TextView tvArcData;
        private TextView tvArcAuthor;
        private TextView tvArcCommonnums;
        private TextView tvArcReadnums;
        public SearchResItemViewHolder(View itemView) {
            super(itemView);
            tvArcData = (TextView) itemView.findViewById(R.id.arcDate);
            tvArcAuthor = (TextView) itemView.findViewById(R.id.arcAuthor);
            tvArcDesc = (TextView) itemView.findViewById(R.id.arcDesc);
            tvArcCommonnums = (TextView) itemView.findViewById(R.id.arcCommnums);
            tvArcTitle = (TextView) itemView.findViewById(R.id.arcTitle);
            tvArcReadnums = (TextView) itemView.findViewById(R.id.arcReadTimes);
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
        if(searchResBackInfo.list.size() == 0){
            return 0;
        }
        return searchResBackInfo.list.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == (getItemCount() - 1)){
            return VIEWTYPE_FOOTER;
        }
        return VIEWTYPE_LIST;
    }
}
