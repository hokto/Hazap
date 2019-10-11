package com.example.hazap;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HazapModules extends Activity{
    private  static int DisplayHeight=0; //端末の縦方向の長さ
    private static int DisplayWidth=0;  //端末の横方向の長さ
    private static float HeightDpi=0;
    private static float WidthDpi=0;
    public void Init(WindowManager wm){
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();//端末の情報を取得
        display.getMetrics(displayMetrics);
        DisplayWidth = displayMetrics.widthPixels;//端末の高さ、幅を代入
        DisplayHeight = displayMetrics.heightPixels;
        HeightDpi=displayMetrics.xdpi;
        WidthDpi=displayMetrics.ydpi;
    }
    public void setView(RelativeLayout relativeLayout, View view,int width,int height,int lmargin,int tmargin){
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(width*DisplayWidth/800,height*DisplayHeight/1216);
        params.leftMargin=lmargin*DisplayWidth/1080;
        params.topMargin=tmargin*DisplayHeight/1794;
        relativeLayout.addView(view,params);
    }
    public int DispWid(){
        return DisplayWidth;
    }
    public int DispHei(){
        return DisplayHeight;
    }
    public void JudgeEvacu(RelativeLayout relativeLayout, TextView view, int target, int width, int height, int lmargin, int tmargin, int textSize){
        if(target>=90){
            view.setText("S");
            view.setTextColor(Color.parseColor("#DAA520"));
        }
        else if(target>=70){
            view.setText("A");
            view.setTextColor(Color.parseColor("#fc0101"));
        }
        else if(target>=50){
            view.setText("B");
            view.setTextColor(Color.parseColor("#0101fc"));
        }
        else {
            view.setText("C");
            view.setTextColor(Color.parseColor("#fcfc01"));
        }
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP,getTextHeight(textSize));
        setView(relativeLayout,view,width,height,lmargin,tmargin);
    }
    public float getTextHeight(int height){
        float widthInch=DisplayWidth/WidthDpi;
        float heightInch=DisplayHeight/HeightDpi;
        float dispInch=(float)Math.sqrt(Math.pow(widthInch,2)+Math.pow(heightInch,2));
        if(dispInch>4){
            return  dispInch/(float) 4.0*height;
        }
        else{
            return height;
        }
    }
}
