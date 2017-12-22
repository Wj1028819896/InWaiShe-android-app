package com.inwaishe.app.framework.activitytrans;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.inwaishe.app.common.AppUtils;
import com.inwaishe.app.common.CommonData;
import com.inwaishe.app.common.GlideUtils;

import java.io.File;

/**
 * Created by WangJing on 2017/9/30.
 */

public class EnterActivityTool {

    public static EnterTransition getEnterTransition(Context context, View toView, Bundle savedInstanceState){
        ViewCompat.setTransitionName(toView,"shareView");
        if(!isEnable(context)){
            return new EnterTransition();
        }
        return new EnterTransition(context,toView,savedInstanceState);
    }

    public static boolean isEnable(Context context){
        Bundle bd = ((Activity)context).getIntent().getExtras();
        if(bd == null){
            return false;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            return false;
        }
        return bd.getInt("V_WIDTH")==0?false:true;
    }
}
