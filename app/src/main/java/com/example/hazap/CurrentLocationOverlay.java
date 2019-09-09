package com.example.hazap;

import android.app.Activity;
import android.content.Context;
import android.widget.RelativeLayout;

import jp.co.yahoo.android.maps.GeoPoint;
import jp.co.yahoo.android.maps.MapView;
import jp.co.yahoo.android.maps.MyLocationOverlay;

public class CurrentLocationOverlay extends MyLocationOverlay{
    MapView _mapView;//位置情報を描画する
    Activity _activity;
    Game_activity Client_Info;//利用者の情報(体力、IDなど)を持つ
    RelativeLayout relativeLayout;
    private static final int earthR = 6378100;//地球半径
    private long finalTime=0;//体力ゲージが更新されたときの時間を保存
    public CurrentLocationOverlay(Context context, MapView mapView,Activity activity,Game_activity client_info,RelativeLayout relativelayout){
        super(context,mapView);
        _mapView=mapView;//引数で与えられたパラメータを代入
        _activity=activity;
        Client_Info=client_info;
        relativeLayout=relativelayout;
        mapView.getOverlays().add(this);
        mapView.invalidate();
    }
    @Override
    public void onLocationChanged(android.location.Location location){ //位置情報を更新する関数
        super.onLocationChanged(location);
        if(_mapView.getMapController()!=null) {
            GeoPoint currentlocation = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));//現在の位置情報を設定
            _mapView.getMapController().animateTo(currentlocation);
            _mapView.invalidate();
            final Server_activity client=new Server_activity();//サーバに接続するためのインスタンス
            //別スレッドで処理しているため反映されるまでに少し時間がかかる
            if(Client_Info.startFlag) { //シミュレーションがすでにスタートしている場合
                client.Connect("Number:" + Client_Info.myId + ":" + location.getLatitude() + "," + location.getLongitude(), Client_Info,null,null);//自分のIDと現在位置をサーバに送信
                try{
                    Thread.sleep(100); //100ミリ秒Sleepする（通信側の処理を反映させるため）
                }catch(InterruptedException e){}
                boolean damageFlag=false;//ダメージを受けたかどうかの判定
                long starttime=System.currentTimeMillis();//ダメージ判定をするときの時間を取得
                if(Client_Info.aroundpeople>Math.ceil(Client_Info.allpeople/2))//サーバから送られる周囲の人数が全体の半分以上であれば混んでいると判定
                {
                    damageFlag=true;
                }
                else{
                    if((starttime-finalTime)/1000>=10) {//最後にダメージを受けた時間よりも10秒以上たっている
                        for (int i = 0; i < Client_Info.earthquakeInfo.size(); i++) {//どこかの円の中に利用者が入っていればダメージを受ける
                            double dif_lat = Math.abs(location.getLatitude() - (float) Client_Info.earthquakeInfo.get(i).get(0));
                            double dif_lon = Math.abs(location.getLongitude() - (float) Client_Info.earthquakeInfo.get(i).get(1));
                            double dif_distance = earthR * Math.sqrt(Math.pow(Math.toRadians(dif_lat), 2) + Math.pow(Math.toRadians(dif_lon), 2));
                            if ((int) dif_distance <= (int) Client_Info.earthquakeInfo.get(i).get(2)) {
                                damageFlag = true;
                                break;//一度ダメージ判定をしたら一度処理を終わる
                            }
                        }
                }
                    if (damageFlag == true) {
                        Client_Info.hp -= 5;//5ずつ体力を減らす
                        Client_Info.hpbar.setProgress(Client_Info.hp);//体力ゲージの更新
                    }
                    finalTime=System.currentTimeMillis();//最後にダメージを受けた時間の更新
                }
            }
            else
            {
                if(Client_Info.connectEnd){//サーバとの通信がすでに終わっている場合
                    Client_Info.connectEnd=false; //サーバとの通信状態を初期化する
                    client.Connect("Wait:",Client_Info,_mapView,null);//サーバにシミュレーションが始まったかどうかを確認
                    try{
                        Thread.sleep(100); //100ミリ秒Sleepする（通信側の処理を反映させるため）
                    }catch(InterruptedException e){

                    }
                }
            }
        }
    }
}
