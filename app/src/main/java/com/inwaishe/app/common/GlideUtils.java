package com.inwaishe.app.common;

import android.app.Application;
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
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.inwaishe.app.R;

import java.io.File;
import java.math.BigDecimal;

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

    // 获取Glide磁盘缓存大小
    public static String getCacheSize(Context context) {
        try {
            return getFormatSize(getFolderSize(new File(context.getCacheDir() + "/" + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR)));
        }
        catch (Exception e) {
            e.printStackTrace(); return "获取失败";
        }
    }

    // 获取指定文件夹内所有文件大小的和
    private static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } return size;
    }
    // 格式化单位
    private static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    public static void clear(final Context context){
        GlideApp.get(context).clearMemory();
        new Thread(new Runnable() {
            @Override
            public void run() {
                GlideApp.get(context).clearDiskCache();
            }
        }).start();
    }
}
