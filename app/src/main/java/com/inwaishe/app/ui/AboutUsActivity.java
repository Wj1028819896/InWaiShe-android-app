package com.inwaishe.app.ui;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.inwaishe.app.R;

public class AboutUsActivity extends AppCompatActivity implements LifecycleRegistryOwner {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        WebView webView = (WebView) findViewById(R.id.wv);
        webView.loadUrl("https://github.com/Wj1028819896/InWaiShe-android-app");


    }
    LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);

    @Override
    public LifecycleRegistry getLifecycle() {
        return mLifecycleRegistry;
    }
}
