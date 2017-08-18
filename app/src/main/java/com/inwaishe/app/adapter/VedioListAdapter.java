package com.inwaishe.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.inwaishe.app.R;
import com.inwaishe.app.entity.mainpage.Articlelnfo;
import com.inwaishe.app.entity.mainpage.MainPageInfo;
import com.inwaishe.app.ui.ArcDetaileActivity;

/**
 * Created by Administrator on 2017/8/15 0015.
 */

public class VedioListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private LayoutInflater mInflater;
    private MainPageInfo mainPageinfo;
    public VedioListAdapter(Context context, MainPageInfo mainPageInfo){
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mainPageinfo = mainPageInfo;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View itemView = mInflater.inflate(R.layout.item_vedio,parent,false);
        viewHolder = new VedioListAdapter.ListViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        VedioListAdapter.ListViewHolder listViewHolder = (VedioListAdapter.ListViewHolder)holder;
        if(mainPageinfo.articleInfos.size() > 0){
            if(mainPageinfo.articleInfos.size() > position){
                final Articlelnfo info = mainPageinfo.articleInfos.get(position);
                listViewHolder.tvArcTitle.setText(info.artTitle);
                listViewHolder.tvArcPlaytimes.setText("" + info.vedioPlayNum);
                listViewHolder.tvArcCommnums.setText("" + info.usrCommNum);
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

    }

    @Override
    public int getItemCount() {
        return mainPageinfo.articleInfos.size();
    }

    private class ListViewHolder extends RecyclerView.ViewHolder{

        private TextView tvArcTitle,tvArcPlaytimes,tvArcAuthor,tvArcCommnums;
        private ImageView ivArc;
        public ListViewHolder(View itemView) {
            super(itemView);

            tvArcAuthor = (TextView) itemView.findViewById(R.id.arcAuthor);
            tvArcTitle = (TextView) itemView.findViewById(R.id.arcTitle);
            tvArcPlaytimes = (TextView) itemView.findViewById(R.id.arcPlaytimes);
            tvArcCommnums = (TextView) itemView.findViewById(R.id.arcCommnums);
            ivArc = (ImageView) itemView.findViewById(R.id.arcImg);

        }
    }
}
