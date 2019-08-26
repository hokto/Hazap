package com.example.hazap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.co.yahoo.android.maps.GeoPoint;
import jp.co.yahoo.android.maps.MapView;
import jp.co.yahoo.android.maps.MyLocationOverlay;

public class CurrentLocationOverlay extends MyLocationOverlay{
    MapView _mapView;
    Activity _activity;
    Game_activity Client_Info;
    public CurrentLocationOverlay(Context context, MapView mapView,Activity activity,Game_activity client_info){
        super(context,mapView);
        _mapView=mapView;
        _activity=activity;
        Client_Info=client_info;
    }
    @Override
    public void onLocationChanged(android.location.Location location){
        super.onLocationChanged(location);
        if(_mapView.getMapController()!=null) {
            GeoPoint currentlocation = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
            _mapView.getMapController().animateTo(currentlocation);
            _mapView.invalidate();
            Server_activity client=new Server_activity();
            Game_activity client_info=new Game_activity();
            //別スレッドで処理しているため反映されるまでに少し時間がかかる
            client.Connect("Number:"+Client_Info.myId+":"+location.getLatitude()+","+location.getLongitude(),client_info);
            try{
                Thread.sleep(100); //100ミリ秒Sleepする（通信側の処理を反映させるため）
            }catch(InterruptedException e){}
        }
    }
}
