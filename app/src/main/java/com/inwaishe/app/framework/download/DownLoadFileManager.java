package com.inwaishe.app.framework.download;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by WangJing on 2018/3/2.
 */

public class DownLoadFileManager {

    private static DownLoadFileManager mInstance;
    /**
     * 单线程列队执行
     */
    private ExecutorService singleExecutor = null;
    /**
     * 上下文（请使用 AppliCationContext）
     */
    private Context mContext;

    /**
     * 获取实例
     * @param context 请使用 AppliCationContext
     * @return
     */
    public static DownLoadFileManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new DownLoadFileManager(context);
        }
        return mInstance;
    }

    private DownLoadFileManager(Context context){
        this.mContext = context;
        if (singleExecutor == null) {
            singleExecutor = Executors.newSingleThreadExecutor();
        }
    }


    /**
     * 执行单线程列队执行
     */
    private void runOnQueue(Runnable runnable) {
        singleExecutor.submit(runnable);
    }

    /**
     * 启动下载图片
     * @param imageUrl 图片地址
     * @param imageDownLoadCallBack 下载回调
     */
    public void downLoadImage(String saveDir,String imageUrl,DownLoadCallBack imageDownLoadCallBack){
        DownLoadFileService service = new DownLoadFileService(saveDir,imageUrl,imageDownLoadCallBack);
        runOnQueue(service);
    }
}
