package com.inwaishe.app.entity.mainpage;

import com.inwaishe.app.dbroom.NewsTypes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    public ArrayList<ShareInfo> recommandsInfos = new ArrayList<>();//推荐
    public ArrayList<ShareInfo> hotatrsInfos = new ArrayList<>();//热门
    public String keyBoardShortcutSrc = "";//键盘快捷入口
    public String mouseShortcutSrc = "";//鼠标快捷入口
    public String perimeterShortcutSrc = "";//周边快捷入口
    public String audioShortcutSrc = "";//音频快捷入口
    public ArrayList<Articlelnfo> articleInfos = new ArrayList<>();//列表
    public boolean isLoadAll = false;//是否加载完所有分页
    public int pageNum = 1;

    /**
     * 序列化方式对对象进行深克隆
     * @return 克隆对象
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public MainPageInfo deepClone() throws IOException, ClassNotFoundException {
        // 将对象写到流里
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(this);
        // 从流里读出来
        ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
        ObjectInputStream oi = new ObjectInputStream(bi);
        return (MainPageInfo)(oi.readObject());
    }
}
