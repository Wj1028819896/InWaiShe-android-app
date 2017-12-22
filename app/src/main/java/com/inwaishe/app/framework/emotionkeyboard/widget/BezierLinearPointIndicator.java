package com.inwaishe.app.framework.emotionkeyboard.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by WangJing on 2017/12/8.
 * 贝塞尔效果圆点指示器
 */

public class BezierLinearPointIndicator extends View {

    //贝塞尔圆曲率
    private static final float C = 0.551915024494f;
    //指示圆点个数
    private int indicatorPointNum = 5;
    //指示器空心圆环颜色
    private int indicatorRoundColor = 0;
    //指示器实心圆颜色
    private int indicatorRoundFullColor = 0;
    //圆环半径
    private int roundRadio = 0;
    //布局宽高
    private int width = 0;
    private int height = 0;
    //画笔
    private Paint paint;
    //画笔宽度
    private int paintStrokeWidth = 6;




    public BezierLinearPointIndicator(Context context) {
        this(context,null);
    }

    public BezierLinearPointIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BezierLinearPointIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    /**
     * 初始化自定义属性
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRing(canvas);
        drawCircle(canvas);
    }

    /**
     * 画圆
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {

    }

    /**
     * 画圆环
     * @param canvas
     */
    private void drawRing(Canvas canvas) {

        calculateCenterPointOfCircle();

    }

    /**
     * 计算圆心位置
     */
    private void calculateCenterPointOfCircle() {
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int w = width - paddingLeft - paddingRight;
        int h = height - paddingBottom - paddingTop;
        for(int p = 0; p < indicatorPointNum; p++){

            int pointX = w/indicatorPointNum/2 + p*w/indicatorPointNum;

        }


    }
}
