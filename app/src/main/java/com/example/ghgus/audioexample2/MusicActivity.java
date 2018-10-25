package com.example.ghgus.audioexample2;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class MusicActivity extends Activity{
    MediaController mc;
    VideoView vv;
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
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        if ((event.getAction() == MotionEvent.ACTION_DOWN)) {
            Toast.makeText(getApplicationContext(),"touch",Toast.LENGTH_LONG).show();
            mc.hide();
            vv.pause();
            popUpMenu();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

    public void popUpMenu(){
        Intent intent = new Intent(MusicActivity.this,PauseActivity.class);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode==RESULT_OK){
            int value=data.getIntExtra("value",0);
            Toast.makeText(getApplicationContext(),"상태"+value,Toast.LENGTH_SHORT).show();
            if(value==1){
                vv.start();
            }
            else if(value==2){

                setting();
            }
            else if(value==3){
                Intent intent= new Intent(MusicActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }

    }
}

