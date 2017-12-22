package com.inwaishe.app.framework.arch.bus;

import java.util.Observer;

/**
 * Created by WangJing on 2017/10/24.
 */

public interface XBusObserver<T> {

    void onCall(T var);
}
