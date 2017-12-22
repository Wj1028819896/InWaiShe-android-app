package com.inwaishe.app.framework.emotionkeyboard.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.inwaishe.app.R;
import com.inwaishe.app.framework.emotionkeyboard.KeyBoardListenerUtils;
import com.inwaishe.app.framework.emotionkeyboard.model.Emotion;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by WangJing on 2017/12/6.
 */

public class EmotionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<Emotion> emotionArrayList;
    private Context mContext;

    public EmotionAdapter(Context context,ArrayList<Emotion> emotions){
        this.emotionArrayList = emotions;
        this.mContext = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_emotion,viewGroup,false);
        EmotionViewHolder viewHolder = new EmotionViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final Emotion emotion = emotionArrayList.get(position);
        EmotionViewHolder emotionViewHolder = (EmotionViewHolder) viewHolder;
        if(emotion.resId != -1){
            emotionViewHolder.ivEmotion.setImageResource(emotion.resId);
        }else if(!TextUtils.isEmpty(emotion.filepath)){
            if(new File(emotion.filepath).exists()){
                emotionViewHolder.ivEmotion
                        .setImageBitmap(BitmapFactory.decodeFile(emotion.filepath));
            }else{

            }
        }else if(!TextUtils.isEmpty(emotion.url)){
            //Glide显示网络图片(不用）

        }
        emotionViewHolder.ivEmotion.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        KeyBoardListenerUtils
                                .getmInstance()
                                .call(emotion);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return emotionArrayList.size();
    }

    private class EmotionViewHolder extends RecyclerView.ViewHolder{
        ImageView ivEmotion;
        public EmotionViewHolder(View itemView) {
            super(itemView);
            ivEmotion = (ImageView) itemView.findViewById(R.id.ivEmotion);
        }
    }
}
