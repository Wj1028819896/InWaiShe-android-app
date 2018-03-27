package com.inwaishe.app.entity.photocenter;

import java.io.Serializable;

/**
 * Created by WangJing on 2018/2/26.
 * 专辑信息
 */

public class AlbumInfo implements Serializable{

    public String title = "";//标题
    public String artSrc = "";
    public String coverImgSrc = "";//封面
    public String author = "";//作者
    public int visitNums = 0;//访问数
    public int commentsNum = 0;//评论数
    public String desc = "";//描述

}
