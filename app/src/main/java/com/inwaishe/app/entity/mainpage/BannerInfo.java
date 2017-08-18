package com.inwaishe.app.entity.mainpage;

import java.io.Serializable;

/**
 * Created by WangJing on 2017/8/9.
 * 首页-轮播图
 */

public class BannerInfo implements Serializable {

    public String imgUrl = "";//轮播图地址
    public String desc = "";//描述信息
    public String src = "";//跳转地址

    public BannerInfo(String imgUrl,String desc,String src ){

        this.imgUrl = imgUrl;
        this.desc = desc;
        this.src = src;

    }

}
