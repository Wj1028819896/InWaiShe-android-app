package com.inwaishe.app.dataprovider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.inwaishe.app.common.CommonData;
import com.inwaishe.app.dbroom.NewsTypes;
import com.inwaishe.app.dbroom.SharePreferencesStore;
import com.inwaishe.app.entity.mainpage.ArcCommInfo;
import com.inwaishe.app.entity.mainpage.ArcReplyCommInfo;
import com.inwaishe.app.entity.mainpage.Articlelnfo;
import com.inwaishe.app.entity.mainpage.BannerInfo;
import com.inwaishe.app.entity.mainpage.MainPageInfo;
import com.inwaishe.app.entity.mainpage.ShareInfo;
import com.inwaishe.app.entity.mainpage.UserInfo;
import com.inwaishe.app.entity.photocenter.AlbumInfo;
import com.inwaishe.app.entity.photocenter.PhotoCenterBackInfo;
import com.inwaishe.app.http.OkCookieJar;
import com.inwaishe.app.http.OkHttpUtils;
import com.inwaishe.app.http.PreferencesCookieStore;
import com.inwaishe.app.ui.LoginActivity;
import com.inwaishe.app.ui.MyApplication;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.OkHttpClient;

/**
 * Created by WangJing on 2017/10/19.
 */

public class DataProvider {


    public DataProvider(){

    }
    /**
     * 通过URL 获得文章的评论列表
     * @param artSrc 文章地址
     * @return 评论列表
     */
    public ArrayList<ArcCommInfo> getArcCommInfos(String artSrc) throws Exception{
        checkMainThread();
        Document doc = Jsoup.connect(artSrc).get();
        ArrayList<ArcCommInfo> list = new ArrayList<>();
        Elements els = doc.select("div.cmsoftcomment");
        for (int i = 0; i < els.size(); i++) {
            Element e = els.get(i);
            String usrName = e.select("a").get(3).text();
            String usrImgUrl = e.select("img").first().attr("src");
            String usrDate = e.select("span").get(1).text();
            String usrComm = e.select("dd").first().text();
            ArcCommInfo arcCommInfo = new ArcCommInfo(usrName,usrImgUrl,usrDate,usrComm);
            list.add(arcCommInfo);
        }
        return list;
    }

    public ArrayList<ArcCommInfo> getArcCommInfosByLT(String src) throws Exception {
        checkMainThread();
        Document doc = Jsoup.connect(src)
                .header("Cookie",getLoginCookies(MyApplication.con))
                .get();
        ArrayList<ArcCommInfo> list = new ArrayList<>();
        Elements tables = doc.select("table[class=plhin]");
        int index = src.contains("-1-1.html")?1:0;
        for(int i = index;i < tables.size();i++){
            ArcCommInfo commInfo = new ArcCommInfo();
            Element table = tables.get(i);
            commInfo.userName = table.select("div[class=authi]").first().text();
            commInfo.userIconUrl = table.select("div[class=avatar]").first().select("img").first().attr("src");
            commInfo.userDate = table.select("div[class=authi]").get(1).select("em").text();
            commInfo.userCommon = table.select("div[class=t_fsz]").first().select("table").first().text();
            commInfo.usrCommPage = table.select("div[class=authi]").get(1).select("a").first().attr("href");

            Elements blockquote = table.select("div[class=t_fsz]").first().select("table").first().select("blockquote");
            if(blockquote != null && blockquote.size() > 0){
                commInfo.usrQuoteComm = blockquote.first().text();
                commInfo.userCommon = commInfo.userCommon.replace(commInfo.usrQuoteComm,"");
            }

            Element reply = table.select("div[class=pob cl]").first();
            commInfo.replyAction = "http://www.inwaishe.com/" + reply.select("a[class=fastre]").first().attr("href");
            commInfo.replyAction = StringEscapeUtils.unescapeHtml(commInfo.replyAction) + "&infloat=yes&handlekey=reply&inajax=1&ajaxtarget=fwin_content_reply";

            Elements replys = table.select("div[class=pstl xs1 cl]");
            if(replys!=null){
                for(int p=0;p<replys.size();p++){
                    ArcReplyCommInfo replyCommInfo = new ArcReplyCommInfo();
                    Element re = replys.get(p);
                    Element reUsr = re.select("div[class=psta vm]").first();
                    replyCommInfo.userName = reUsr.select("a").get(1).text();
                    replyCommInfo.userIconUrl = reUsr.select("img").first().attr("src");
                    Element reComm = re.select("div[class=psti]").first();
                    replyCommInfo.userDate = reComm.select("span").first().text();
                    replyCommInfo.replyAction = StringEscapeUtils.unescapeHtml(reComm.select("a").get(1).attr("href")) + "&infloat=yes&handlekey=reply&inajax=1&ajaxtarget=fwin_content_reply";
                    replyCommInfo.userCommon = "@^_^@ "+ reComm.textNodes().get(0).text();
                    commInfo.arcReplyCommInfos.add(replyCommInfo);
                }
            }
            list.add(commInfo);
        }
        return list;
    }

    /**
     * 获取评楼楼主及其子评论
     * @param pageUrl
     * @return
     */
    public ArcCommInfo getSingalArcCommInfo(String pageUrl,ArcCommInfo commInfo) throws Exception {
        checkMainThread();
        Document doc = Jsoup.connect(pageUrl)
                .header("Cookie",getLoginCookies(MyApplication.con))
                .get();
        Element table = doc.select("table[class=plhin]").last();
        commInfo.userName = table.select("div[class=authi]").first().text();
        commInfo.userIconUrl = table.select("div[class=avatar]").first().select("img").first().attr("src");
        commInfo.userDate = table.select("div[class=authi]").get(1).select("em").text();

        //commInfo.usrCommPage = table.select("div[class=authi]").get(1).select("a").first().attr("href");
        commInfo.userCommon = table.select("div[class=t_fsz]").first().select("table").first().text();
        Elements blockquote = table.select("div[class=t_fsz]").first().select("table").first().select("blockquote");
        if(blockquote != null && blockquote.size() > 0){
            commInfo.usrQuoteComm = blockquote.first().text();
            commInfo.userCommon = commInfo.userCommon.replace(commInfo.usrQuoteComm,"");
        }

        Element reply = table.select("div[class=pob cl]").first();
        commInfo.replyAction = "http://www.inwaishe.com/" + reply.select("a[class=fastre]").first().attr("href");
        commInfo.replyAction = StringEscapeUtils.unescapeHtml(commInfo.replyAction) + "&infloat=yes&handlekey=reply&inajax=1&ajaxtarget=fwin_content_reply";

        Elements replys = table.select("div[class=pstl xs1 cl]");
        if(replys!=null){
            commInfo.arcReplyCommInfos.clear();
            for(int p=0;p<replys.size();p++){
                ArcReplyCommInfo replyCommInfo = new ArcReplyCommInfo();
                Element re = replys.get(p);
                Element reUsr = re.select("div[class=psta vm]").first();
                replyCommInfo.userName = reUsr.select("a").get(1).text();
                replyCommInfo.userIconUrl = reUsr.select("img").first().attr("src");
                Element reComm = re.select("div[class=psti]").first();
                replyCommInfo.userDate = reComm.select("span").first().text();
                replyCommInfo.replyAction = StringEscapeUtils.unescapeHtml(reComm.select("a").get(1).attr("href")) + "&infloat=yes&handlekey=reply&inajax=1&ajaxtarget=fwin_content_reply";
                replyCommInfo.userCommon = reComm.textNodes().get(0).text();
                commInfo.arcReplyCommInfos.add(replyCommInfo);
            }
        }
        return commInfo;
    }

    /**
     * 检查主线程抛出异常
     * @throws Exception
     */
    private void checkMainThread() throws Exception{
        if(Looper.myLooper() == Looper.getMainLooper()){
            throw new Exception("Must not be run on Main Thread");
        }
    }
    /**
     * 获取登录Cookies
     * @param context 上下文
     * @return
     */
    public static String getLoginCookies(Context context){
        OkHttpClient ok = OkHttpUtils.make(context).okHttpClient;
        StringBuilder cookieStr = new StringBuilder("");
        List<Cookie> cookies = ((OkCookieJar)ok.cookieJar()).getLoginCookies("www.inwaishe.com");
        for(Cookie cookie : cookies){
            cookieStr.append(cookie.name()).append("=").append(cookie.value()+";");
        }
        System.out.println(cookieStr.toString());
        return cookieStr.toString();
    }


    public void getPhotoCenterInfo(PhotoCenterBackInfo photoCenterBackInfo) throws Exception {
        checkMainThread();
        Document doc = Jsoup.connect("http://www.inwaishe.com/forum.php?" +
                "mod=forumdisplay" +
                "&fid=53" +
                "&page=" + photoCenterBackInfo.pageNum +
                "&filter=lastpost" +
                "&orderby=lastpost")
                .header("Cookie",getLoginCookies(MyApplication.con))
                .get();

        Element ul = doc.select("ul[id=waterfall]").first();
        Elements lis = ul.select("li");
        if(photoCenterBackInfo.pageNum == 1){
            photoCenterBackInfo.abList.clear();
        }
        if(lis.size() < 20){
            photoCenterBackInfo.isLoadAll = true;
            return;
        }
        for(int p = 0; p < lis.size(); p++){
            AlbumInfo albumInfo = new AlbumInfo();
            Element li = lis.get(p);
            Element div01 = li.select("div[class=c cl]").first();
            Element a = div01.select("a").first();
            Element div02 = li.select("div[class=auth cl]").first();

            albumInfo.title = li.select("h3[class=xw0]").first().select("a").first().text();
            albumInfo.artSrc = a.attr("href");
            albumInfo.coverImgSrc = "http://www.inwaishe.com/" + a.select("img").first().attr("src");
            albumInfo.commentsNum = Integer.valueOf(div02.select("a").first().text());
            albumInfo.author = div02.select("a").get(1).text();
            photoCenterBackInfo.abList.add(albumInfo);
        }
        photoCenterBackInfo.pageNum++;
    }

    /**
     * 获取文章内容html
     * @param artSrc 地址
     * @return
     */
    public String getWellShareArtDetailHtmlString(String artSrc,Articlelnfo articlelnfo) throws Exception{

        checkMainThread();
        Document doc = Jsoup.connect(artSrc)
                .header("Cookie",getLoginCookies(MyApplication.con))
                .get();

        Element fastpostform = doc.select("form[id=fastpostform]").first();
        String action = fastpostform.attr("action");
        String posttime = "" + System.currentTimeMillis();
        if(fastpostform.select("input[name=posttime]").size() > 0){
            posttime = fastpostform.select("input[name=posttime]").first().attr("value");
        }
        String formhash = fastpostform.select("input[name=formhash]").first().attr("value");
        String usesig = fastpostform.select("input[name=usesig]").first().attr("value");
        String subject = fastpostform.select("input[name=subject]").first().attr("value");

        articlelnfo.replyFrom.action = "http://www.inwaishe.com/" + action;
        articlelnfo.replyFrom.posttime = posttime;
        articlelnfo.replyFrom.formhash = formhash;
        articlelnfo.replyFrom.usesig = usesig;
        articlelnfo.replyFrom.subject = subject;

        Element ta = doc.select("div[id=postlist]").first().select("table").first();
        Element td = ta.select("td").first();
        articlelnfo.usrCommNum = Integer.valueOf(td.select("span").get(4).text());
        articlelnfo.artReadTimes = Integer.valueOf(td.select("span").get(1).text());

        /*****************************/
        Element tb_detail = doc.select("table[class=plhin]").first();
        Element tb_tr = tb_detail.select("tr").first();
        Element tb_tr_td = tb_tr.select("td[class=plc]").first().select("div[class=pct]").first().select("table").first();

        String table = tb_tr_td.html();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.con);
        boolean isNight = sharedPreferences.getBoolean("THEME_NIGHT",false);

        String bodyColor = "#222222";
        String fontColor = "#666";
        if(isNight){
            bodyColor = "#222222";
            fontColor = "#666";
        }else{
            bodyColor = "#FFFFFF";
            fontColor = "#222222";
        }

        String Html = "<!DOCTYPE html>\n" +
                "<head>\n</head>"
                +"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
                +"<style type=\"text/css\">\n" +
                "\t\tbody {background-color:" + bodyColor + ";padding:0px;margin-top: 250px;margin-bottom: 150px;}\n" +
                "\t\tfont {color:"+ fontColor +"}\n" +
                "\t</style>"
                + "<body onload=\"loadimgwh();\">"
                + "<h1 id=\"title\" style=\"display: none;position: fixed;top: 0px;background: burlywood;width: 100%;\"> 一直在顶部 </h1>"
                + table
                + "</body>"
                + "<script type=\"text/javascript\" charset=\"UTF-8\">\n" +
                "\tvar totalimgnum = document.getElementsByTagName(\"img\").length;\n" +
                "\tvar windowWidth = document.documentElement.clientWidth;\n" +
                "\tvar windowHeight = document.documentElement.clientHeight;\n" +
                "\tvar loadwhfinshnum = 0;\n" +
                "\tfunction loadimgwh(){\n" +
                "\t\tvar imgs = document.getElementsByTagName(\"img\");\n" +
                "\t\tvar num = imgs.length;\n" +
                "\t\tvar imgsurls = new Array();\n"+
                "\t\tfor(var px=0;px<num;px++){\n"+
                "\t\t\t\timgs[px].setAttribute(\"position\",''+px);\n"+
                "\t\t\t\timgsurls[px] = imgs[px].getAttribute(\"data-src\");\n"+
                "\t\t}\n" +
                "\t\tfor(var p=0;p<num;p++){\n" +
                "\t\t\timgs[p].onclick=function()\n" +
                "\t\t\t{\n" +
                "\t\t\t\twindow.inwaishe.showImage(this.getAttribute(\"position\"),JSON.stringify(imgsurls));"+
                "\t\t\t}\n" +
                "\t\t\tpreLoadImg(imgs[p]);\n" +
                "\t\t}\n" +
                "\t}\n" +
                "\tfunction setimgHideorShow(){\n" +
                "\t\t\n" +
                "\t\tvar topScroll = document.documentElement.scrollTop || document.body.scrollTop;\n" +
                "\t\tdocument.getElementById(\"title\").innerHTML = \"H:\" + windowHeight + \" top:\" + topScroll;\n" +
                "\t\tvar imgs = document.getElementsByTagName(\"img\");\n" +
                "\t\tvar num = imgs.length;\n" +
                "\t\tfor(var p=0;p<num;p++){\n" +
                "\t\t\tif(imgs[p].offsetTop >= topScroll && imgs[p].offsetTop <= (topScroll + windowHeight)){\n" +
                "\t\t\t\tif(imgs[p].src != imgs[p].getAttribute(\"data-src\")){\n" +
                "\t\t\t\t\timgs[p].src = imgs[p].getAttribute(\"data-src\");\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}else if((imgs[p].offsetTop + imgs[p].height) < (topScroll + windowHeight) && (imgs[p].offsetTop + imgs[p].height) > topScroll){\n" +
                "\t\t\t\tif(imgs[p].src != imgs[p].getAttribute(\"data-src\")){\n" +
                "\t\t\t\t\timgs[p].src = imgs[p].getAttribute(\"data-src\");\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}else{\n" +
                "\t\t\t\tconsole.log(\"src->\" + imgs[p].src);\n" +
                "\t\t\t\tif(imgs[p].src.toString().indexOf(\"https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=1981777613,4033035273&fm=58&s=4A02EA0ABCC4AE904E3C19860100A0A1&bpow=121&bpoh=75\") > 0){\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t}else{\n" +
                "\t\t\t\t\timgs[p].setAttribute(\"src\",\"https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=1981777613,4033035273&fm=58&s=4A02EA0ABCC4AE904E3C19860100A0A1&bpow=121&bpoh=75\");\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t}\n" +
                "\tfunction preLoadImg(imgtag){\n" +
                "\t\tvar tempimg = new Image();\n" +
                "\t\ttempimg.onload = function(){\n" +
                "\t\t\timgtag.width = windowWidth;\n" +
                "\t\t\timgtag.height = tempimg.height*windowWidth/tempimg.width;\n" +
                "\t\t\timgtag.setAttribute(\"background\",null);\n" +
                "\t\t\timgtag.border = 1;\n" +
                "\t\t\tloadwhfinshnum++;\n" +
                "\t\t\tif(loadwhfinshnum >= totalimgnum){\n" +
                "\t\t\t\tsetimgHideorShow();\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t\ttempimg.src = imgtag.getAttribute(\"data-src\");\n" +
                "\t}\n" +
                "\twindow.onscroll = setimgHideorShow;\n" +
                "\t</script>"
                + "</html>";
        /************************************/
        Document load = Jsoup.parse(Html);
        Elements elements = load.select("img[src]");
        for (Element el : elements) {
            String imgUrl = "http://www.inwaishe.com/" + el.attr("zoomfile");
            el.attr("src","https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=1981777613,4033035273&fm=58&s=4A02EA0ABCC4AE904E3C19860100A0A1&bpow=121&bpoh=75");
            el.attr("data-src",imgUrl);
            el.attr("width", "100%");
            //el.removeAttr("zoomfile");
            el.removeAttr("id");
            el.removeAttr("aid");
            el.removeAttr("w");
        }
        return load.html();
    }

    /**
     * 获得文章详情的Html，并将评论form表单数据填充到 Articlelnfo（来自LiveData）
     * @param artSrc
     * @param articlelnfo
     * @return
     * @throws Exception
     */
    public String getArtDetailHtmlString(String artSrc, Articlelnfo articlelnfo) throws Exception{
        checkMainThread();
        Document doc = Jsoup.connect(artSrc)
                .header("Cookie",getLoginCookies(MyApplication.con))
                .get();
        //
        Element re = doc.select("div[class=cmttips]").first().select("a").first();
        articlelnfo.commSrc = re.attr("href");
        articlelnfo.usrCommNum = Integer.valueOf(re.select("em[class=cmtnum]").first().text());
        //
        //
        Element cform = doc.select("form[id=cform]form[name=cform]").first();
        String action = cform.attr("action");
        String portal_referer = cform.select("input[name=portal_referer]").first().attr("value");
        String referer = cform.select("input[name=referer]").first().attr("value");
        String id = cform.select("input[name=id]").first().attr("value");
        String aid = cform.select("input[name=aid]").first().attr("value");
        String idtype = cform.select("input[name=idtype]").first().attr("value");
        String formhash = cform.select("input[name=formhash]").first().attr("value");
        String replysubmit = cform.select("input[name=replysubmit]").first().attr("value");
        String commentsubmit = cform.select("input[name=commentsubmit]").first().attr("value");

        articlelnfo.replyFrom.action = "http://www.inwaishe.com/" + action;
        articlelnfo.replyFrom.referer = referer;
        articlelnfo.replyFrom.portal_referer = portal_referer;
        articlelnfo.replyFrom.formhash = formhash;
        articlelnfo.replyFrom.id = id;
        articlelnfo.replyFrom.idtype = idtype;
        articlelnfo.replyFrom.replysubmit = replysubmit;
        articlelnfo.replyFrom.commentsubmit = commentsubmit;
        articlelnfo.replyFrom.aid = aid;
        articlelnfo.replyFrom.commentsubmit_btn = "true";
        //

        Element tableElement = doc.select("table").get(1);

        if("视频".equals(articlelnfo.arcType)){
            if(tableElement.select("iframe").size() > 0){
                //youku
                String src = tableElement.select("iframe").get(0).attr("src");
                if(src.contains("youku")){
                    articlelnfo.youku = src;
                }else if(src.contains("bilibili")){
                    if(src.startsWith("//")){
                        tableElement.select("iframe").get(0).attr("src","https:" + src);
                    }
                    int s = src.indexOf("?");
                    String params = src.substring(s + 1,src.length()-1);
                    String[] ps = params.split("&");
                    for(String param : ps){
                        String[] kv = param.split("=");
                        if("aid".equals(kv[0])){
                            articlelnfo.avid = kv[1];
                            break;
                        }
                    }
                }

            }
            if(tableElement.select("embed").size() > 0){
                //Bilibili 视频号
                String flashvars = tableElement.select("embed").first().attr("flashvars");
                int end = flashvars.indexOf("&");
                String avid = flashvars.substring(4,end);
                articlelnfo.avid = avid;
            }
        }

        String table = tableElement.html();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.con);
        boolean isNight = sharedPreferences.getBoolean("THEME_NIGHT",false);

        String bodyColor = "#222222";
        String fontColor = "#666";
        if(isNight){
            bodyColor = "#222222";
            fontColor = "#666";
        }else{
            bodyColor = "#FFFFFF";
            fontColor = "#222222";
        }

        String Html = "<!DOCTYPE html>\n" +
                "<head>\n</head>"
                +"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
                +"<style type=\"text/css\">\n" +
                "\t\tbody {background-color:" + bodyColor + "}\n" +
                "\t\tfont {color:"+ fontColor +"}\n" +
                "\t</style>"
                + "<body onload=\"loadimgwh();\">"
                + "<h1 id=\"title\" style=\"display: none;position: fixed;top: 0px;background: burlywood;width: 100%;\"> 一直在顶部 </h1>"
                + "<table>"
                + table
                + "</table>"
                + "</body>"
                + "<script type=\"text/javascript\" charset=\"UTF-8\">\n" +
                "\tvar totalimgnum = document.getElementsByTagName(\"img\").length;\n" +
                "\tvar windowWidth = document.documentElement.clientWidth;\n" +
                "\tvar windowHeight = document.documentElement.clientHeight;\n" +
                "\tvar loadwhfinshnum = 0;\n" +
                "\tfunction loadimgwh(){\n" +
                "\t\tvar imgs = document.getElementsByTagName(\"img\");\n" +
                "\t\tvar num = imgs.length;\n" +
                "\t\tfor(var p=0;p<num;p++){\n" +
                "\t\t\tpreLoadImg(imgs[p]);\n" +
                "\t\t}\n" +
                "\t}\n" +
                "\tfunction setimgHideorShow(){\n" +
                "\t\t\n" +
                "\t\tvar topScroll = document.documentElement.scrollTop || document.body.scrollTop;\n" +
                "\t\tdocument.getElementById(\"title\").innerHTML = \"H:\" + windowHeight + \" top:\" + topScroll;\n" +
                "\t\tvar imgs = document.getElementsByTagName(\"img\");\n" +
                "\t\tvar num = imgs.length;\n" +
                "\t\tfor(var p=0;p<num;p++){\n" +
                "\t\t\tif(imgs[p].offsetTop >= topScroll && imgs[p].offsetTop <= (topScroll + windowHeight)){\n" +
                "\t\t\t\tif(imgs[p].src != imgs[p].getAttribute(\"data-src\")){\n" +
                "\t\t\t\t\timgs[p].src = imgs[p].getAttribute(\"data-src\");\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}else if((imgs[p].offsetTop + imgs[p].height) < (topScroll + windowHeight) && (imgs[p].offsetTop + imgs[p].height) > topScroll){\n" +
                "\t\t\t\tif(imgs[p].src != imgs[p].getAttribute(\"data-src\")){\n" +
                "\t\t\t\t\timgs[p].src = imgs[p].getAttribute(\"data-src\");\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}else{\n" +
                "\t\t\t\tconsole.log(\"src->\" + imgs[p].src);\n" +
                "\t\t\t\tif(imgs[p].src.toString().indexOf(\"https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=1981777613,4033035273&fm=58&s=4A02EA0ABCC4AE904E3C19860100A0A1&bpow=121&bpoh=75\") > 0){\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t}else{\n" +
                "\t\t\t\t\timgs[p].setAttribute(\"src\",\"https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=1981777613,4033035273&fm=58&s=4A02EA0ABCC4AE904E3C19860100A0A1&bpow=121&bpoh=75\");\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t}\n" +
                "\tfunction preLoadImg(imgtag){\n" +
                "\t\tvar tempimg = new Image();\n" +
                "\t\ttempimg.onload = function(){\n" +
                "\t\t\timgtag.width = windowWidth;\n" +
                "\t\t\timgtag.height = tempimg.height*windowWidth/tempimg.width;\n" +
                "\t\t\timgtag.setAttribute(\"background\",null);\n" +
                "\t\t\timgtag.border = 1;\n" +
                "\t\t\tloadwhfinshnum++;\n" +
                "\t\t\tif(loadwhfinshnum >= totalimgnum){\n" +
                "\t\t\t\tsetimgHideorShow();\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t\ttempimg.src = imgtag.getAttribute(\"data-src\");\n" +
                "\t}\n" +
                "\twindow.onscroll = setimgHideorShow;\n" +
                "\t</script>"
                + "</html>";
        Document load = Jsoup.parse(Html);
        Elements elements = load.select("img[src]");
        for (Element el : elements) {
            String imgUrl = "http://www.inwaishe.com/" + el.attr("src");
            el.attr("src","https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=1981777613,4033035273&fm=58&s=4A02EA0ABCC4AE904E3C19860100A0A1&bpow=121&bpoh=75");
            el.attr("data-src",imgUrl);
            el.attr("width", "100%");
            el.removeAttr("zoomfile");
            el.removeAttr("id");
            el.removeAttr("aid");
            el.removeAttr("w");
        }
        return load.html();
    }
    /**
     * 更新主页区数据
     * @param original 源数据源
     * @return
     */
    public void upDataMainPageInfo(MainPageInfo original) throws Exception {
        checkMainThread();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.inwaishe.com/portal.php?page=" + original.pageNum)
                    .timeout(6000)
                    .get();
            original.code = 1;
            original.msg = "success";
        } catch (IOException e) {
            //网络请求失败
            e.printStackTrace();
            original.code = -1;
            original.msg = "网络请求失败！请检查网络 :" + e.getMessage();
            return;
        }
        Element div = doc.select("div.bd").first();
        /*爬取Banner*/
        original.bannerInfos.clear();
        Elements lis = div.select("li");
        for(Element li : lis){
            String src = li.select("a").first().attr("href");
            String imgUrl =  "http://www.inwaishe.com/" + li.select("img").first().attr("src");
            original.bannerInfos.add(new BannerInfo(imgUrl,"",src));
        }

        /*爬取分享|福利*/
        div = doc.select("div.cmsoftrollc").first();
        lis = div.select("li");
        original.shareInfos.clear();
        for(Element li : lis){
            String imgUrl = "http://www.inwaishe.com/" + li.select("img").first().attr("src");
            String title = li.select("h3").first().text();
            String src = li.select("a").first().attr("href");
            original.shareInfos.add(new ShareInfo(imgUrl,title,src));
        }

        /*爬取分享推荐*/
        div = doc.select("div.cmsofthac").first();
        lis = div.select("li");
        original.recommandsInfos.clear();
        for(Element li : lis){
            String imgUrl = "http://www.inwaishe.com/" + li.select("img").first().attr("src");
            String title = li.select("h3").first().text();
            String src = li.select("a").first().attr("href");
            original.recommandsInfos.add(new ShareInfo(imgUrl,title,src));
        }

        /*爬取热门文章列表*/
        div = doc.select("div.cmsoftkpc").first();
        lis = div.select("li");
        original.hotatrsInfos.clear();
        for(Element li : lis){
            String title = li.select("a").first().text();
            String src = li.select("a").first().attr("href");
            original.hotatrsInfos.add(new ShareInfo(CommonData.LOGO_3_1,title,src));
        }

        /*爬取列表*/
        div = doc.select("div.cmsoftarclist").first();
        lis = div.select("li");
        if(original.pageNum == 1){
            original.isLoadAll = false;
            original.articleInfos.clear();
        }
        for(Element li : lis){
            Element a = li.select("h2").first().select("a").first();
            String href = a.attr("href");
            String title = a.text();

            Element a2 = li.select("h2").first().select("a").get(1);
            String type = a2.text();

            Element arcstatus = li.select("div.arcstatus").first();
            Element arcavt = arcstatus.select("div.arcavt").first();
            String avter = arcavt.select("img").first().attr("src");

            String author = arcstatus.select("a").get(1).text();
            String num = arcstatus.select("a").get(2).text();

            String date = arcstatus.select("div.arctime").first().text();

            Element shadow = li.select("div.shadow").first();

            String img = "http://www.inwaishe.com/" + shadow.select("img").first().attr("src");

            String desc = shadow.select("p").first().text();
            original.articleInfos.add(new Articlelnfo(title,author,avter,date,img,desc,href,type,Integer.valueOf(num)));
        }
        original.pageNum++;
        if(original.pageNum > 300){
            original.isLoadAll = true;
        }
    }

    /**
     * 更新资讯区数据
     * @param type 类型
     * @param original 数据源
     */
    public void upDataNewsPageInfo(NewsTypes type,MainPageInfo original) throws Exception {
        checkMainThread();
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

        String url = "http://www.inwaishe.com/portal.php?mod=list&catid=" + catid + "&page=" + original.pageNum;
        Document doc = null;
        try {
            doc = Jsoup.connect(url).timeout(6000).get();
            original.code = 1;
            original.msg = "success";
        } catch (IOException e) {
            e.printStackTrace();
            original.code = -1;
            original.msg = "网络请求失败！请检查网络 :" + e.getMessage();
            return;
        }
        Element contentinner = doc.select("div.contentinner").first();
        //<div class="cmnlview cl">
        Elements items = contentinner.select("div.cmnlview.cl");
        if(items.size() == 0){
            original.isLoadAll = true;
            return;
        }
        ArrayList<Articlelnfo> list = new ArrayList<>();
        for(Element item: items){

            String title = item.select("a").get(0).text();
            String src = item.select("a").get(0).attr("href");
            String author = item.select("a").get(3).text();
            String num = item.select("a").get(4).text();
            String iconurl = item.select("img").get(0).attr("src");
            String bgurl = "";
            try {
                bgurl = "http://www.inwaishe.com/" + item.select("img").get(1).attr("src");
            }catch (Exception e) {
                bgurl = "";
            }

            String desc = item.select("p").first().text();
            String date = item.select("div.arctime").first().text();
            Articlelnfo info = new Articlelnfo(title,author,iconurl,date,bgurl,desc,src,type.toString(),Integer.valueOf(num));
            list.add(info);
        }
        if(original.pageNum == 1){
            original.articleInfos.clear();
        }
        original.articleInfos.addAll(list);
        original.pageNum++;
    }

    /**
     * 更新视频区数据
     * @param original 数据源
     */
    public void updataVedioPageInfo(MainPageInfo original) throws Exception {
        checkMainThread();

        String url = "http://www.inwaishe.com/portal.php?mod=list&catid=" + 3 + "&page=" + original.pageNum;
        Document doc = null;
        try {
            doc = Jsoup.connect(url).timeout(6000).get();
            original.code = 1;
            original.msg = "success";
        } catch (IOException e) {
            e.printStackTrace();
            original.code = -1;
            original.msg = "网络请求失败！请检查网络 :" + e.getMessage();
            return;
        }

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


        /*小*/
        ArrayList<Articlelnfo> listsm = new ArrayList<>();
        Elements lis = vsmall.select("li");
        for(Element li : lis){
            String playtimes_sm = li.select("span.vplaycount").first().text();
            String imgurl_sm = "http://www.inwaishe.com/" + li.select("img").first().attr("src");
            String src_sm = li.select("a").first().attr("href");
            String title_sm = li.select("a").first().text();

            Articlelnfo info_sm = new Articlelnfo(title_sm, "", "", imgurl_sm, ""
                    , src_sm, "视频", "", Integer.parseInt(playtimes_sm)
                    , 0);
            listsm.add(info_sm);
        }
        /*列表*/

        Elements items = doc.select("div.videolview.cl");
        if(items.size() == 0){
            original.isLoadAll = true;
            return;
        }
        ArrayList<Articlelnfo> list = new ArrayList<>();
        for(Element item : items){
            String href = item.select("a").first().attr("href");
            String imgurl = "http://www.inwaishe.com/" + item.select("img").first().attr("src");
            String title = item.select("img").first().attr("alt");
            Element vinfo = item.select("div.vinfo").first();
            String usrName =  vinfo.select("a.vnickname").first().text();
            String vmsgcount_n = vinfo.select("span.vmsgcount").first().text();
            String vplaycount_n = vinfo.select("span.vplaycount").first().text();
            Articlelnfo info_n = new Articlelnfo(title, usrName, "", imgurl, ""
                    , href, "视频", "", Integer.parseInt(vmsgcount_n)
                    , Integer.parseInt(vplaycount_n));
            list.add(info_n);
        }
        if(original.pageNum == 1){
            original.articleInfos.clear();
            original.articleInfos.add(info_la);//大
            original.articleInfos.addAll(listsm);//小
        }
        original.articleInfos.addAll(list);//列表添加
        original.pageNum++;
    }

    /***
     * 获取登录页面的表单信息
     * @return
     */
    public Map<String,String> getLoginPageFormInfo() throws Exception {
        checkMainThread();
        Document doc = Jsoup.connect(CommonData.LOGIN_PAGE).get();
        String str = doc.toString();
        str =StringEscapeUtils.unescapeHtml(str);
        doc = Jsoup.parse(str);
        Element form = doc.select("form").first();
        String action = "http://www.inwaishe.com/" + form.attr("action") + "&inajax=1";
        String hash = doc.select("input[name=formhash]").first().attr("value");
        String referer = doc.select("input[name=referer]").first().attr("value");
        Map<String,String> map = new ArrayMap<>();
        map.put("action",action);
        map.put("formhash",hash);
        map.put("referer",referer);
        return map;
    }

    /**
     * 获得回复表单信息
     * @return
     * @throws Exception
     */
    public Map<String,String> getReplyFormInfo(String url) throws Exception{

        checkMainThread();
        Document doc = Jsoup.connect(url)
                .header("Cookie",getLoginCookies(MyApplication.con))
                .get();
        //反转义
        String str = doc.toString();
        str =StringEscapeUtils.unescapeHtml(str);
        doc = Jsoup.parse(str);
        //提取表单信息
        Element form = doc.select("form[id=postform]").first();
        String formhash = form.select("input[name=formhash]").first().attr("value");
        String handlekey = form.select("input[name=handlekey]").first().attr("value");
        String noticeauthor = form.select("input[name=noticeauthor]").first().attr("value");
        String noticetrimstr = form.select("input[name=noticetrimstr]").first().attr("value");
        String noticeauthormsg = form.select("input[name=noticeauthormsg]").first().attr("value");
        String usesig = form.select("input[name=usesig]").first().attr("value");
        String reppid = form.select("input[name=reppid]").first().attr("value");
        String reppost = form.select("input[name=reppost]").first().attr("value");
        String action = "http://www.inwaishe.com/" + form.attr("action") + "&inajax=1";

        Map<String,String> map = new ArrayMap<>();
        map.put("formhash",formhash);
        map.put("handlekey",handlekey);
        map.put("noticeauthor",noticeauthor);
        map.put("noticetrimstr",noticetrimstr);
        map.put("noticeauthormsg",noticeauthormsg);
        map.put("usesig",usesig);
        map.put("reppid",reppid);
        map.put("reppost",reppost);
        map.put("action",action);
        map.put("subject","");

        return map;

    }

    /**
     * 获取 用户信息
     * @param uid UId
     * @return
     * @throws Exception
     */
    public UserInfo getUesrInfoFromUidSpace(String uid) throws Exception {
        checkMainThread();
        String url = String.format(CommonData.UID_SPACE,uid);
        Document doc = Jsoup.connect(url)
                //.header("Cookie",getLoginCookies(MyApplication.con))
                .get();
        Elements divs = doc.select("div[class=pbm mbm bbda cl]");

        Element div = divs.get(1);
        Elements lis = div.select("ul").first().select("li");

        String userGroup = lis.get(0).text();

        Element div_hcl = doc.select("div[class=h cl]").first();

        String userName = div_hcl.select("h2").first().text();

        String userIcon = div_hcl.select("img").first().attr("src");

        Elements li = doc.select("div[id=psts]div[class=cl]").first().select("li");

        String userSpceused = li.get(0).text();
        String userBp = li.get(1).text();
        String userFy = li.get(2).text();
        String userFireValue = li.get(3).text();

        UserInfo info = new UserInfo();
        info.userBp = userBp;
        info.userFireValue = userFireValue;
        info.userSpceused = userSpceused;
        info.userGroup = userGroup;
        info.userName = userName;
        info.userIcon = userIcon;
        info.uid = uid;
        return info;
    }


    /**
     * 是否需要登陆
     * @param context
     * @return
     */
    public static boolean isNeedLogin(Context context){
        SharePreferencesStore sharePreferencesStore = SharePreferencesStore.getInstance(context);
        if(sharePreferencesStore.get(CommonData.UID) == null
                || TextUtils.isEmpty(getLoginCookies(context))){
            context.startActivity(new Intent(context,LoginActivity.class));
            return true;
        }
        return false;
    }
}
