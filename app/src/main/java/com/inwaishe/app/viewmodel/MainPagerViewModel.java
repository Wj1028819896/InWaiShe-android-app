package com.inwaishe.app.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.inwaishe.app.dataprovider.DataProvider;
import com.inwaishe.app.entity.mainpage.MainPageInfo;
import com.inwaishe.app.http.ThreadPollUtil;

/**
 * Created by WangJing on 2017/8/9.
 *  ViewModel
 * android LifeCycle LiveData
 */

public class MainPagerViewModel extends AndroidViewModel {
    /**LiveData 观察对象**/
    private MutableLiveData<MainPageInfo> mainPageInfoLiveData = new MutableLiveData<>();
    public MainPagerViewModel(Application application) {
        super(application);
    }

    public LiveData<MainPageInfo> getMainPageInfoLiveData(){
        return mainPageInfoLiveData;
    }

    /**
     * 初始化
     * init 必须在 observe 之前
     */
    public void init(){
        mainPageInfoLiveData.setValue(new MainPageInfo());
    }
    /**
     *  增量加载
     */
    public void loadDataMore(){
        //爬虫抓数据
        Thread exe = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MainPageInfo mainPageInfo = mainPageInfoLiveData.getValue();
                    mainPageInfo.isRefreshBack = false;
                    new DataProvider().upDataMainPageInfo(mainPageInfo);
                    mainPageInfoLiveData.postValue(mainPageInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                    MainPageInfo mainPageInfo = mainPageInfoLiveData.getValue();
                    mainPageInfo.code = -1;
                    mainPageInfo.isRefreshBack = false;
                    mainPageInfo.msg = "未知错误";
                    mainPageInfoLiveData.postValue(mainPageInfo);
                }
            }
        });
        ThreadPollUtil.getInstance().exeCute(exe);

    }

    /**
     * 刷新加载
     */
    public void loadData(){
        //爬虫抓数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MainPageInfo mainPageInfo = mainPageInfoLiveData.getValue();
                    mainPageInfo.pageNum = 1;
                    mainPageInfo.isRefreshBack = true;
                    new DataProvider().upDataMainPageInfo(mainPageInfo);
                    mainPageInfoLiveData.postValue(mainPageInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                    MainPageInfo mainPageInfo = mainPageInfoLiveData.getValue();
                    mainPageInfo.code = -1;
                    mainPageInfo.msg = "未知错误";
                    mainPageInfo.isRefreshBack = true;
                    mainPageInfoLiveData.postValue(mainPageInfo);
                }
            }
        }).start();
    }
}
