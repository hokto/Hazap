package com.example.hazap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity{
    String coordinates;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Home();
    }

    //ホーム画面
    public void Home() {
        setContentView(R.layout.home);
        Button gamestart_button = findViewById(R.id.gamestart_btn);
        Button option_button = findViewById(R.id.option_btn);
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
    }

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
                Jisseki();
            }
        });
    }
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