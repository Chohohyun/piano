package com.example.ghgus.audioexample2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ResultActivity extends Activity {
    TextView scoreTv;
    Button repeatBtn,backBtn;
    RelativeLayout rl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_result);
        rl = (RelativeLayout) findViewById(R.id.rl);
        Intent inIntent = getIntent();
        int musicScore = inIntent.getIntExtra("musicScore", 0);
        /*70점이하일시 백그라운드 변경*/
        if(musicScore>=70){
            rl.setBackgroundResource(R.drawable.over);
        }
        scoreTv = (TextView) findViewById(R.id.score);
        scoreTv.setText(musicScore+"");


        repeatBtn = (Button)findViewById(R.id.repeatButton);
        backBtn=(Button)findViewById(R.id.mainButton);
        repeatBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                playWaveFile();
            }
        });
        backBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent backIntent = new Intent();
                setResult(RESULT_OK);
                finish();
            }
        });

    }
    /* 다시듣기 */
    private void playWaveFile() {
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        // 현재 볼륨 가져오기
        int volume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // volume이 15보다 작을 때만 키우기 동작
        am.setStreamVolume(AudioManager.STREAM_MUSIC, volume-3, AudioManager.FLAG_PLAY_SOUND);
        int mBufferSize=1024;
        int mSampleRate=44100;
        short mAudioFormat= AudioFormat.ENCODING_PCM_16BIT;
        short mChannelConfig = AudioFormat.CHANNEL_IN_STEREO;
        String sd = Environment.getExternalStorageDirectory().getAbsolutePath();
        String mPath = sd + "/record_audiorecord.pcm";

        int minBufferSize = AudioTrack.getMinBufferSize(mSampleRate, mChannelConfig, mAudioFormat);
        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelConfig, mAudioFormat, minBufferSize, AudioTrack.MODE_STREAM);
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



