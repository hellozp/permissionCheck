package com.zp.mypermissioncheck;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zp.mypermissioncheck.permission.EasyPermission;
import com.zp.mypermissioncheck.permission.PermissionResultCallBack;

/**
 * author : zp
 * e-mail : 943611512@qq.com
 * time : 2017/9/30 上午10:05
 * desc : 6.0系统敏感权限检测处理
 * version: 1.0
 */
public class MainActivity extends AppCompatActivity implements PermissionResultCallBack {
    Button btn_permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_permission = (Button) findViewById(R.id.btn_permission);
        btn_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestMyPermission();
            }
        });

    }

    //*********************************测试敏感权限申请*********************************
    private void requestMyPermission() {
        String[] arr_permision = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        //根据列出的权限分次获取敏感权限
        EasyPermission.with(this).code(AppContants.REQUEST_CODE).permissions(arr_permision).request();
    }

    //必须复写此处的回调，否则无法获取用户授权结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermission.handleResult(this, requestCode, permissions, grantResults);//处理权限申请回调结果
    }

    @Override
    public void onBasicPermissionSuccess() {
        Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBasicPermissionFailed() {
        Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBasicPermissionFailedNeedRational() {
        Toast.makeText(this, "授权失败并被勾选不再提示，此时引导用户去应用设置中手动授权", Toast.LENGTH_SHORT).show();

        //最好以对话框形式引导
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle("请前往应用授权设置中添加授权").setPositiveButton("去授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 001);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();

    }
    /**************************END 权限申请****************************************/
}
