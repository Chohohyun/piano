package com.example.ghgus.audioexample2;

import android.app.Activity;
import android.content.Context;
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
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class MusicActivity extends Activity{

    MediaController mc;
    MyVideoView vv;
    boolean valueReset = false;
    boolean endValue = false;
    int countvalue = 1;
    private int average=0;
    private Vector<Integer> vecList = new Vector<>();
    private Vector<Integer> vecList2 = new Vector<>();

    private final int mBufferSize = 1024;
    private final int mBytesPerElement = 2;
// 설정할 수 있는 sampleRate, AudioFormat, channelConfig 값들을 정의

    private final int[] mSampleRates = new int[] {44100, 22050, 11025, 8000};
    private final short[] mAudioFormats = new short[] {AudioFormat.ENCODING_PCM_16BIT, AudioFormat.ENCODING_PCM_8BIT};
    private final short[] mChannelConfigs = new short[] { AudioFormat.CHANNEL_IN_STEREO,AudioFormat.CHANNEL_IN_MONO};
    private double time=0.0;
    private String textpath="";

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
    private int musicNumber;
    private String mPath = null;
    private FileOutputStream fos = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_music);
        Intent intent = getIntent();
        musicNumber = intent.getIntExtra("musicNumber",0);
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        // 현재 볼륨 가져오기
        int volume = am.getStreamVolume(AudioManager.STREAM_MUSIC); //volume은 0~15 사이어야 함
        // volume이 0보다 클 때만 키우기 동작

            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND);

        Log.d("여기까진","된다");
        setting();

        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                time= mp.getDuration()/1000;
                //Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();
                endValue=true;
                valueReset=true;
                stopRecording();

                //playWaveFile();
                //if((Environment.getExternalStorageDirectory().getPath()+"/answerRecord.txt").length() == 0) {
                    //recordSoundWrite();
                //}
                //else{
                    fileCompare();
               // }
                Log.d("recordertest","successs");

                //Toast.makeText(getApplicationContext(),average,Toast.LENGTH_LONG).show();

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

        vv = (MyVideoView) findViewById(R.id.vv);
        mc= new MediaController(MusicActivity.this);
        String path="";
        switch (musicNumber){
            case 1:
                path = "android.resource://" + getPackageName() + "/" + R.raw.gom3;
                break;
            case 2:
                path = "android.resource://" + getPackageName() + "/" + R.raw.school;
                break;
            case 3:
                path = "android.resource://" + getPackageName() + "/" + R.raw.star;
        }


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
    private void recordSoundWrite() {
        //파일에 읽은 시계열 벡터를 차례대로 기록한다.
        String savePath = Environment.getExternalStorageDirectory().getPath();

        File file = new File(savePath+ "/answerRecord.txt");
        try{
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(vecList.size()+"\n");
            for (int i = 1 ; i< vecList.size() ; i++) {
                Log.v("vecList"+i , vecList.get(i)+"");
                fileWriter.write(vecList.get(i)+"\n");
            }
            fileWriter.close();
            Toast.makeText(getApplicationContext(),"file saved",Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(),"size"+vecList.size(),Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //FileOutputStream fos = new FileOutputStream();
        //기록 후, 유클리디안 거리 비교
    }


    private void fileCompare(){

        int[] index = new int[5000];
        int[] index2 = new int[5000];
        double success = 0.0;
        double total = 0.0;
        int musicCount = 0;
        File file = new File(textpath);
        try {
            String savePath = Environment.getExternalStorageDirectory().getPath();

            //FileReader fileReader = new FileReader(data);
            //BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = "";
            int sum = 0;


            File file2 = new File(savePath + "/answerRecord4.txt");

            FileWriter fileWriter = new FileWriter(file2);
            fileWriter.write(vecList.size() + "\n");
            for (int i = 1; i < vecList.size(); i++) {
                fileWriter.write(vecList.get(i) + "\n");
            }
            fileWriter.close();

            FileReader fileReader2 = new FileReader(file2);
            BufferedReader bufferedReader2 = new BufferedReader(fileReader2);

            Iterator iterator = vecList.iterator();
            String line2 = "";
            InputStream inputData = getResources().openRawResource(R.raw.originalgom3);
            if(musicNumber==2){
                inputData=getResources().openRawResource(R.raw.originalschool);
            }
            else if(musicNumber==3){
                inputData=getResources().openRawResource(R.raw.originalstar);
            }

            BufferedReader bufferedReader3= new BufferedReader(new InputStreamReader(inputData,"EUC_KR"));
            while(true){
                String string= bufferedReader3.readLine();
                    if(string != null){
                       index[musicCount]=Integer.parseInt(string);
                    }else{
                        break;
                    }
                    musicCount++;
                }

            int i = 0;
           /* while (((line = bufferedReader.readLine()) != null)) {
                index[musicCount] = Integer.parseInt(line);
                musicCount++;
            }*/
            musicCount = 0;
            while (((line2 = bufferedReader2.readLine()) != null)) {
                index2[musicCount] = Integer.parseInt(line2);
                musicCount++;
            }
        }
            catch(Exception e){

            }
            double original = (index[0]/time)*0.75;
            double play = (index2[0]/time)*0.75;
            int[] pianoFreq = {261,293,329,349,391,440,493,523,587,659,698,798,880,987};

            for(musicCount=1;musicCount<index.length;musicCount++){
                int idx = Arrays.binarySearch(pianoFreq,index[musicCount]);
                if(idx>=0){
                    total+=1;
                    double checkPlay=(double)(musicCount*index2[0])/(double)index[0];
                    int intCheck= (int)Math.round(checkPlay);
                    int c=0;
                    int check2=0;
                        while (c < play) {
                            if (c % 2 == 1) {
                                check2 += c;
                            } else {
                                check2 -= c;
                            }
                            if(intCheck+check2<1 || intCheck+check2>index2[0]){
                                total--;
                                break;
                            }
                            if (index[musicCount] == index2[intCheck + check2]) {

                                if (((double) c / (double) play) < 0.3) {
                                    success += 1;
                                } else if (((double) c / (double) play) < 0.6) {
                                    success += 0.9;
                                } else if (((double) c / (double) play) < 1) {
                                    success += 0.7;
                                }
                                break;
                            }
                            c++;
                        }


                }
            }
            Log.d("musicCount",index[1999]+"");
            average = (int)Math.round((success / total)*100);

            int pianoScore= 100-average;

           // Toast.makeText(getApplicationContext(),"t"+total+"s"+(int)success+"average"+average,Toast.LENGTH_SHORT).show();


        try{
        Intent intent3 = new Intent(MusicActivity.this, ResultActivity.class);
        intent3.putExtra("musicScore", average);
        startActivityForResult(intent3, 10);
        }
        catch(Exception e) {
        }
    }

    // 실제 녹음한 data를 file에 쓰는 함수

    private void writeAudioDataToFile() {
        if(valueReset==false) {
            String sd = Environment.getExternalStorageDirectory().getAbsolutePath();
            mPath = sd + "/record_audiorecord.pcm";// 녹음한 파일을 저장할 경로
            try {
                fos = new FileOutputStream(mPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        short sData[] = new short[mBufferSize];

        while (mIsRecording) {

            int read =0;
            mRecorder.read(sData, 0, mBufferSize);
            byte bData[] = short2byte(sData);
            // fos.write(bData, 0, mBufferSize * mBytesPerElement);
            //read=mRecorder.read(bData,0,mBufferSize * mBytesPerElement);
            try {
                fos.write(bData, 0, mBufferSize * mBytesPerElement);
                //fos.write(sData, 0, mBufferSize);
                // read=mRecorder.read(bData,0,mBufferSize * mBytesPerElement);
            }
            catch (Exception e){

            }
            //read = mRecorder.read(bData,0,mBufferSize * mBytesPerElement);


           // if(read>0){

                int length= bData.length/2;
                int sampleSize = 8192;
                while(sampleSize>length) sampleSize = sampleSize >> 1;

                FrequencyCalculator frequencyCalculator = new FrequencyCalculator(sampleSize);
                frequencyCalculator.feedData(bData,length);

                double max = resizeNumber(frequencyCalculator.getFreq());
                int[] data = {0,130,146,164,174,195,220,246,261,293,329,349,391,440,493,523,587,659,698,798,880,987};
                int target = (int)max; //찾을값을 지정
                int near = 0 ; //가까운값을 저장할 변수
                int min = Integer.MAX_VALUE ; //차이값의 절대값의 최소값을 저장할변수
                //초기값은 정수형에서 최대값;
                //[2] 처리
                for (int i = 0; i < data.length; i++) {
                    int a = Math.abs((data[i] - target)); //Math.abs(값) : 절대값 구하는 함수
                    //int a = Abs((data[i] - target));
                    if(min > a){
                        min =  a; //최소값 알고리즘
                        near = data[i]; //최종적으로 가까운값
                    }
                }
              //  double[] absNormalizedSignal = calculateFFT(bData);

               // if(max<1000) {
                    vecList.add(near);
               // }

         //   }
        }
            if(endValue ==true) {
                try {
                    fos.close();
                }
                catch (Exception e){
                }
            }

    }
    public double resizeNumber(double value) {
        int temp = (int) (value * 10.0d);
        return temp / 10.0d;
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
            //Toast.makeText(getApplicationContext(),"touch",Toast.LENGTH_LONG).show();
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
            if(requestCode==1){
            int value=data.getIntExtra("value",0);
            //Toast.makeText(getApplicationContext(),"상태"+value,Toast.LENGTH_SHORT).show();
            if(value==1){
                vv.start();
                mIsRecording=true;
                valueReset=true;
                mRecordingThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        writeAudioDataToFile();
                    }
                }, "AudioRecorder Thread");
                mRecordingThread.start();
            }
            else if(value==2){
                stopRecording();
                setting();
            }
            else if(value==3) {
                endValue=true;
                valueReset=true;
                stopRecording();
                finish();
            }

            }
            else if(requestCode==10){
                finish();
            }
        }

    }
    /*public double[] calculateFFT(byte[] bData)
    {


        double sum=0.0;
        int avg=0;
        double mMaxFFTSample;
        int count=0;
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

            sum+=absSignal[i];
            count++;
        }
        avg=(int)sum/count;
        //vecList.add(avg);

        Log.d(mMaxFFTSample+"","maxfft");
        return absSignal;
    }*/




}

