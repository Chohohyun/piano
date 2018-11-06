package com.example.ghgus.audioexample2;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
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
import java.util.TreeMap;

public class MainActivity extends Activity implements ListViewBtnAdapter.ListBtnClickListener

{
    @Override
    public void onListBtnClick(int position) {
        if(position<3) {
            Intent intent = new Intent(this, PreviewActivity.class);
            intent.putExtra("musicNumber", position + 1);
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(),"Test 목록입니다.",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onStartBtnClick(int position) {
        if(position<4) {
            Intent intent = new Intent(this, MusicActivity.class);
            intent.putExtra("musicNumber", position);
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(),"Test 목록입니다.",Toast.LENGTH_LONG).show();
        }
    }
    int countvalue = 1;
    JSONObject jmusic = new JSONObject();
    private Map<String,String> timeList = new TreeMap<String, String>();
    private final int mBufferSize = 1024;
    private final int mBytesPerElement = 2;

    // 위의 값들 중 실제 녹음 및 재생 시 선택된 설정값들을 저장
    private String result="";
    private int mSampleRate;
    private short mAudioFormat;
    private short mChannelConfig;
    private AudioRecord mRecorder = null;
    private Thread mRecordingThread = null;
    private Button mRecordBtn, mPlayBtn;
    ListView listview ;
    ListViewBtnAdapter adapter;
    private TextView tv;
    private boolean mIsRecording = false;           // 녹음 중인지에 대한 상태값
    private String mPath = "";// 녹음한 파일을 저장할 경로
    /*public void printLog(){
        Log.d("jsontest",timeList.size()+"" );
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jobj = new JSONObject();
        //JSONObject jmusic = new JSONObject();
        //JSONObject jmusic2 = new JSONObject();

        OkHttpClient client = new OkHttpClient();
        try {
            Log.d("jointest",timeList.toString());
            //jmusic.put("0:00:1", "4");
            //jmusic2.put("0:00:1", "4");
            jobj.put("music",jmusic);
            // Log.d("jsontest 2 ",timeList.get(2));

            Log.d("jsontest",jobj.toString());
            //jobj.put("music2", jmusic2);
        }
        catch(JSONException e){

        }*/
                        /*HttpUrl thhpUrl = new HttpUrl.Builder()
                                .scheme("http")
                                .host("52.78.60.235:9090")
                                .addPathSegment("piano/score/a")
                                .build();*/
/*
        RequestBody requestBody = RequestBody.create(
                (JSON) , jobj+"");

        Request request = new Request.Builder()
                //.url("http://52.78.60.235:9090/piano/score/a")
                .url("http://192.168.0.23:9090/piano/score/a")
                .post(requestBody)
                .addHeader("content-type","application/json; charset=utf-8")
                .build();

        client.newCall(request).enqueue(updateUserInfoCallback);
    }*/
    //Thread th = new Thread(MainActivity.this);

    /*private View.OnClickListener btnClick = new View.OnClickListener() {

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
                        printLog();
                        mRecordBtn.setText("Start Recording");
                    }
                    break;

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
/*
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
                    }*/

// 녹음된 파일이 있는 경우 해당 파일 재생
                   // playWaveFile();
                //    break;
                /*case R.id.test:
                    Intent intent = new Intent(MainActivity.this,MusicActivity.class);
                    startActivity(intent);*/
/*
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

*/
    @Override
// Layout을 연결하고 각 Button의 OnClickListener를 연결
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent loadingIntent= new Intent(this,LoadingActivity.class);
        startActivity(loadingIntent);

        ArrayList<ListViewBtnItem> items = new ArrayList<ListViewBtnItem>() ;
        // items 로드.
        loadItemsFromDB(items) ;

        // Adapter 생성
        adapter = new ListViewBtnAdapter(this, R.layout.listview_btn_item, items, this);// 리스트뷰 참조 및 Adapter달기
                listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mRecordBtn = (Button) findViewById(R.id.start);
        //mMusicBtn=(Button) findViewById(R.id.test);
        //mMusicBtn.setOnClickListener(btnClick);
            // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView parent, View v, int position, long id) {
                    // TODO : item click
                }
            }) ;






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



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
   public boolean loadItemsFromDB(ArrayList<ListViewBtnItem> list) {
        ListViewBtnItem item ;
        int cc ;

        if (list == null) {
            list = new ArrayList<ListViewBtnItem>() ;
        }

        // 순서를 위한 cc 값을 1로 초기화.
        cc= 1 ;

        // 아이템 생성.
        item = new ListViewBtnItem() ;
        item.setIcon(ContextCompat.getDrawable(this, R.drawable.easy));
        item.setText("곰 세마리") ;
        list.add(item) ;
        cc++ ;

        item = new ListViewBtnItem() ;
        item.setIcon(ContextCompat.getDrawable(this, R.drawable.easy)) ;
        item.setText("School bell") ;
        list.add(item) ;
        cc++ ;

        item = new ListViewBtnItem() ;
        item.setIcon(ContextCompat.getDrawable(this, R.drawable.easy)) ;
        item.setText("Little Star") ;
        list.add(item) ;
        cc++ ;

        item = new ListViewBtnItem() ;
        item.setIcon(ContextCompat.getDrawable(this, R.drawable.medium)) ;
        item.setText("베토벤 바이러스") ;
        list.add(item) ;
        cc++;


       item = new ListViewBtnItem() ;
       item.setIcon(ContextCompat.getDrawable(this, R.drawable.medium)) ;
       item.setText("가을동화 OST") ;
       list.add(item) ;
       cc++;


       item = new ListViewBtnItem() ;
       item.setIcon(ContextCompat.getDrawable(this, R.drawable.hard)) ;
       item.setText("젓가락 행진곡") ;
       list.add(item) ;
       cc++;

       item = new ListViewBtnItem() ;
       item.setIcon(ContextCompat.getDrawable(this, R.drawable.hard)) ;
       item.setText("사계") ;
       list.add(item) ;
       cc++;


       return true ;
    }

}

