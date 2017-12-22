package com.inwaishe.app.framework.animation;

import android.view.animation.Interpolator;

/**
 * Created by WangJing on 2017/10/26.
 */


public class SpringInterpolator implements Interpolator {
    private static final float DEFAULT_FACTOR = 0.4f;

    private float mFactor;

    public SpringInterpolator() {
        this(DEFAULT_FACTOR);
    }

    public SpringInterpolator(float factor) {
        mFactor = factor;
    }

    @Override
    public float getInterpolation(float input) {
        // pow(2, -10 * input) * sin((input - factor / 4) * (2 * PI) / factor) + 1
        return (float) (Math.pow(2, -10 * input) * Math.sin((input - mFactor / 4.0d) * (2.0d * Math.PI) / mFactor) + 1);

    }
}
