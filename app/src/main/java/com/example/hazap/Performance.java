package com.example.hazap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeConstants;

public class Performance extends Activity {
    private static int aliveRate;//生存率
    private static Bitmap routeMap;//サーバから取得した避難結果の画像を格納
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jisseki);
        BufferedReader resultReader=null;
        String textstring="";
        try{
            resultReader=new BufferedReader(new InputStreamReader(openFileInput("EvacuationResult.txt")));
            String str;
            while((str=resultReader.readLine())!=null){
                textstring+=str;
            }
            resultReader.close();
        }catch(Exception e){}
        String[] resultInfo=textstring.split(":",0);
        aliveRate=Integer.parseInt(resultInfo[0]);
        String[] bytedata=resultInfo[1].split(",",0);
        byte[] routeMapByte=new byte[bytedata.length];
        for(int i=0;i<bytedata.length;i++){
            routeMapByte[i]=Integer.valueOf(bytedata[i]).byteValue();
        }
        MainActivity display=new MainActivity();
        int baseHypotenuse=(int)Math.sqrt(Math.pow(1216,2)+Math.pow(800,2));
        int displayHypotenuse=(int)Math.sqrt(Math.pow(display.DisplayWidth,2)+Math.pow(display.DisplayHeight,2));
        routeMap=BitmapFactory.decodeByteArray(routeMapByte,0,routeMapByte.length);
        RelativeLayout relativeLayout=findViewById(R.id.performanceLayout);
        TextView advice=new TextView(this);//主催者からのメッセージに関する設定
        advice.setText("主催者からのメッセージ");
        advice.setTextSize(10*displayHypotenuse/baseHypotenuse);
        RelativeLayout.LayoutParams textParam=new RelativeLayout.LayoutParams(400*display.DisplayWidth/800,50*display.DisplayHeight/1216);
        textParam.topMargin=50*display.DisplayHeight/1216;
        textParam.leftMargin=100*display.DisplayWidth/800;
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
        RelativeLayout.LayoutParams rateParam=new RelativeLayout.LayoutParams(500*display.DisplayWidth/800,500*display.DisplayHeight/1216);
        rateParam.leftMargin=400*display.DisplayWidth/800;
        relativeLayout.addView(aliveRateCircle,rateParam);
        TextView aliveRatetxt=new TextView(this);
        aliveRatetxt.setText("生存率 "+Integer.valueOf(aliveRate)+"%");
        aliveRatetxt.setTextSize(10*displayHypotenuse/baseHypotenuse);
        RelativeLayout.LayoutParams ratetxtParam=new RelativeLayout.LayoutParams(150*display.DisplayWidth/800,50*display.DisplayHeight/1216);
        ratetxtParam.leftMargin=537*display.DisplayWidth/800;
        ratetxtParam.topMargin=213*display.DisplayHeight/1216;
        relativeLayout.addView(aliveRatetxt,ratetxtParam);
        Button btn=new Button(this);//ホームに戻るボタンの設定
        btn.setText("戻る");
        btn.setTextSize(20*displayHypotenuse/baseHypotenuse);
        RelativeLayout.LayoutParams btnParam=new RelativeLayout.LayoutParams(250*display.DisplayWidth/800,100*display.DisplayHeight/1216);
        btnParam.topMargin=1050*display.DisplayHeight/1216;
        btnParam.leftMargin=30*display.DisplayWidth/800;
        relativeLayout.addView(btn,btnParam);
        ImageView routeImg=new ImageView(this);//避難結果が表示されている画像の設定
        RelativeLayout.LayoutParams imgParam=new RelativeLayout.LayoutParams(1000*display.DisplayWidth/800,1000*display.DisplayHeight/1216);
        imgParam.topMargin=450*display.DisplayHeight/1216;
        imgParam.leftMargin=15*display.DisplayWidth/800;
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
