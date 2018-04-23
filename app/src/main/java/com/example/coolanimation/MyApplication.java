package com.example.coolanimation;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;


/**
 * @author wanlijun
 * @description
 * @time 2018/4/20 11:48
 */

public class MyApplication extends Application implements ReactApplication{
    public static final String JS_BUNDLE_LOCAL_PATH = Environment.getExternalStorageDirectory().toString() + File.separator + "index.android.bundle";
    private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
        @Override
        public boolean getUseDeveloperSupport() {
            return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                    new MainReactPackage()
                    //将我们创建的包管理器给添加进来
            );
        }

        @Nullable
        @Override
        protected String getJSBundleFile() {
            File file = new File(JS_BUNDLE_LOCAL_PATH);
            if(file != null && file.exists()){
                Log.i("wanlijun",JS_BUNDLE_LOCAL_PATH);
                return  JS_BUNDLE_LOCAL_PATH;
            }else {
//                Log.i("wanlijun",super.getJSBundleFile());
                return super.getJSBundleFile();
            }
        }
    };
    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SoLoader.init(getApplicationContext(),false);
    }
}
