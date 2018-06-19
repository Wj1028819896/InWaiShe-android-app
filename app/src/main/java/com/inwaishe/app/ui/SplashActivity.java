package com.inwaishe.app.ui;

import android.animation.Animator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.inwaishe.app.R;

public class SplashActivity extends AppCompatActivity {

    private ImageView mIvAD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mIvAD = (ImageView) findViewById(R.id.ivAd);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mIvAD.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onGlobalLayout() {
                    mIvAD.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    if(mIvAD.getVisibility() == View.INVISIBLE) {
                        mIvAD.setVisibility(View.VISIBLE);
                    }
                    startCircularReveal();
                }
            });
        }else{
            mIvAD.setVisibility(View.VISIBLE);
            mIvAD.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    finish();
                }
            },300);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startCircularReveal(){
        Animator animator = ViewAnimationUtils.createCircularReveal(
                mIvAD,
                mIvAD.getWidth(),
                mIvAD.getHeight(),
                0,
                mIvAD.getHeight()+mIvAD.getWidth())
                .setDuration(2000);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }
            @Override
            public void onAnimationEnd(Animator animation) {
                mIvAD.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
                        finish();
                    }
                },300);
            }
            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }
}
