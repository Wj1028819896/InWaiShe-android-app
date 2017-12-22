package com.inwaishe.app.framework.webviewstyleselector.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.widget.ProgressBar;

/**
 * Created by WangJing on 2017/12/12.
 */

public class NumSeekBar extends AppCompatSeekBar {

    private String TAG = "NumSeekBar";
    //刻度文字列表
    private String[] scaleWords;
    //刻度文字颜色列表
    private int[] scaleWordColors;
    //刻度文字大小
    private int[] scaleWordSp;
    //刻度最终停留位置
    private int selectScalePosition = 0;
    //刻度标志的颜色
    private int[] scaleMarksColor;
    //选择回调
    private selecterListener selecterListener;
    //开始时刻度位置
    private int beginScalePosition = 0;
    //配置
    private BuilderConfig config;

    public NumSeekBar(Context context) {
        super(context);
        init();
    }

    public NumSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NumSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    /**
     * 初始化
     */
    private void init() {
        int color = Color.parseColor("#ff0099cc");
        getConfig()
                .setScaleWords(new String[]{"小","中","大","特大"})
                .setScaleWordColors(new int[]{color,color,color,color})
                .setScaleWordSp(new int[]{10,12,14,16})
                .setScaleMarksColor(new int[]{color,color,color,color})
                .setBeginScalePosition(1)
                .setSelecterListener(new selecterListener() {
                    @Override
                    public void onSelected(int position, String scaleWord) {
                        Log.i(TAG,"selected " + position + " : " + scaleWord);
                    }
                })
                .Build();
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
    }

    public interface selecterListener{
        /**
         * 回调监听
         * @param position 刻度位置
         * @param scaleWord 刻度文字
         */
        void onSelected(int position,String scaleWord);
    }

    public BuilderConfig getConfig(){
        if(config == null){
            config = new BuilderConfig(this);

        }
        config.setScaleMarksColor(scaleMarksColor);
        config.setScaleWordColors(scaleWordColors);
        config.setScaleWords(scaleWords);
        config.setScaleWordSp(scaleWordSp);
        config.setSelecterListener(selecterListener);
        config.setBeginScalePosition(beginScalePosition);
        return config;
    }

    public void build(BuilderConfig config){
        scaleMarksColor = config.scaleMarksColor;
        scaleWordColors = config.scaleWordColors;
        scaleWords = config.scaleWords;
        scaleWordSp = config.scaleWordSp;
        selecterListener = config.selecterListener;
        beginScalePosition = config.beginScalePosition;

        avalidBuild();
    }

    /**
     * 神效配置
     */
    private void avalidBuild() {
        setMax(getMax()*(scaleWords.length-1));//保证长度被整除
        setProgress(beginScalePosition*(getMax()/(scaleWords.length-1)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(getProgressDrawable() == null){
            super.onDraw(canvas);
            return;
        }
        Rect pbRect = getProgressDrawable().getBounds();
        /**画刻度**/
        //进度条宽高
        int pbWidth = pbRect.width();
        int pbHeight = pbRect.height() - getPaddingTop() - getPaddingBottom();
        //刻度间距
        int kdDivider = pbWidth/(scaleWords.length-1);
        //圆的半径
        int radio = getThumb().getIntrinsicWidth()/2;
        //刻度进度间距
        int dv = getMax()/(scaleWords.length-1);
        //第几个刻度
        int position = getProgress()/dv;
        //画笔
        Paint mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#ff0099cc"));
        mPaint.setAntiAlias(true);
        //画
        for(int p = 0;p < scaleWords.length;p++){
            if(p <= position){
                mPaint.setColor(scaleMarksColor[p]);
            }else{
                mPaint.setColor(Color.parseColor("#BEBBB4"));
            }
            float dx = getPaddingLeft() + kdDivider*p;
            float dy = getPaddingTop()+ pbRect.top + pbRect.height()/2;
            canvas.drawCircle(dx,dy,radio/2,mPaint);
        }
        super.onDraw(canvas);
        /**画文字**/
        int add = (getProgress()%dv)>(dv/2)?1:0;
        if(add == 0){
            mPaint.setColor(scaleWordColors[position]);
        }else{
            if(getProgress()%dv == 0){
                mPaint.setColor(scaleWordColors[position + 1]);
            }else{
                mPaint.setColor(Color.parseColor("#BEBBB4"));
            }
        }
        position = position + add;
        selectScalePosition = position;
        //文字
        String shouldDrawTxt = scaleWords[position];
        //文字字体大小
        float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP
                ,scaleWordSp[position],getResources().getDisplayMetrics());
        //测量文字宽度
        mPaint.setTextSize(size);
        mPaint.setStrokeWidth(7);
        float mTextWidth = mPaint.measureText(shouldDrawTxt);
        //测量文字高度
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        //计算文字基线位置
        float dx = getPaddingLeft() + kdDivider*position - mTextWidth/2;
        float dy = pbRect.top + pbRect.height()/2 - Math.max(fm.bottom,radio) - 5;
        canvas.drawText(shouldDrawTxt,dx,dy,mPaint);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP){
            int dv = getMax()/(scaleWords.length-1);
            final int progress = dv*selectScalePosition;
            Log.e("pb","dv-->" + dv);
            Log.e("pb","max-->" + getMax());
            Log.e("pb","需要跳到-->" + progress);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        setProgress(progress,true);
                    }
                },100);

            }else{
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setProgress(progress);
                    }
                },100);
            }
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(selecterListener != null){
                        selecterListener.onSelected(selectScalePosition,scaleWords[selectScalePosition]);
                    }
                }
            },200);
        }
        return super.onTouchEvent(event);
    }

    /**
     * 属性动画 不能改变 mProgress 变量的值
     * 但是 显示效果没问题
     * @param pb
     * @param progressTo
     */
    private void setProgressAnimate(ProgressBar pb, int progressTo)
    {
        ObjectAnimator animation = ObjectAnimator.ofInt(pb, "progress", pb.getProgress(), progressTo);
        animation.setDuration(500);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.start();
    }
}
