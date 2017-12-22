package com.inwaishe.app.framework.arch.bus;

import android.arch.lifecycle.LifecycleOwner;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WangJing on 2017/10/24.
 *
 * 数据总线 用于  跨越组件的通信
 * 参考 android.arch.lifecycle LiveData
 * （减少startActivityForResult）
 * 用于 Activity fragment 之间数据有效传递
 */

public class XBus<T> {

    HashMap<String,HashMap<XBusObserver,XBusLifecycleBoundObserver>> mXbusObservers;
    public static XBus xBus;
    public static XBus getInstance(){
        if(xBus == null){
            xBus = new XBus();
        }
        return xBus;
    }
    private XBus(){
        mXbusObservers = new HashMap<>();
    }

    public void remove(XBusLifecycleBoundObserver observer){
        mXbusObservers.get(observer.tag).remove(observer.observer);
    }
    public XBusLifecycleBoundObserver observe(LifecycleOwner owner,String tag,XBusObserver observer){

        XBusLifecycleBoundObserver xBusLifecycleBoundObserver = new XBusLifecycleBoundObserver(tag,owner,observer);
        if(mXbusObservers.get(tag) == null){
            mXbusObservers.put(tag,new HashMap<XBusObserver, XBusLifecycleBoundObserver>());
            mXbusObservers.get(tag).put(observer,xBusLifecycleBoundObserver);
            owner.getLifecycle().addObserver(xBusLifecycleBoundObserver);
        }else{
            if(mXbusObservers.get(tag).containsKey(observer)){
                if(mXbusObservers.get(tag).get(observer).owner != xBusLifecycleBoundObserver.owner){

                }else{
                    return mXbusObservers.get(tag).get(observer);
                }
            }else{
                mXbusObservers.get(tag).put(observer,xBusLifecycleBoundObserver);
                owner.getLifecycle().addObserver(xBusLifecycleBoundObserver);
            }
        }
        return xBusLifecycleBoundObserver;
    }

    public void post(final String tag, final T t){
        for(Map.Entry entry : mXbusObservers.get(tag).entrySet()){
            final XBusLifecycleBoundObserver xBusLifecycleBoundObserver = ((XBusLifecycleBoundObserver)entry.getValue());
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    xBusLifecycleBoundObserver.observer.onCall(t);
                }
            };
            switch (xBusLifecycleBoundObserver.xBusThreadModel.xThradModel){
                case XBusThreadModel.xThreadCurrent:
                    XBusToolkitTaskExecutor.getInstance().excuteCurrentThread(runnable);
                    break;
                case XBusThreadModel.xThreadMain:
                    XBusToolkitTaskExecutor.getInstance().postToMainThread(runnable);
                    break;
                case XBusThreadModel.xThreadIO:
                    XBusToolkitTaskExecutor.getInstance().executeOnDiskIO(runnable);
                    break;
            }
        }

    }
}
