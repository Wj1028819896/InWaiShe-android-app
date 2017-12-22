package com.inwaishe.app.ui.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;

import com.inwaishe.app.R;
import com.inwaishe.app.framework.activitytrans.EnterActivityTool;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        EnterActivityTool.getEnterTransition(this,findViewById(R.id.iv2),savedInstanceState)
                .setAnimatorDuration(2000)
                .setTimeInterpolator(new DecelerateInterpolator())
                .startAnimate();
    }
}
