package com.example.ghgus.audioexample2;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ResultActivity extends Activity {
    TextView scoreTv;
    Button repeatBtn,backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_result);
        Intent inIntent = getIntent();
        int musicScore = inIntent.getIntExtra("musicScore", 0);
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
    private void playWaveFile() {
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



