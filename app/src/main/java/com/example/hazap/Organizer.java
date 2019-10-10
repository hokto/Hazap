package com.example.hazap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
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

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import jp.co.yahoo.android.maps.GeoPoint;
import jp.co.yahoo.android.maps.MapView;
import jp.co.yahoo.android.maps.MyLocationOverlay;
import jp.co.yahoo.android.maps.PinOverlay;
import jp.co.yahoo.android.maps.PopupOverlay;


public class Organizer extends Activity {
    private String[] disasterItems={"災害を選択","地震","津波"};//災害の種類を格納した配列
    private String[] earthquakeItems={"震度1","震度2","震度3","震度4","震度5弱","震度5強","震度6弱","震度6強","震度7"};//地震の規模の大きさを格納した配列
    private TextView playerNumText;//利用者人数を表示する
    private double lat=0,lon=0;//主催者の位置情報
    private MainActivity playDisplay=new MainActivity();
    private Server_activity organizerSocket=new Server_activity();//サーバに接続するためのインスタンス
    private Spinner disasterSpinner;
    private Spinner nextSpinner;
    private PinOverlay[] pinArr;
    private PopupOverlay[] popupArr;
    private EditText waveHeight;//津波が選択された場合、波の高さと震源地までの距離を設定
    private EditText distance;
    private HazapModules modules=new HazapModules();

    public int sound_back;
    public int sound_start;
    public static int allPlayers=0;//参加者の人数を格納
    public static List<GeoPoint> playerCoordinates;
    public static boolean startFlag=false;
    public static JsonNode tsunamiNode=null;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sound_back = soundPool.load(this, R.raw.back, 1);
        final SoundPool sound_s = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sound_start = sound_s.load(this, R.raw.se_maoudamashii_onepoint30, 1);

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
                    switch((String)(parent.getSelectedItem())){//各災害が選ばれた時の処理を分岐
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
                            modules.setView(relativeLayout,waveHeight,820,200,150,600);
                            modules.setView(relativeLayout,distance,820,200,150,850);
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
        modules.setView(relativeLayout,startbtn,250,100,600,1500);
        final Timer timer=new Timer();//一定時間ごとに同じ処理を行うためのタイマー
        final Handler handler=new Handler();//非同期処理用
        playerNumText=new TextView(this);//全参加者を表示するテキストの設定
        playerNumText.setTextSize(25);
        modules.setView(relativeLayout,playerNumText,500,80,300,1200);
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
        modules.setView(relativeLayout,back_button,250,100,50,1500);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(sound_back,0.1f,0.1f,0,0,1);
                timer.cancel();
                finish();
            }
        });

        startbtn.setOnClickListener(new View.OnClickListener() {//開始ボタンが押された場合
            @Override
            public void onClick(View v) {
                //避難開始
                sound_s.play(sound_start,0.1f,0.1f,0,0,1);
                timer.cancel();//2秒ごとの処理を止める
                OrganizerMap();
            }
        });
    }
    @SuppressLint("NewApi")
    private void OrganizerMap(){
        final RelativeLayout mapLayout=new RelativeLayout(this);
        final MapView organizerMap=new MapView(Organizer.this,"dj00aiZpPWNIMG5nZEpkSXk3OSZzPWNvbnN1bWVyc2VjcmV0Jng9ZDk-");
        final MyLocationOverlay location=new MyLocationOverlay(getApplicationContext(),organizerMap);//主催者の現在地（スタート地点）を取得
        pinArr=new PinOverlay[allPlayers];
        popupArr=new PopupOverlay[allPlayers];
        location.enableMyLocation();
        location.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                try{
                    String disasterInfo= "";
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
                            if(disasterSpinner.getSelectedItem()=="津波"){
                                new tsunami().Simulate(mapLayout,organizerMap,tsunamiNode);
                            }
                        }
                    }
                });
            }
        },0,100);

        final CurrentLocationOverlay locationOverlay=new CurrentLocationOverlay(getApplicationContext(),organizerMap,this,null,null);
        locationOverlay.enableMyLocation();//locationOverlayの現在地の有効化
        setContentView(mapLayout);
        modules.setView(mapLayout,organizerMap,1100,1800,0,0);
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
                            if(playerCoordinates!=null) {
                                for (int i = 0; i < allPlayers; i++) {
                                    organizerMap.getOverlays().remove(pinArr[i]);
                                    organizerMap.getOverlays().remove(popupArr[i]);
                                    pinArr[i] = new PinOverlay(i%3);
                                    organizerMap.getOverlays().add(pinArr[i]);
                                    popupArr[i]=new PopupOverlay();
                                    organizerMap.getOverlays().add(popupArr[i]);
                                    pinArr[i].setOnFocusChangeListener(popupArr[i]);
                                    pinArr[i].addPoint(playerCoordinates.get(i), "参加者ID:"+String.valueOf(i+1));
                                }
                            }
                        }
                    }
                });
            }
        },0,1000);
        Button btn=new Button(this);
        Drawable btn_color = ResourcesCompat.getDrawable(getResources(), R.drawable.button_state, null);//リソースから作成したDrawableのリソースを取得
        btn.setBackground(btn_color);//ボタンにDrawableを適用する
        btn.setTextColor(Color.parseColor("#FFFFFF"));//ボタンの文字の色を白に変更する
        btn.setText("メッセージを書く");
        btn.setBackgroundResource(R.drawable.button_state);
        btn.setTextSize(20);
        btn.setTextColor(Color.WHITE);
        modules.setView(mapLayout,btn,350,100,30,1550);
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
        ArrayAdapter nextAdapter= new ArrayAdapter(this,R.layout.spinner_item,items);
        nextAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(nextAdapter);
        //spinner.setBackground( ResourcesCompat.getDrawable(getResources(), R.drawable.spinner_custom, null));//枠線設定
        modules.setView(relativeLayout,spinner,820,200,150,850);
    }
    @Override
    public void onBackPressed()
    {
    }
}