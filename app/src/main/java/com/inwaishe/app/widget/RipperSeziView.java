package com.inwaishe.app.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.inwaishe.app.R;

/**
 * Created by WangJing on 2017/11/24.
 */

public class RipperSeziView extends View {
    /**画笔**/
    private Paint mPainter;
    /**水波扩散标志**/
    private boolean startRipper = false;
    /**当前水波颜色**/
    private int ctxRipperColor = 0;
    /**当前水波纹半径**/
    private float ctxRipperRadio = 1.0f;
    /**水波扩散时间 ms **/
    private long ctxRipperDuration = 1000;

    public RipperSeziView(Context context) {
        this(context,null);
    }

    public RipperSeziView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RipperSeziView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RipperSeziView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context) {
        mPainter = new Paint();
        mPainter.setAntiAlias(true);
        mPainter.setStyle(Paint.Style.FILL_AND_STROKE);
        mPainter.setStrokeWidth(5);
        mPainter.setColor(getResources().getColor(R.color.green_light_dark));
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        //super.onDraw(canvas);
        if(startRipper){
            mPainter.setColor(ctxRipperColor);
            Log.e("onDraw",""+ctxRipperRadio);
            canvas.drawCircle(getWidth() / 1, getHeight() / 1 + getTop(),ctxRipperRadio,mPainter);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    public void start(){
        startRipper = true;
        int w = getWidth();
        int h = getHeight();
        int maxRadius = (int) Math.sqrt(w * w + h * h) + 1;
        final ValueAnimator animator = ValueAnimator.ofFloat(0,maxRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(ctxRipperDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ctxRipperRadio = (float) animation.getAnimatedValue();
                synchronized (animator){
                   invalidate();
                }
            }
        });

        ValueAnimator animator1 = ValueAnimator.ofInt(
                getResources().getColor(R.color.holo_blue_dark),
                getResources().getColor(R.color.gray_80));
        animator1.setEvaluator(new ArgbEvaluator());
        animator1.setDuration(ctxRipperDuration);
        animator1.setInterpolator(new AccelerateInterpolator());
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ctxRipperColor = (int) animation.getAnimatedValue();
            }
        });
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.playTogether(animator,animator1);
        animatorSet.setDuration(ctxRipperDuration);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(mListenr!=null){
                    mListenr.onAnimationStart();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(mListenr!=null){
                    mListenr.onAnimationEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private RAnimatorListener mListenr;
    public void setAnimatorListener(RAnimatorListener listener){
        this.mListenr = listener;
    }

    public interface RAnimatorListener{
        void onAnimationEnd();
        void onAnimationStart();
    }

}
