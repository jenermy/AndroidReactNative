package com.example.coolanimation;

import android.graphics.Camera;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.nio.ByteBuffer;

public class MediaExtractorActivity extends AppCompatActivity{
    private Button playBtn;
    private String filePath = Environment.getExternalStorageDirectory() +"/DCIM/Camera/pink.mp4";
    private boolean isPlaying = false;
    private VideoThread videoThread;
    private SurfaceView vedioSv;
    private SurfaceHolder surfaceHolder;
    private SurfaceHolder.Callback callback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_extractor);
        playBtn = (Button)findViewById(R.id.playBtn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPlaying){
                    play();
                }else{
                    stop();
                }
            }
        });
        callback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        };
        vedioSv = (SurfaceView)findViewById(R.id.vedioSv);
        surfaceHolder = vedioSv.getHolder();
        surfaceHolder.addCallback(callback);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void play(){
        isPlaying = true;
        if(videoThread == null){
            videoThread = new VideoThread();
            videoThread.start();
        }
    }
    private void stop(){
        isPlaying = false;
    }

    //播放视频
    class VideoThread extends Thread{
        @Override
        public void run() {
            MediaExtractor extractor = new MediaExtractor();
            MediaCodec mediaCodec = null;
            try {
                extractor.setDataSource(filePath);
            }catch (Exception e){
                e.printStackTrace();
            }
            int trackIndex = -1;
            for(int i=0;i<extractor.getTrackCount();i++){
                MediaFormat format = extractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if(mime.startsWith("video/")){
                    trackIndex = i;
                    break;
                }
            }
            if(trackIndex >= 0){
                MediaFormat format = extractor.getTrackFormat(trackIndex);
                int width = format.getInteger(MediaFormat.KEY_WIDTH);
                int height = format.getInteger(MediaFormat.KEY_HEIGHT);
                long time = format.getLong(MediaFormat.KEY_DURATION);
                extractor.selectTrack(trackIndex);
                try {
                    mediaCodec = MediaCodec.createDecoderByType(format.getString(MediaFormat.KEY_MIME));
                    mediaCodec.configure(format, surfaceHolder.getSurface(), null, 0);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.i("wanlijun",e.toString());
                }
                if(mediaCodec == null)return;
                mediaCodec.start();
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                ByteBuffer[] byteBuffers = mediaCodec.getInputBuffers();
                boolean isVedioEos = false;
                long startMs = System.currentTimeMillis();
                while (!Thread.interrupted()){
                    if(!isPlaying)continue;
                    if(!isVedioEos){
                        isVedioEos = putBufferToCoder(extractor,mediaCodec,byteBuffers);
                    }
                    int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo,1000);
                    switch (outputBufferIndex){
                        case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                            Log.i("wanlijun","INFO_OUTPUT_FORMAT_CHANGED");
                            break;
                        case MediaCodec.INFO_TRY_AGAIN_LATER:
                            Log.i("wanlijun","INFO_TRY_AGAIN_LATER");
                            break;
                        case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                            Log.i("wanlijun","INFO_OUTPUT_BUFFERS_CHANGED");
                            break;
                            default:
                                Log.i("wanlijun","default");
                                mediaCodec.releaseOutputBuffer(outputBufferIndex,true);
                                break;
                    }
                    if((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0){
                        Log.i("wanlijun","buffer stream end");
                        break;
                    }
                }
                mediaCodec.stop();
                mediaCodec.release();
                extractor.release();
            }
        }
    }
    class AudioThread extends Thread{
        @Override
        public void run() {
            super.run();
        }
    }

    private boolean putBufferToCoder(MediaExtractor extractor,MediaCodec codec,ByteBuffer[] byteBuffers){
        boolean isMediaEos = false;
        int inputBufferIndex = codec.dequeueInputBuffer(1000);
        if(inputBufferIndex >= 0){
            ByteBuffer byteBuffer = byteBuffers[inputBufferIndex];
            int sampleSize = extractor.readSampleData(byteBuffer,0);
            if(sampleSize < 0){
                codec.queueInputBuffer(inputBufferIndex,0,0,0,MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                isMediaEos = true;
            }else{
                codec.queueInputBuffer(inputBufferIndex,0,sampleSize,extractor.getSampleTime(),0);
                extractor.advance();
            }
        }
        return  isMediaEos;
    }
}
