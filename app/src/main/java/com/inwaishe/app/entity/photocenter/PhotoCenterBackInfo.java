package com.inwaishe.app.entity.photocenter;

import com.inwaishe.app.entity.mainpage.baseResponse;

import java.util.ArrayList;

/**
 * Created by WangJing on 2018/2/26.
 */

public class PhotoCenterBackInfo extends baseResponse {

    public ArrayList<AlbumInfo> abList = new ArrayList<>();
    public int pageNum = 1;
    public boolean isLoadAll = false;//是否加载完所有分页
}
