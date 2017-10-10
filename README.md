# permissionCheck
自定义权限检测工具类EasyPermissionUtil实现权限检测及授权，重点处理用户拒绝并选择“不再提示”的操作回调。直接运行demo，一看便知，亲测有效。好使请不吝start

1、在要检查权限的Activity或fragment中实现自定义接口implements PermissionResultCallBack
2、实现以下三个回调方法
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

     }
     
 3、复写Activity的onRequestPermissionsResult回调方法，在里面调用自定义的权限检测方法：
     EasyPermission.handleResult(this, requestCode, permissions, grantResults);
