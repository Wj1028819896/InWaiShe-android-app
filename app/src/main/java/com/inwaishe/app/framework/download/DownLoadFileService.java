package com.inwaishe.app.framework.download;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by WangJing on 2018/3/2.
 */

public class DownLoadFileService implements Runnable{

    private String url;
    private Context context;
    private DownLoadCallBack callBack;
    private Handler mainThreadHandler;
    private String saveDir;

    public DownLoadFileService(String saveDir, String url, DownLoadCallBack callBack) {
        this.url = url;
        this.callBack = callBack;
        //this.context = context;
        this.mainThreadHandler = new Handler(Looper.getMainLooper());
        this.saveDir = saveDir;
    }

    @Override
    public void run() {
        Request request = new Request.Builder().url(url).build();

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savePath = isExistDir(saveDir);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    final File file = new File(savePath, getNameFromUrl(url));
                    if(file.exists()){
                        file.delete();
                    }
                    file.createNewFile();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        final int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        mainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onDownLoading(progress,100L);
                            }
                        });
                    }
                    fos.flush();
                    // 下载完成
                    mainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onDownLoadSuccess(file);
                        }
                    });
                } catch (Exception e) {
                    callBack.onDownLoadFailed(e.getMessage());
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException
     * 判断下载目录是否存在
     */
    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * @param url
     * @return
     * 从下载连接中解析出文件名
     */
    public String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

}
