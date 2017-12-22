package com.inwaishe.app.framework.activitytrans;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.View;

import com.facebook.stetho.common.android.FragmentCompat;
import com.inwaishe.app.R;
import com.inwaishe.app.common.AppUtils;
import com.inwaishe.app.common.CommonData;

/**
 * Created by WangJing on 2017/9/30.
 */

public class FromActivityTool {


    /**
     * 常规启动
     * @param base
     * @param it
     */
    public static void defaultLaucher(Activity base, Intent it){
        base.startActivity(it);
    };

    /**
     * 带结果返回的启动
     * @param base
     * @param it
     * @param RequestCode
     */
    public static void defaultLaucherWithResult(Activity base,Intent it,int RequestCode){
        base.startActivityForResult(it,RequestCode);
    }

    /**
     *
     * @param base
     * @param it
     * @param fromView
     */
    public static void laucherWithShareView(Activity base, Intent it, View fromView){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(base,fromView,"shareView");
            ActivityCompat.startActivity(base,it,compat.toBundle());
        }else{
            Bundle bd = createBundleWithView(fromView);
            it.putExtras(bd);
            base.startActivity(it);
            base.overridePendingTransition(0,0);
        }
    }

    /**
     *
     * @param base
     * @param it
     * @param fromView
     * @param RequestCode
     */
    public static void laucherWithShareViewForResult(Activity base, Intent it, View fromView, int RequestCode){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(base,fromView,"shareView");
            ActivityCompat.startActivity(base,it,compat.toBundle());
        }else{
            Bundle bd = createBundleWithView(fromView);
            it.putExtras(bd);
            base.startActivityForResult(it,RequestCode);
            base.overridePendingTransition(0,0);
        }
    }
    /**
     *
     * @param base
     * @param it
     * @param fromView
     * @param RequestCode
     */
    public static void laucherWithShareViewForResult(Fragment base, Intent it, View fromView, int RequestCode){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(base.getActivity(),fromView,"shareView");
            ActivityCompat.startActivityForResult(base.getActivity(),it,RequestCode,compat.toBundle());
        }else{
            Bundle bd = createBundleWithView(fromView);
            it.putExtras(bd);
            base.startActivityForResult(it,RequestCode);
            base.getActivity().overridePendingTransition(0,0);
        }
    }

    private static Bundle createBundleWithView(View fromView) {


        //共享元素的位置
        int location[] = new int[2];
        fromView.getLocationOnScreen(location);
        //共享元素的宽高
        int width = fromView.getMeasuredWidth();
        int height = fromView.getMeasuredHeight();
        //元素内容(获得View的bitmap将bm保存文件中)
        Bitmap bm = AppUtils.getBitmapFrowView(fromView);
        AppUtils.saveBitmap(bm, CommonData.SHAREVIEW_FILENAME);
        bm.recycle();
        //组装BUNDLE
        Bundle bd = new Bundle();
        bd.putInt("V_LEFT",location[0]);
        bd.putInt("V_TOP",location[1]);
        bd.putInt("V_WIDTH",width);
        bd.putInt("V_HEIGHT",height);
        return bd;
    }
}
