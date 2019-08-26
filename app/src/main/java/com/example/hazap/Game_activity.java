package com.example.hazap;

import android.app.Activity;
import android.view.Menu;
import java.util.Timer;
import java.util.TimerTask;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.example.hazap.Server_connect;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import jp.co.yahoo.android.maps.CircleOverlay;
import jp.co.yahoo.android.maps.GeoPoint;
import jp.co.yahoo.android.maps.MapController;
import jp.co.yahoo.android.maps.MapView;
import jp.co.yahoo.android.maps.MyLocationOverlay;
import jp.co.yahoo.android.maps.Overlay;

import java.net.Socket;
import java.io.IOException;
import java.io.DataOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;
import java.util.Map;
import java.util.ServiceConfigurationError;



public class Game_activity extends Activity {

    private MapView mapView=null;
    private LocationManager mLocationManager;
    private String BestProvider;
    private MyLocationOverlay _overlay;

    public double latitude;
    public double longitude;
    public static Point getDisplaySize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point;
    }

    public class SubMyLocationOverlay extends MyLocationOverlay {

        MapView _mapView = null;
        Activity _activity = null;

        public SubMyLocationOverlay(Context context, MapView mapView, Activity activity) {
            super(context, mapView);

            _mapView = mapView;
            _activity = activity;
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //マップ表示
        super.onCreate(savedInstanceState);
        RelativeLayout relativeLayout=new RelativeLayout(this);
        mapView = new MapView(this, "dj00aiZpPWNIMG5nZEpkSXk3OSZzPWNvbnN1bWVyc2VjcmV0Jng9ZDk-");
        MapController c = mapView.getMapController();
        c.setCenter(new GeoPoint(31760254,131080396));
        c.setZoom(1);


        //ソケット通信
        Server_connect Connect=new Server_connect();
        Connect.SocketClient();
        Connect.client.send("Hello");


        //ポリゴン精製、表示
        setContentView(mapView);
        GeoPoint mid = new GeoPoint(31760254, 131080396);
        CircleOverlay circleOverlay = new CircleOverlay(mid, 300, 300){

            @Override
            protected boolean onTap(){
                //円をタッチした際の処理

                return true;
            }
        };
        //色の変更
        circleOverlay.setFillColor(Color.argb(127, 255, 40, 40));
        circleOverlay.setStrokeColor(Color.argb(127, 255, 50, 50));
        mapView.getOverlays().add(circleOverlay);
        setContentView(relativeLayout);


        //終了ボタン
        relativeLayout.addView(mapView,1100,1800);
        Button button=new Button(this);
        button.setText("避難終了");
        relativeLayout.addView(button,350,150);
        ViewGroup.LayoutParams layoutParams=button.getLayoutParams();
        ViewGroup.MarginLayoutParams marginLayoutParams=(ViewGroup.MarginLayoutParams)layoutParams;
        marginLayoutParams.setMargins(marginLayoutParams.leftMargin,1000,marginLayoutParams.rightMargin,marginLayoutParams.bottomMargin);
        button.setLayoutParams(marginLayoutParams);
        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {

                                          Intent result_intent=new Intent(getApplication(),Result_activity.class);
                                          startActivity(result_intent);
                                          finish();
                                      }
                                  }
        );


        //体力ゲージ
       /* relativeLayout.addView(mapView,500,200);
        ProgressBar bar=new ProgressBar(this);

        {

        }*/
    }


    @Override
    protected void  onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}