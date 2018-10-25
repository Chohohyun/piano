package com.example.ghgus.audioexample2;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class MusicActivity extends Activity{

    MediaController mc;
    VideoView vv;
    boolean valueReset = false;
    int countvalue = 1;
    private Map<String,String> timeList = new TreeMap<String, String>();
    private final int mBufferSize = 1024;
    private final int mBytesPerElement = 2;
    JSONObject jmusic = new JSONObject();
// 설정할 수 있는 sampleRate, AudioFormat, channelConfig 값들을 정의

    private final int[] mSampleRates = new int[] {44100, 22050, 11025, 8000};
    private final short[] mAudioFormats = new short[] {AudioFormat.ENCODING_PCM_16BIT, AudioFormat.ENCODING_PCM_8BIT};
    private final short[] mChannelConfigs = new short[] {AudioFormat.CHANNEL_IN_STEREO, AudioFormat.CHANNEL_IN_MONO};



    // 위의 값들 중 실제 녹음 및 재생 시 선택된 설정값들을 저장
    private String result="";
    private int mSampleRate;
    private short mAudioFormat;
    private short mChannelConfig;
    private AudioRecord mRecorder = null;
    private Thread mRecordingThread = null;
    private Button mRecordBtn, mPlayBtn, mMusicBtn;
    private TextView tv;
    private boolean mIsRecording = false;           // 녹음 중인지에 대한 상태값
    private String sd = Environment.getExternalStorageDirectory().getAbsolutePath();

    private String mPath = sd + "/record_audiorecord.pcm";// 녹음한 파일을 저장할 경로
    private FileOutputStream fos = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_music);
        Log.d("여기까진","된다");
        setting();

        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();
                valueReset=true;
                stopRecording();
                playWaveFile();
            }
        });
    }//확인 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
       // Intent intent = new Intent();
     //   intent.putExtra("result", "Close Popup");
     //   setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }
    public void setting(){

        vv = (VideoView) findViewById(R.id.vv);
        mc= new MediaController(MusicActivity.this);
        String path="android.resource://"+getPackageName()+"/"+R.raw.piano;
        Uri uri = Uri.parse(path);
        mc.setVisibility(View.GONE);
        vv.setMediaController(mc);
        vv.setVideoURI(uri);
        vv.requestFocus();
        vv.start();
        startRecording();
    }
    private void startRecording() {
        mRecorder = findAudioRecord();
        mRecorder.startRecording();
        mIsRecording = true;
        mRecordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        mRecordingThread.start();
    }
    // 녹음을 하기 위한 sampleRate, audioFormat, channelConfig 값들을 설정
    private AudioRecord findAudioRecord() {
        for (int rate : mSampleRates) {
            for (short format : mAudioFormats) {
                for (short channel : mChannelConfigs) {
                    try {
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channel, format);
                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            mSampleRate = rate;
                            mAudioFormat = format;
                            mChannelConfig = channel;
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, mSampleRate, mChannelConfig, mAudioFormat, bufferSize);
                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                                return recorder;    // 적당한 설정값들로 생성된 Recorder 반환
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;                     // 적당한 설정값들을 찾지 못한 경우 Recorder를 찾지 못하고 null 반환
    }
    // 실제 녹음한 data를 file에 쓰는 함수
    private void writeAudioDataToFile() {

        short sData[] = new short[mBufferSize];
        try {

            fos = new FileOutputStream(mPath);
            while (mIsRecording && valueReset == false) {

                int read =0;
                mRecorder.read(sData, 0, mBufferSize);
                byte bData[] = short2byte(sData);
               // fos.write(bData, 0, mBufferSize * mBytesPerElement);
                //read=mRecorder.read(bData,0,mBufferSize * mBytesPerElement);

                 fos.write(bData, 0, mBufferSize * mBytesPerElement);
                read=mRecorder.read(bData,0,mBufferSize * mBytesPerElement);


               /* if(read>0) {
                    double[] absNormalizedSignal = calculateFFT(bData);
                    if(countvalue==0){
                        Log.d("jsontest","success");
                    }
                    for(int i=0;i<absNormalizedSignal.length;i++){
                        //if(max<absNormalizedSignal[i]){
                        //  max=absNormalizedSignal[i];
                        //}
                        try{
                            jmusic.put(String.valueOf(countvalue),String.valueOf(absNormalizedSignal[i]));
                        }
                        catch (Exception e){

                        }
                        timeList.put(String.valueOf(countvalue),String.valueOf(absNormalizedSignal[i]));
                        countvalue++;
                    }
                    //  for(int i=0;i<absNormalizedSignal.length;i++) {
                    //    Log.d(String.valueOf((int)(absNormalizedSignal[i])),"hihihihi");
                    //}
                    Log.d(String.valueOf(absNormalizedSignal.length),"lengthsize");
                }*/
            }
            if(valueReset ==true) {
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private byte[] short2byte(short[] sData) {

        int shortArrsize = sData.length;

        byte[] bytes = new byte[shortArrsize * 2];

        for (int i = 0; i < shortArrsize; i++) {

            bytes[i * 2] = (byte) (sData[i] & 0x00FF);

            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);

            sData[i] = 0;

        }

        return bytes;

    }

    // 녹음을 중지하는 함수
    private void stopRecording() {
        if (mRecorder != null && valueReset==true) {
            mIsRecording = false;
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            mRecordingThread = null;
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        if ((event.getAction() == MotionEvent.ACTION_DOWN)) {
            Toast.makeText(getApplicationContext(),"touch",Toast.LENGTH_LONG).show();
           // mc.hide();
            vv.pause();
            popUpMenu();
            mIsRecording=false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
    //화면 클릭시 일시정지 되면서 팝업메뉴 제공
    public void popUpMenu(){
        Intent intent = new Intent(MusicActivity.this,PauseActivity.class);
        startActivityForResult(intent,1);
    }
    //pause 상태에서 3가지의 버튼 클릭 받았을 때 각각 실행
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode==RESULT_OK){
            int value=data.getIntExtra("value",0);
            Toast.makeText(getApplicationContext(),"상태"+value,Toast.LENGTH_SHORT).show();
            if(value==1){
                vv.start();
                mIsRecording=true;
            }
            else if(value==2){
                stopRecording();
                mIsRecording = false;
                valueReset=true;
                setting();
            }
            else if(value==3){
                stopRecording();
                mIsRecording = false;
                valueReset=true;
                Intent intent= new Intent(MusicActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }

    }
    public double[] calculateFFT(byte[] bData)
    {
        double mMaxFFTSample;

        double temp;
        Complex[] y;
        Complex[] complexSignal = new Complex[mBufferSize];
        double[] absSignal = new double[mBufferSize/2];

        for(int i = 0; i < mBufferSize; i++){
            temp = (double)((bData[2*i] & 0xFF) | (bData[2*i+1] << 8)) / 32768.0F;
            complexSignal[i] = new Complex(temp,0.0);
        }

        y = FFT.fft(complexSignal); // --> Here I use FFT class

        mMaxFFTSample = 0.0;
        int mPeakPos = 0;
        for(int i = 0; i < (mBufferSize/2); i++)
        {
            absSignal[i] = Math.sqrt(Math.pow(y[i].re(), 2) + Math.pow(y[i].im(), 2));
            if(absSignal[i] > mMaxFFTSample)
            {
                mMaxFFTSample = absSignal[i];
                mPeakPos = i;
            }
        }
        Log.d(String.valueOf(mMaxFFTSample),"maxfft");
        return absSignal;
    }
    // 녹음할 때 설정했던 값과 동일한 설정값들로 해당 파일을 재생하는 함수
    private void playWaveFile() {

        int minBufferSize = AudioTrack.getMinBufferSize(mSampleRate, mChannelConfig, mAudioFormat);
        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, mSampleRate, mChannelConfig, mAudioFormat, minBufferSize, AudioTrack.MODE_STREAM);
        int count = 0;
        byte[] data = new byte[mBufferSize];

        try {
            FileInputStream fis = new FileInputStream(mPath);
            DataInputStream dis = new DataInputStream(fis);
            audioTrack.play();
            while ((count = dis.read(data, 0, mBufferSize)) > -1) {
                audioTrack.write(data, 0, count);
            }
            audioTrack.stop();
            audioTrack.release();
            dis.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

