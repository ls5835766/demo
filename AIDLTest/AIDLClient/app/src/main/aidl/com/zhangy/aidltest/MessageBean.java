package com.zhangy.aidltest;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * zhangy
 * 载体
 * Created by td on 2017/10/25.
 */

public class MessageBean implements Parcelable {

    private String content;//需求内容
    private int level;//重要等级

    public MessageBean(){
    }

    protected MessageBean(Parcel in) {
        content = in.readString();
        level = in.readInt();
    }

    public MessageBean(String content, int level) {
        this.content = content;
        this.level = level;
    }


    //如果需要支持定向tag为out,inout，就要重写该方法
    public void readFromParcel(Parcel dest) {
        //注意，此处的读值顺序应当是和writeToParcel()方法中一致的
        this.content = dest.readString();
        this.level = dest.readInt();
    }


    public static final Creator<MessageBean> CREATOR = new Creator<MessageBean>() {
        @Override
        public MessageBean createFromParcel(Parcel in) {
            return new MessageBean(in);
        }

        @Override
        public MessageBean[] newArray(int size) {
            return new MessageBean[size];
        }
    };

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeString(this.content);
        parcel.writeInt(this.level);
    }
}
