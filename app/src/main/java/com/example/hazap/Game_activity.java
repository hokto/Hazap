package com.example.hazap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.yahoo.android.maps.CircleOverlay;
import jp.co.yahoo.android.maps.GeoPoint;
import jp.co.yahoo.android.maps.MapActivity;
import jp.co.yahoo.android.maps.MapView;
import jp.co.yahoo.android.maps.MyLocationOverlay;
import jp.co.yahoo.android.maps.routing.RouteOverlay;

import java.io.IOException;
import java.util.Iterator;

public class Game_activity extends MapActivity {
    private MapView hazapView = null;                   //マップ表示用
    private RouteOverlay routeOverlay;
    private CurrentLocationOverlay locationOverlay;     //現在地追跡用
    private int DisplayHeight=0;                        //端末の縦方向の長さ
    private int DisplayWidth=0;                         //端末の横方向の長さ
    public static String myId="";                               //サーバによって割り振られるID
    public static int allpeople=0;                             //訓練に参加中の参加人数
    public static int aroundpeople=0;                          //自分の周囲にいる人数
    public static boolean startFlag=false;
    public static String dangerplaces="";//スタートしたかどうかのフラグ(後で変更の可能性あり)
    public static boolean connectEnd=false;
    MyLocationOverlay location;                          //スタートしたり終了したりするために必要
    Server_activity client=new Server_activity();        //サーバと接続するためにインスタンス
    public int hp=100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final RelativeLayout relativeLayout = new RelativeLayout(Game_activity.this);//マップ表示やボタン配置用のレイアウト
        hazapView = new MapView(this, "dj00aiZpPWNIMG5nZEpkSXk3OSZzPWNvbnN1bWVyc2VjcmV0Jng9ZDk-");
        location=new MyLocationOverlay(getApplicationContext(),hazapView);
        location.enableMyLocation();//locationの現在地の有効化
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();//端末の情報を取得
        display.getMetrics(displayMetrics);
        DisplayWidth = displayMetrics.widthPixels;//端末の高さ、幅を代入
        DisplayHeight = displayMetrics.heightPixels;
        setContentView(hazapView);
        location.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                System.out.println("Debug1");
                client.Connect("Recruit:"+location.getMyLocation().getLatitude()+","+location.getMyLocation().getLongitude(),Game_activity.this,null,null);//サーバに参加することを伝え、IDをもらう
                try{
                    Thread.sleep(100); //100ミリ秒Sleepする（通信側の処理を反映させるため）
                }catch(InterruptedException e){ }
        }});
        locationOverlay=new CurrentLocationOverlay(getApplicationContext(),hazapView,this,Game_activity.this,relativeLayout);
        locationOverlay.enableMyLocation();//locationOverlayの現在地の有効化
        hazapView.getOverlays().add(locationOverlay);
        hazapView.invalidate();
        setContentView(relativeLayout);//layoutに追加されたものを表示
        relativeLayout.addView(hazapView,DisplayWidth*1100/1080,DisplayHeight*1800/1794);
        //色の変更
        final ProgressBar hpbar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
        hpbar.setProgressDrawable(getResources().getDrawable(R.drawable.hpbarcustom));
        hpbar.setMax(hp);
        hpbar.setProgress(hp);
        hpbar.setSecondaryProgress(100);
        RelativeLayout.LayoutParams barParam=new RelativeLayout.LayoutParams(DisplayWidth*300/1080,DisplayHeight*30/1794);
        //表示座標の設定
        barParam.leftMargin=DisplayWidth*700/1080;
        barParam.topMargin=DisplayHeight*100/1794;
        relativeLayout.addView(hpbar,barParam);
        Button button = new Button(this);//終了ボタン
        button.setText("避難終了");
        relativeLayout.addView(button, DisplayWidth*350/1080, DisplayHeight*150/1794);
        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();//ボタンの配置を調整
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
        int top_margin=(DisplayHeight*1400)/1794;//ボタンの配置場所を一定にする
        marginLayoutParams.setMargins(marginLayoutParams.leftMargin,top_margin , marginLayoutParams.rightMargin, marginLayoutParams.bottomMargin);
        button.setLayoutParams(marginLayoutParams);
        //テスト用
        Button testbtn=new Button(this);
        testbtn.setText("Damage");
        relativeLayout.addView(testbtn, DisplayWidth*100/1080, DisplayHeight*100/1794);
        testbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hp-=10;
                hpbar.setProgress(hp);
            }
        });
        //ここまでテスト用
        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          client.Connect("End:"+myId,Game_activity.this,null,null);
                                          locationOverlay.disableMyLocation();
                                          try{
                                              Thread.sleep(100); //100ミリ秒Sleepする（通信側の処理を反映させるため）
                                          }catch(InterruptedException e){}
                                          client.Connect("Cancel:"+myId,Game_activity.this,null,null);//避難が完了したらサーバ上からこのプレイヤーのIDを削除し、終了
                                          try{
                                              Thread.sleep(100); //100ミリ秒Sleepする（通信側の処理を反映させるため）
                                          }catch(InterruptedException e){}

                                          Intent result_intent = new Intent(getApplication(), Result_activity.class);//リザルト画面への遷移
                                          startActivity(result_intent);
                                          finish();
                                      }
                                  }

        );}
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
}