package com.inwaishe.app.entity.mainpage;

import java.io.Serializable;

/**
 * Created by WangJing on 2017/9/13.
 */

public class baseResponse implements Serializable{


    public int code = 0;//状态码（success ? code>0 : code<0）
    public String msg = "";//消息
    public String html = "";



}
