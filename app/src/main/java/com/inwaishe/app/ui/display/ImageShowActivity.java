package com.inwaishe.app.ui.display;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inwaishe.app.R;
import com.inwaishe.app.base.BaseActivity;
import com.inwaishe.app.common.CommonData;
import com.inwaishe.app.common.GlideUtils;
import com.inwaishe.app.framework.download.DownLoadFileManager;
import com.inwaishe.app.framework.download.DownLoadCallBack;

import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import java.io.File;

import uk.co.senab.photoview.PhotoView;

/**
 * 图片展示 大图 缩放 使用 photoview
 */
public class ImageShowActivity extends BaseActivity {

    private ViewPager mVpPicList;
    private TextView mTvTitle;
    private TextView mTvCusror;
    private TextView mTvArtTitle;
//    private TextView mTvReadNum;
//    private TextView mTvReplyNum;
//    private RelativeLayout mRlReply;
//    private RelativeLayout mRlShare;
//    private RelativeLayout mRlDownload;
    private String[] mPicUrls;
    private ImageView mIvBack;
    private ImageView mIvDownLoad;
    private ImageView mIvShare;
    private int mIndex = 0;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show);
        mContext = this;
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mVpPicList = (ViewPager) findViewById(R.id.vpPicList);
        mTvTitle = (TextView) findViewById(R.id.tvTitle);
//        mTvReadNum = (TextView) findViewById(R.id.tvReadNum);
//        mRlReply = (RelativeLayout) findViewById(R.id.rlReply);
//        mRlShare = (RelativeLayout) findViewById(R.id.rlShare);
//        mTvReplyNum = (TextView) findViewById(R.id.tvReplyNum);
//        mRlDownload = (RelativeLayout) findViewById(R.id.rlDownload);
        mTvCusror = (TextView) findViewById(R.id.tvCursor);
        mTvArtTitle = (TextView) findViewById(R.id.tvArtTitle);
        mIvBack = (ImageView) findViewById(R.id.ivBack);
        mIvDownLoad = (ImageView) findViewById(R.id.ivDownload);
        mIvShare = (ImageView) findViewById(R.id.ivShare);
    }

    private void initData() {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mPicUrls = bundle.getStringArray(CommonData.PIC_URLS);
            mIndex = bundle.getInt(CommonData.PIC_INDEX);

            mTvArtTitle.setText(bundle.getString(CommonData.PIC_ARTTITLE,""));
            mTvTitle.setText(bundle.getString(CommonData.PIC_ARTAUTHOR,""));
            //mTvReadNum.setText("" + bundle.getInt(CommonData.PIC_ARTREADNUM,0));
            //mTvReplyNum.setText("" + bundle.getInt(CommonData.PIC_ARTCOMMENTNUM,0));
            mTvCusror.setText((mIndex+1)+"/"+mPicUrls.length);
        }

        mVpPicList.setAdapter(new ImagesPagerAdapter());
        mVpPicList.setCurrentItem(mIndex);
        mVpPicList.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mTvCusror.setText((i+1)+"/"+mVpPicList.getAdapter().getCount());

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    private void initEvent() {

        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mIvDownLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downLoadImage();
            }
        });
    }

    /**
     * 下载图片
     */
    private void downLoadImage() {

        String imageUrl = mPicUrls[mVpPicList.getCurrentItem()];
        String saveDir = CommonData.SAVE_BASE_DIR;
        DownLoadFileManager
                .getInstance(getApplicationContext())
                .downLoadImage(saveDir,imageUrl, new DownLoadCallBack() {
                    @Override
                    public void onDownLoading(long crxProgress, long totalMax) {

                    }

                    @Override
                    public void onDownLoadSuccess(File file) {
                        Toast.makeText(mContext,"保存为：" + file.getAbsolutePath(),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onDownLoadFailed(String error) {
                        Toast.makeText(mContext,"下载失败:!>_<|||" + error,Toast.LENGTH_LONG).show();
                    }
                });
    }

    class ImagesPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return mPicUrls.length;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());

            GlideUtils.disPlayUrl(ImageShowActivity.this,mPicUrls[position],photoView);
            container.addView(photoView,LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
