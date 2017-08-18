package com.inwaishe.app.entity.mainpage;

import java.io.Serializable;

/**
 * Created by WangJing on 2017/8/9.
 * 首页-分享|福利
 */

public class ShareInfo implements Serializable {

    public String imgUrl = "";//图地址
    public String title = "";//描述信息
    public String src = "";//跳转地址

    public ShareInfo(String imgUrl, String title, String src) {
        this.imgUrl = imgUrl;
        this.title = title;
        this.src = src;
    }
}
