package com.example.coolanimation;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * @author wanlijun
 * @description
 * @time 2018/4/11 15:24
 */

public class PermissionHelper {
    private Context mContext;
    public PermissionHelper(Context context){
        this.mContext = context.getApplicationContext();
    }

    public boolean lackPermissions(String... permissions){
        for(String permission:permissions){
            if(lackPermission(permission)){
                return true;
            }
        }
        return false;
    }
    private boolean lackPermission(String permission){
       return ContextCompat.checkSelfPermission(mContext,permission) == PackageManager.PERMISSION_GRANTED;
    }
}
