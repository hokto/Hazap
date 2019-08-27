package com.example.hazap;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.yahoo.android.maps.CircleOverlay;
import jp.co.yahoo.android.maps.GeoPoint;
import jp.co.yahoo.android.maps.MapActivity;
import jp.co.yahoo.android.maps.MapView;
import jp.co.yahoo.android.maps.MyLocationOverlay;
import jp.co.yahoo.android.maps.routing.RouteOverlay;

import java.io.IOException;
public class Game_activity extends MapActivity {
    private MapView hazapView = null;                   //マップ表示用
    private RouteOverlay routeOverlay;
    private CurrentLocationOverlay locationOverlay;     //現在地追跡用
    private int DisplayHeight=0;                        //端末の縦方向の長さ
    private int DisplayWidth=0;                         //端末の横方向の長さ
    public String myId="";                               //サーバによって割り振られるID
    public int allpeople=0;                             //訓練に参加中の参加人数
    public int aroundpeople=0;                          //自分の周囲にいる人数
    public boolean startFlag=false;                    //スタートしたかどうかのフラグ(後で変更の可能性あり)
    MyLocationOverlay location;                          //スタートしたり終了したりするために必要
    Server_activity client=new Server_activity();        //サーバと接続するためにインスタンス


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout relativeLayout = new RelativeLayout(this);//マップ表示やボタン配置用のレイアウト
        hazapView = new MapView(this, "dj00aiZpPWNIMG5nZEpkSXk3OSZzPWNvbnN1bWVyc2VjcmV0Jng9ZDk-");
        //マップ表示
        location=new MyLocationOverlay(getApplicationContext(),hazapView);
        location.enableMyLocation();//locationの現在地の有効化
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();//端末の情報を取得
        display.getMetrics(displayMetrics);
        DisplayWidth = displayMetrics.widthPixels;//端末の高さ、幅を代入
        DisplayHeight = displayMetrics.heightPixels;
        location.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                client.Connect("Recruit:"+location.getMyLocation().getLatitude()+","+location.getMyLocation().getLongitude(),Game_activity.this);//サーバに参加することを伝え、IDをもらう
                try{
                    Thread.sleep(100); //100ミリ秒Sleepする（通信側の処理を反映させるため）
                }catch(InterruptedException e){}
            }
        });
        locationOverlay=new CurrentLocationOverlay(getApplicationContext(),hazapView,this,this);
        locationOverlay.enableMyLocation();//locationOverlayの現在地の有効化*/

        /*int lat[] = {31759551,31759431,31758775,31758520,31759086,31759181,31758867,31757998,31757286,31757195};
        int lon[] = {131079983,131081174,131079808,131080438,131081880,131078574,131082099,131080646,131080830,131082341};
        int step[] = {12,12,20,12,12,12,20,12,12,12};*/try {
            String data="{\n" +
                    "    \"0\": {\n" +
                    "        \"Coordinates\": \"131.079983055556,31.759550833333\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.21\"\n" +
                    "    },\n" +
                    "    \"1\": {\n" +
                    "        \"Coordinates\": \"131.081174444444,31.759431388889\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.20\"\n" +
                    "    },\n" +
                    "    \"2\": {\n" +
                    "        \"Coordinates\": \"131.079808055556,31.758775833333\",\n" +
                    "        \"Step\": \"4\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"3\": {\n" +
                    "        \"Coordinates\": \"131.080438333333,31.758520277778\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"3\": {\n" +
                    "        \"Coordinates\": \"131.081880000000,31.759086944444\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.20\"\n" +
                    "    },\n" +
                    "    \"4\": {\n" +
                    "        \"Coordinates\": \"131.078544166667,31.759181388889\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.21\"\n" +
                    "    },\n" +
                    "    \"5\": {\n" +
                    "        \"Coordinates\": \"131.082099444444,31.758867500000\",\n" +
                    "        \"Step\": \"4\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"6\": {\n" +
                    "        \"Coordinates\": \"131.080646666667,31.757998055556\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"7\": {\n" +
                    "        \"Coordinates\": \"131.081521666667,31.757878611111\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"8\": {\n" +
                    "        \"Coordinates\": \"131.079016388889,31.757920277778\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"9\": {\n" +
                    "        \"Coordinates\": \"131.078999722222,31.757892500000\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"10\": {\n" +
                    "        \"Coordinates\": \"131.082802222222,31.758823055556\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"11\": {\n" +
                    "        \"Coordinates\": \"131.078083055556,31.758550833333\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"12\": {\n" +
                    "        \"Coordinates\": \"131.077508055556,31.759836666667\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.21\"\n" +
                    "    },\n" +
                    "    \"13\": {\n" +
                    "        \"Coordinates\": \"131.080830000000,31.757286944444\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"14\": {\n" +
                    "        \"Coordinates\": \"131.081807777778,31.757590000000\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"15\": {\n" +
                    "        \"Coordinates\": \"131.083329722222,31.759336944444\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.20\"\n" +
                    "    },\n" +
                    "    \"16\": {\n" +
                    "        \"Coordinates\": \"131.078069166667,31.758053611111\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"17\": {\n" +
                    "        \"Coordinates\": \"131.083421388889,31.759084166667\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.20\"\n" +
                    "    },\n" +
                    "    \"18\": {\n" +
                    "        \"Coordinates\": \"131.081166111111,31.757056666667\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"19\": {\n" +
                    "        \"Coordinates\": \"131.083535277778,31.759270277778\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.20\"\n" +
                    "    },\n" +
                    "    \"20\": {\n" +
                    "        \"Coordinates\": \"131.083749166667,31.760048055556\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"21\": {\n" +
                    "        \"Coordinates\": \"131.083471388889,31.758636944444\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"22\": {\n" +
                    "        \"Coordinates\": \"131.083490833333,31.758609166667\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"23\": {\n" +
                    "        \"Coordinates\": \"131.076866666667,31.759745000000\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.21\"\n" +
                    "    },\n" +
                    "    \"24\": {\n" +
                    "        \"Coordinates\": \"131.077919166667,31.757628611111\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"25\": {\n" +
                    "        \"Coordinates\": \"131.083565833333,31.758517500000\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"26\": {\n" +
                    "        \"Coordinates\": \"131.082341111111,31.757195555556\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"27\": {\n" +
                    "        \"Coordinates\": \"131.082671666667,31.757312222222\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"28\": {\n" +
                    "        \"Coordinates\": \"131.083910277778,31.759020277778\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"29\": {\n" +
                    "        \"Coordinates\": \"131.084165833333,31.759950833333\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"30\": {\n" +
                    "        \"Coordinates\": \"131.083921388889,31.758667500000\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.18\"\n" +
                    "    },\n" +
                    "    \"31\": {\n" +
                    "        \"Coordinates\": \"131.084026944444,31.758750833333\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.18\"\n" +
                    "    },\n" +
                    "    \"32\": {\n" +
                    "        \"Coordinates\": \"131.084338055556,31.759450833333\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"33\": {\n" +
                    "        \"Coordinates\": \"131.076319444444,31.759731111111\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.21\"\n" +
                    "    },\n" +
                    "    \"34\": {\n" +
                    "        \"Coordinates\": \"131.083751944444,31.757859444444\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.18\"\n" +
                    "    },\n" +
                    "    \"35\": {\n" +
                    "        \"Coordinates\": \"131.08401314,31.75816768\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.18\"\n" +
                    "    },\n" +
                    "    \"36\": {\n" +
                    "        \"Coordinates\": \"131.084013055556,31.758167777778\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.18\"\n" +
                    "    },\n" +
                    "  \n" +
                    "    \"37\": {\n" +
                    "        \"Coordinates\": \"131.084407500000,31.758587222222\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.18\"\n" +
                    "    },\n" +
                    "    \"38\": {\n" +
                    "        \"Coordinates\": \"131.084463055556,31.758720555556\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.18\"\n" +
                    "    },\n" +
                    " \n" +
                    "    \"39\": {\n" +
                    "        \"Coordinates\": \"131.084743611111,31.759167500000\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"40\": {\n" +
                    "        \"Coordinates\": \"131.085029722222,31.759464722222\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"41\": {\n" +
                    "        \"Coordinates\": \"131.084518611111,31.757803888889\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.18\"\n" +
                    "    },\n" +
                    "    \"42\": {\n" +
                    "        \"Coordinates\": \"131.084165833333,31.757248333333\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.18\"\n" +
                    "    },\n" +
                    "    \"43\": {\n" +
                    "        \"Coordinates\": \"131.085265833333,31.759739722222\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.19\"\n" +
                    "    },\n" +
                    "    \"44\": {\n" +
                    "        \"Coordinates\": \"131.08499304884,31.758239930897\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.18\"\n" +
                    "    },\n" +
                    "    \"45\": {\n" +
                    "        \"Coordinates\": \"131.085026944444,31.758234444444\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.18\"\n" +
                    "    },\n" +
                    "    \"46\": {\n" +
                    "        \"Coordinates\": \"131.08507138,31.75820660\",\n" +
                    "        \"Step\": \"2\",\n" +
                    "        \"ARV\": \"1.18\"\n" +
                    "    }\n" +
                   /* "   \"47\": {\n" +
                    "        \"Coordinates\": \"131.76025422,31.08039611\",\n" +
                    "        \"Step\": \"180\",\n" +
                    "        \"ARV\": \"1.18\"\n" +*/
                    "}";
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(data);
            //ポリゴン精製、表示
            for (int a = 0; a < 47; a++) {
                setContentView(hazapView);
                String[] coordinates = jsonNode.get(String.valueOf(a)).get("Coordinates").asText().split(",", 0);
                System.out.println(coordinates[0]);
                int lon = (int) (Float.parseFloat(coordinates[0]) * 10E5);
                System.out.println(lon);
                int lat = (int) (Float.parseFloat(coordinates[1]) * 10E5);
                int step = Integer.parseInt(jsonNode.get(String.valueOf(a)).get("Step").asText()) * 5;
                GeoPoint mid = new GeoPoint(lat, lon);
                CircleOverlay circleOverlay = new CircleOverlay(mid, step, step) {
                    @Override
                    protected boolean onTap() {
                        //円をタッチした際の処理

                        return true;
                    }
                };

                //色の変更
                circleOverlay.setFillColor(Color.argb(127, 255, 40, 40));
                circleOverlay.setStrokeColor(Color.argb(127, 255, 50, 50));
                hazapView.getOverlays().add(locationOverlay);
                hazapView.invalidate();
                hazapView.getOverlays().add(circleOverlay);
                setContentView(relativeLayout);//layoutに追加されたものを表示
            }
        }catch(IOException e){}
        GeoPoint mid = new GeoPoint(31761813, 131079560);
        CircleOverlay circleOverlay = new CircleOverlay(mid, 30, 30) {
            @Override
            protected boolean onTap() {
                //円をタッチした際の処理

                return true;
            }
        };

        //色の変更
        circleOverlay.setFillColor(Color.argb(127, 255, 40, 40));
        circleOverlay.setStrokeColor(Color.argb(127, 255, 50, 50));
        hazapView.getOverlays().add(locationOverlay);
        hazapView.invalidate();
        hazapView.getOverlays().add(circleOverlay);
        setContentView(relativeLayout);//layoutに追加されたものを表示
            ImageView img=new ImageView(this);
            img.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.hpbar3));
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(1000,DisplayHeight*200/1794);
            //表示座標の設定
            lp.leftMargin = DisplayWidth*550/800;
            lp.topMargin = 0;
            relativeLayout.addView(hazapView,1100,1800);
            relativeLayout.addView(img,lp);
            Button button = new Button(this);//終了ボタン
            button.setText("避難終了");
            relativeLayout.addView(button, 350, 150);
            ViewGroup.LayoutParams layoutParams = button.getLayoutParams();//ボタンの配置を調整
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;


        int top_margin=(DisplayHeight*1400)/1794;//ボタンの配置場所を一定にする
        marginLayoutParams.setMargins(marginLayoutParams.leftMargin,top_margin , marginLayoutParams.rightMargin, marginLayoutParams.bottomMargin);
        button.setLayoutParams(marginLayoutParams);
        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          client.Connect("Cancel:"+myId,Game_activity.this);//避難が完了したらサーバ上からこのプレイヤーのIDを削除し、終了
                                          try{
                                              Thread.sleep(100); //100ミリ秒Sleepする（通信側の処理を反映させるため）
                                          }catch(InterruptedException e){}
                                          client.Connect("End",Game_activity.this);
                                          locationOverlay.disableMyLocation();
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