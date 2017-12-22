package com.inwaishe.app.framework.emotionkeyboard.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by WangJing on 2017/12/8.
 * 一个表情包的配置
 */

public class EmotionGroupCofig implements Parcelable {

    public String assetfile = "";
    public String filepath = "";
    public int resId = -1;

    public EmotionGroupCofig(){

    }

    protected EmotionGroupCofig(Parcel in) {
        assetfile = in.readString();
        filepath = in.readString();
        resId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(assetfile);
        dest.writeString(filepath);
        dest.writeInt(resId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EmotionGroupCofig> CREATOR = new Creator<EmotionGroupCofig>() {
        @Override
        public EmotionGroupCofig createFromParcel(Parcel in) {
            return new EmotionGroupCofig(in);
        }

        @Override
        public EmotionGroupCofig[] newArray(int size) {
            return new EmotionGroupCofig[size];
        }
    };
}
