package com.zp.mypermissioncheck.permission;

/**
 * author : zp
 * e-mail : zhangping1@hrbb.com.cn
 * time : 2017/9/19 上午9:48
 * desc : 权限申请回调接口
 * version: 1.0
 */

public interface PermissionResultCallBack {

    /**
     * 权限申请成功回调
     */
    public void onBasicPermissionSuccess();

    /**
     * 用户拒绝授权回调
     */
    public void onBasicPermissionFailed();

    /**
     * 用户禁止且勾选“不再提示”选择项的权限回调
     */
    public void onBasicPermissionFailedNeedRational();
}
