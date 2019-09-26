package com.example.hazap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
public class Result_activity extends Activity   {
    public static int aliveRate;//生存率
    public static Bitmap routeMap;//サーバから取得した避難結果の画像を格納
    public static String message;
    private HazapModules modules=new HazapModules();

    public int sound_back;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        final SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sound_back = soundPool.load(this, R.raw.back, 1);

        setContentView(R.layout.result);
        final RelativeLayout relativeLayout=findViewById(R.id.resultLayout);
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
        feedin.setDuration(2000);
        rateText.startAnimation(feedin);
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
        modules.setView(relativeLayout,rateText,400,400,750,150);
        ImageView routeImg=new ImageView(this);//避難結果が表示されている画像の設定
        RelativeLayout.LayoutParams imgParam=new RelativeLayout.LayoutParams(1000*modules.DispWid()/800,1000*modules.DispHei()/1216);
        imgParam.topMargin=450*modules.DispHei()/1216;
        imgParam.leftMargin=15*modules.DispWid()/800;
        routeImg.setImageBitmap(routeMap);
        relativeLayout.addView(routeImg,imgParam);
        Button back_btn=new Button(this);//ホームに戻るボタンの設定
        Drawable btn_color = ResourcesCompat.getDrawable(getResources(), R.drawable.button_state, null);//リソースから作成したDrawableのリソースを取得
        back_btn.setBackground(btn_color);//ボタンにDrawableを適用する
        back_btn.setTextColor(Color.parseColor("#FFFFFF"));//ボタンの文字の色を白に変更する
        back_btn.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);//ボタンの文字の大きさを調節
        back_btn.setText("ホームに戻る");
        modules.setView(relativeLayout,back_btn,250,100,50,1500);
        back_btn.setOnClickListener(new View.OnClickListener(){ //ボタンが押された場合、ホームに戻る
            @Override
            public void onClick(View v){
                soundPool.play(sound_back,0.2f,0.2f,0,0,1);
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
    @Override
    public void onBackPressed()
    {
    }
}