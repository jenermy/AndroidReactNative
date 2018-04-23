package com.example.coolanimation;

//import android.icu.text.IDNA;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.net.rtp.AudioCodec;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.util.ArrayList;

public class ChangeToneActivity extends AppCompatActivity {
    private String videoPath = Environment.getExternalStorageDirectory() +"/DCIM/Camera/pink.mp4";
    private String audioPath = Environment.getExternalStorageDirectory() + "/MIUI/music/mp3/only.m4a";
    private String tempPath = Environment.getExternalStorageDirectory() + "/MIUI/music/mp3/code.acc";
    private Button changeToneBtn;
    public static final String COMPRESSED_AUDIO_FILE_MIME_TYPE = "audio/mp4a-latm";
    public static final int COMPRESSED_AUDIO_FILE_BIT_RATE = 64000; // 64kbps
    public static final int SAMPLING_RATE = 48000;
    private MediaExtractor mp3Extractor;
    private MediaCodec mp3Decode;
    private MediaCodec accEncode;
    private ByteBuffer[] decodeOutput;
    private ByteBuffer[] decodeInput;
    private MediaCodec.BufferInfo decodeInfo;
    private ByteBuffer[] encodeOutput;
    private ByteBuffer[] encodeInput;
    private MediaCodec.BufferInfo encodeInfo;
    private FileOutputStream fos;
    private BufferedOutputStream bos;
    private Button mp3toaccBtn;
    private ArrayList<byte[]> chunkPCMDataContainer;//PCM数据块容器
    private OnCompleteListener onCompleteListener = new OnCompleteListener() {
        @Override
        public void completed() {
            realse();
            Toast.makeText(ChangeToneActivity.this,"编码成功",Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_tone);
        changeToneBtn = (Button)findViewById(R.id.changeToneBtn);
        changeToneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    muxAudioAndVideo();
                }catch (Exception e){
                    e.printStackTrace();
                    Log.i("wanlijun",e.toString());
                }
            }
        });
        mp3toaccBtn = (Button)findViewById(R.id.mp3toaccBtn);
        mp3toaccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAsync();
            }
        });
        try {
            fos = new FileOutputStream(new File(tempPath));
            bos = new BufferedOutputStream(fos,500*1024);
            File file = new File(audioPath);
        }catch (Exception e){
            e.printStackTrace();
        }
        chunkPCMDataContainer= new ArrayList<>();
        initMediaDecode();
        initMediaEncode();
    }
    private void muxAudioAndVideo() throws IOException{
        MediaMuxer mediaMuxer = new MediaMuxer(tempPath,MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        //音频
        MediaExtractor audioExtractor = new MediaExtractor();
        audioExtractor.setDataSource(audioPath);
        int audioTrack = -1;
        for(int i=0;i<audioExtractor.getTrackCount();i++){
            MediaFormat outputFormat = MediaFormat.createAudioFormat(COMPRESSED_AUDIO_FILE_MIME_TYPE,SAMPLING_RATE, 1);
            outputFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            outputFormat.setInteger(MediaFormat.KEY_BIT_RATE, COMPRESSED_AUDIO_FILE_BIT_RATE);
            outputFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 16384);
            MediaFormat format = audioExtractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if(mime.startsWith("audio/")){
                audioExtractor.selectTrack(i);
                audioTrack = mediaMuxer.addTrack(outputFormat);
                break;
            }
        }
        //视频
        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(videoPath);
        int vedioTrack = -1;
        for(int i=0;i<extractor.getTrackCount();i++){
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if(mime.startsWith("video/")){
                extractor.selectTrack(i);
                vedioTrack = mediaMuxer.addTrack(format);
                break;
            }
        }

        mediaMuxer.start();
        ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
        //视频封装
        if(vedioTrack >= 0){
            MediaCodec.BufferInfo bufferInfoVideo = new MediaCodec.BufferInfo();
            bufferInfoVideo.presentationTimeUs = 0;
            while (true){
                int sampleSize = extractor.readSampleData(byteBuffer,0);
                if(sampleSize < 0)break;
                bufferInfoVideo.offset = 0;
                bufferInfoVideo.size = sampleSize;
                bufferInfoVideo.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
                bufferInfoVideo.presentationTimeUs =extractor.getSampleTime();
                mediaMuxer.writeSampleData(vedioTrack,byteBuffer,bufferInfoVideo);
                extractor.advance(); //移动到下一帧
            }
        }
        //音频封装
        if(audioTrack >= 0){
            MediaCodec.BufferInfo bufferInfoAudio = new MediaCodec.BufferInfo();
            bufferInfoAudio.presentationTimeUs = 0;
            while (true){
                int sampleSize = audioExtractor.readSampleData(byteBuffer,0);
                if(sampleSize < 0)break;
                bufferInfoAudio.offset = 0;
                bufferInfoAudio.size = sampleSize;
                bufferInfoAudio.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
                bufferInfoAudio.presentationTimeUs = audioExtractor.getSampleTime();
                mediaMuxer.writeSampleData(audioTrack,byteBuffer,bufferInfoAudio);
                audioExtractor.advance(); //移动到下一帧
            }
        }
        extractor.release();
        audioExtractor.release();
        mediaMuxer.stop();
        mediaMuxer.release();
    }

    //初始化解码器
    private void initMediaDecode(){
        mp3Extractor = new MediaExtractor();
        try {
            mp3Extractor.setDataSource(audioPath);
            for(int i=0;i<mp3Extractor.getTrackCount();i++){
                MediaFormat format = mp3Extractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if(mime.startsWith("audio/")){
                    mp3Extractor.selectTrack(i);
                    mp3Decode = MediaCodec.createDecoderByType(mime);
                    mp3Decode.configure(format,null,null,0);
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(mp3Decode == null)return;
        mp3Decode.start();
        decodeInput = mp3Decode.getInputBuffers();
        decodeOutput = mp3Decode.getOutputBuffers();
        decodeInfo = new MediaCodec.BufferInfo();
    }

    //初始化ACC编码器
    private void initMediaEncode(){
        try {
            MediaFormat mediaFormat = MediaFormat.createAudioFormat(COMPRESSED_AUDIO_FILE_MIME_TYPE,44100,2);
            mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE,MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE,256000);
            mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE,500*1024);
            accEncode = MediaCodec.createEncoderByType(COMPRESSED_AUDIO_FILE_MIME_TYPE);
            accEncode.configure(mediaFormat,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(accEncode == null)return;
        accEncode.start();
        encodeInput = accEncode.getInputBuffers();
        encodeOutput = accEncode.getOutputBuffers();
        encodeInfo = new MediaCodec.BufferInfo();
    }
    //解码
    private void srcAudioFormatToPCM(){
        for(int i=0;i<decodeInput.length -1;i++) {
            //获取可用的inputBuffer -1代表一直等待，0表示不等待 建议-1,避免丢帧
            int inputIndex = mp3Decode.dequeueInputBuffer(-1);
            Log.i("wanlijun","inputIndex="+inputIndex);
            if (inputIndex < 0) {
                codeOver = true;
                return;
            }
            ByteBuffer inputBuffer = decodeInput[inputIndex];
            inputBuffer.clear();
            int sampleSize = mp3Extractor.readSampleData(inputBuffer, 0);
            Log.i("wanlijun","sampleSize="+sampleSize);
            if (sampleSize >= 0) {
                mp3Decode.queueInputBuffer(inputIndex, 0, sampleSize, 0, 0);
                mp3Extractor.advance();
            } else {
                codeOver = true;
            }
        }
            int outputIndex = mp3Decode.dequeueOutputBuffer(decodeInfo,10000);
            ByteBuffer outputBuffer;
            byte[] chunkPCM;
        Log.i("wanlijun","outputIndex="+outputIndex);
            while (outputIndex >= 0){
                outputBuffer = decodeOutput[outputIndex];
                chunkPCM = new byte[decodeInfo.size];
                outputBuffer.get(chunkPCM);
                outputBuffer.clear();
                putPCMData(chunkPCM);
                mp3Decode.releaseOutputBuffer(outputIndex,false);
                outputIndex = mp3Decode.dequeueOutputBuffer(decodeInfo,10000);
                Log.i("wanlijun","outputIndex="+outputIndex);
            }
    }
    //编码
    private void dstAudioFormatFromPCM(){
        int inputIndex = -1;
        int outputIndex;
        ByteBuffer inputBuffer;
        ByteBuffer outputBuffer;
        int outBitSize;
        int outPacketSize;
        byte[] chunkAcc;
        byte[] chunkPCM;
        for(int i=0;i<encodeInput.length -1;i++){
            chunkPCM = getPCMData();
            if(chunkPCM == null)return;
            inputIndex = accEncode.dequeueInputBuffer(-1);
            Log.i("wanlijun","inputIndex="+inputIndex);
            inputBuffer = encodeInput[inputIndex];
            inputBuffer.clear();
            inputBuffer.limit(chunkPCM.length);
            inputBuffer.put(chunkPCM);
            accEncode.queueInputBuffer(inputIndex,0,chunkPCM.length,0,0);
        }
        outputIndex = accEncode.dequeueOutputBuffer(encodeInfo,10000);
        Log.i("wanlijun","outputIndex="+outputIndex);
        while (outputIndex >= 0){
            outBitSize = encodeInfo.size;
            outPacketSize = outBitSize + 7;
            outputBuffer = encodeOutput[outputIndex];
            outputBuffer.position(encodeInfo.offset);
            outputBuffer.limit(encodeInfo.offset + outBitSize);
            chunkAcc = new byte[outPacketSize];
            addADTSToPacket(chunkAcc,outPacketSize);
            outputBuffer.get(chunkAcc,7,outBitSize);
            outputBuffer.position(encodeInfo.offset);
            try {
                bos.write(chunkAcc,0,chunkAcc.length);
            }catch (Exception e){
                e.printStackTrace();
            }
            accEncode.releaseOutputBuffer(outputIndex,false);
            outputIndex = accEncode.dequeueOutputBuffer(encodeInfo,10000);
            Log.i("wanlijun","outputIndex="+outputIndex);
        }
    }

    private void addADTSToPacket(byte[] packet,int packetLen){
        int profile = 2; // AAC LC
        int freqIdx = 4; // 44.1KHz
        int chanCfg = 2; // CPE
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }

    /**
     * 初始化MPEG编码器
     */
    private void initMPEGMediaEncode() {

    }

    private boolean codeOver = false;
    /**
     * 开始转码
     * 音频数据先解码成PCM  PCM数据在编码成想要得到的音频格式
     * mp3->PCM->aac
     */
    public void startAsync() {
        new Thread(new DecodeRunnable()).start();
        new Thread(new EncodeRunnable()).start();

    }
    /**
     * 将PCM数据存入{@link #chunkPCMDataContainer}
     * @param pcmChunk PCM数据块
     */
    private void putPCMData(byte[] pcmChunk) {
        synchronized (AudioCodec.class) {//记得加锁
            chunkPCMDataContainer.add(pcmChunk);
        }
    }

    /**
     * 在Container中{@link #chunkPCMDataContainer}取出PCM数据
     * @return PCM数据块
     */
    private byte[] getPCMData() {
        synchronized (AudioCodec.class) {//记得加锁
            if (chunkPCMDataContainer.isEmpty()) {
                return null;
            }

            byte[] pcmChunk = chunkPCMDataContainer.get(0);//每次取出index 0 的数据
            chunkPCMDataContainer.remove(pcmChunk);//取出后将此数据remove掉 既能保证PCM数据块的取出顺序 又能及时释放内存
            return pcmChunk;
        }
    }

    private void realse(){
        try {
            if(bos != null){
                bos.flush();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(bos != null){
                    bos.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                bos = null;
            }
        }
        try {
           if(fos != null){
               fos.close();
           }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            fos = null;
        }
        if(accEncode != null){
            accEncode.stop();
            accEncode.release();
            accEncode = null;
        }
        if(mp3Decode != null){
            mp3Decode.stop();
            mp3Decode.release();
            mp3Decode = null;
        }
        if(mp3Extractor != null){
            mp3Extractor.release();
            mp3Extractor = null;
        }

    }
    class EncodeRunnable implements Runnable{
        @Override
        public void run() {
            long t=System.currentTimeMillis();
            while (!codeOver || !chunkPCMDataContainer.isEmpty()) {
                dstAudioFormatFromPCM();
            }
            if (onCompleteListener != null) {
                onCompleteListener.completed();
            }
        }
    }
    class DecodeRunnable implements Runnable{
        @Override
        public void run() {
            while (!codeOver){
                srcAudioFormatToPCM();
            }
        }
    }

    /**
     * 转码完成回调接口
     */
    public interface OnCompleteListener{
        void completed();
    }
    /**
     * 设置转码完成监听器
     * @param onCompleteListener
     */
    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener=onCompleteListener;
    }

}
