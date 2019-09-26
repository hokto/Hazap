package com.example.hazap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.widget.RelativeLayout;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import jp.co.yahoo.android.maps.GeoPoint;
import jp.co.yahoo.android.maps.MapView;
import jp.co.yahoo.android.maps.PolygonOverlay;
import jp.co.yahoo.android.maps.PolylineOverlay;


public class tsunami extends Activity {
    private int tsunamiIdx=1;//津波のシミュレーションが何秒進んでいるかを示す
    private List<GeoPoint> coastPlaces=new ArrayList<GeoPoint>();//海岸線の座標
    private static List<GeoPoint> places;//津波のシミュレーションによる座標が格納されたリスト
    private PolygonOverlay polygonOverlay=null;//ポリゴン表示用
    @SuppressLint("StaticFieldLeak")
    public void Simulate(RelativeLayout relativeLayout, final MapView mapView, final JsonNode tsunamiNode){//一度呼び出せば、シミュレーションが実行される
        final Timer simulateTimer=new Timer();
        final Handler handler=new Handler();
        JsonNode currentNode=tsunamiNode.get("0");
        int i=0;
        while(currentNode.size()>i){//海岸線の座標を取得し、リストにpush
            String[] coastPos=currentNode.get(String.valueOf(i)).asText().split(",",0);
            coastPlaces.add(new GeoPoint((int)(Float.parseFloat(coastPos[1])*Math.pow(10.0,6.0)),(int)(Float.parseFloat(coastPos[0])*Math.pow(10.0,6.0))));
            i++;
        }
        final PolylineOverlay polylineOverlay=new PolylineOverlay((GeoPoint[]) coastPlaces.toArray(new GeoPoint[]{})){//最初は線を描画
            @Override
            protected boolean onTap(){
                return true;
            }
        };
        polylineOverlay.setColor(Color.argb(127, 255, 40, 40));
        mapView.getOverlays().add(polylineOverlay);
        simulateTimer.schedule(new TimerTask() {
            @Override
            public void run() {//1秒ごとにシミュレーションを実行する
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        int i=0;
                        places=new ArrayList<GeoPoint>(coastPlaces);//津波が進んでいる座標を格納するリスト
                        String place;
                        while(tsunamiNode.get(String.valueOf(tsunamiIdx)).size()>i){//全て取得
                            place=tsunamiNode.get(String.valueOf(tsunamiIdx)).get(String.valueOf(i)).asText();
                            String[] placePos=place.split(",",0);
                            places.add(new GeoPoint((int)(Float.parseFloat(placePos[1])*Math.pow(10.0,6.0)),(int)(Float.parseFloat(placePos[0])*Math.pow(10.0,6.0))));
                            i++;
                        }
                        if(polygonOverlay!=null) {
                            mapView.getOverlays().remove(polygonOverlay);
                            System.out.println("Removed");
                        }
                        polygonOverlay=new PolygonOverlay((GeoPoint[]) places.toArray(new GeoPoint[]{})){
                            @Override
                            protected boolean onTap(){
                                return true;
                            }
                        };
                        polygonOverlay.setFillColor(Color.argb(127, 255, 40, 40));
                        polygonOverlay.setStrokeColor(Color.argb(127, 255, 50, 50));
                        mapView.getOverlays().add(polygonOverlay);
                        tsunamiIdx++;
                        if(tsunamiNode.get(String.valueOf(tsunamiIdx))==null){//これ以上シミュレーションを行わないなら止める
                            simulateTimer.cancel();
                        }
                    }
                });
            }
        },0,1000);
    }
    public boolean isSwallowed(GeoPoint currentLocation){//津波に飲み込まれているかどうかの判定処理(Winding Number Algorithm)
        double sumDec=0;
        if(places==null){//シミュレーションが始まってなければfalse
            return false;
        }
        for(int i=0;i<places.size()-1;i++){
            double x1=places.get(i).getLongitude()-currentLocation.getLongitude();
            double x2=places.get(i+1).getLongitude()-currentLocation.getLongitude();
            double y1=places.get(i).getLatitude()-currentLocation.getLatitude();
            double y2=places.get(i+1).getLatitude()-currentLocation.getLatitude();
            sumDec+=Math.atan((x1*y2-y1*x2)/(x1*x2+y1*y2));//Atan(外積/内積)で回転方向を含めた角度を算出
        }
        System.out.println("Dec:"+sumDec);
        if((int)sumDec==0){//合計角度がほぼ0であれば外側であると判定
            return false;
        }
        else{
            return true;
        }
    }

}
