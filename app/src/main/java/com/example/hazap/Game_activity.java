package com.example.hazap;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/*import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;*/

import org.json.JSONObject;

import java.util.jar.Attributes;

import jp.co.yahoo.android.maps.CircleOverlay;
import jp.co.yahoo.android.maps.GeoPoint;
import jp.co.yahoo.android.maps.MapActivity;
import jp.co.yahoo.android.maps.MapView;
import jp.co.yahoo.android.maps.MyLocationOverlay;
import jp.co.yahoo.android.maps.routing.RouteOverlay;

public class Game_activity extends MapActivity {
    private MapView hazapView = null;//マップ表示用
    private RouteOverlay routeOverlay;
    private CurrentLocationOverlay locationOverlay;//現在地追跡用
    private int DisplayHeight=0;//端末の縦方向の長さ
    private int DisplayWidth=0;//端末の横方向の長さ
    public String myId="";//サーバによって割り振られるID
    public int allpeople=0;//訓練に参加中の参加人数
    public int aroundpeople=0;//自分の周囲にいる人数
    public boolean startFlag=false;//スタートしたかどうかのフラグ(後で変更の可能性あり)
    MyLocationOverlay location;//スタートしたり終了したりするために必要
    Server_activity client=new Server_activity();//サーバと接続するためにインスタンス
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout relativeLayout = new RelativeLayout(this);//マップ表示やボタン配置用のレイアウト
        hazapView = new MapView(this, "dj00aiZpPWNIMG5nZEpkSXk3OSZzPWNvbnN1bWVyc2VjcmV0Jng9ZDk-");

        /*location=new MyLocationOverlay(getApplicationContext(),hazapView);
        location.enableMyLocation();//locationの現在地の有効化
        location.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                client.Connect("Recruit:"+location.getMyLocation().getLatitude()+","+location.getMyLocation().getLongitude(),Game_activity.this);//サーバに参加することを伝え、IDをもらう
                try{
                    Thread.sleep(100); //100ミリ秒Sleepする（通信側の処理を反映させるため）
                }catch(InterruptedException e){

                }
            }
        });
        locationOverlay=new CurrentLocationOverlay(getApplicationContext(),hazapView,this,this);
        locationOverlay.enableMyLocation();//locationOverlayの現在地の有効化*/
        //MapViewにRouteOverlayを追加
        //ポリゴン精製、表示
        relativeLayout.addView(hazapView, 1100, 1800);
        Button button = new Button(this);//終了ボタン
        button.setText("避難終了");
        relativeLayout.addView(button, 350, 150);
        GeoPoint mid = new GeoPoint();
        CircleOverlay circleOverlay = new CircleOverlay(mid, 300, 300) {
            @Override
            protected boolean onTap() {
                //円をタッチした際の処理
                   return true;
            }
        };
        //色の変更
        circleOverlay.setFillColor(Color.argb(127, 255, 40, 40));
        circleOverlay.setStrokeColor(Color.argb(127, 255, 50, 50));
        //hazapView.getOverlays().add(locationOverlay);
        hazapView.invalidate();
        hazapView.getOverlays().add(circleOverlay);
        setContentView(relativeLayout);//layoutに追加されたものを表示

        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();//ボタンの配置を調整
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();//端末の情報を取得
        display.getMetrics(displayMetrics);
        /*ImageView img=new ImageView(this);
        img.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.hpbar));
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(500,200);
        //表示座標の設定
        lp.leftMargin = 600;
        lp.topMargin = 0;*/
        ProgressBar bar=(ProgressBar) this.getLayoutInflater().inflate(R.layout.hpbar,null);
        relativeLayout.addView(bar);
        //relativeLayout.addView(img,lp);
        DisplayWidth = displayMetrics.widthPixels;//端末の高さ、幅を代入
        DisplayHeight = displayMetrics.heightPixels;
        int top_margin=(DisplayHeight*1400)/1794;//ボタンの配置場所を一定にする
        marginLayoutParams.setMargins(marginLayoutParams.leftMargin,top_margin , marginLayoutParams.rightMargin, marginLayoutParams.bottomMargin);
        button.setLayoutParams(marginLayoutParams);
        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          client.Connect("End:"+myId,Game_activity.this);
                                          locationOverlay.disableMyLocation();
                                          try{
                                              Thread.sleep(100); //100ミリ秒Sleepする（通信側の処理を反映させるため）
                                          }catch(InterruptedException e){}
                                          client.Connect("Cancel:"+myId,Game_activity.this);//避難が完了したらサーバ上からこのプレイヤーのIDを削除し、終了
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