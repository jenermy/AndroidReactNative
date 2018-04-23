package com.example.coolanimation;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PermissionActivity extends AppCompatActivity {
//    private static final String[] permissions = {
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.RECORD_AUDIO,
//            Manifest.permission.CAMERA
//    };
    public static final int PERMISSION_GRATED = 1010;
    public static final int PERMISSION_DENY = 1011;
    public static final String EXTRA_PERMISSIONS = "coolanimation_extra_permissions";
    private PermissionHelper permissionHelper;
    private static boolean isShowSettings = true;
    private boolean isRequiredCheck = false;
    private static final int REQUEST_PERMISSIONS_CODE = 101;
    private static final String PACKAGE_URL_SCHEME = "package:";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent() == null || !getIntent().hasExtra(EXTRA_PERMISSIONS)){
            throw new RuntimeException("PermissionsActivity需要使用静态startActivityForResult方法启动!");
        }
        setContentView(R.layout.activity_permission);
        permissionHelper = new PermissionHelper(this);
        isRequiredCheck = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isRequiredCheck){
            String[] permissions = getPermissions();
            if(permissionHelper.lackPermissions(permissions)){
                ActivityCompat.requestPermissions(PermissionActivity.this,permissions,REQUEST_PERMISSIONS_CODE);
            }else{
                setResult(PERMISSION_GRATED);
                finish();
            }
        }
    }
    private String[] getPermissions(){
        return getIntent().getStringArrayExtra(EXTRA_PERMISSIONS);
    }

    public static void startActivityForResult(Activity activity, int requestCode, String... permissions){
        startActivityForResult(activity,requestCode,true,permissions);
    }
    private static void startActivityForResult(Activity activity,int requestCode,boolean showSettings,String... permissions){
        Intent intent = new Intent(activity,PermissionActivity.class);
        intent.putExtra(EXTRA_PERMISSIONS,permissions);
        ActivityCompat.startActivityForResult(activity,intent,requestCode,null);
        isShowSettings = showSettings;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSIONS_CODE && hasAllPermissionGranted(grantResults)){
            setResult(PERMISSION_GRATED);
            finish();
        }else{
            showDenyDialog();
        }
    }
    private boolean hasAllPermissionGranted(int[] grantResults){
        for (int grantResult:grantResults){
            if(grantResult == PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }
    private void showDenyDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("权限申请提示");
        builder.setMessage("你拒绝了一些必需的应用权限，可以点击设置按钮开启权限");
        builder.setNegativeButton("残忍的拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setResult(PERMISSION_DENY);
                finish();
            }
        });
        builder.setPositiveButton("设置",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
                startActivity(intent);
            }
        });
    }
}
