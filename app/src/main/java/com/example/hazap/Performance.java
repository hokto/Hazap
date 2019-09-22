package com.example.hazap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
public class Performance extends Activity {
    private static int aliveRate;//生存率
    private static Bitmap routeMap;//サーバから取得した避難結果の画像を格納
    private HazapModules modules=new HazapModules();
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jisseki);
        BufferedReader resultReader=null;
        String message;
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
        message=resultInfo[1];
        String[] bytedata=resultInfo[2].split(",",0);
        byte[] routeMapByte=new byte[bytedata.length];
        for(int i=0;i<bytedata.length;i++){
            routeMapByte[i]=Integer.valueOf(bytedata[i]).byteValue();
        }
        routeMap=BitmapFactory.decodeByteArray(routeMapByte,0,routeMapByte.length);
        RelativeLayout relativeLayout=findViewById(R.id.performanceLayout);
        TextView advice=new TextView(this);//主催者からのメッセージに関する設定
        advice.setText("主催者からのメッセージ");
        advice.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        advice.setPadding(4,2,4,2);
        advice.setBackgroundResource(R.drawable.framestyle);
        modules.setView(relativeLayout,advice,300,40,100,80);
        TextView organizerMessage=new TextView(this);
        organizerMessage.setText(message);
        organizerMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
        organizerMessage.setBackgroundResource(R.drawable.framestyle);
        modules.setView(relativeLayout,organizerMessage,400,300,50,150);
        TextView rateText=new TextView(this);
        AlphaAnimation feedin =new AlphaAnimation(0,1);
        feedin.setDuration(1000);
        if(aliveRate>=90){
            rateText.setText("S");
            rateText.setTextColor(Color.parseColor("#DAA520"));
        }
        else if(aliveRate>=60){
            rateText.setText("A");
            rateText.setTextColor(Color.parseColor("#fc0101"));
        }
        else if(aliveRate>=40){
            rateText.setText("B");
            rateText.setTextColor(Color.parseColor("#0101fc"));
        }
        else {
            rateText.setText("C");
            rateText.setTextColor(Color.parseColor("#fcfc01"));
        }
        rateText.setTextSize(100);
        rateText.startAnimation(feedin);
        modules.setView(relativeLayout,rateText,400,400,700,100);
        Button btn=new Button(this);//ホームに戻るボタンの設定
        Drawable btn_color = ResourcesCompat.getDrawable(getResources(), R.drawable.button_state, null);//リソースから作成したDrawableのリソースを取得
        btn.setBackground(btn_color);//ボタンにDrawableを適用する
        btn.setTextColor(Color.parseColor("#FFFFFF"));//ボタンの文字の色を白に変更する
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP,50);//ボタンの文字の大きさを調節
        btn.setText("戻る");
        modules.setView(relativeLayout,btn,250,100,50,1500);
        ImageView routeImg=new ImageView(this);//避難結果が表示されている画像の設定
        RelativeLayout.LayoutParams imgParam=new RelativeLayout.LayoutParams(1000*modules.DispWid()/800,1000*modules.DispHei()/1216);
        imgParam.topMargin=450*modules.DispHei()/1216;
        imgParam.leftMargin=15*modules.DispWid()/800;
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
