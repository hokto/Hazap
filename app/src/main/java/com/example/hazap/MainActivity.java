package com.example.hazap;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity{
    public ArrayList<ArrayList<Integer>> coastline = new ArrayList<ArrayList<Integer>>();
    public ArrayList<Integer> point = new ArrayList<>();
    public int sound1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager wm = getWindowManager();
        new HazapModules().Init(wm);
        Home();
    }

    //ホーム画面
    public void Home() {
        final SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sound1 = soundPool.load(this, R.raw.se_maoudamashii_onepoint30, 1);

        setContentView(R.layout.home);
        Button gamestart_button = findViewById(R.id.gamestart_btn);
        Button option_button = findViewById(R.id.option_btn);
        Button organizerBtn=findViewById(R.id.organizerBtn);
        gamestart_button.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent game_intent = new Intent(getApplication(), Game_activity.class);
                                                    soundPool.play(sound1,1.0f,1.0f,0,0,1);
                                                    startActivity(game_intent);

                                                }
                                            }
        );
        option_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Jisseki_intent = new Intent(getApplication(),Performance.class);
                startActivity(Jisseki_intent);
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

    @Override
    public void onBackPressed()
    {
    }
}