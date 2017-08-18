package com.inwaishe.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inwaishe.app.R;

/**
 * Created by Administrator on 2017/8/10 0010.
 */

public class TitleItemDecoration extends RecyclerView.ItemDecoration {


    private Context mContext;
    public TitleItemDecoration(Context context,RecyclerView recyclerView){



    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {


    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }
}
