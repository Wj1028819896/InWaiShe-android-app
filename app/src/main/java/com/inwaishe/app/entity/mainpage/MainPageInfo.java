package com.inwaishe.app.entity.mainpage;

import com.inwaishe.app.dbroom.NewsTypes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import static com.inwaishe.app.dbroom.NewsTypes.*;

/**
 * Created by WangJing on 2017/8/9.
 * 主页信息
 */

public class MainPageInfo implements Serializable{

    public ArrayList<BannerInfo> bannerInfos = new ArrayList<>();//轮播图信息

    public ArrayList<ShareInfo> shareInfos = new ArrayList<>();//分享|福利

    public String keyBoardShortcutSrc = "";//键盘快捷入口

    public String mouseShortcutSrc = "";//鼠标快捷入口

    public String perimeterShortcutSrc = "";//周边快捷入口

    public String audioShortcutSrc = "";//音频快捷入口

    public ArrayList<Articlelnfo> articleInfos = new ArrayList<>();//列表


    /**
     * 抓取数据视频区列表
     * @return
     */
    public MainPageInfo pullVedioDataJsoup() throws Exception {

        articleInfos.clear();

        String url = "http://www.inwaishe.com/portal.php?mod=list&catid=" + 3;
        Document doc = Jsoup.connect(url).get();

        /*大*/
        Element vmain = doc.select("div.vmain").first();
        Element vlarge = vmain.select("div.vlarge").first();
        Element vsmall = vmain.select("div.vsmall").first();
        Element vtitbar = vmain.select("div.vtitbar").first();
        String imgurl_la =  "http://www.inwaishe.com/" + vlarge.select("img").first().attr("src");
        String src_la = vlarge.select("a").first().attr("href");
        String title_la = vtitbar.select("h2").first().text();
        String playtimes_la = vtitbar.select("span.vplaycount").first().text();
        String vmsgcount_la = vtitbar.select("span.vmsgcount").first().text();
        Articlelnfo info_la = new Articlelnfo(title_la, "", "", imgurl_la, ""
                , src_la, "视频", "", Integer.parseInt(playtimes_la)
                , Integer.parseInt(vmsgcount_la));
        articleInfos.add(info_la);

        /*小*/
        Elements lis = vsmall.select("li");
        for(Element li : lis){
            String playtimes_sm = li.select("span.vplaycount").first().text();
            String imgurl_sm = "http://www.inwaishe.com/" + li.select("img").first().attr("src");
            String src_sm = li.select("a").first().attr("href");
            String title_sm = li.select("a").first().text();

            Articlelnfo info_sm = new Articlelnfo(title_sm, "", "", imgurl_sm, ""
                    , src_sm, "视频", "", Integer.parseInt(playtimes_sm)
                    , 0);
            articleInfos.add(info_sm);
        }
        /*列表*/

        Elements items = doc.select("div.videolview.cl");
        for(Element item : items){
            String href = item.select("a").first().attr("href");
            String imgurl = "http://www.inwaishe.com/" + item.select("img").first().attr("src");
            String title = item.select("img").first().attr("alt");
            Element vinfo = item.select("div.vinfo").first();
            String usrName =  vinfo.select("a.vnickname").first().text();
            String vmsgcount_n = vinfo.select("span.vmsgcount").first().text();
            String vplaycount_n = vinfo.select("span.vplaycount").first().text();
            Articlelnfo info_n = new Articlelnfo(title, usrName, "", imgurl, ""
                    , href, "视频", "", Integer.parseInt(vplaycount_n)
                    , Integer.parseInt(vmsgcount_n));
            articleInfos.add(info_n);
        }

        return  this;
    }

    /**
     * 抓取数据 资讯区列表
     * @param type 分类
     * @return
     */
    public MainPageInfo pullDataJsoupByType(NewsTypes type) throws Exception {
        int catid = 0;
        switch (type){
            case 手柄:
                catid = 25;
                break;
            case 数码:
                catid = 4;
                break;
            case 活动:
                catid = 28;
                break;
            case 游戏:
                catid = 5;
                break;
            case 键帽:
                catid = 26;
                break;
            case 键盘:
                catid = 23;
                break;
            case 音频:
                catid = 24;
                break;
            case 鼠垫:
                catid = 29;
                break;
            case 鼠标:
                catid = 22;
                break;
            case 周边:
                catid = 27;
                break;
        }
        articleInfos.clear();
        String url = "http://www.inwaishe.com/portal.php?mod=list&catid=" + catid;
        Document doc = Jsoup.connect(url).get();
        Element contentinner = doc.select("div.contentinner").first();
        //<div class="cmnlview cl">
        Elements items = contentinner.select("div.cmnlview.cl");
        for(Element item: items){

            String title = item.select("a").get(0).text();
            String src = item.select("a").get(0).attr("href");
            String author = item.select("a").get(3).text();
            String iconurl = "http://www.inwaishe.com/" + item.select("img").get(0).attr("src");
            String bgurl = "";
            try {
                bgurl = "http://www.inwaishe.com/" + item.select("img").get(1).attr("src");
            }catch (Exception e) {
                bgurl = "";
            }

            String desc = item.select("p").first().text();
            String date = item.select("div.arctime").first().text();
            articleInfos.add(new Articlelnfo(title,author,date,bgurl,desc,src,type.toString()));
        }
        return this;
    }


    /***
     * 通过JSOUP 爬取Html数据 主页各种数据
     * @return
     * @throws IOException
     */
    public MainPageInfo pullDataJsoup() throws Exception {
        Document doc = Jsoup.connect("http://www.inwaishe.com/portal.php").get();
        Element div = doc.select("div.bd").first();

        /*爬取Banner*/
        bannerInfos.clear();
        Elements lis = div.select("li");
        for(Element li : lis){
            String src = li.select("a").first().attr("href");
            String imgUrl =  "http://www.inwaishe.com/" + li.select("img").first().attr("src");
            bannerInfos.add(new BannerInfo(imgUrl,"",src));

        }

        /*爬取分享|福利*/
        div = doc.select("div.cmsoftrollc").first();
        lis = div.select("li");
        shareInfos.clear();
        for(Element li : lis){
            String imgUrl = "http://www.inwaishe.com/" + li.select("img").first().attr("src");
            String title = li.select("h3").first().text();
            String src = li.select("a").first().attr("href");
            shareInfos.add(new ShareInfo(imgUrl,title,src));
        }

        /*爬取列表*/
        div = doc.select("div.cmsoftarclist").first();
        lis = div.select("li");
        articleInfos.clear();
        for(Element li : lis){
            Element a = li.select("h2").first().select("a").first();
            String href = a.attr("href");
            String title = a.text();

            Element a2 = li.select("h2").first().select("a").get(1);
            String type = a2.text();

            Element arcstatus = li.select("div.arcstatus").first();
            Element arcavt = arcstatus.select("div.arcavt").first();

            String author = arcstatus.select("a").get(1).text();
            String num = arcstatus.select("a").get(2).text();

            String date = arcstatus.select("div.arctime").first().text();

            Element shadow = li.select("div.shadow").first();

            String img = "http://www.inwaishe.com/" + shadow.select("img").first().attr("src");

            String desc = shadow.select("p").first().text();
            articleInfos.add(new Articlelnfo(title,author,date,img,desc,href,type));
        }
        return this;
    }
}
