package com.inwaishe.app.entity.mainpage;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/12 0012.
 * 评论信息
 */

public class ArcCommInfo implements Serializable{

    public String userName = "";
    public String userIconUrl = "";
    public String userDate = "";
    public String userCommon = "";

    public ArcCommInfo(String userName, String userIconUrl, String userDate, String userCommon) {
        this.userName = userName;
        this.userIconUrl = userIconUrl;
        this.userDate = userDate;
        this.userCommon = userCommon;
    }
}
