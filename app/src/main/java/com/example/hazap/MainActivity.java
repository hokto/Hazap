package com.example.hazap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Home();
    }
    public void Home(){
        setContentView(R.layout.home);
        Button gamestart_button=findViewById(R.id.gamestart_btn);
        Button option_button=findViewById(R.id.option_btn);
        gamestart_button.setOnClickListener(new View.OnClickListener(){
                                                @Override
                                                public void onClick(View v) {
                                                    Game();
                                                }
                                            }
        );
        option_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Option();
            }
        });
    }
    public void Game(){
        setContentView(R.layout.game);
        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.681382, 139.766084), 15));
            }
        });
        Button backhome_button=findViewById(R.id.goresult_btn);
        backhome_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Result();
            }
        });
    }
    public void Option(){
        setContentView(R.layout.option);
        Button backhome_button=findViewById(R.id.Homemove_btn);
        backhome_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Home();
            }
        });
    }
    public void Result(){
        setContentView(R.layout.result);
        Button backhome_button=findViewById(R.id.Homemove_btn);
        backhome_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Home();
            }
        });
    }
}
