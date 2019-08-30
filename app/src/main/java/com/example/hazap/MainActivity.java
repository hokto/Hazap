package com.example.hazap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
//テスト用
import android.os.Handler;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity{

    //一定時間ごとに更新するプログラムのテスト
    private Timer mTimer = null;
    Handler mHandler = new Handler();
    public int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ここから
        mTimer = new Timer(true);
        mTimer.schedule(new TimerTask(){
            @Override
            public void run() {
                mHandler.post( new Runnable() {
                    public void run() {
                        //ここに処理を書く
                        System.out.println(count);
                        count = count+1;
                    }
                });
            }
        },0,2000); //2秒間隔で実行
        //ここまで
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
                Jisseki();
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


}