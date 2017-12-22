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

public class MainPageInfo extends baseResponse{

    public ArrayList<BannerInfo> bannerInfos = new ArrayList<>();//轮播图信息

    public ArrayList<ShareInfo> shareInfos = new ArrayList<>();//分享|福利
    public String keyBoardShortcutSrc = "";//键盘快捷入口
    public String mouseShortcutSrc = "";//鼠标快捷入口
    public String perimeterShortcutSrc = "";//周边快捷入口
    public String audioShortcutSrc = "";//音频快捷入口
    public ArrayList<Articlelnfo> articleInfos = new ArrayList<>();//列表
    public boolean isLoadAll = false;//是否加载完所有分页
    public int pageNum = 1;
}
