package com.example.hazap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import jp.co.yahoo.android.maps.GeoPoint;
import jp.co.yahoo.android.maps.MapController;
import jp.co.yahoo.android.maps.MapView;
import java.util.Map;
public class Game_activity extends Activity {
    private MapView mapView=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout relativeLayout=new RelativeLayout(this);
        mapView = new MapView(this, "dj00aiZpPWNIMG5nZEpkSXk3OSZzPWNvbnN1bWVyc2VjcmV0Jng9ZDk-");
        MapController c = mapView.getMapController();
        c.setCenter(new GeoPoint());
        c.setZoom(1);
        relativeLayout.addView(mapView,1100,1800);
        Button button=new Button(this);
        button.setText("避難終了");
        relativeLayout.addView(button,350,150);
        ViewGroup.LayoutParams layoutParams=button.getLayoutParams();
        ViewGroup.MarginLayoutParams marginLayoutParams=(ViewGroup.MarginLayoutParams)layoutParams;
        marginLayoutParams.setMargins(marginLayoutParams.leftMargin,1500,marginLayoutParams.rightMargin,marginLayoutParams.bottomMargin);
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
        setContentView(relativeLayout);
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
