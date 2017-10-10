package com.zp.mypermissioncheck.permission.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


import com.zp.mypermissioncheck.permission.model.Model_Permission;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 受保护权限检测申请工具类——对外提供公共方法：提取检测权限方法、提取被禁止的权限、提取被禁止且勾选“不再提示”的权限
 */
public class EasyPermissionUtil {
    private static final String TAG = "EasyPermissionUtil:";


    public static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 获取没被授权或被禁止的权限
     *
     * @param activity
     * @param permission
     * @return
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    public static List<String> findDeniedPermissions(Activity activity, String... permission) {
        List<String> denyPermissions = new ArrayList<>();

        for (String value : permission) {
            if (activity.checkSelfPermission(value) != PackageManager.PERMISSION_GRANTED) {
                //保存所有未被授权的权限
                denyPermissions.add(value);
            }
        }
        return denyPermissions;
    }

    /**
     * 返回所有被拒绝的权限，包括“不再提示”的权限
     *
     * @param activity
     * @param permission
     * @return
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    public static List<Model_Permission> findRationalePermissions(Activity activity, String... permission) {
        List<Model_Permission> mPermissionListNeedReq = new ArrayList<Model_Permission>();

        for (String value : permission) {
            if (activity.checkSelfPermission(value) != PackageManager.PERMISSION_GRANTED) {
                //判断是否勾选“不再提示”
                Model_Permission info = new Model_Permission();
                info.setName(value);
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, value)) {//如果用户拒绝了申请并勾选“不再提示”则返回false
                    info.setRationalNeed(true);//需要手动提示授权，因为此时用户勾选了“不再提示”
                } else {
                    info.setRationalNeed(false);
                }
                mPermissionListNeedReq.add(info);
                Log.e("my", "各项被禁权限名称==" + value);
            }
        }
        return mPermissionListNeedReq;
    }

    /**
     * 检测Maniest.xml中申请的权限，把受保护权限提取出来进行单独授权
     *
     * @param mContext
     * @return
     */
    public static String[] getPermissions(Context mContext) {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] permissions = pi.requestedPermissions;
            Set<String> result = new HashSet<>();
            for (String permission : permissions) {
                if (checkIsDanderPermission(permission)) {
                    result.add(permission);
                }
            }
            return result.toArray(new String[result.size()]);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return new String[]{};
    }

    public static boolean checkIsDanderPermission(String permission) {
        if (permission.equals("android.permission.WRITE_CONTACTS")
                || permission.equals("android.permission.GET_ACCOUNTS")
                || permission.equals("android.permission.READ_CONTACTS")
                //联系人权限组
                || permission.equals("android.permission.READ_CALL_LOG")
                || permission.equals("android.permission.READ_PHONE_STATE")
                || permission.equals("android.permission.CALL_PHONE")
                || permission.equals("android.permission.WRITE_CALL_LOG")
                || permission.equals("android.permission.USE_SIP")
                || permission.equals("android.permission.PROCESS_OUTGOING_CALLS")
                || permission.equals("com.android.voicemail.permission.ADD_VOICEMAIL")
                //通话权限组
                || permission.equals("android.permission.READ_CALENDAR")
                || permission.equals("android.permission.WRITE_CALENDAR")
                //日程权限组
                || permission.equals("android.permission.CAMERA")
                //相机权限
                || permission.equals("android.permission.BODY_SENSORS")
                //？
                || permission.equals("android.permission.ACCESS_FINE_LOCATION")
                || permission.equals("android.permission.ACCESS_COARSE_LOCATION")
                //定位权限组
                || permission.equals("android.permission.READ_EXTERNAL_STORAGE")
                || permission.equals("android.permission.WRITE_EXTERNAL_STORAGE")
                //读写存储权限
                || permission.equals("android.permission.RECORD_AUDIO")
                //录音权限
                || permission.equals("android.permission.READ_SMS")
                || permission.equals("android.permission.RECEIVE_WAP_PUSH")
                || permission.equals("android.permission.RECEIVE_MMS")
                || permission.equals("android.permission.RECEIVE_SMS")
                || permission.equals("android.permission.SEND_SMS")
                || permission.equals("android.permission.READ_CELL_BROADCASTS")
            //短信权限组
                ) {
            return true;
        }
        return false;
    }
}
