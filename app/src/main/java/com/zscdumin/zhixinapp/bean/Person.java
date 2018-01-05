package com.zscdumin.zhixinapp.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by ZSCDumin on 2017/12/28.
 * 作者邮箱：2712220318@qq.com
 */

public class Person extends BmobObject {
    private String userAccount;
    private String userPassword;

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}