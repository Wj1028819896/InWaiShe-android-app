package com.inwaishe.app.framework.arch.bus;

import android.os.Looper;

/**
 * Created by WangJing on 2017/10/24.
 */

public class XBusThreadModel {

    public int xThradModel = 0;
    public static final int xThreadCurrent = 0x001;
    public static final int xThreadMain = 0x002;
    public static final int xThreadIO = 0x003;

    private XBusThreadModel(int model){
        this.xThradModel = model;
    }

    public static XBusThreadModel mainThread(){
        return new XBusThreadModel(xThreadMain);
    }

    public static XBusThreadModel currentThread(){
        return new XBusThreadModel(xThreadCurrent);
    }

    public static XBusThreadModel ioThread(){
        return new XBusThreadModel(xThreadIO);
    }
}
