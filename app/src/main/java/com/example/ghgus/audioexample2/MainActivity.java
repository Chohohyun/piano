package com.example.ghgus.audioexample2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity{

    private final int mBufferSize = 1024;
    private final int mBytesPerElement = 2;
// 설정할 수 있는 sampleRate, AudioFormat, channelConfig 값들을 정의

    private final int[] mSampleRates = new int[]{44100, 22050, 11025, 8000};
    private final short[] mAudioFormats = new short[]{AudioFormat.ENCODING_PCM_16BIT, AudioFormat.ENCODING_PCM_8BIT};
    private final short[] mChannelConfigs = new short[]{AudioFormat.CHANNEL_IN_STEREO, AudioFormat.CHANNEL_IN_MONO};

// 위의 값들 중 실제 녹음 및 재생 시 선택된 설정값들을 저장
    private String result="";
    private int mSampleRate;
    private short mAudioFormat;
    private short mChannelConfig;
    private AudioRecord mRecorder = null;
    private Thread mRecordingThread = null;
    private Button mRecordBtn, mPlayBtn;
    private TextView tv;
    private boolean mIsRecording = false;           // 녹음 중인지에 대한 상태값
    private String mPath = "";// 녹음한 파일을 저장할 경로

    //Thread th = new Thread(MainActivity.this);
    private View.OnClickListener btnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
// 녹음 버튼일 경우 녹음 중이지 않을 때는 녹음 시작, 녹음 중일 때는 녹음 중지로 텍스트 변경
                case R.id.start:
                    if (mIsRecording == false) {
                        startRecording();
                        mIsRecording = true;
                        mRecordBtn.setText("Stop Recording");
                    } else {
                        stopRecording();
                        mIsRecording = false;
                        mRecordBtn.setText("Start Recording");
                    }
                    break;

                case R.id.play:

// 녹음 파일이 없는 상태에서 재생 버튼 클릭 시, 우선 녹음부터 하도록 Toast 표시
                    if (mPath.length() == 0 || mIsRecording) {
                        //th.start();
                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        JSONObject jobj = new JSONObject();
                        JSONObject jmusic = new JSONObject();
                        JSONObject jmusic2 = new JSONObject();
                        OkHttpClient client = new OkHttpClient();
                        try {

                            jmusic.put("0:00:1", "4");
                            jmusic2.put("0:00:1", "4");
                            jobj.put("music", jmusic);
                            jobj.put("music2", jmusic2);
                        }
                        catch(JSONException e){

                        }
                        /*HttpUrl thhpUrl = new HttpUrl.Builder()
                                .scheme("http")
                                .host("52.78.60.235:9090")
                                .addPathSegment("piano/score/a")
                                .build();*/

                        RequestBody requestBody = RequestBody.create(
                                (JSON) , jobj.toString());

                        Request request = new Request.Builder()
                                .url("http://52.78.60.235:9090/piano/score/a")
                                .post(requestBody)
                                .addHeader("content-type","application/json; charset=utf-8")
                                .build();

                        client.newCall(request).enqueue(updateUserInfoCallback);



                        Toast.makeText(MainActivity.this, "Please record, first.", Toast.LENGTH_SHORT).show();
                        return;
                    }

// 녹음된 파일이 있는 경우 해당 파일 재생
                    playWaveFile();
                    break;
            }
            }

    };
    private Callback updateUserInfoCallback = new Callback() {
        @Override
        public void onFailure(Request request, IOException e) {
            Log.d("Testis", "Error message:" + e.getMessage());
        }

        @Override
        public void onResponse(Response response) throws IOException {
            final String responseData = response.body().string();
            Log.d("Testis", "responseData" + responseData);

        }
    };


    @Override
// Layout을 연결하고 각 Button의 OnClickListener를 연결
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textView);
        mRecordBtn = (Button) findViewById(R.id.start);
        mPlayBtn = (Button) findViewById(R.id.play);
        mRecordBtn.setOnClickListener(btnClick);
        mPlayBtn.setOnClickListener(btnClick);

    }


 /*   @Override
    public void run(){
        try {
                String request = "http://52.78.60.235:9090/piano/score/a";
                Log.d("Testisgood3", "된다");
                URL url = new URL("http://52.78.60.235:9090/piano/score/a");
                BufferedReader reader = null;
                HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();

                InputStream is;

                JSONObject jobj = new JSONObject();
                JSONObject jmusic = new JSONObject();
                JSONObject jmusic2 = new JSONObject();

                jmusic.put("0:00:1", "4");
                jmusic2.put("0:00:1", "4");
                jobj.put("music", jmusic);
                jobj.put("music2", jmusic2);

                httpCon.setRequestMethod("POST");
                //httpCon.setRequestProperty("Accept-Charset","UTF-8");
                httpCon.setRequestProperty("Content-type", "application/json");
                //httpCon.setRequestProperty("Accept","application/json");

                httpCon.setUseCaches(false);
                httpCon.setConnectTimeout(3000);
                httpCon.setReadTimeout(3000);

                Log.d("Testisgood4", jobj.toString());

                httpCon.setDoOutput(true);

                Log.d("Testisgood4", "5번째됩니다.");
                //httpCon.setDoInput(true);

                Log.d("Testisgood4", "6번째됩니다.");
                //httpCon.connect();

                Log.d("Testisgood4", "7번째됩니다.");
                OutputStream os = null;
                os = httpCon.getOutputStream();

                Log.d("Testisgood4", "8번째됩니다.");
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));

                Log.d("Testisgood4", "9번째됩니다.");
                writer.write(jobj.toString());

                Log.d("Testisgood4", "10번째됩니다.");
                writer.flush();
                writer.close();

                is = httpCon.getInputStream();
                if (is != null) {
                    reader = new BufferedReader(new InputStreamReader(is));
                    String a;
                    while ((a = reader.readLine()) != null) {
                        result += a;
                    }
                } else {
                    result = "Did not work!";
                }

        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch(Exception e){
            Log.d("InputStream",e.getLocalizedMessage());
        }

            tv.setText(result);
        th.stop();


    }*/

    // 녹음을 수행할 Thread를 생성하여 녹음을 수행하는 함수
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

   /* public static int calculate(int mSampleRate,short[]sData){

        int numSamples = sData.length;
        int numCrossing = 0;
        for (int p = 0; p < numSamples-1; p++)
        {
            if ((sData[p] > 0 && sData[p + 1] <= 0) ||
                    (sData[p] < 0 && sData[p + 1] >= 0))
            {
                numCrossing++;
            }
        }

        float numSecondsRecorded = (float)numSamples/(float)mSampleRate;
        float numCycles = numCrossing/2;
        float frequency = numCycles/numSecondsRecorded;

        return (int)frequency;
    }*/

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

    // 실제 녹음한 data를 file에 쓰는 함수
    private void writeAudioDataToFile() {
        String sd = Environment.getExternalStorageDirectory().getAbsolutePath();
        mPath = sd + "/record_audiorecord.pcm";
        short sData[] = new short[mBufferSize];
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(mPath);
            while (mIsRecording) {
                int read =0;
                mRecorder.read(sData, 0, mBufferSize);
                byte bData[] = short2byte(sData);
                fos.write(bData, 0, mBufferSize * mBytesPerElement);
                read=mRecorder.read(bData,0,mBufferSize * mBytesPerElement);

                    Log.d(String.valueOf(bData), "bData");

                if(read>0) {
                    double[] absNormalizedSignal = calculateFFT(bData);
                  //  for(int i=0;i<absNormalizedSignal.length;i++) {
                    //    Log.d(String.valueOf((int)(absNormalizedSignal[i])),"hihihihi");
                    //}
                    Log.d(String.valueOf(absNormalizedSignal.length),"lengthsize");
                }
            }

            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


// short array형태의 data를 byte array형태로 변환하여 반환하는 함수
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
        if (mRecorder != null) {
            mIsRecording = false;
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            mRecordingThread = null;
        }
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}

