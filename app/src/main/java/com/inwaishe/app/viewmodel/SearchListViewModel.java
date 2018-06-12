package com.inwaishe.app.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.inwaishe.app.dataprovider.DataProvider;
import com.inwaishe.app.entity.SearchResBackInfo;

/**
 * Created by WangJing on 2018/6/11.
 */

public class SearchListViewModel extends AndroidViewModel {
    private MutableLiveData<SearchResBackInfo> resBackInfoMutableLiveData = new MutableLiveData<>();
    public SearchListViewModel(Application application) {
        super(application);
        resBackInfoMutableLiveData.setValue(new SearchResBackInfo());
    }

    public MutableLiveData<SearchResBackInfo> getResBackInfoMutableLiveData(){
        return resBackInfoMutableLiveData;
    }

    public void loadData(final boolean isRefresh, final String searchKey){
        Log.e("SearchListViewModel","loadData");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SearchResBackInfo searchResBackInfo = resBackInfoMutableLiveData.getValue();
                    if (isRefresh) {
                        searchResBackInfo.pageNum = 1;
                    }
                    new DataProvider().getSearchResInfo(searchResBackInfo, searchKey);
                    resBackInfoMutableLiveData.postValue(searchResBackInfo);
                }catch (Exception e){
                    e.printStackTrace();
                    SearchResBackInfo searchResBackInfo = resBackInfoMutableLiveData.getValue();
                    searchResBackInfo.code = -1;
                    searchResBackInfo.msg = "未知错误：" + e.getMessage();
                    resBackInfoMutableLiveData.postValue(searchResBackInfo);
                }

            }
        }).start();

    }
}
