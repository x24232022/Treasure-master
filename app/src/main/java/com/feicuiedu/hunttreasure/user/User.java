package com.feicuiedu.hunttreasure.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/1/9 0009.
 */

public class User {


    /**
     * UserName : qjd
     * Password : 654321
     */

    @SerializedName("UserName")
    private String name;
    @SerializedName("Password")
    private String password;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

