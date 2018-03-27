package com.inwaishe.app.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.inwaishe.app.dataprovider.DataProvider;
import com.inwaishe.app.entity.mainpage.MainPageInfo;
import com.inwaishe.app.http.ThreadPollUtil;

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

    public void loadData(final boolean isRefresh){
        //爬虫抓数据
        Thread exe = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MainPageInfo mainPageInfo = mainPageInfoLiveData.getValue();
                    if(isRefresh){
                        mainPageInfo.pageNum = 1;
                    }
                    new DataProvider().updataVedioPageInfo(mainPageInfo);
                    mainPageInfoLiveData.postValue(mainPageInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                    MainPageInfo mainPageInfo = mainPageInfoLiveData.getValue();
                    mainPageInfo.code = -1;
                    mainPageInfo.msg = "未知错误：" + e.getMessage();
                    mainPageInfoLiveData.postValue(mainPageInfo);
                }
            }
        });
        ThreadPollUtil.getInstance().exeCute(exe);
    }
}
