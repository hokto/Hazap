package com.example.hazap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SyncAdapterType;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.content.res.ResourcesCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Performance extends Activity {
    private int aliveRate;//生存率
    private Bitmap routeMap;//サーバから取得した避難結果の画像を格納
    private HazapModules modules=new HazapModules();
    private int[] evacuParams=new int[4];//0:Place 1:HP 2:Route 3:Time
    public int sound_back;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ImageView tairyoku = new ImageView(this);
        ImageView time = new ImageView(this);
        ImageView route = new ImageView(this);
        ImageView place = new ImageView(this);
        final SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sound_back = soundPool.load(this, R.raw.back, 1);

        setContentView(R.layout.jisseki);
        BufferedReader resultReader=null;
        String message="";
        String textstring="";
        try{
            resultReader=new BufferedReader(new InputStreamReader(openFileInput("EvacuationResult.txt")));
            String tempStr;
            while((tempStr=resultReader.readLine())!=null){
                textstring+=tempStr;
            }
            resultReader.close();
        }catch(IOException e){
        }
        String[] resultInfo=textstring.split(":",0);
        try{
            aliveRate=Integer.parseInt(resultInfo[0]);
            message=resultInfo[1];
            String[] bytedata=resultInfo[2].split(",",0);
            byte[] routeMapByte=new byte[bytedata.length];
            for(int i=0;i<bytedata.length;i++){
                routeMapByte[i]=Integer.valueOf(bytedata[i]).byteValue();
            }
            for(int i=0;i<4;i++){
                evacuParams[i]=(int)Float.parseFloat(resultInfo[i+3]);
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
            AlphaAnimation feedin =new AlphaAnimation(0,1);//アニメーション処理
            feedin.setDuration(1000);
            modules.JudgeEvacu(relativeLayout,rateText,aliveRate,400,400,700,100,100);//避難評価判定用の関数
            rateText.startAnimation(feedin);
            Button btn=new Button(this);//ホームに戻るボタンの設定
            Drawable btn_color = ResourcesCompat.getDrawable(getResources(), R.drawable.button_state, null);//リソースから作成したDrawableのリソースを取得
            btn.setBackground(btn_color);//ボタンにDrawableを適用する
            btn.setTextColor(Color.parseColor("#FFFFFF"));//ボタンの文字の色を白に変更する
            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP,50);//ボタンの文字の大きさを調節
            btn.setText("戻る");
            RelativeLayout.LayoutParams tairyoku_Param =new RelativeLayout.LayoutParams(80*modules.DispWid()/800,80*modules.DispHei()/1216);
            tairyoku_Param.topMargin=300*modules.DispHei()/1216;
            tairyoku_Param.leftMargin=400*modules.DispWid()/800;
            tairyoku.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.hpbar));
            relativeLayout.addView(tairyoku,tairyoku_Param);
            TextView tairyoku_text = new TextView(this);
            feedin.setDuration(1000);
            tairyoku_text.startAnimation(feedin);
            modules.JudgeEvacu(relativeLayout,tairyoku_text,evacuParams[1],100,100,560,510,40);

            RelativeLayout.LayoutParams time_Param =new RelativeLayout.LayoutParams(80*modules.DispWid()/800,80*modules.DispHei()/1216);
            time_Param.topMargin=300*modules.DispHei()/1216;
            time_Param.leftMargin=490*modules.DispWid()/800;
            time.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.time));
            relativeLayout.addView(time,time_Param);
            TextView time_text = new TextView(this);
            feedin.setDuration(1000);
            time_text.startAnimation(feedin);
            modules.JudgeEvacu(relativeLayout,time_text,evacuParams[3],100,100,680,510,40);

            RelativeLayout.LayoutParams place_Param =new RelativeLayout.LayoutParams(80*modules.DispWid()/800,80*modules.DispHei()/1216);
            place_Param.topMargin=300*modules.DispHei()/1216;
            place_Param.leftMargin=580*modules.DispWid()/800;
            place.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.place_eva));
            relativeLayout.addView(place,place_Param);
            TextView place_text = new TextView(this);
            feedin.setDuration(1000);
            place_text.startAnimation(feedin);
            modules.JudgeEvacu(relativeLayout,place_text,evacuParams[0],100,100,800,510,40);

            RelativeLayout.LayoutParams route_Param =new RelativeLayout.LayoutParams(80*modules.DispWid()/800,80*modules.DispHei()/1216);
            route_Param.topMargin=300*modules.DispHei()/1216;
            route_Param.leftMargin=670*modules.DispWid()/800;
            route.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.route));
            relativeLayout.addView(route,route_Param);
            TextView route_text = new TextView(this);
            feedin.setDuration(1000);
            route_text.startAnimation(feedin);
            modules.JudgeEvacu(relativeLayout,route_text,evacuParams[2],100,100,920,510,40);

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
                    soundPool.play(sound_back,0.2f,0.2f,0,0,1);
                    finish();
                }
            });
        }catch (RuntimeException e){
                Toast errorToast=Toast.makeText(this,"避難を行っていません",Toast.LENGTH_LONG);
                errorToast.setGravity(Gravity.CENTER,0,600);
                errorToast.show();
                finish();
        }
    }
}
