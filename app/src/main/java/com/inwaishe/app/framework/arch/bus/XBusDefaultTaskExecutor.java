package com.inwaishe.app.framework.arch.bus;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by WangJing on 2017/10/24.
 */

public class XBusDefaultTaskExecutor extends XBusTaskExecutor{

    private final Object mLock = new Object();
    private ExecutorService mDiskIO = Executors.newFixedThreadPool(2);
    private volatile Handler mMainHandler;

    public XBusDefaultTaskExecutor(){

    }

    @Override
    public void executeOnDiskIO(Runnable runnable) {
        this.mDiskIO.execute(runnable);
    }

    @Override
    public void postToMainThread(Runnable runnable) {
        if(this.mMainHandler == null) {
            Object var2 = this.mLock;
            synchronized(this.mLock) {
                if(this.mMainHandler == null) {
                    this.mMainHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        this.mMainHandler.post(runnable);
    }

    @Override
    public void executeNewThread(Runnable runnable) {

    }

    @Override
    public void excuteCurrentThread(Runnable runnable) {
        runnable.run();
    }

    @Override
    public boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }
}
