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
public class LoginUserInfo implements Parcelable {
    public static final Creator<LoginUserInfo> CREATOR = new Creator<LoginUserInfo>() {
        @Override
        public LoginUserInfo createFromParcel(Parcel source) {
            return new LoginUserInfo(source);
        }

        @Override
        public LoginUserInfo[] newArray(int size) {
            return new LoginUserInfo[size];
        }
    };
    public String phone;
    public String pwd;
    public String icoUrl;

    public LoginUserInfo(String phone, String pwd, String icoUrl) {
        this.phone = phone;
        this.pwd = pwd;
        this.icoUrl = icoUrl;
    }

    protected LoginUserInfo(Parcel in) {
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
