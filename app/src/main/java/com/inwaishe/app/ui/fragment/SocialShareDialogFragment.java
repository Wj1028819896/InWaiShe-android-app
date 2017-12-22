package com.inwaishe.app.ui.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;

import com.inwaishe.app.R;
import com.inwaishe.app.framework.animation.SpringInterpolator;

/**
 * Created by WangJing on 2017/10/25.
 */

public class SocialShareDialogFragment extends DialogFragment {
    Button btnCancel;
    LinearLayout LlCopyLink,LlShareToWxCircle,LlShareToWxfriends;
    public static final int EVENT_COPYLIK = 0x001;
    public static final int EVENT_SHAREWXCIRCLE = 0x002;
    public static final int EVENT_SHAREWXFRIENDS = 0x003;
    EventCallBack mEventCallBack;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_social_share,container,false);

        LinearLayout ll = (LinearLayout) view.findViewById(R.id.llItems);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        LlCopyLink = (LinearLayout) view.findViewById(R.id.llCopyLink);
        LlShareToWxCircle = (LinearLayout) view.findViewById(R.id.llWxCircle);
        LlShareToWxfriends = (LinearLayout) view.findViewById(R.id.llWxFriends);
        waveView(ll);
        initEvent();
        slideToUp(view);
        return view;
    }

    private void initEvent() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        LlCopyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEventCallBack != null){
                    mEventCallBack.callBack(EVENT_COPYLIK);
                }
                dismiss();
            }
        });
        LlShareToWxCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEventCallBack != null){
                    mEventCallBack.callBack(EVENT_SHAREWXCIRCLE);
                }
                dismiss();
            }
        });
        LlShareToWxfriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEventCallBack != null){
                    mEventCallBack.callBack(EVENT_SHAREWXFRIENDS);
                }
                dismiss();
            }
        });
    }

    public void setEventCallBack(EventCallBack eventCallBack){
        this.mEventCallBack = eventCallBack;
    }
    public interface EventCallBack{
        void callBack(int eventCode);
    }

    private void waveView(final View view) {
        int cnum = ((ViewGroup)view).getChildCount();
        for(int i = 0;i< cnum;i++){
            final View childView = ((ViewGroup)view).getChildAt(i);
            final int finalI = i;
            childView.getViewTreeObserver()
                    .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {

                    int rootViewHeight = view.getMeasuredHeight();
                    childView.getViewTreeObserver().removeOnPreDrawListener(this);

                    childView.setPivotX(0.0F);
                    childView.setPivotY(0.0F);
                    childView.setTranslationY((float) rootViewHeight);

                    childView.animate()
                            .setDuration(350* (finalI + 1))
                            .translationY(0.0F)
                            .setInterpolator(new SpringInterpolator(0.25f))
                            .start();
                    return true;
                }
            });
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        /**
         * 设置无标题透明背景样式
         */
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void slideToUp(View view){
        Animation slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        slide.setDuration(400);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        view.startAnimation(slide);
        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}
