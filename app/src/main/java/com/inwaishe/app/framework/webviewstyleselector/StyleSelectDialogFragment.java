package com.inwaishe.app.framework.webviewstyleselector;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.SeekBar;

import com.inwaishe.app.R;
import com.inwaishe.app.framework.webviewstyleselector.widget.NumSeekBar;

/**
 * Created by WangJing on 2017/12/15.
 *
 */

public class StyleSelectDialogFragment extends DialogFragment {


    View rootView;
    Button btnCancel;
    NumSeekBar fontSizeSeekBar;
    AppCompatSeekBar brightNessSeerBar;
    FontSizeChangeListener fontSizeListener;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = inflater.inflate(R.layout.dialog_styleselect,container,false);
        fontSizeSeekBar = (NumSeekBar) rootView.findViewById(R.id.fontSizeSeerBar);
        brightNessSeerBar = (AppCompatSeekBar) rootView.findViewById(R.id.brightNessSeerBar);
        btnCancel = (Button) rootView.findViewById(R.id.btnCancel);
        slideToUp(rootView);
        initData();
        initEvent();
        return rootView;
    }

    private void initData() {
        fontSizeSeekBar.getConfig().setSelecterListener(lis).Build();
    }

    private void initEvent() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        brightNessSeerBar.setOnSeekBarChangeListener(new BrightNessSeekBarChangeListener());
    }

    class BrightNessSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    NumSeekBar.selecterListener lis = new NumSeekBar.selecterListener() {
        @Override
        public void onSelected(int position, String scaleWord) {
            if(fontSizeListener != null){
                fontSizeListener.onChanged(position,scaleWord);
            }
        }
    };

    public interface FontSizeChangeListener{
        void onChanged(int position, String scaleWord);
    }

    public void setfontSizeChangeListener(FontSizeChangeListener lis){
        this.fontSizeListener = lis;
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
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
