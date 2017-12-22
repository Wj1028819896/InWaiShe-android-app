package com.inwaishe.app.framework.arch.bus;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;

/**
 * Created by WangJing on 2017/10/24.
 */

public class XBusLifecycleBoundObserver implements LifecycleObserver{

    public final LifecycleOwner owner;
    public final XBusObserver observer;
    public final String tag;
    public XBusThreadModel xBusThreadModel = XBusThreadModel.currentThread();
    public XBusLifecycleBoundObserver(String tag,LifecycleOwner owner, XBusObserver observer){
        this.observer = observer;
        this.owner = owner;
        this.tag = tag;
    }
    @OnLifecycleEvent({Lifecycle.Event.ON_ANY})
    public void onStateChange(){
        if(this.owner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED){
            XBus.getInstance().remove(this);
        }
    }
    /**
     * 设置执行线程
     * @param xBusThreadModel
     */
    public void runOn(XBusThreadModel xBusThreadModel){
        this.xBusThreadModel = xBusThreadModel;
    }

}
