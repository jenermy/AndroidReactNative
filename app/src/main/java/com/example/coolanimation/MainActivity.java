package com.example.coolanimation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener{
    private Camera mCamera;
    private TextureView cameraTextureView;
    private HandlerThread handlerThread;
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraTextureView = (TextureView)findViewById(R.id.cameraTextureView);
        cameraTextureView.setSurfaceTextureListener(this);
        handlerThread = new HandlerThread("ZOMBIE");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.i("wanlijun","chocolate chocolate");
            }
        };

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        Log.i("wanlijun","onSurfaceTextureAvailable");
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},101);
        }else{
            int cameraNum = Camera.getNumberOfCameras();
            if(cameraNum > 0){
                try {
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                    cameraTextureView.setRotation(90);
                    mCamera.setPreviewTexture(surfaceTexture);
//                    mCamera.setPreviewTexture(cameraTextureView.getSurfaceTexture());
//                    mCamera.setDisplayOrientation(90);
                    mCamera.startPreview();
                }catch (Exception e){
                    e.printStackTrace();
                    Log.i("wanlijun",e.toString());
                }
            }else{
                Toast.makeText(MainActivity.this,"camera obsolyo",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        Log.i("wanlijun","onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        Log.i("wanlijun","onSurfaceTextureDestroyed");
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 101){
            if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                int cameraNum = Camera.getNumberOfCameras();
                if(cameraNum > 0){
                    try {
                        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                        cameraTextureView.setRotation(90);
                        mCamera.setPreviewTexture(cameraTextureView.getSurfaceTexture());
//                    mCamera.setPreviewTexture(cameraTextureView.getSurfaceTexture());
//                    mCamera.setDisplayOrientation(90);
                        mCamera.startPreview();
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.i("wanlijun",e.toString());
                    }
                }else{
                    Toast.makeText(MainActivity.this,"camera obsolyo",Toast.LENGTH_SHORT).show();
                }
            }else{
                Snackbar.make(cameraTextureView,"权限被拒绝",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
            }
        }
    }
}
