package com.inwaishe.app.framework.arch.bus;

/**
 * Created by WangJing on 2017/10/24.
 * 任务执行器
 * IO线程
 * Main UI
 * new Thread
 */

public class XBusToolkitTaskExecutor extends XBusTaskExecutor {

    private static volatile XBusToolkitTaskExecutor sInstance;

    private XBusTaskExecutor xBusTaskExector;

    public static XBusToolkitTaskExecutor getInstance(){
        if(sInstance == null){
            sInstance = new XBusToolkitTaskExecutor();
        }
        return sInstance;
    }

    private XBusToolkitTaskExecutor(){
        xBusTaskExector = new XBusDefaultTaskExecutor();
    }

    @Override
    public void executeOnDiskIO(Runnable runnable) {
        xBusTaskExector.executeOnDiskIO(runnable);
    }

    @Override
    public void postToMainThread(Runnable runnable) {
        xBusTaskExector.postToMainThread(runnable);

    }

    @Override
    public void executeNewThread(Runnable runnable) {
        xBusTaskExector.executeNewThread(runnable);
    }

    @Override
    public void excuteCurrentThread(Runnable runnable) {
        xBusTaskExector.excuteCurrentThread(runnable);
    }

    @Override
    public boolean isMainThread() {
        return xBusTaskExector.isMainThread();
    }
}
