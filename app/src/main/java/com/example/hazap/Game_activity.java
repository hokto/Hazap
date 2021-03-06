package com.example.hazap;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.yahoo.android.maps.MapActivity;
import jp.co.yahoo.android.maps.MapView;
import jp.co.yahoo.android.maps.MyLocationOverlay;



public class Game_activity extends MapActivity {
    private MapView hazapView = null;                   //マップ表示用
    private CurrentLocationOverlay locationOverlay;     //現在地追跡用
    private long startTime=0;
    private HazapModules modules=new HazapModules();
    private MyLocationOverlay location;                          //スタートしたり終了したりするために必要
    private Server_activity client=new Server_activity();        //サーバと接続するためにインスタンス

    public static String disastersize;
    public static String disaster;
    public static String myId="";                               //サーバによって割り振られるID
    public static int allpeople=0;                             //訓練に参加中の参加人数
    public static int aroundpeople=0;                          //自分の周囲にいる人数
    public static boolean startFlag=false;//スタートしたかどうかのフラグ
    public static boolean connectEnd=false;
    public static ArrayList<ArrayList> earthquakeInfo=new ArrayList<ArrayList>();
    public static int hp=100;
    public static ProgressBar hpbar;
    public static Bitmap routeMap;
    public static int aliveRate;
    public static String organizerMessage;
    public static List<List<String>> coor= new ArrayList<List<String>>();
    public Vibrator vibrator;
    public static JsonNode tsunamiNode=null;
    public static int[] evacuParams;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final RelativeLayout relativeLayout = new RelativeLayout(Game_activity.this);//マップ表示やボタン配置用のレイアウト
        hazapView = new MapView(this, "dj00aiZpPWNIMG5nZEpkSXk3OSZzPWNvbnN1bWVyc2VjcmV0Jng9ZDk-");
        location=new MyLocationOverlay(getApplicationContext(),hazapView);
        location.enableMyLocation();//locationの現在地の有効化
        hazapView.getMapController().setZoom(0);
        setContentView(hazapView);
       location.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                client.Connect("Recruit:"+location.getMyLocation().getLatitude()+","+location.getMyLocation().getLongitude(),Game_activity.this,null,null);//サーバに参加することを伝え、IDをもらう
                try{
                    Thread.sleep(100); //100ミリ秒Sleepする（通信側の処理を反映させるため）
                }catch(InterruptedException e){ }
        }});

        hp=100;
        locationOverlay=new CurrentLocationOverlay(getApplicationContext(),hazapView,this,Game_activity.this,relativeLayout);
        locationOverlay.enableMyLocation();//locationOverlayの現在地の有効化
        setContentView(relativeLayout);
        modules.setView(relativeLayout,hazapView,1100,1800,0,0);
        hpbar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);//体力ゲージの実装
        hpbar.setProgressDrawable(getResources().getDrawable(R.drawable.hpbarcustom));
        hpbar.setMax(hp);//体力の最大値(100)
        hpbar.setProgress(hp);//最初の体力(100)
        hpbar.setSecondaryProgress(100);//体力減少用の設定
        modules.setView(relativeLayout,hpbar,300,30,600,100);
       final ProgressDialog startDialog=new ProgressDialog(this);
        startDialog.setTitle("待機中");
        startDialog.setMessage("全員の参加が完了するまでしばらくお待ちください");
        startDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        startDialog.setCanceledOnTouchOutside(false);
        startDialog.setCancelable(false);
        startDialog.show();
        final Timer timer=new Timer();
        final Handler handler=new Handler();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(startFlag){
                            vibrator.vibrate(450);
                            timer.cancel();
                            startDialog.dismiss();
                            startTime=System.currentTimeMillis();
                            TextView eventText =new TextView(Game_activity.this);
                            String hazardevent="避難訓練中...    今回の想定:"+ disaster+"     規模:"+disastersize;
                            eventText.setText(hazardevent);
                            int tsize=28;
                            eventText.setTextSize(TypedValue.COMPLEX_UNIT_SP,modules.getTextHeight(tsize));
                            eventText.setTextColor(Color.WHITE);
                            eventText.setBackgroundColor(Color.rgb(200,200,200));
                            eventText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                            eventText.setSingleLine(true);
                            eventText.setMarqueeRepeatLimit(10000);
                            eventText.setSelected(true);
                            modules.setView(relativeLayout,eventText,1000,100,0,1650);
                            if(disaster.equals("津波")){
                                new tsunami().Simulate(relativeLayout,hazapView,tsunamiNode);
                            }
                        }
                    }
                });
            }
        },0,100);
        final Button button = new Button(this);//終了ボタン
        Drawable btn_color = ResourcesCompat.getDrawable(getResources(), R.drawable.button_state, null);//リソースから作成したDrawableのリソースを取得
        button.setBackground(btn_color);//ボタンにDrawableを適用する
        button.setTextColor(Color.parseColor("#FFFFFF"));//ボタンの文字の色を白に変更する
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP,modules.getTextHeight(20));//ボタンの文字の大きさを調節
        button.setText("避難終了");
        modules.setView(relativeLayout,button,300,100,50,1500);
        button.setOnClickListener(new View.OnClickListener() { //避難終了ボタンが押された場合

            @Override
            public void onClick(View v) {
                locationOverlay.disableMyLocation();//位置情報の取得を終了
                final long endTime=System.currentTimeMillis();
                final Timer timer = new Timer();
                final Handler handler = new Handler();
                final ProgressDialog resultDialog=new ProgressDialog(Game_activity.this);
                resultDialog.setTitle("計算中");
                resultDialog.setMessage("避難結果を計算中です。しばらくお待ちください。");
                resultDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                resultDialog.setCanceledOnTouchOutside(false);
                resultDialog.setCancelable(false);
                resultDialog.show();
                evacuParams=new int[4];
                timer.schedule(new TimerTask() {//100msごとに同じ処理をする
                    @Override
                    public void run() {
                        handler.post(new Runnable() {//非同期処理を行う
                            @Override
                            public void run() {
                                if (routeMap != null) {
                                    client.Connect("Cancel:" + myId, Game_activity.this, null, null);//避難が完了したらサーバ上からこのプレイヤーのIDを削除し、終了
                                    resultDialog.dismiss();
                                    timer.cancel();
                                    connectEnd=false;
                                    Result_activity result = new Result_activity();
                                    Result_activity.aliveRate = aliveRate;
                                    Result_activity.routeMap = routeMap;
                                    Result_activity.message =organizerMessage;
                                    Result_activity.evacuParams=evacuParams;
                                    Intent result_intent = new Intent(getApplication(), result.getClass());//リザルト画面への遷移
                                    startActivity(result_intent);
                                    startFlag=false;
                                    routeMap=null;
                                    button.setOnClickListener(null);
                                    finish();
                                }
                                if(connectEnd) {
                                    connectEnd=false;
                                    client.Connect("End:" + myId+":"+hpbar.getProgress()+":"+((endTime-startTime)/1000), Game_activity.this, null, null);//避難が終わったことを伝える
                                }
                            }
                        });
                    }
                }, 0, 1000);
            }
        });

    }

    @Override
    protected boolean isRouteDisplayed(){
        return true;
    }
    @Override
    protected void  onResume() {
        super.onResume();
        hazapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        hazapView.onPause();
    }
    @Override
    public void onBackPressed()
    {
    }
}