package com.inwaishe.app.entity;

import com.inwaishe.app.entity.mainpage.baseResponse;

import java.util.ArrayList;

/**
 * Created by WangJing on 2018/6/11.
 */

public class SearchResBackInfo extends baseResponse {

    public ArrayList<SearchResItemInfo> list = new ArrayList<>();
    public int pageNum = 1;
    public boolean isLoadAll = false;//是否加载完所有分页

}
