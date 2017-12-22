package com.inwaishe.app.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.inwaishe.app.dataprovider.DataProvider;
import com.inwaishe.app.dbroom.LruCacheManager;
import com.inwaishe.app.entity.mainpage.ArcCommInfo;
import com.inwaishe.app.entity.mainpage.Articlelnfo;
import com.inwaishe.app.http.OkCookieJar;
import com.inwaishe.app.http.OkHttpUtils;
import com.inwaishe.app.ui.MyApplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2017/8/12 0012.
 * 文章详情ViewModel
 */

public class ArcDetailViewModel extends AndroidViewModel {


    MutableLiveData<Articlelnfo> articlelnfoMutableLiveData = new MutableLiveData<>();
    MutableLiveData<Document> documentMutableLiveData = new MutableLiveData<>();
    MutableLiveData<ArrayList<ArcCommInfo>> arccommMutableLiveData = new MutableLiveData<>();

    public ArcDetailViewModel(Application application) {
        super(application);
    }

    public void init(Articlelnfo articlelnfo) {
        articlelnfoMutableLiveData.setValue(articlelnfo);
        Document document = LruCacheManager.getInstance().getHtmlDocment(articlelnfo.artSrc);
        if (document == null) {
            document = new Document("");
            LruCacheManager.getInstance().putHtmlDocment(articlelnfo.artSrc, document);
        }
        documentMutableLiveData.setValue(document);

        if (arccommMutableLiveData.getValue() == null) {
            arccommMutableLiveData.setValue(new ArrayList<ArcCommInfo>());
        }
    }

    public MutableLiveData<Document> getDocumentMutableLiveData() {
        return documentMutableLiveData;
    }

    public MutableLiveData<Articlelnfo> getArticlelnfoMutableLiveData() {
        return articlelnfoMutableLiveData;
    }

    public MutableLiveData<ArrayList<ArcCommInfo>> getArccommMutableLiveData() {
        return arccommMutableLiveData;
    }

    public void loadDataForArcCommFragment() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    arccommMutableLiveData.getValue().clear();
                    int page = 1;
                    int totoalSize = articlelnfoMutableLiveData.getValue().usrCommNum;
                    int getSize = 0;
                    while(getSize < totoalSize){
                        int size = articlelnfoMutableLiveData.getValue().commSrc.length();
                        String src = articlelnfoMutableLiveData.getValue().commSrc.substring(0,size - 8) + page + "-1.html";
                        ArrayList<ArcCommInfo> commInfos = new DataProvider().getArcCommInfosByLT(src);
                        arccommMutableLiveData.getValue().addAll(commInfos);
                        getSize += commInfos.size();
                        page++;
                    }
                    Collections.reverse(arccommMutableLiveData.getValue());//时间倒叙
                    arccommMutableLiveData.postValue(arccommMutableLiveData.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /***
     * 抓取文章详情页的Html
     * 进行组装
     */
    public void loadDataForArcDetailFragment() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String html = new DataProvider().getArtDetailHtmlString(articlelnfoMutableLiveData.getValue().artSrc,articlelnfoMutableLiveData.getValue());
                    Document load = Jsoup.parse(html);
                    Document old = LruCacheManager.getInstance().getHtmlDocment(articlelnfoMutableLiveData.getValue().artSrc);
                    if (old == null) {
                        LruCacheManager.getInstance().putHtmlDocment(articlelnfoMutableLiveData.getValue().artSrc, load);
                    } else {
                        if (!old.html().equals(html)) {
                            LruCacheManager.getInstance().putHtmlDocment(articlelnfoMutableLiveData.getValue().artSrc, load);
                            documentMutableLiveData.postValue(load);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
