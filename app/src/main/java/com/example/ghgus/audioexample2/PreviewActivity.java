package com.example.ghgus.audioexample2;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class PreviewActivity extends Activity {
    VideoView vv;
    int musicNumber=0;
    MediaController mc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_preview);
        Intent inIntent = getIntent();
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        // 현재 볼륨 가져오기
        int volume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // volume이 15보다 작을 때만 키우기 동작
        am.setStreamVolume(AudioManager.STREAM_MUSIC, volume-3, AudioManager.FLAG_PLAY_SOUND);
        musicNumber = inIntent.getIntExtra("musicNumber",0);
        vv = (VideoView) findViewById(R.id.vv);
        mc= new MediaController(PreviewActivity.this);
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
                break;
        }
        Uri uri = Uri.parse(path);
        mc.setVisibility(View.GONE);
        vv.setMediaController(mc);
        vv.setVideoURI(uri);
        vv.requestFocus();
        vv.start();
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }



}
