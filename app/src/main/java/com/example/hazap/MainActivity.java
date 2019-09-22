package com.example.hazap;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity{
    public ArrayList<ArrayList<Integer>> coastline = new ArrayList<ArrayList<Integer>>();
    public ArrayList<Integer> point = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager wm = getWindowManager();
        new HazapModules().Init(wm);
        Home();
    }
    //ホーム画面
    public void Home() {
        setContentView(R.layout.home);
        point.add(13974477);
        point.add(398261);
        coastline.add(point);

        System.out.println(coastline.get(0).get(0));
        System.out.println(coastline.get(0).get(1));

        Button gamestart_button = findViewById(R.id.gamestart_btn);
        Button option_button = findViewById(R.id.option_btn);
        Button organizerBtn=findViewById(R.id.organizerBtn);
        gamestart_button.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent game_intent = new Intent(getApplication(), Game_activity.class);

                                                    startActivity(game_intent);

                                                }
                                            }
        );
        option_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Option();
            }
        });
        organizerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent organizer_intent=new Intent(getApplication(),Organizer.class);
                startActivity(organizer_intent);
            }
        });
    }

    //オプション画面
    public void Option(){
        setContentView(R.layout.option);
        Button backhome_button=findViewById(R.id.Homemove_btn);
        backhome_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Home();
            }
        });
        Button hazardmap_button=findViewById(R.id.hazardmap_btn);
        hazardmap_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Hazardmap();
            }
        });

        Button jisseki_button=findViewById(R.id.jisseki_btn);
        jisseki_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent performanceIntent=new Intent(getApplication(),Performance.class);
                startActivity(performanceIntent);
            }
        });
    }
    //ハザードマップ画面
    public void Hazardmap(){
        setContentView(R.layout.hazardmap);
        Button backoption_button=findViewById(R.id.backoption_btn);
        backoption_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Option();
            }
        });
    }
    //実績確認画面
    public void Jisseki(){
        setContentView(R.layout.jisseki);
        Button backoption_button=findViewById(R.id.backoption_btn);
        backoption_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Option();
            }
        });
    }
    //リザルト画面
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
    @Override
    public void onBackPressed()
    {
    }
}