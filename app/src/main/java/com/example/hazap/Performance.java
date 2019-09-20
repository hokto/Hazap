package com.example.hazap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class Performance extends Activity {
    private static int aliveRate;//生存率
    private static Bitmap routeMap;//サーバから取得した避難結果の画像を格納
    public int sound_back;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        final SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sound_back = soundPool.load(this, R.raw.back, 1);

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
        MainActivity display=new MainActivity();
        int baseHypotenuse=(int)Math.sqrt(Math.pow(1216,2)+Math.pow(800,2));
        int displayHypotenuse=(int)Math.sqrt(Math.pow(MainActivity.DisplayWidth,2)+Math.pow(MainActivity.DisplayHeight,2));
        routeMap=BitmapFactory.decodeByteArray(routeMapByte,0,routeMapByte.length);
        RelativeLayout relativeLayout=findViewById(R.id.performanceLayout);
        TextView advice=new TextView(this);//主催者からのメッセージに関する設定
        advice.setText("主催者からのメッセージ");
        RelativeLayout.LayoutParams textParam=new RelativeLayout.LayoutParams(300* MainActivity.DisplayWidth /800, 40* MainActivity.DisplayHeight /1216);
        textParam.topMargin=50* MainActivity.DisplayHeight /1216;
        textParam.leftMargin=80* MainActivity.DisplayWidth /800;
        advice.setTextSize(20*displayHypotenuse/baseHypotenuse);
        advice.setPadding(4,2,4,2);
        advice.setBackgroundResource(R.drawable.framestyle);
        relativeLayout.addView(advice,textParam);
        TextView organizerMessage=new TextView(this);
        organizerMessage.setText(message);
        organizerMessage.setTextSize(20*displayHypotenuse/baseHypotenuse);
        organizerMessage.setBackgroundResource(R.drawable.framestyle);
        RelativeLayout.LayoutParams messageParam=new RelativeLayout.LayoutParams(400* MainActivity.DisplayWidth /800,300* MainActivity.DisplayHeight /1216);
        messageParam.topMargin=100* MainActivity.DisplayHeight /1216;
        messageParam.leftMargin=35* MainActivity.DisplayWidth /800;
        relativeLayout.addView(organizerMessage,messageParam);
        TextView rateText=new TextView(this);
        if(aliveRate>=90){
            rateText.setText("S");
        }
        else if(aliveRate>=60){
            rateText.setText("A");
        }
        else if(aliveRate>=40){
            rateText.setText("B");
        }
        else{
            rateText.setText("C");
        }
        rateText.setTextSize(100);
        RelativeLayout.LayoutParams rateParams=new RelativeLayout.LayoutParams(400* MainActivity.DisplayWidth /800,400* MainActivity.DisplayHeight /1216);
        rateParams.leftMargin=600* MainActivity.DisplayWidth /800;
        rateParams.topMargin=100* MainActivity.DisplayHeight /1216;
        relativeLayout.addView(rateText,rateParams);
        Button btn=new Button(this);//ホームに戻るボタンの設定
        Drawable btn_color = ResourcesCompat.getDrawable(getResources(), R.drawable.button_state, null);//リソースから作成したDrawableのリソースを取得
        btn.setBackground(btn_color);//ボタンにDrawableを適用する
        btn.setTextColor(Color.parseColor("#FFFFFF"));//ボタンの文字の色を白に変更する
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);//ボタンの文字の大きさを調節
        btn.setText("戻る");
        btn.setTextSize(20*displayHypotenuse/baseHypotenuse);
        RelativeLayout.LayoutParams btnParam=new RelativeLayout.LayoutParams(250* MainActivity.DisplayWidth /800,100* MainActivity.DisplayHeight /1216);
        btnParam.topMargin=1050* MainActivity.DisplayHeight /1216;
        btnParam.leftMargin=30* MainActivity.DisplayWidth /800;
        relativeLayout.addView(btn,btnParam);
        ImageView routeImg=new ImageView(this);//避難結果が表示されている画像の設定
        RelativeLayout.LayoutParams imgParam=new RelativeLayout.LayoutParams(1000* MainActivity.DisplayWidth /800,1000* MainActivity.DisplayHeight /1216);
        imgParam.topMargin=450* MainActivity.DisplayHeight /1216;
        imgParam.leftMargin=15* MainActivity.DisplayWidth /800;
        routeImg.setImageBitmap(routeMap);
        relativeLayout.addView(routeImg,imgParam);
        btn.setOnClickListener(new View.OnClickListener(){ //ボタンが押された場合、ホームに戻る
            @Override
            public void onClick(View v){
                soundPool.play(sound_back,0.1f,0.1f,0,0,1);
                finish();
            }
        });
    }
}
