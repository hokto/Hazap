package com.example.hazap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
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
import android.widget.LinearLayout;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class Game_activity extends MapActivity {
    private MapView hazapView = null;                   //マップ表示用
    private RouteOverlay routeOverlay;
    private CurrentLocationOverlay locationOverlay;     //現在地追跡用
    public static String myId="";                               //サーバによって割り振られるID
    public static int allpeople=0;                             //訓練に参加中の参加人数
    public static int aroundpeople=0;                          //自分の周囲にいる人数
    public static boolean startFlag=false;
    public static String dangerplaces="";//スタートしたかどうかのフラグ(後で変更の可能性あり)
    public static boolean connectEnd=false;
    public static ArrayList<ArrayList> earthquakeInfo=new ArrayList<ArrayList>();
    MyLocationOverlay location;                          //スタートしたり終了したりするために必要
    Server_activity client=new Server_activity();        //サーバと接続するためにインスタンス
    public static int hp=100;
    public static ProgressBar hpbar;
    public static Bitmap routeMap;
    public static int aliveRate;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity playDisplay=new MainActivity();
        final RelativeLayout relativeLayout = new RelativeLayout(Game_activity.this);//マップ表示やボタン配置用のレイアウト
        hazapView = new MapView(this, "dj00aiZpPWNIMG5nZEpkSXk3OSZzPWNvbnN1bWVyc2VjcmV0Jng9ZDk-");
        location=new MyLocationOverlay(getApplicationContext(),hazapView);
        location.enableMyLocation();//locationの現在地の有効化
        hazapView.getMapController().setZoom(0);
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
        setContentView(relativeLayout);
        relativeLayout.addView(hazapView,playDisplay.DisplayWidth*1100/1080,playDisplay.DisplayHeight*1800/1794);
        hpbar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);//体力ゲージの実装
        hpbar.setProgressDrawable(getResources().getDrawable(R.drawable.hpbarcustom));
        hpbar.setMax(hp);//体力の最大値(100)
        hpbar.setProgress(hp);//最初の体力(100)
        hpbar.setSecondaryProgress(100);//体力減少用の設定
        RelativeLayout.LayoutParams barParam=new RelativeLayout.LayoutParams(playDisplay.DisplayWidth*300/1080,playDisplay.DisplayHeight*30/1794);//体力ゲージを表示する場所を一定にする
        barParam.leftMargin=playDisplay.DisplayWidth*700/1080;
        barParam.topMargin=playDisplay.DisplayHeight*100/1794;
        relativeLayout.addView(hpbar,barParam);
        Button button = new Button(this);//終了ボタン
        button.setText("避難終了");
        relativeLayout.addView(button, playDisplay.DisplayWidth*350/1080, playDisplay.DisplayHeight*150/1794);
        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();//ボタンの配置を調整
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
        int top_margin=(playDisplay.DisplayHeight*1400)/1794;//ボタンの配置場所を一定にする
        marginLayoutParams.setMargins(marginLayoutParams.leftMargin,top_margin , marginLayoutParams.rightMargin, marginLayoutParams.bottomMargin);
        button.setLayoutParams(marginLayoutParams);
        button.setOnClickListener(new View.OnClickListener() { //避難終了ボタンが押された場合
                                      @Override
                                      public void onClick(View v) {
                                          client.Connect("End:"+myId,Game_activity.this,null,null);//避難が終わったことを伝える
                                          locationOverlay.disableMyLocation();//位置情報の取得を終了
                                          try{
                                              Thread.sleep(20000); //20000ミリ秒Sleepする（通信側の処理を反映させるため）
                                          }catch(InterruptedException e){}
                                          client.Connect("Cancel:"+myId,Game_activity.this,null,null);//避難が完了したらサーバ上からこのプレイヤーのIDを削除し、終了
                                          try{
                                              Thread.sleep(100); //100ミリ秒Sleepする（通信側の処理を反映させるため）
                                          }catch(InterruptedException e){}
                                          Result_activity result=new Result_activity();
                                          result.aliveRate=aliveRate;
                                          result.routeMap=routeMap;
                                          Intent result_intent = new Intent(getApplication(), result.getClass());//リザルト画面への遷移
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