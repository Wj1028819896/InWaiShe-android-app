package com.inwaishe.app.entity.mainpage;

import org.w3c.dom.Document;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by WangJing on 2017/8/9.
 * 首页-文章（视频共用）列表
 */

public class Articlelnfo implements Serializable {

    public String artTitle = "";//标题
    public String artAuthor = "";//作者
    public String artAuthorAvter = "";//头像
    public String artDate = "";//发布时间
    public String artImageUrl = "";//图片地址
    public String artDesc = "";//简介
    public String artSrc = "";//跳转地址
    public String arcType = "";//文章类型 、视频最特殊
    public String avid = "";//B站视频ID
    public String youku = "";
    public int vedioPlayNum = 0;//视频播放数
    public int usrCommNum = 0;//评论数
    public int artReadTimes = 0;//阅读书目
    public ReplyFrom replyFrom = new ReplyFrom();//回复表单数据
    public String commSrc = "";//评论完整论坛地址
    public ArrayList<ArcCommInfo> arcCommInfos = new ArrayList<>();//评论列表数据

    public class ReplyFrom implements Serializable{
        public String action = "";//当前评论接口url
        public String portal_referer = "";
        public String referer = "";
        public String id = "";
        public String aid = "";
        public String idtype = "";
        public String formhash = "";
        public String replysubmit = "";
        public String commentsubmit = "";
        public String commentsubmit_btn = "true";
        public String message = "";
        public String posttime = "";
        public String usesig = "";
        public String subject = "";

    }
    public Articlelnfo(){

    }
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

    public Articlelnfo(String artTitle, String artAuthor,String artAuthorAvter,String artDate, String artImageUrl, String artDesc, String artSrc, String arcType,int usrCommNum) {
        this.artTitle = artTitle;
        this.artAuthor = artAuthor;
        this.artAuthorAvter = artAuthorAvter;
        this.artDate = artDate;
        this.artImageUrl = artImageUrl;
        this.artDesc = artDesc;
        this.artSrc = artSrc;
        this.arcType = arcType;
        this.usrCommNum = usrCommNum;
    }
}
