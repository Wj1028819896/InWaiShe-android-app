package com.inwaishe.app.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.inwaishe.app.entity.mainpage.MainPageInfo;

/**
 * Created by Administrator on 2017/8/16 0016.
 */

public class VedioPagerViewModel extends AndroidViewModel {

    /**LiveData 观察对象**/
    private MutableLiveData<MainPageInfo> mainPageInfoLiveData = new MutableLiveData<>();
    public LiveData<MainPageInfo> getMainPageInfoLiveData(){
        return mainPageInfoLiveData;
    }

    public VedioPagerViewModel(Application application) {
        super(application);
    }

    /**
     * 初始化
     * init 必须在 observe 之前
     */
    public void init(){
        mainPageInfoLiveData.setValue(new MainPageInfo());
    }

    public void loadData(){
        //爬虫抓数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MainPageInfo mainPageInfo = mainPageInfoLiveData.getValue();
                    mainPageInfo.pullVedioDataJsoup();
                    mainPageInfoLiveData.postValue(mainPageInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}