package com.example.hazap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.RotateDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.Templates;

public class Result_activity extends Activity   {
    public static int aliveRate;//生存率
    public static Bitmap routeMap;//サーバから取得した避難結果の画像を格納
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        RelativeLayout relativeLayout=findViewById(R.id.resultLayout);
        TextView advice=new TextView(this);//主催者からのメッセージに関する設定
        advice.setText("主催者からのメッセージ");
        advice.setTextSize(15);
        RelativeLayout.LayoutParams textParam=new RelativeLayout.LayoutParams(400,50);
        textParam.topMargin=50;
        textParam.leftMargin=100;
        PieChart aliveRateCircle=new PieChart(this);//生存率を表示する設定
        aliveRateCircle.setHoleColor(Color.parseColor("#00000000"));//真ん中の色を透明色に
        aliveRateCircle.setUsePercentValues(true);
        List<PieEntry> entryList=new ArrayList<>();//グラフに入れる値を格納するリスト
        entryList.add(new PieEntry((float)(aliveRate)));
        entryList.add(new PieEntry((float)(100-aliveRate)));
        PieDataSet dataSet=new PieDataSet(entryList,"");
        ArrayList<Integer> colors=new ArrayList<>();//色の設定
        colors.add(Color.parseColor("#05b371"));
        colors.add(Color.parseColor("#00000000"));
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);
        PieData data=new PieData(dataSet);
        aliveRateCircle.setData(data);//円グラフにデータを代入
        relativeLayout.addView(advice,textParam);//円グラフ描画
        RelativeLayout.LayoutParams rateParam=new RelativeLayout.LayoutParams(500,500);
        rateParam.leftMargin=400;
        relativeLayout.addView(aliveRateCircle,rateParam);
        Button btn=new Button(this);//ホームに戻るボタンの設定
        btn.setText("ホームに戻る");
        btn.setTextSize(23);
        RelativeLayout.LayoutParams btnParam=new RelativeLayout.LayoutParams(250,100);
        btnParam.topMargin=1050;
        btnParam.leftMargin=30;
        relativeLayout.addView(btn,btnParam);
        ImageView routeImg=new ImageView(this);//避難結果が表示されている画像の設定
        RelativeLayout.LayoutParams imgParam=new RelativeLayout.LayoutParams(1000,1000);
        imgParam.topMargin=450;
        imgParam.leftMargin=15;
        routeImg.setImageBitmap(routeMap);
        relativeLayout.addView(routeImg,imgParam);
        btn.setOnClickListener(new View.OnClickListener(){ //ボタンが押された場合、ホームに戻る
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }
}