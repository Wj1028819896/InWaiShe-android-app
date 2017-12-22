package com.inwaishe.app.framework.activitytrans;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.LayoutTransition;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.inwaishe.app.R;
import com.inwaishe.app.common.CommonData;

import java.io.File;

/**
 * Created by WangJing on 2017/9/30.
 */

public class EnterTransition {

    private Context mContext;
    private View toView;
    private Bundle savedInstanceState;
    private ViewGroup decorView;//
    int ori_left;
    int ori_top;
    int ori_width;
    int ori_height;
    ImageView toViewCopy;
    Animator.AnimatorListener animatorListener;
    private long animatorDuration = 500;
    private TimeInterpolator mTimerInterpolator = new DecelerateInterpolator();

    public EnterTransition(Context con,View toView,Bundle bd){
        this.mContext = con;
        this.toView = toView;
        this.savedInstanceState = bd;

        init();
    }

    public EnterTransition(){

    }

    public EnterTransition setAnimatorDuration(long duration){
        this.animatorDuration = duration;
        return this;
    }

    public EnterTransition setTimeInterpolator(TimeInterpolator timenterpolator){
        this.mTimerInterpolator = timenterpolator;
        return this;
    }
    private void init() {

        decorView = (ViewGroup) ((Activity)mContext).getWindow().getDecorView();
        toViewCopy = new ImageView(mContext);
        Bundle bd = ((Activity)mContext).getIntent().getExtras();
        ori_left = bd.getInt("V_LEFT");
        ori_top = bd.getInt("V_TOP");
        ori_width = bd.getInt("V_WIDTH");
        ori_height = bd.getInt("V_HEIGHT");

        animatorListener = new Animator.AnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                toView.setVisibility(View.INVISIBLE);
                toViewCopy.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                hideViewWithAlphaAnimation(toViewCopy,750);
                showViewWithAlphaAnimation(toView,1000);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
    }

    private void hideViewWithAlphaAnimation(final View view,long duration){
        AlphaAnimation alphaAnimationhide = new AlphaAnimation(1,0);
        alphaAnimationhide.setDuration(duration);
        view.startAnimation(alphaAnimationhide);
        alphaAnimationhide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void showViewWithAlphaAnimation(final View view,long duration){
        AlphaAnimation alphaAnimationshow = new AlphaAnimation(0,1);
        alphaAnimationshow.setDuration(duration);
        view.startAnimation(alphaAnimationshow);
        alphaAnimationshow.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void startAnimate(){
        if(mContext == null){
            return;
        }
        ViewGroup content = (ViewGroup) decorView.findViewById(android.R.id.content);
        //decorView.setBackgroundResource(R.drawable.transparent_drawable);//decorView 背景色透明
        //((Activity)mContext).getWindow().setBackgroundDrawableResource(R.drawable.transparent_drawable);//window 背景透明
        content.setAlpha(0.0F);//contentview 透明
        content.animate()
                .alpha(1.0F)
                .setInterpolator(mTimerInterpolator)
                .setDuration(animatorDuration);

        if(savedInstanceState == null){
            ViewTreeObserver viewTreeObserver = toView.getViewTreeObserver();
            viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    toView.getViewTreeObserver().removeOnPreDrawListener(this);
                    int[] screenLocation = new int[2];
                    toView.getLocationOnScreen(screenLocation);

                    int des_left = screenLocation[0];
                    int des_top = screenLocation[1];

                    int des_width = toView.getMeasuredWidth();
                    int des_height = toView.getMeasuredHeight();

                    toViewCopy.setBackgroundColor(Color.GRAY);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(des_width, des_height);
                    params.setMargins(des_left,des_top,0,0);
                    decorView.addView(toViewCopy, params);

                    String share_url = "file://" + CommonData.SAVE_BASE_DIR + File.separator + CommonData.SHAREVIEW_FILENAME;
                    toViewCopy.setImageURI(Uri.parse(share_url));
                    toViewCopy.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    int leftDelta = ori_left - des_left;
                    int topDelta = ori_top - des_top;

                    float widthScale = (float) ori_width / (float) des_width;
                    float heightScale = (float) ori_height / (float) des_height;

                    toViewCopy.setPivotX(0.0F);
                    toViewCopy.setPivotY(0.0F);
                    toViewCopy.setScaleX(widthScale);
                    toViewCopy.setScaleY(heightScale);
                    toViewCopy.setTranslationX((float) leftDelta);
                    toViewCopy.setTranslationY((float) topDelta);

                    toViewCopy.animate()
                            .setDuration(animatorDuration)
                            .scaleX(1.0F)
                            .scaleY(1.0F)
                            .translationX(0.0F)
                            .translationY(0.0F)
                            .setListener(null)
                            .setInterpolator(mTimerInterpolator)
                            .setListener(animatorListener);
                    return true;
                }
            });
        }
    }
}