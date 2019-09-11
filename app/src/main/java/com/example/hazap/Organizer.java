package com.example.hazap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.yahoo.android.maps.GeoPoint;
import jp.co.yahoo.android.maps.MapView;
import jp.co.yahoo.android.maps.MyLocationOverlay;
import jp.co.yahoo.android.maps.PinOverlay;

public class Organizer extends Activity {
    private String[] disasterItems={" ","地震","津波"};//災害の種類を格納した配列
    private String[] earthquakeItems={"震度1","震度2","震度3","震度4","震度5弱","震度5強","震度6弱","震度6強","震度7"};//地震の規模の大きさを格納した配列
    private String[] tsunamiItems={"規模小","規模大"};//津波の規模の大きさを格納した配列
    public static int allPlayers=0;//参加者の人数を格納
    private TextView playerNumText;//利用者人数を表示する
    private double lat=0,lon=0;//主催者の位置情報
    private MainActivity playDisplay=new MainActivity();
    public static List<GeoPoint> playerCoordinates;
    private Server_activity organizerSocket=new Server_activity();//サーバに接続するためのインスタンス
    private Spinner disasterSpinner;
    private Spinner nextSpinner;
    public static boolean startFlag=false;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizerhome);
        disasterSpinner=findViewById(R.id.disasterspinner);//災害の種類を選ばせる処理の設定
        final RelativeLayout relativeLayout=findViewById(R.id.relativeLayout);
        nextSpinner=new Spinner(this);
        ArrayAdapter<String> disasterAdapter=new ArrayAdapter<>(this,R.layout.spinner_item,disasterItems);
        disasterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        disasterSpinner.setAdapter(disasterAdapter);
        disasterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){ //災害の種類を選ぶところが押された場合
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int positio, long id){
                    switch((String)(((Spinner)parent).getSelectedItem())){//各災害が選ばれた時の処理を分岐
                        case "地震":
                            setSpinnerItems(nextSpinner,earthquakeItems,relativeLayout,playDisplay);
                            break;
                        case "津波":
                            setSpinnerItems(nextSpinner,tsunamiItems,relativeLayout,playDisplay);
                            break;
                        case " ":
                            String[] noneItems={"災害を選択してください"};
                            setSpinnerItems(nextSpinner,noneItems,relativeLayout,playDisplay);
                            break;
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent){
                    //何も選択されていないときは特に何もしない
                }
        });
        Button back_button = findViewById(R.id.back_btn);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home_intent = new Intent(getApplication(),MainActivity.class);

                startActivity(home_intent);
            }
        });
        Button startbtn=new Button(this);//避難開始用のボタン
        Drawable btn_color = ResourcesCompat.getDrawable(getResources(), R.drawable.btn_custom, null);//リソースから作成したDrawableのリソースを取得
        startbtn.setBackground(btn_color);//ボタンにDrawableを適用する
        startbtn.setTextColor(Color.parseColor("#FFFFFF"));//ボタンの文字の色を白に変更する
        startbtn.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);//ボタンの文字の大きさを調節
        startbtn.setText("訓練開始");
        RelativeLayout.LayoutParams btnParam=new RelativeLayout.LayoutParams(400* MainActivity.DisplayWidth /1080,150* MainActivity.DisplayHeight /1794);
        btnParam.leftMargin=540* MainActivity.DisplayWidth /1080;
        btnParam.topMargin=1500* MainActivity.DisplayHeight /1794;
        relativeLayout.addView(startbtn,btnParam);
        final Timer timer=new Timer();//一定時間ごとに同じ処理を行うためのタイマー
        final Handler handler=new Handler();//非同期処理用
        playerNumText=new TextView(this);//全参加者を表示するテキストの設定
        RelativeLayout.LayoutParams textParam=new RelativeLayout.LayoutParams(500*playDisplay.DisplayWidth/1080,80*playDisplay.DisplayHeight/1794);
        textParam.leftMargin=300*playDisplay.DisplayWidth/1080;
        textParam.topMargin=1200*playDisplay.DisplayHeight/1794;
        playerNumText.setTextSize(25);
        relativeLayout.addView(playerNumText,textParam);
        timer.schedule(new TimerTask() {//1秒ごとに同じ処理をする
            @Override
            public void run() {
                handler.post(new Runnable() {//非同期処理を行う
                    @Override
                    public void run() {
                        organizerSocket.Connect("Allpeople:",null,null,Organizer.this);//サーバから全参加者人数を所得する
                        playerNumText.setText("参加人数:"+Integer.valueOf(allPlayers));//取得した人数を設定
                        try{
                            Thread.sleep(100); //100ミリ秒Sleepする（通信側の処理を反映させるため）
                        }catch(Exception e){}
                    }
                });
            }
        },0,2000);
        startbtn.setOnClickListener(new View.OnClickListener() {//開始ボタンが押された場合
            @Override
            public void onClick(View v) {
                //避難開始
                timer.cancel();//2秒ごとの処理を止める
                OrganizerMap();
            }
        });
    }
    private void OrganizerMap(){
        RelativeLayout mapLayout=new RelativeLayout(this);
        final MapView organizerMap=new MapView(Organizer.this,"dj00aiZpPWNIMG5nZEpkSXk3OSZzPWNvbnN1bWVyc2VjcmV0Jng9ZDk-");
        final MyLocationOverlay location=new MyLocationOverlay(getApplicationContext(),organizerMap);//主催者の現在地（スタート地点）を取得
        location.enableMyLocation();
        location.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                try{
                    //organizerSocket.Connect("Start:"+location.getMyLocation().getLatitude()+","+location.getMyLocation().getLongitude()+":"+(String)disasterSpinner.getSelectedItem()+":"+(String) nextSpinner.getSelectedItem(),null,null,null);//サーバにシミュレーション開始を伝える
                    organizerSocket.Connect("Start:31.760254,131.080396:"+(String)disasterSpinner.getSelectedItem()+":"+(String) nextSpinner.getSelectedItem(),null,null,null);//サーバにシミュレーション開始を伝える
                    Thread.sleep(100); //100ミリ秒Sleepする（通信側の処理を反映させるため）
                }catch(InterruptedException e){ }
            }
        });
        final Timer timer=new Timer();
        final Handler handler=new Handler();
        final ProgressDialog organizerDialog=new ProgressDialog(this);
        organizerDialog.setTitle("待機中");
        organizerDialog.setMessage("サーバからの返答待ちです。");
        organizerDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        organizerDialog.setCanceledOnTouchOutside(false);
        organizerDialog.show();
        organizerSocket.Connect("Wait:",null,organizerMap,Organizer.this);
        try{
            Thread.sleep(100);
        }catch(InterruptedException e){}
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(startFlag){
                            organizerDialog.dismiss();
                        }
                    }
                });
            }
        },0,100);
        CurrentLocationOverlay locationOverlay=new CurrentLocationOverlay(getApplicationContext(),organizerMap,this,null,null);
        locationOverlay.enableMyLocation();//locationOverlayの現在地の有効化
        setContentView(mapLayout);
        mapLayout.addView(organizerMap,playDisplay.DisplayWidth*1100/1080,playDisplay.DisplayHeight*1800/1794);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        organizerSocket.Connect("Coordinates",null,null,Organizer.this);
                        try{
                            Thread.sleep(500); //100ミリ秒Sleepする（通信側の処理を反映させるため）
                        }catch(InterruptedException e){ }
                        organizerMap.removeOverlayAll();
                        for(int i=0;i<2;i++){
                            PinOverlay pin=new PinOverlay(i);
                            organizerMap.getOverlays().add(pin);
                            pin.addPoint(playerCoordinates.get(i),Integer.toString(i));
                        }
                    }
                });
            }
        },0,1000);
        Button btn=new Button(this);
        btn.setText("メッセージを書く");
        btn.setTextSize(20);
        RelativeLayout.LayoutParams btnParam=new RelativeLayout.LayoutParams(600*playDisplay.DisplayWidth/1080,250*playDisplay.DisplayHeight/1794);
        btnParam.topMargin=1500*playDisplay.DisplayWidth/1080;
        mapLayout.addView(btn,btnParam);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                final EditText writeMessage=new EditText(Organizer.this);
                writeMessage.setHint("メッセージ");
                new AlertDialog.Builder(Organizer.this)
                        .setTitle("メッセージ入力")
                        .setView(writeMessage)
                        .setPositiveButton("訓練終了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }
        });
    }
    private void setSpinnerItems(Spinner spinner,String[] items,RelativeLayout relativeLayout,MainActivity playDisplay){ //各災害における選択肢の設定
        relativeLayout.removeView(spinner);//前に設定されていたものを取り除く
        ArrayAdapter nextAdapter=new ArrayAdapter(this,R.layout.spinner_item,items);
        nextAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(nextAdapter);
        RelativeLayout.LayoutParams nextParams=new RelativeLayout.LayoutParams(820* MainActivity.DisplayWidth /1080,200* MainActivity.DisplayHeight /1794);
        nextParams.leftMargin=150* MainActivity.DisplayWidth /1080;
        nextParams.topMargin=900* MainActivity.DisplayHeight /1794;
        relativeLayout.addView(spinner,nextParams);
    }
}