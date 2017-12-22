package com.inwaishe.app.framework.emotionkeyboard.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by WangJing on 2017/12/1.
 * 表情
 */

public class Emotion implements Parcelable {
    public String code = "";//匹配码
    public String name = "";
    public String msg = "";
    public int resId = -1;//本地资源
    public String filepath = "";//本地文件路径
    public String url = "";//网络路径

    public Emotion(){

    }

    protected Emotion(Parcel in) {
        code = in.readString();
        name = in.readString();
        msg = in.readString();
        resId = in.readInt();
        filepath = in.readString();
        url = in.readString();
    }

    public static final Creator<Emotion> CREATOR = new Creator<Emotion>() {
        @Override
        public Emotion createFromParcel(Parcel in) {
            return new Emotion(in);
        }

        @Override
        public Emotion[] newArray(int size) {
            return new Emotion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(name);
        dest.writeString(msg);
        dest.writeInt(resId);
        dest.writeString(filepath);
        dest.writeString(url);
    }
}
