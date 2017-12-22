package com.inwaishe.app.adapter;

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
import com.inwaishe.app.common.GlideUtils;
import com.inwaishe.app.entity.mainpage.ArcCommInfo;
import com.inwaishe.app.framework.emotionkeyboard.EmojiUtils;
import com.inwaishe.app.ui.UsrCommDetailActivity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/12 0012.
 * 用户评论适配器
 */

public class CommonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private ArrayList<ArcCommInfo> arcCommInfos;
    private Context context;

    public CommonAdapter(Context context,ArrayList<ArcCommInfo> arcCommInfos) {
        this.arcCommInfos = arcCommInfos;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_arccomm,parent,false);
        return new CommListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(arcCommInfos.size() > position){
            final ArcCommInfo info = arcCommInfos.get(position);
            if(info != null){
                CommListViewHolder viewHolder = (CommListViewHolder)holder;
                viewHolder.userName.setText(info.userName);
                viewHolder.userDate.setText(info.userDate);

                viewHolder.userComm.setText(info.userCommon);
                EmojiUtils.getInstance(context).setEmojiTextView(context,viewHolder.userComm,info.userCommon);



                viewHolder.replyCommsNum.setText((info.arcReplyCommInfos.size()==0?"":(info.arcReplyCommInfos.size() + "·")) + "回复");
                if(info.arcReplyCommInfos.size()==0){
                    viewHolder.replyCommsNum.setBackgroundResource(R.color.transparent100);
                }else{
                    viewHolder.replyCommsNum.setBackgroundResource(R.drawable.corner_bg);
                }
                GlideUtils.disPlayUrl(context,info.userIconUrl,viewHolder.userIcon);

                viewHolder.replyCommsNum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(context, UsrCommDetailActivity.class);
                        it.putExtra("ArcCommInfo",info);
                        context.startActivity(it);
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        return arcCommInfos.size();
    }

    private class CommListViewHolder extends RecyclerView.ViewHolder{

        private TextView userName;
        private TextView userDate;
        private TextView userComm;
        private ImageView userIcon;
        private TextView replyCommsNum;

        public CommListViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.userName);
            userDate = (TextView) itemView.findViewById(R.id.userDate);
            userComm = (TextView) itemView.findViewById(R.id.userCommon);
            userIcon = (ImageView) itemView.findViewById(R.id.userIcon);
            replyCommsNum = (TextView) itemView.findViewById(R.id.tvReplyNum);
        }
    }
}
