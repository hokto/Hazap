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
    public int test_map[][] = {
            {5,4,3,2},
            {4,3,2,1},
            {3,2,1,0},
            {2,1,0,0}
    };
    public ArrayList<Integer> coastline_lot = new ArrayList<>();
    public int list_size;
    public ArrayList<Integer> coastline_lon = new ArrayList<>();
    public int high = 1;

    @Override
       protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ここから
        coastline_lot.add(3);
        coastline_lot.add(2);
        coastline_lon.add(2);
        coastline_lon.add(3);
        try{
            Thread.sleep(100); //
        }catch(Exception e){}
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        //ここに処理を書く
                        int a[] = {1,-1,0,0};
                        int b[] = {0,0,1,-1};
                        list_size = coastline_lon.size();
                        for(int i=0;i<list_size;i++)
                        {
                            for(int j=0;j<4;j++)
                            {
                                if(!(test_map[coastline_lot.get(i) +a[i]][coastline_lon.get(i) +b[i]]==0)&&test_map[a[i]][b[i]]<high)   //縦横の4方向をチェックして高さが0では無く既定の高さ未満の時だけ
                                {
                                    test_map[a[i]][b[i]]=0;
                                    coastline_lon.add(a[i]);
                                    coastline_lot.add(b[i]);
                                }
                            }
                        }

                     high++;
                        //ここまで定期的に作動
                    }
                });
            }
        }, 0, 2000); //2秒間隔で実行
        //ここまで
    }
}
