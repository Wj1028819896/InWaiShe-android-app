package com.inwaishe.app.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.inwaishe.app.dataprovider.DataProvider;
import com.inwaishe.app.entity.mainpage.ArcCommInfo;
import com.inwaishe.app.entity.mainpage.Articlelnfo;
import com.inwaishe.app.entity.mainpage.baseResponse;
import com.inwaishe.app.framework.arch.bus.XBus;
import com.inwaishe.app.framework.arch.bus.XBusObserver;
import com.inwaishe.app.framework.arch.bus.XBusThreadModel;
import com.inwaishe.app.http.OkCallback;
import com.inwaishe.app.http.OkHttpUtils;
import com.inwaishe.app.ui.MyApplication;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by WangJing on 2017/10/27.
 */

public class CommDetailViewModel extends AndroidViewModel {
    public CommDetailViewModel(Application application) {
        super(application);
    }

    MutableLiveData<ArcCommInfo> arcCommInfoMutableLiveData = new MutableLiveData<>();

    public void init(ArcCommInfo arcCommInfo) {
        if(arcCommInfoMutableLiveData.getValue() == null){
            arcCommInfoMutableLiveData.setValue(arcCommInfo);
        }
    }

    public MutableLiveData<ArcCommInfo> getArcCommInfoMutableLiveData() {
        return arcCommInfoMutableLiveData;
    }

    public void loadPageDataForArcComm(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArcCommInfo info = arcCommInfoMutableLiveData.getValue();
                    new DataProvider().getSingalArcCommInfo(arcCommInfoMutableLiveData.getValue().usrCommPage,info);
                    //Collections.reverse(info.arcReplyCommInfos);
                    arcCommInfoMutableLiveData.postValue(info);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 回复楼主 或 子评论
     * @param owner 生命周期Context
     * @param observer 观察者
     */
    public void replyToHost(final String action,final String msg,LifecycleOwner owner, XBusObserver observer){
        XBus.getInstance().observe(owner,"replyToHost",observer).runOn(XBusThreadModel.mainThread());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String,String> map = new DataProvider().getReplyFormInfo(action);
                    String actionR = map.get("action");
                    map.remove("action");
                    map.put("message","" + msg);
                    OkHttpUtils.make(MyApplication.con).formRequest(actionR,true,map,
                            new OkCallback(){
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    super.onFailure(call, e);
                                    baseResponse baseResponse = new baseResponse();
                                    baseResponse.code = -1;
                                    baseResponse.msg = e.getMessage();
                                    baseResponse.html = "";
                                    XBus.getInstance().post("replyToHost",baseResponse);
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    super.onResponse(call, response);
                                    baseResponse baseResponse = new baseResponse();
                                    if(response.isSuccessful()){
                                        baseResponse.code = 1;
                                        baseResponse.msg = "请求成功！";
                                        baseResponse.html = response.body().string();
                                    }else{
                                        baseResponse.code = -1;
                                        baseResponse.msg = "请求失败！"+ response.code();
                                        baseResponse.html = "";
                                    }
                                    XBus.getInstance().post("replyToHost",baseResponse);
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                    baseResponse baseResponse = new baseResponse();
                    baseResponse.code = -1;
                    baseResponse.msg = e.getMessage();
                    baseResponse.html = "";
                    XBus.getInstance().post("replyToHost",baseResponse);
                }
            }
        }).start();
    }
}
