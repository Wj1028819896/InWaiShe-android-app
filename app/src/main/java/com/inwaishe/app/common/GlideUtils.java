package com.inwaishe.app.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.inwaishe.app.R;

import static android.R.attr.resource;

/**
 * Created by WangJing on 2017/9/12.
 */

public class GlideUtils {

    public static void disPlayUrlAutoWh(Context context, Object url, final ImageView imageView){
        RequestOptions options = new RequestOptions();
        options.placeholder(R.mipmap.logo);
        options.fitCenter();
        DrawableTransitionOptions doptions = new DrawableTransitionOptions().crossFade();
        RequestBuilder requestBuilder
                = GlideApp.with(context)
                .setDefaultRequestOptions(options)
                .asBitmap();
        requestBuilder.load(url)
                .transition(doptions)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                        ViewGroup.LayoutParams params = imageView.getLayoutParams();
                        int vw = imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
                        float scale = (float) vw / (float) bitmap.getWidth();
                        int vh = Math.round(bitmap.getHeight() * scale);
                        if(vh <= 0){
                            vh = vw;
                        }
                        params.height = vh + imageView.getPaddingTop() + imageView.getPaddingBottom();
                        imageView.setLayoutParams(params);
                        imageView.setImageBitmap(bitmap);
                    }
                });
    }

    public static void disPlayUrl(Context context,Object url,ImageView iv){

        RequestOptions options = new RequestOptions();
        options.placeholder(R.mipmap.logo);
        options.fitCenter();
        DrawableTransitionOptions doptions = new DrawableTransitionOptions().crossFade();
        GlideApp.with(context)
                .setDefaultRequestOptions(options)
                .load(url)
                .transition(doptions)
                .into(iv);
    }

    public static void disPlayUrlBackground(Context context, Object url, final View iv){

        RequestOptions options = new RequestOptions();
        options.placeholder(R.mipmap.logo);
        options.fitCenter();
        DrawableTransitionOptions doptions = new DrawableTransitionOptions().crossFade();
        GlideApp.with(context)
                .setDefaultRequestOptions(options)
                .load(url)
                .transition(doptions)
                .into(new SimpleTarget<Drawable>(iv.getWidth(),iv.getHeight()) {
                    @Override
                    public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                        iv.setBackground(drawable);
                    }
                });
    }

    public static void disPlayUrl(Fragment context, Object url, ImageView iv){

        RequestOptions options = new RequestOptions();
        options.placeholder(R.mipmap.logo);
        options.centerCrop();
        DrawableTransitionOptions doptions = new DrawableTransitionOptions().crossFade();
        GlideApp.with(context)
                .setDefaultRequestOptions(options)
                .load(url)
                .transition(doptions)
                .into(iv);
    }
}
