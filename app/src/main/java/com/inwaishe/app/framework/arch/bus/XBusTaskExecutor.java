package com.inwaishe.app.framework.arch.bus;

/**
 * Created by WangJing on 2017/10/24.
 */

public abstract class XBusTaskExecutor {

    public XBusTaskExecutor(){

    }

    public abstract void executeOnDiskIO(Runnable var1);

    public abstract void postToMainThread(Runnable var1);

    public abstract void executeNewThread(Runnable var1);

    public abstract void excuteCurrentThread(Runnable var1);

    public void executeOnMainThread(Runnable runnable) {
        if(this.isMainThread()) {
            runnable.run();
        } else {
            this.postToMainThread(runnable);
        }

    }

    public abstract boolean isMainThread();
}
