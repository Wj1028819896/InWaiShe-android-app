package com.inwaishe.app.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.inwaishe.app.entity.mainpage.BannerInfo;
import com.inwaishe.app.entity.mainpage.MainPageInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

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

    public void loadData(){
        //爬虫抓数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MainPageInfo mainPageInfo = mainPageInfoLiveData.getValue();
                    mainPageInfo.pullDataJsoup();
                    mainPageInfoLiveData.postValue(mainPageInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
