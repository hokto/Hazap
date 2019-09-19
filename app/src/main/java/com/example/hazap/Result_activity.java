package com.example.hazap;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class Result_activity extends Activity   {
    public static int aliveRate;//生存率
    public static Bitmap routeMap;//サーバから取得した避難結果の画像を格納
    public static String message;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        MainActivity display=new MainActivity();
        int baseHypotenuse=(int)Math.sqrt(Math.pow(1216,2)+Math.pow(800,2));
        int displayHypotenuse=(int)Math.sqrt(Math.pow(display.DisplayWidth,2)+Math.pow(display.DisplayHeight,2));
        final RelativeLayout relativeLayout=findViewById(R.id.resultLayout);
        TextView advice=new TextView(this);//主催者からのメッセージに関する設定
        advice.setText("主催者からのメッセージ");
        advice.setTextSize(10*displayHypotenuse/baseHypotenuse);
        RelativeLayout.LayoutParams textParam=new RelativeLayout.LayoutParams(400*display.DisplayWidth/800,50*display.DisplayHeight/1216);
        textParam.topMargin=50*display.DisplayHeight/1216;
        textParam.leftMargin=80*display.DisplayWidth/800;
        relativeLayout.addView(advice,textParam);
        TextView organizerMessage=new TextView(this);
        organizerMessage.setText(message);
        organizerMessage.setTextSize(12*displayHypotenuse/baseHypotenuse);
        RelativeLayout.LayoutParams messageParam=new RelativeLayout.LayoutParams(350*display.DisplayWidth/800,350*display.DisplayHeight/1216);
        messageParam.topMargin=100*display.DisplayHeight/1216;
        messageParam.leftMargin=35*display.DisplayWidth/800;
        relativeLayout.addView(organizerMessage,messageParam);
        TextView rateText=new TextView(this);
        AlphaAnimation feedin =new AlphaAnimation(1,0);
        feedin.setDuration(1000);
        if(aliveRate>=90){
            rateText.setText("S");
            rateText.setTextColor(Color.YELLOW);
            rateText.startAnimation(feedin);
        }
        else if(aliveRate>=60){
            rateText.setText("A");
            rateText.setTextColor(Color.RED);
            rateText.startAnimation(feedin);
        }
        else if(aliveRate>=40){
            rateText.setText("B");
            rateText.setTextColor(Color.GREEN);
            rateText.startAnimation(feedin);
        }
        else{
            rateText.setText("C");
            rateText.setTextColor(Color.rgb(127,127,0));
            rateText.startAnimation(feedin);
        }
        rateText.setTextSize(100);
        RelativeLayout.LayoutParams rateParams=new RelativeLayout.LayoutParams(400,400);
        rateParams.leftMargin=800;
        rateParams.topMargin=100;
        relativeLayout.addView(rateText,rateParams);
        Button btn=new Button(this);//ホームに戻るボタンの設定
        Drawable btn_color = ResourcesCompat.getDrawable(getResources(), R.drawable.button_state, null);//リソースから作成したDrawableのリソースを取得
        btn.setBackground(btn_color);//ボタンにDrawableを適用する
        btn.setTextColor(Color.parseColor("#FFFFFF"));//ボタンの文字の色を白に変更する
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);//ボタンの文字の大きさを調節
        btn.setText("ホームに戻る");
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
                final ProgressDialog imgsaveDialog=new ProgressDialog(Result_activity.this);
                imgsaveDialog.setTitle("結果を保存中");
                imgsaveDialog.setMessage("リザルト結果を端末に保存しています。");
                imgsaveDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                imgsaveDialog.setCanceledOnTouchOutside(false);
                imgsaveDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            BufferedWriter resultWriter=null;
                            resultWriter = new BufferedWriter(new OutputStreamWriter(openFileOutput("EvacuationResult.txt", Context.MODE_PRIVATE)));
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            routeMap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            String bytedata = "";
                            for (int i = 0; i < baos.toByteArray().length; i++) {
                                bytedata += (Integer.valueOf(baos.toByteArray()[i]).byteValue() + ",");
                            }
                            resultWriter.write(Integer.valueOf(aliveRate) + ":"+message+":" + bytedata);//Aliverate:OrganizerMessage:RouteMapBytes
                            resultWriter.close();
                        }catch(IOException e){
                        }
                        imgsaveDialog.dismiss();
                        finish();
                    }
                }).start();
            }
        });
    }
    @Override
    protected Dialog onCreateDialog(int id){
        return null;
    }
}