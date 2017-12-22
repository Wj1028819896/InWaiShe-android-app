package com.inwaishe.app.common;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.inwaishe.app.R;

/**
 * Created by WangJing on 2017/9/12.
 */

public class GlideUtils {

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
