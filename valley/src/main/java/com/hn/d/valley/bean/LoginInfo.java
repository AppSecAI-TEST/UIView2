package com.hn.d.valley.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/15 12:25
 * 修改人员：Robi
 * 修改时间：2016/12/15 12:25
 * 修改备注：
 * Version: 1.0.0
 */
public class LoginInfo implements Parcelable {
    public static final Creator<LoginInfo> CREATOR = new Creator<LoginInfo>() {
        @Override
        public LoginInfo createFromParcel(Parcel source) {
            return new LoginInfo(source);
        }

        @Override
        public LoginInfo[] newArray(int size) {
            return new LoginInfo[size];
        }
    };
    public String phone;
    public String pwd;
    public String icoUrl;

    public LoginInfo(String phone, String pwd, String icoUrl) {
        this.phone = phone;
        this.pwd = pwd;
        this.icoUrl = icoUrl;
    }

    protected LoginInfo(Parcel in) {
        this.phone = in.readString();
        this.pwd = in.readString();
        this.icoUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.phone);
        dest.writeString(this.pwd);
        dest.writeString(this.icoUrl);
    }
}
