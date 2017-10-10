package com.zp.mypermissioncheck.permission.model;

/**
 * author : zp
 * e-mail : zhangping1@hrbb.com.cn
 * time : 2017/8/23 13:36
 * desc :
 * version: 1.0
 */

public class Model_Permission {
    private boolean rationalNeed;//是否需要手动弹框提示授权
    private String name;//权限名称

    /**
     * 是否弹出自定义权限申请框
     *
     * @return true：弹出自定义框；false：不再弹框
     */
    public boolean isRationalNeed() {
        return rationalNeed;
    }

    public void setRationalNeed(boolean rationalNeed) {
        this.rationalNeed = rationalNeed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
