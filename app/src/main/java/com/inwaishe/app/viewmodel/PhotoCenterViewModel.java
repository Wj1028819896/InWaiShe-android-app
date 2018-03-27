package com.inwaishe.app.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;

import com.inwaishe.app.dataprovider.DataProvider;
import com.inwaishe.app.entity.photocenter.PhotoCenterBackInfo;

/**
 * Created by WangJing on 2018/2/26.
 */

public class PhotoCenterViewModel extends AndroidViewModel {

    private MutableLiveData<PhotoCenterBackInfo> photoCenterBackInfoMutableLiveData = new MutableLiveData<>();

    public PhotoCenterViewModel(Application application) {
        super(application);
        photoCenterBackInfoMutableLiveData.setValue(new PhotoCenterBackInfo());
    }

    public MutableLiveData<PhotoCenterBackInfo> getPhotoCenterBackInfoMutableLiveData(){
        return photoCenterBackInfoMutableLiveData;
    }

    /**
     * 请求数据
     * @param isRefresh 是否刷新加载
     */
    public void loadData(final boolean isRefresh){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PhotoCenterBackInfo photoCenterBackInfo = photoCenterBackInfoMutableLiveData.getValue();
                    if(isRefresh){
                        photoCenterBackInfo.pageNum = 1;
                    }
                    new DataProvider().getPhotoCenterInfo(photoCenterBackInfo);
                    photoCenterBackInfoMutableLiveData.postValue(photoCenterBackInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                    PhotoCenterBackInfo photoCenterBackInfo = photoCenterBackInfoMutableLiveData.getValue();
                    photoCenterBackInfo.code = -1;
                    photoCenterBackInfo.msg = "未知错误：" + e.getMessage();
                    photoCenterBackInfoMutableLiveData.postValue(photoCenterBackInfo);
                }
            }
        }).start();

    }


}
