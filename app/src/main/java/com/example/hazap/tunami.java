package com.example.hazap;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class tunami extends AppCompatActivity {
    private Timer timer = null;
    Handler handler = new Handler();
    public ArrayList<ArrayList<Integer>> coastline = new ArrayList<ArrayList<Integer>>();
    public ArrayList<Integer> point = new ArrayList<>();
    public boolean home = true;
//    public ArrayList<Integer>


    @Override
       protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ここから
        point.add(13974477);
        point.add(398261);
        coastline.add(point);

        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        //ここに処理を書く
                        for(int j=0;j<8;j++)
                        {
                            if(home==true)
                            {

                            }
                            else
                            {

                            }
                        }

                        //ここまで定期的に作動
                    }
                });
            }
        }, 15000, 2000); //2秒間隔で実行
    }
}