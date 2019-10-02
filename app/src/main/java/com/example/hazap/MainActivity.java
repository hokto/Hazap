package com.example.hazap;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;


public class MainActivity extends AppCompatActivity{
    //効果音用
    public int start_sound;
    public int jisseki_sound;
    public int organizer_sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        WindowManager wm = getWindowManager();
        new HazapModules().Init(wm);
        Home();
    }

    //ホーム画面
    public void Home() {
        final SoundPool Start_sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        start_sound = Start_sound.load(this, R.raw.se_maoudamashii_onepoint30, 1);
        final SoundPool Jisseki_sound = new SoundPool(1,AudioManager.STREAM_MUSIC,0);
        jisseki_sound = Jisseki_sound.load(this,R.raw.se_maoudamashii_system29,1);
        final SoundPool Organizer_sound = new SoundPool(1,AudioManager.STREAM_MUSIC,0);
        organizer_sound = Organizer_sound.load(this,R.raw.se_maoudamashii_onepoint26,1);
        setContentView(R.layout.home);
        final Button gamestart_button = findViewById(R.id.gamestart_btn);
        Button option_button = findViewById(R.id.option_btn);
        Button organizerBtn=findViewById(R.id.organizerBtn);
        Button manual_btn = findViewById(R.id.pdf_btn);

        gamestart_button.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent game_intent = new Intent(getApplication(), Game_activity.class);
                                                    Start_sound.play(start_sound,0.2f,0.2f,0,0,1);
                                                    startActivity(game_intent);

                                                }
                                            }
        );
        option_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Jisseki_intent = new Intent(getApplication(),Performance.class);
                Jisseki_sound.play(jisseki_sound,0.2f,0.2f,0,0,1);
                startActivity(Jisseki_intent);
            }
        });
        organizerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent organizer_intent=new Intent(getApplication(),Organizer.class);
                Organizer_sound.play(organizer_sound,0.5f,0.5f,0,0,1);
                startActivity(organizer_intent);
            }
        });
        manual_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pdf_intent = new Intent(getApplication(),manual.class);
                startActivity(pdf_intent);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
    }
}