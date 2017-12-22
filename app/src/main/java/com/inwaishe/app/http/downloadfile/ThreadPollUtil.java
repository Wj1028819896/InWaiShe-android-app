package com.inwaishe.app.http.downloadfile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by WangJing on 2017/10/10.
 */

public class ThreadPollUtil {

    private static ThreadPollUtil sInstance;
    private ExecutorService executorService;
    private ThreadPollUtil(){
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }
    public static ThreadPollUtil getInstance(){
        if(sInstance == null){
            sInstance = new ThreadPollUtil();
        }
        return sInstance;
    }

    public void exeCute(Runnable runnable){
        executorService.execute(runnable);
    }
}
