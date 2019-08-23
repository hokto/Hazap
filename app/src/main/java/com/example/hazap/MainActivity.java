package com.example.hazap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    @Override/*1番目に実行*/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Home();/*home関数を実行*/
    }

    public void Home() {
        setContentView(R.layout.home);/*home.xmlを読み出す*/
        Button gamestart_button = findViewById(R.id.gamestart_button);
        Button option_button = findViewById(R.id.option_btn);
        Button admin_button=findViewById(R.id.admin_btn);
        gamestart_button.setOnClickListener(new View.OnClickListener() {/*gamestart_buttonをクリックしたときのイベント*/
                                                @Override
                                                public void onClick(View v) {
                                                    Intent game_intent = new Intent(getApplication(), Game_activity.class);/*gameactivutyに飛ぶ(収量はfinsh();)*/
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

        admin_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
               Intent adminActivity=new Intent(getApplication(),admin.class);/*adminactivityをintentで宣言*/
               startActivity(adminActivity);/*adminactivityを実行*/
            }
        });
    }
    public void Option(){
        setContentView(R.layout.option);
        Button backhome_button=findViewById(R.id.backhome_btn);
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
        Button backhome_button=findViewById(R.id.backhome_btn);
        backhome_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Home();
            }
        });
    }
}