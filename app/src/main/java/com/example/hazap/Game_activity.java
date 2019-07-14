package com.example.hazap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import jp.co.yahoo.android.maps.GeoPoint;
import jp.co.yahoo.android.maps.MapController;
import jp.co.yahoo.android.maps.MapView;
import java.util.Map;
public class Game_activity extends Activity {
    private MapView mapView=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapView = new MapView(this, "dj00aiZpPWNIMG5nZEpkSXk3OSZzPWNvbnN1bWVyc2VjcmV0Jng9ZDk-");
        MapController c = mapView.getMapController();
        c.setCenter(new GeoPoint());
        c.setZoom(1);
        setContentView(mapView);
        /*Button backhome_button=findViewById(R.id.goresult_btn);
        backhome_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });*/
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
