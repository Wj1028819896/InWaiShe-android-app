package com.inwaishe.app.entity;

import java.io.Serializable;

/**
 * Created by WangJing on 2018/6/11.
 */

public class SearchResItemInfo implements Serializable{
    public String artTitle = "";//标题
    public String artAuthor = "";//作者
    public String artDate = "";//发布时间
    public int artReadTimes = 0;//阅读书目
    public int usrCommNum = 0;//评论数
    public String artSrc = "";//跳转地址
    public String artDesc = "";//简介

}
