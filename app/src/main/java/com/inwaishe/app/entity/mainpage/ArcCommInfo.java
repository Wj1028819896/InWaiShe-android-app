package com.inwaishe.app.entity.mainpage;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by Administrator on 2017/8/12 0012.
 * 评论信息（直接评论）
 */

public class ArcCommInfo implements Serializable{

    public String userName = "";
    public String userIconUrl = "";
    public String userDate = "";
    public String userCommon = "";
    public String replyAction = "";//回复Action
    public String usrCommPage = "";//用户评论详情页
    public String usrQuoteComm = "";//引用的评论
    public ArrayList<ArcReplyCommInfo> arcReplyCommInfos = new ArrayList<>();

    public ArcCommInfo(String userName, String userIconUrl, String userDate, String userCommon) {
        this.userName = userName;
        this.userIconUrl = userIconUrl;
        this.userDate = userDate;
        this.userCommon = userCommon;
    }

    public ArcCommInfo(){

    }

    public static  class ReplyFrom implements Serializable{
        public String action = "";//当前评论接口url
        public String formhash = "";
        public String handlekey = "";
        public String noticeauthor = "";
        public String noticetrimstr = "";
        public String noticeauthormsg = "";
        public String usesig = "";
        public String reppid = "";
        public String reppost = "";
        public String subject = "";
    }
}
