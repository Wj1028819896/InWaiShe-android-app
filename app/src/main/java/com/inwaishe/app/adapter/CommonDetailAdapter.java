package com.inwaishe.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inwaishe.app.R;
import com.inwaishe.app.common.GlideUtils;
import com.inwaishe.app.entity.mainpage.ArcCommInfo;
import com.inwaishe.app.entity.mainpage.ArcReplyCommInfo;
import com.inwaishe.app.framework.arch.bus.XBus;
import com.inwaishe.app.framework.emotionkeyboard.EmojiUtils;

/**
 * Created by WangJing on 2017/10/27.
 */

public class CommonDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private ArcCommInfo mArcCommInfo;
    private LayoutInflater mInflater;
    private static final int VIEWTYPE_PARENT = 1;
    private static final int VIEWTYPE_CHILD = 2;
    private static final int VIEWTYPE_DIVIDERTITLE = 3;
    public CommonDetailAdapter(Context context, ArcCommInfo arcCommInfo){
        this.mArcCommInfo = arcCommInfo;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View itemView = null;
        switch (viewType){
            case VIEWTYPE_PARENT:
                itemView = mInflater.inflate(R.layout.item_arccomm,viewGroup,false);
                viewHolder = new ParentCommViewHolder(itemView);
                break;
            case VIEWTYPE_DIVIDERTITLE:
                itemView = mInflater.inflate(R.layout.item_divider,viewGroup,false);
                viewHolder = new DividerTitleViewHolder(itemView);
                break;
            case VIEWTYPE_CHILD:
                itemView = mInflater.inflate(R.layout.item_arccomm,viewGroup,false);
                viewHolder = new ChildCommViewHolder(itemView);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        int viewType = getItemViewType(position);
        if(viewType == VIEWTYPE_PARENT){
            ParentCommViewHolder parentCommViewHolder = (ParentCommViewHolder) viewHolder;
            parentCommViewHolder.userName.setText("" + mArcCommInfo.userName);
            parentCommViewHolder.userDate.setText("" + mArcCommInfo.userDate);
            parentCommViewHolder.userComm.setText("" + mArcCommInfo.userCommon);
            EmojiUtils.getInstance(mContext)
                    .setEmojiTextView(mContext
                            ,parentCommViewHolder.userComm,""+ mArcCommInfo.userCommon);
            GlideUtils.disPlayUrl(mContext,mArcCommInfo.userIconUrl,parentCommViewHolder.userIcon);
            parentCommViewHolder.more.setVisibility(View.GONE);
            parentCommViewHolder.replyCommsNum.setText("回复楼主");
            parentCommViewHolder.itemView.setBackgroundResource(R.drawable.login_top_bg);
            parentCommViewHolder.replyCommsNum.setVisibility(View.INVISIBLE);

        }else if(viewType == VIEWTYPE_CHILD){
            final ArcReplyCommInfo info = mArcCommInfo.arcReplyCommInfos.get(position-2);
            ChildCommViewHolder childCommViewHolder = (ChildCommViewHolder) viewHolder;
            childCommViewHolder.userName.setText("" + info.userName);
            childCommViewHolder.userDate.setText("" + info.userDate);
            childCommViewHolder.userComm.setText("" + info.userCommon);
            EmojiUtils.getInstance(mContext)
                    .setEmojiTextView(mContext
                            ,childCommViewHolder.userComm,""+ info.userCommon);
            GlideUtils.disPlayUrl(mContext,info.userIconUrl,childCommViewHolder.userIcon);
            childCommViewHolder.more.setVisibility(View.VISIBLE);
            childCommViewHolder.replyCommsNum.setVisibility(View.GONE);
            childCommViewHolder.replyCommsNum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XBus.getInstance().post("ReplyChildClick", info.userName + "<<>>" +info.replyAction);
                }
            });
        }else if(viewType == VIEWTYPE_DIVIDERTITLE){
            DividerTitleViewHolder dividerTitleViewHolder = (DividerTitleViewHolder) viewHolder;
            dividerTitleViewHolder.tvTitle.setText("回复列表" + "【"+ mArcCommInfo.arcReplyCommInfos.size() +"】");
        }
    }

    @Override
    public int getItemCount() {
        return mArcCommInfo.arcReplyCommInfos.size() + 1 + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return VIEWTYPE_PARENT;
        }else if(position == 1){
            return VIEWTYPE_DIVIDERTITLE;
        }
        return VIEWTYPE_CHILD;
    }

    private class ParentCommViewHolder extends RecyclerView.ViewHolder{

        private TextView userName;
        private TextView userDate;
        private TextView userComm;
        private ImageView userIcon;
        private TextView replyCommsNum;
        private TextView more;
        public ParentCommViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.userName);
            userDate = (TextView) itemView.findViewById(R.id.userDate);
            userComm = (TextView) itemView.findViewById(R.id.userCommon);
            userIcon = (ImageView) itemView.findViewById(R.id.userIcon);
            replyCommsNum = (TextView) itemView.findViewById(R.id.tvReplyNum);
            more = (TextView) itemView.findViewById(R.id.tvMore);
        }
    }

    private class ChildCommViewHolder extends RecyclerView.ViewHolder{
        private TextView userName;
        private TextView userDate;
        private TextView userComm;
        private ImageView userIcon;
        private TextView replyCommsNum;
        private TextView more;
        public ChildCommViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.userName);
            userDate = (TextView) itemView.findViewById(R.id.userDate);
            userComm = (TextView) itemView.findViewById(R.id.userCommon);
            userIcon = (ImageView) itemView.findViewById(R.id.userIcon);
            replyCommsNum = (TextView) itemView.findViewById(R.id.tvReplyNum);
            more = (TextView) itemView.findViewById(R.id.tvMore);
        }
    }

    private class DividerTitleViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTitle;
        public DividerTitleViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        }
    }
}
