package com.inwaishe.app.entity.mainpage;

import java.io.Serializable;

/**
 * Created by WangJing on 2017/10/26.
 * 回复别人的评论
 */

public class ArcReplyCommInfo implements Serializable{

    public String userName = "";
    public String userIconUrl = "";
    public String userDate = "";
    public String userCommon = "";
    public String replyAction = "";//回复Action

    public ArcReplyCommInfo(String userName, String userIconUrl, String userDate, String userCommon) {
        this.userName = userName;
        this.userIconUrl = userIconUrl;
        this.userDate = userDate;
        this.userCommon = userCommon;
    }

    public ArcReplyCommInfo(){

    }
}
