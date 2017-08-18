package com.inwaishe.app.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.inwaishe.app.dbroom.LruCacheManager;
import com.inwaishe.app.entity.mainpage.ArcCommInfo;
import com.inwaishe.app.entity.mainpage.Articlelnfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

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

    ;

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
                    Document doc = Jsoup.connect(articlelnfoMutableLiveData.getValue().artSrc).get();
                    Elements els = doc.select("div.cmsoftcomment");
                    arccommMutableLiveData.getValue().clear();
                    for (int i = 0; i < els.size(); i++) {
                        Element e = els.get(i);

                        String usrName = e.select("a").get(3).text();
                        String usrImgUrl = e.select("img").first().attr("src");
                        String usrDate = e.select("span").get(1).text();
                        String usrComm = e.select("dd").first().text();
                        ArcCommInfo arcCommInfo = new ArcCommInfo(usrName,usrImgUrl,usrDate,usrComm);
                        arccommMutableLiveData.getValue().add(arcCommInfo);
                    }
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

                    Document doc = Jsoup.connect(articlelnfoMutableLiveData.getValue().artSrc).get();

                    Element tableElement = doc.select("table").get(1);

                    String table = tableElement.html();

                    String Html = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                            "<html xmlns:wb=\"http://open.weibo.com/wb\">\n" +
                            "<head>\n</head>"
                            +"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
                            + "<body>"
                            + "<table>"
                            + table
                            + "</table>"
                            + "</body></html>";


                    if("视频".equals(articlelnfoMutableLiveData.getValue().arcType)){
                        if(tableElement.select("embed").size() > 0){
                            //Bilibili 视频号
                            String flashvars = tableElement.select("embed").first().attr("flashvars");
                            int end = flashvars.indexOf("&");
                            String avid = flashvars.substring(4,end);
                            articlelnfoMutableLiveData.getValue().avid = avid;
                        }else{
                            //优酷
                        }
                    }
                    Document load = Jsoup.parse(Html);
                    Elements elements = load.select("img[src]");
                    for (Element el : elements) {
                        String imgUrl = "http://www.inwaishe.com/" + el.attr("src");
                        el.attr("src", imgUrl);
                        el.attr("width", "100%");
                        el.removeAttr("onclick");
                        el.removeAttr("class");
                        el.removeAttr("zoomfile");
                        el.removeAttr("id");
                        el.removeAttr("aid");
                        el.removeAttr("w");
                    }

                    Document old = LruCacheManager.getInstance().getHtmlDocment(articlelnfoMutableLiveData.getValue().artSrc);
                    if (old == null) {
                        LruCacheManager.getInstance().putHtmlDocment(articlelnfoMutableLiveData.getValue().artSrc, load);
                    } else {
                        if (!old.html().equals(load.html())) {
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
