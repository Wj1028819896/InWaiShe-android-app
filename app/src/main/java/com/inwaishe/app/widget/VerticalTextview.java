package com.inwaishe.app.widget;
/**
 * Created by wangjing on 18/1/16.
 */

import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.inwaishe.app.R;

import java.util.ArrayList;

/**
 * 广告textview
 */

public class VerticalTextview extends TextSwitcher implements ViewSwitcher.ViewFactory {

    private static final int FLAG_START_AUTO_SCROLL = 0;
    private static final int FLAG_STOP_AUTO_SCROLL = 1;

    private float mTextSize = 16 ;
    private int mPadding = 5;
    private int textColor = Color.BLACK;

    /**
     * @param textSize 字号
     * @param padding 内边距
     * @param textColor 字体颜色
     */
    public void setText(float textSize,int padding,int textColor) {
        mTextSize = textSize;
        mPadding = padding;
        this.textColor = textColor;
    }

    private OnItemClickListener itemClickListener;
    private Context mContext;
    private int currentId = -1;
    private ArrayList<String> textList;
    private Handler handler;

    public VerticalTextview(Context context) {
        this(context, null);
        mContext = context;
    }

    public VerticalTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        textList = new ArrayList<String>();
    }

    private boolean isInitFactory = false;
    public void setAnimTime(long animDuration) {
        if(!isInitFactory){
            setFactory(this);
            isInitFactory = true;
        }
        Animation in = new TranslateAnimation(
                Animation.ABSOLUTE,0,
                Animation.ABSOLUTE,0,
                Animation.RELATIVE_TO_SELF,1.0f,
                Animation.RELATIVE_TO_SELF,0);

        Animation out = new TranslateAnimation(
                Animation.ABSOLUTE,0,
                Animation.ABSOLUTE,0,
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,-1.0f);

        AnimationSet animationSetIn = new AnimationSet(true);
        animationSetIn.addAnimation(in);
        animationSetIn.addAnimation(new AlphaAnimation(0.0f, 1.0f));
        animationSetIn.setDuration(animDuration);
        animationSetIn.setInterpolator(new AccelerateInterpolator());
        AnimationSet animationSetOut = new AnimationSet(true);
        animationSetOut.addAnimation(out);
        animationSetOut.addAnimation(new AlphaAnimation(1.0f,0.0f));
        animationSetOut.setDuration(animDuration);
        animationSetOut.setInterpolator(new AccelerateInterpolator());

        setInAnimation(animationSetIn);
        setOutAnimation(animationSetOut);
    }

    /**
     * 间隔时间
     * @param time
     */
    public void setTextStillTime(final long time){
        handler =new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case FLAG_START_AUTO_SCROLL:
                        if (textList.size() > 0) {
                            currentId++;
                            setText(textList.get(currentId % textList.size()));
                        }
                        handler.sendEmptyMessageDelayed(FLAG_START_AUTO_SCROLL,time);
                        break;
                    case FLAG_STOP_AUTO_SCROLL:
                        handler.removeMessages(FLAG_START_AUTO_SCROLL);
                        break;
                }
            }
        };
    }
    /**
     * 设置数据源
     * @param titles
     */
    public void setTextList(ArrayList<String> titles) {
        textList.clear();
        textList.addAll(titles);
        currentId = -1;
    }

    /**
     * 开始滚动
     */
    public void startAutoScroll() {
        handler.sendEmptyMessage(FLAG_START_AUTO_SCROLL);
    }

    /**
     * 停止滚动
     */
    public void stopAutoScroll() {
        if(handler!= null){
            handler.sendEmptyMessage(FLAG_STOP_AUTO_SCROLL);
        }
    }

    @Override
    public View makeView() {
        TextView t = new TextView(mContext);
        t.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        t.setMaxLines(1);
        t.setPadding(mPadding, mPadding, mPadding, mPadding);
        t.setTextColor(textColor);
        t.setTextSize(mTextSize);

        t.setClickable(true);
        t.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null && textList.size() > 0 && currentId != -1) {
                    itemClickListener.onItemClick(currentId % textList.size());
                }
            }
        });
        return t;
    }

    /**
     * 设置点击事件监听
     * @param itemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /**
     * 轮播文本点击监听器
     */
    public interface OnItemClickListener {
        /**
         * 点击回调
         * @param position 当前点击ID
         */
        void onItemClick(int position);
    }

}
