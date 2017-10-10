package com.zp.mypermissioncheck.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;


import com.zp.mypermissioncheck.permission.model.Model_Permission;
import com.zp.mypermissioncheck.permission.util.EasyPermissionUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 权限逻辑处理——直接在activity中调用设置需要申请的权限；页面中使用注解获取回调
 * author : zp
 * e-mail : zhangping1@hrbb.com.cn
 * time : 2017/8/31 上午9:21
 * desc :动态权限申请工具——用于执行授权、处理回调
 * version: 1.0
 */
public class EasyPermission {
    private Activity activity;
    private String[] permissions;//受保护权限列表
    private int requestCode = 101;//默认值
    private static List<Model_Permission> mPermissionListNeedReq;//未被授权的权限列表
    private static PermissionResultCallBack permissionResultCallBack;


    private EasyPermission(Activity object) {
        this.activity = object;
    }

    public static EasyPermission with(Activity activity) {
        permissionResultCallBack = (PermissionResultCallBack) activity;
        return new EasyPermission(activity);
    }

    /**
     * author : zp
     * e-mail : zhangping1@hrbb.com.cn
     * time : 2017/8/25 上午9:33
     * desc : 外部方法：【可选则性调用】用户指定申请哪些权限时调用，不设置 默认在下面是直接获取Maniest.xml中权限通过判断提取出受保护（敏感）权限
     * version: 1.0
     */
    public EasyPermission permissions(String... permissions) {
        this.permissions = permissions;
        return this;
    }

    public EasyPermission code(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    /**
     * author : zp
     * e-mail : zhangping1@hrbb.com.cn
     * time : 2017/8/25 上午9:34
     * desc :外部方法：开始请求授权——在Activity中调用
     * version: 1.0
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    public void request() {
        if (null == this.activity) {
            throw new IllegalArgumentException("null activity is not supported");
        }
        if (null == this.permissions) {
            //自动获取Maniest.xml中声明的敏感权限列表
            this.permissions = EasyPermissionUtil.getPermissions(activity);
        }
        requestPermissions(activity, requestCode, permissions);
    }

    @TargetApi(value = Build.VERSION_CODES.M)
    private static void requestPermissions(Activity activity, int requestCode, String[] permissions) {
        //系统版本小于6.0时不作处理，默认允许
        if (!EasyPermissionUtil.isOverMarshmallow()) {
            permissionResultCallBack.onBasicPermissionSuccess();
            return;
        }

        //获取所有被拒绝权限——回调处理那些已经被选择为“不再提示”且被禁止的权限
        mPermissionListNeedReq = EasyPermissionUtil.findRationalePermissions(activity, permissions);
        //获取未被授权的权限 进行请求授权用——只包含权限名称
        List<String> deniedPermissions = EasyPermissionUtil.findDeniedPermissions(activity, permissions);
        Log.i("permission", "被禁止权限==" + mPermissionListNeedReq.size() + "deniedPermissions.size==" + deniedPermissions.size());
        if (deniedPermissions.size() > 0) {
            activity.requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
        } else {
            permissionResultCallBack.onBasicPermissionSuccess();
        }
    }

    /**
     * 权限请求结果处理——在Activity复写的onRequestPermissionsResult方法内调用
     *
     * @param obj
     * @param requestCode  以下参数直接传onRequestPermissionsResult中参数即可
     * @param permissions
     * @param grantResults
     */
    public static void handleResult(Activity obj, int requestCode, String[] permissions, int[] grantResults) {
        Log.i("permission", "handleResult回调处理");
        //所有已经勾选“不再提示”且禁用的权限，需要手动弹框提示用户必须开启的权限
        List<Model_Permission> needRationalPermissionList = new ArrayList<Model_Permission>();
        //所有被禁用的权限
        List<Model_Permission> deniedPermissionList = new ArrayList<Model_Permission>();
        boolean isSuccess = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                if (mPermissionListNeedReq != null && mPermissionListNeedReq.size() > 0) {
                    if (mPermissionListNeedReq.get(i).isRationalNeed())
                        needRationalPermissionList.add(mPermissionListNeedReq.get(i));
                    else
                        deniedPermissionList.add(mPermissionListNeedReq.get(i));
                }
                isSuccess = false;
            }
        }//end for

        if (needRationalPermissionList != null && needRationalPermissionList.size() > 0) {
            Log.i("permission", "needRationalPermission.size==" + needRationalPermissionList.size());
            for (Model_Permission info : needRationalPermissionList) {
                Log.i("permission", "needRationalPermission权限名称==" + info.getName());
            }

            permissionResultCallBack.onBasicPermissionFailedNeedRational();
        } else if (deniedPermissionList != null && deniedPermissionList.size() > 0) {
            Log.i("permission", "onBasicPermissionFailed==");

            //直接反馈申请权限失败，下次再申请权限
            permissionResultCallBack.onBasicPermissionFailed();
        } else if (isSuccess) { //全部授权成功
            Log.i("permission", "onBasicPermissionSuccess==");

            //这里代表所有权限申请成功
            permissionResultCallBack.onBasicPermissionSuccess();
        }
    }

}
