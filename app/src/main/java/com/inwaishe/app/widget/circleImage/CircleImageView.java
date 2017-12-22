package com.inwaishe.app.widget.circleImage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

/**
 * 圆形 ImageView
 * Created by WangJing on 2017/9/21.
 */

public class CircleImageView extends android.support.v7.widget.AppCompatImageView {


    public CircleImageView(Context context) {
        super(context);
    }
    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public CircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int mCircleRadius = 0;//半径
    private List<PointF> mPointDatas; //放置四个数据贝塞尔点的集合
    private List<PointF> mPointControlls;//方式8个贝塞尔控制点的集合
    float magic_number = 0.55228475f;//4分之1 圆弧
    private int mCenterX,mCenterY;//圆心
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //初始化坐标系
        mCenterX = getWidth() / 2;
        mCenterY = getHeight() / 2;
        mCircleRadius = mCenterY;
        //初始化贝塞尔点和控制点
        mPointDatas = new ArrayList<>();
        mPointControlls = new ArrayList<>();
        mPointDatas.add(new PointF(mCenterX, mCenterY - mCircleRadius));
        mPointDatas.add(new PointF(mCenterX + mCircleRadius, mCenterY));
        mPointDatas.add(new PointF(mCenterX, mCenterY + mCircleRadius));
        mPointDatas.add(new PointF(mCenterX - mCircleRadius, mCenterY));
        mPointControlls.add(new PointF(mCenterX + mCircleRadius * magic_number, mCenterY - mCircleRadius));
        mPointControlls.add(new PointF(mCenterX + mCircleRadius, mCenterY - mCircleRadius * magic_number));
        mPointControlls.add(new PointF(mCenterX + mCircleRadius, mCenterY + mCircleRadius * magic_number));
        mPointControlls.add(new PointF(mCenterX + mCircleRadius * magic_number, mCenterY + mCircleRadius));
        mPointControlls.add(new PointF(mCenterX - mCircleRadius * magic_number, mCenterY + mCircleRadius));
        mPointControlls.add(new PointF(mCenterX - mCircleRadius, mCenterY + mCircleRadius * magic_number));
        mPointControlls.add(new PointF(mCenterX - mCircleRadius, mCenterY - mCircleRadius * magic_number));
        mPointControlls.add(new PointF(mCenterX - mCircleRadius * magic_number, mCenterY - mCircleRadius));
    }

    /***
     * 获取当前View的圆形Path
     * @return
     */
    private Path getRoundClipPath() {

        Path path = new Path();
        path.addCircle(mCenterX,mCenterY,mCircleRadius, Path.Direction.CW);
        /**path.moveTo(mPointDatas.get(0).x, mPointDatas.get(0).y);
        for (int i = 0; i < mPointDatas.size(); i++) {
            if (i == mPointDatas.size() - 1) {
                path.cubicTo(mPointControlls.get(2 * i).x, mPointControlls.get(2 * i).y, mPointControlls.get(2 * i + 1).x, mPointControlls.get(2 * i + 1).y, mPointDatas.get(0).x, mPointDatas.get(0).y);

            } else {
                path.cubicTo(mPointControlls.get(2 * i).x, mPointControlls.get(2 * i).y, mPointControlls.get(2 * i + 1).x, mPointControlls.get(2 * i + 1).y, mPointDatas.get(i + 1).x, mPointDatas.get(i + 1).y);
            }

        }***/
        path.close();
        return path;
    }

    /**
     * 绘制SRC
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Path clipPath = getRoundClipPath();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        super.onDraw(canvas);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        canvas.drawPath(clipPath, paint);
        canvas.restoreToCount(saveCount);
        paint.setXfermode(null);
    }
    /***
     * 绘制Background
     * @param canvas
     */
    @Override
    public void draw(Canvas canvas) {
        Path clipPath = getRoundClipPath();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        super.draw(canvas);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        canvas.drawPath(clipPath, paint);
        canvas.restoreToCount(saveCount);
        paint.setXfermode(null);
    }
}
