package com.inwaishe.app.entity.mainpage;

import org.w3c.dom.Document;

import java.io.Serializable;

/**
 * Created by WangJing on 2017/8/9.
 * 首页-文章（视频共用）列表
 */

public class Articlelnfo implements Serializable {

    public String artTitle = "";//标题
    public String artAuthor = "";//作者
    public String artDate = "";//发布时间
    public String artImageUrl = "";//图片地址
    public String artDesc = "";//简介
    public String artSrc = "";//跳转地址
    public String arcType = "";//文章类型 、视频最特殊
    public String avid = "";//B站视频ID
    public int vedioPlayNum = 0;//视频播放数
    public int usrCommNum = 0;//评论数

    public Articlelnfo(String artTitle, String artAuthor, String artDate, String artImageUrl, String artDesc, String artSrc, String arcType, String avid, int vedioPlayNum, int usrCommNum) {
        this.artTitle = artTitle;
        this.artAuthor = artAuthor;
        this.artDate = artDate;
        this.artImageUrl = artImageUrl;
        this.artDesc = artDesc;
        this.artSrc = artSrc;
        this.arcType = arcType;
        this.avid = avid;
        this.vedioPlayNum = vedioPlayNum;
        this.usrCommNum = usrCommNum;
    }

    public Articlelnfo(String artTitle, String artAuthor, String artDate, String artImageUrl, String artDesc, String artSrc, String arcType) {
        this.artTitle = artTitle;
        this.artAuthor = artAuthor;
        this.artDate = artDate;
        this.artImageUrl = artImageUrl;
        this.artDesc = artDesc;
        this.artSrc = artSrc;
        this.arcType = arcType;
    }
}
