package com.inwaishe.app.framework.download;

import java.io.File;

/**
 * Created by WangJing on 2018/3/2.
 */

public interface DownLoadCallBack {
    void onDownLoading(long crxProgress,long totalMax);
    void onDownLoadSuccess(File file);
    void onDownLoadFailed(String error);
}
