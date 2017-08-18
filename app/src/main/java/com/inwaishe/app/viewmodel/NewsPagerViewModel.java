package com.inwaishe.app.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.inwaishe.app.dbroom.NewsTypes;
import com.inwaishe.app.entity.mainpage.MainPageInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by WangJing on 2017/8/9.
 * NewsPager ViewModel
 * android LifeCycle LiveData
 */

public class NewsPagerViewModel extends AndroidViewModel {
    /**LiveData 观察对象**/
    private MutableLiveData<HashMap<NewsTypes,MutableLiveData<MainPageInfo>>> newsListsPagerLiveData = new MutableLiveData<>();

    public NewsPagerViewModel(Application application) {
        super(application);
    }

    public boolean init  = false;

    public MutableLiveData<HashMap<NewsTypes,MutableLiveData<MainPageInfo>>> getNewsPageInfoLiveData(){
        return newsListsPagerLiveData;
    }

    /**
     * 初始化
     * init 必须在 observe 之前
     */
    public void init(){
        if(init){
          return;
        }
        HashMap<NewsTypes,MutableLiveData<MainPageInfo>> hash = new HashMap<NewsTypes,MutableLiveData<MainPageInfo>>();

        hash.put(NewsTypes.周边,new MutableLiveData<MainPageInfo>());
        hash.put(NewsTypes.手柄,new MutableLiveData<MainPageInfo>());
        hash.put(NewsTypes.数码,new MutableLiveData<MainPageInfo>());
        hash.put(NewsTypes.活动,new MutableLiveData<MainPageInfo>());
        hash.put(NewsTypes.游戏,new MutableLiveData<MainPageInfo>());
        hash.put(NewsTypes.键帽,new MutableLiveData<MainPageInfo>());
        hash.put(NewsTypes.键盘,new MutableLiveData<MainPageInfo>());
        hash.put(NewsTypes.音频,new MutableLiveData<MainPageInfo>());
        hash.put(NewsTypes.鼠垫,new MutableLiveData<MainPageInfo>());
        hash.put(NewsTypes.鼠标,new MutableLiveData<MainPageInfo>());

        hash.get(NewsTypes.周边).setValue(new MainPageInfo());
        hash.get(NewsTypes.手柄).setValue(new MainPageInfo());
        hash.get(NewsTypes.数码).setValue(new MainPageInfo());
        hash.get(NewsTypes.活动).setValue(new MainPageInfo());
        hash.get(NewsTypes.游戏).setValue(new MainPageInfo());
        hash.get(NewsTypes.键帽).setValue(new MainPageInfo());
        hash.get(NewsTypes.键盘).setValue(new MainPageInfo());
        hash.get(NewsTypes.音频).setValue(new MainPageInfo());
        hash.get(NewsTypes.鼠垫).setValue(new MainPageInfo());
        hash.get(NewsTypes.鼠标).setValue(new MainPageInfo());
        newsListsPagerLiveData.setValue(hash);
        init = true;
    }

    public void loadData(final NewsTypes type){
        //爬虫抓数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    MainPageInfo mainPageInfo = newsListsPagerLiveData.getValue().get(type).getValue();
                    mainPageInfo.pullDataJsoupByType(type);
                    newsListsPagerLiveData.getValue().get(type).postValue(mainPageInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
