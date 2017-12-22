package com.inwaishe.app.common;

import android.animation.Animator;

/**
 * Created by WangJing on 2017/11/24.
 */

public class XAnimatorListener implements Animator.AnimatorListener{
    @Override
    public void onAnimationStart(Animator animation) {
        this.onAnimationStartX(animation);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        this.onAnimationEndX(animation);
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        this.onAnimationCancelX(animation);
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        this.onAnimationRepeatX(animation);
    }

    public void onAnimationStartX(Animator animation){

    }

    public void onAnimationEndX(Animator animation) {

    }

    public void onAnimationCancelX(Animator animation) {

    }

    public void onAnimationRepeatX(Animator animation) {

    }
}
