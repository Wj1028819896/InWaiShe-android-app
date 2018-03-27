package com.inwaishe.app.ui;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.inwaishe.app.R;
import com.inwaishe.app.adapter.PhotoCenterAdapter;
import com.inwaishe.app.base.BaseActivity;
import com.inwaishe.app.entity.photocenter.PhotoCenterBackInfo;
import com.inwaishe.app.viewmodel.PhotoCenterViewModel;

/**
 * 摄影社区
 */
public class PhotoCenterActivity extends BaseActivity implements LifecycleRegistryOwner {

    private PhotoCenterViewModel mViewModel;
    private RecyclerView mRcPhotoList;
    private PhotoCenterAdapter mAdapter;
    private ImageView mIvBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_center);

        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mRcPhotoList = (RecyclerView) findViewById(R.id.rcPhotoList);
        mIvBack = (ImageView) findViewById(R.id.ivBack);
    }

    private void initData() {
        mViewModel = ViewModelProviders.of(this).get(PhotoCenterViewModel.class);
        mViewModel.getPhotoCenterBackInfoMutableLiveData().observe(this, new Observer<PhotoCenterBackInfo>() {
            @Override
            public void onChanged(@Nullable PhotoCenterBackInfo photoCenterBackInfo) {
                if(mAdapter != null){
                    mAdapter.setLoadingMore(false);
                    mAdapter.setLoadAll(photoCenterBackInfo.isLoadAll);
                    mAdapter.notifyDataSetChanged();
                    if(photoCenterBackInfo.code < 0){
                        Toast.makeText(PhotoCenterActivity.this,"" + photoCenterBackInfo.msg,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mAdapter = new PhotoCenterAdapter(mRcPhotoList,this,mViewModel.getPhotoCenterBackInfoMutableLiveData().getValue());
        mAdapter.setOnLoadOrRefreshListener(new PhotoCenterAdapter.OnLoadOrRefreshListener() {
            @Override
            public void onLoadMore() {
                mViewModel.loadData(false);
            }

            @Override
            public void onRefresh() {
                mViewModel.loadData(true);
            }
        });
        mRcPhotoList.setAdapter(mAdapter);
        mViewModel.loadData(true);
    }

    private void initEvent() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);

    @Override
    public LifecycleRegistry getLifecycle() {
        return mLifecycleRegistry;
    }
}
