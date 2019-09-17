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
import android.text.InputType;
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
    private String[] disasterItems={"災害を選択","地震","津波"};//災害の種類を格納した配列
    private String[] earthquakeItems={"震度1","震度2","震度3","震度4","震度5弱","震度5強","震度6弱","震度6強","震度7"};//地震の規模の大きさを格納した配列
    public static int allPlayers=0;//参加者の人数を格納
    private TextView playerNumText;//利用者人数を表示する
    private double lat=0,lon=0;//主催者の位置情報
    private MainActivity playDisplay=new MainActivity();
    public static List<GeoPoint> playerCoordinates;
    private Server_activity organizerSocket=new Server_activity();//サーバに接続するためのインスタンス
    private Spinner disasterSpinner;
    private Spinner nextSpinner;
    private PinOverlay pin = null;
    public static boolean startFlag=false;
    private EditText waveHeight;//津波が選択された場合、波の高さと震源地までの距離を設定
    private EditText distance;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizerhome);
        disasterSpinner=findViewById(R.id.disasterspinner);//災害の種類を選ばせる処理の設定
        final RelativeLayout relativeLayout=findViewById(R.id.relativeLayout);
        nextSpinner=new Spinner(this);
        waveHeight=new EditText(Organizer.this);
        distance=new EditText(Organizer.this);
        final ArrayAdapter<String> disasterAdapter=new ArrayAdapter<>(this,R.layout.spinner_item,disasterItems);
        disasterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        disasterSpinner.setAdapter(disasterAdapter);
        disasterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){ //災害の種類を選ぶところが押された場合
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int positio, long id){
                    switch((String)(((Spinner)parent).getSelectedItem())){//各災害が選ばれた時の処理を分岐
                        case "地震":
                            relativeLayout.removeView(distance);
                            relativeLayout.removeView(waveHeight);
                            setSpinnerItems(nextSpinner,earthquakeItems,relativeLayout,playDisplay);
                            break;
                        case "津波":
                            relativeLayout.removeView(nextSpinner);
                            waveHeight.setHint("海岸線での津波の高さを入力");
                            distance.setHint("海岸線から震源地までの距離を入力");
                            waveHeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);//数値のみ入力可能
                            distance.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            RelativeLayout.LayoutParams waveHeightParams=new RelativeLayout.LayoutParams(820* MainActivity.DisplayWidth /1080,200* MainActivity.DisplayHeight /1794);
                            waveHeightParams.leftMargin=150* MainActivity.DisplayWidth /1080;
                            waveHeightParams.topMargin=600* MainActivity.DisplayHeight /1794;
                            RelativeLayout.LayoutParams distanceParams=new RelativeLayout.LayoutParams(820* MainActivity.DisplayWidth /1080,200* MainActivity.DisplayHeight /1794);
                            distanceParams.leftMargin=150* MainActivity.DisplayWidth /1080;
                            distanceParams.topMargin=850* MainActivity.DisplayHeight /1794;
                            relativeLayout.addView(waveHeight,waveHeightParams);
                            relativeLayout.addView(distance,distanceParams);
                            break;
                        case "災害を選択":
                            relativeLayout.removeView(distance);
                            relativeLayout.removeView(waveHeight);
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

        Button startbtn=new Button(this);//避難開始用のボタン
        Drawable btn_color = ResourcesCompat.getDrawable(getResources(), R.drawable.btn_custom, null);//リソースから作成したDrawableのリソースを取得
        startbtn.setBackground(btn_color);//ボタンにDrawableを適用する
        startbtn.setTextColor(Color.parseColor("#FFFFFF"));//ボタンの文字の色を白に変更する
        startbtn.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);//ボタンの文字の大きさを調節
        startbtn.setText("訓練開始");
        RelativeLayout.LayoutParams btnParam=new RelativeLayout.LayoutParams(400* MainActivity.DisplayWidth /1080,150* MainActivity.DisplayHeight /1794);
        btnParam.leftMargin=580* MainActivity.DisplayWidth /1080;
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
        //ホーム画面に戻るボタン
        Button back_button = new Button(this);
        Drawable button_color = ResourcesCompat.getDrawable(getResources(), R.drawable.button_state,null);
        back_button.setBackground(button_color);
        back_button.setTextColor(Color.parseColor("#FFFFFF"));
        back_button.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
        back_button.setText("戻る");
        RelativeLayout.LayoutParams back_btnParam=new RelativeLayout.LayoutParams(300* MainActivity.DisplayWidth /1080,150* MainActivity.DisplayHeight /1794);
        back_btnParam.leftMargin=100* MainActivity.DisplayWidth /1080;
        back_btnParam.topMargin=1500* MainActivity.DisplayHeight /1794;
        relativeLayout.addView(back_button,back_btnParam);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                finish();
            }
        });

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
                    String disasterInfo=new String();
                    if(disasterSpinner.getSelectedItem()=="地震"){ //地震が選ばれた場合
                        disasterInfo=disasterSpinner.getSelectedItem()+":"+nextSpinner.getSelectedItem();
                    }
                    else if(disasterSpinner.getSelectedItem()=="津波"){ //津波が選ばれた場合
                        disasterInfo=disasterSpinner.getSelectedItem()+":"+waveHeight.getText()+","+distance.getText();
                    }
                    organizerSocket.Connect("Start:"+location.getMyLocation().getLatitude()+","+location.getMyLocation().getLongitude()+":"+disasterInfo,null,null,null);//サーバにシミュレーション開始を伝える
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
        organizerDialog.setCancelable(false);
        organizerDialog.show();
        organizerSocket.Connect("Wait:",null,organizerMap,this);
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
        if(startFlag) {
            organizerSocket.Connect("Coordinates", null, null, Organizer.this);
            try {
                Thread.sleep(500); //100ミリ秒Sleepする（通信側の処理を反映させるため）
            } catch (InterruptedException e) {
            }
            if(playerCoordinates!=null) {
                organizerMap.getOverlays().remove(pin);
                for (int i = 0; i < allPlayers; i++) {
                    pin = new PinOverlay(i);
                    organizerMap.getOverlays().add(pin);
                    pin.addPoint(playerCoordinates.get(i), Integer.toString(i));
                }
            }
        }

        final CurrentLocationOverlay locationOverlay=new CurrentLocationOverlay(getApplicationContext(),organizerMap,this,null,null);
        locationOverlay.enableMyLocation();//locationOverlayの現在地の有効化
        setContentView(mapLayout);
        mapLayout.addView(organizerMap,playDisplay.DisplayWidth*1100/1080,playDisplay.DisplayHeight*1800/1794);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(startFlag) {
                            organizerSocket.Connect("Coordinates", null, null, Organizer.this);
                            try {
                                Thread.sleep(500); //100ミリ秒Sleepする（通信側の処理を反映させるため）
                            } catch (InterruptedException e) {
                            }
                            organizerMap.removeOverlayAll();
                            for (int i = 0; i < allPlayers; i++) {
                                PinOverlay pin = new PinOverlay(i);
                                organizerMap.getOverlays().add(pin);
                                pin.addPoint(playerCoordinates.get(i), Integer.toString(i));
                            }
                        }
                    }
                });
            }
        },0,1000);
        Button btn=new Button(this);
        btn.setText("メッセージを書く");
        btn.setBackgroundResource(R.drawable.button_state);
        btn.setTextSize(20);
        btn.setTextColor(Color.WHITE);
        RelativeLayout.LayoutParams btnParam=new RelativeLayout.LayoutParams(550*playDisplay.DisplayWidth/1080,100*playDisplay.DisplayHeight/1794);
        btnParam.topMargin=725*playDisplay.DisplayHeight/800;
        btnParam.leftMargin=15*playDisplay.DisplayWidth/1080;
        mapLayout.addView(btn,btnParam);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                locationOverlay.disableMyLocation();
                final EditText writeMessage=new EditText(Organizer.this);
                writeMessage.setHint("メッセージ");
                new AlertDialog.Builder(Organizer.this)
                        .setTitle("メッセージ入力")
                        .setView(writeMessage)
                        .setPositiveButton("訓練終了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                organizerSocket.Connect("Message:"+writeMessage.getText(),null,null,null);//主催者からのメッセージを送信
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
        nextParams.topMargin=850* MainActivity.DisplayHeight /1794;
        relativeLayout.addView(spinner,nextParams);
    }
    @Override
    public void onBackPressed()
    {
    }
}