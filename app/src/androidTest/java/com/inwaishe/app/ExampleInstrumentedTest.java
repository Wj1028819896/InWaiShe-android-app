package com.inwaishe.app;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    final static String TAG = "ExampleInstrumentedTest";
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);

        Handler.Callback callback = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log.e(TAG,"收到消息:" + msg.obj.toString());
                return false;
            }
        };

        final MyThread cT1 = new MyThread(callback){
            @Override
            public void run() {
                super.run();
            }
        };
        final MyThread cT2 = new MyThread(callback){
            @Override
            public void run() {
                super.run();
                Message message = new Message();
                message.arg1 = 1;
                message.obj = "来自ct2 的消息";
                cT1.getHandler().sendMessage(message);
            }
        };

    }

    public static class MyThread extends Thread{
        Looper myLooper;
        Handler myHandler;
        Handler.Callback myCallBack;
        public MyThread(Handler.Callback callback) {
            Looper.prepare();//为当前线程创建Looper，当前Loopper.MessageQueue
            //Message.target = handler
            myLooper = Looper.myLooper();
            myHandler = new Handler(proxyCallback);
            this.myCallBack = callback;
            Looper.loop();//开启循环

        }

        /**
         * 代理callBack
         */
        Handler.Callback proxyCallback = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log.e(TAG,"" + Thread.currentThread().toString());
                return myCallBack.handleMessage(msg);
            }
        };

        public Handler getHandler(){
            return myHandler;
        }
    }
}
