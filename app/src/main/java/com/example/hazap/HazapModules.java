package com.example.hazap;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class HazapModules extends Activity{
    private  static int DisplayHeight=0; //端末の縦方向の長さ
    private static int DisplayWidth=0;  //端末の横方向の長さ
    public void Init(WindowManager wm){
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();//端末の情報を取得
        display.getMetrics(displayMetrics);
        DisplayWidth = displayMetrics.widthPixels;//端末の高さ、幅を代入
        DisplayHeight = displayMetrics.heightPixels;
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
}
