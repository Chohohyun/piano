package com.example.ghgus.audioexample2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class PauseActivity extends Activity {
    Button reStartBtn,resetBtn,endBtn;
    int value =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pause);
        Intent inIntent = getIntent();
        reStartBtn = (Button) findViewById(R.id.reStart);
        resetBtn = (Button) findViewById(R.id.reset);
        endBtn = (Button) findViewById(R.id.end);

        reStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value=1;
                through(value);
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value=2;
                through(value);

            }
        });

        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value=3;
                through(value);

            }
        });
    }
    protected void through(int value){
        Intent outIntent = new Intent(getApplicationContext(),MusicActivity.class);
        outIntent.putExtra("value",value);
        setResult(RESULT_OK,outIntent);
        finish();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

}
