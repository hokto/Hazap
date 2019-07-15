package com.example.hazap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class Result_activity extends Activity implements View.OnTouchListener  {
    private ImageView.ScaleType mImageScaleType = ImageView.ScaleType.CENTER;
    private int mOverX;
    private int mOverY;
    private ImageView mImageView;
    private float mTouchBeginX;
    private float mTouchBeginY;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Result_print();
        this.setContentView(R.layout.result);
        this.mImageView = (this.findViewById(R.id.Map));
        this.mImageView.setScaleType(this.mImageScaleType);
        this.mImageView.setOnTouchListener(this);
        this.updateOverSize();
    }
    public void Result_print() {
        Bestrefuge_print();
        Myrefuge_print();
        Aliverate_print();
    }
    private static int calcOverValue(int display, int image) {
        return (display < image ? (image - display) / 2 : 0);
    }
    private static int calcScrollValue(int move, int pos, int over) {
        int newPos = pos + move;
        if (newPos < -over) {
            move = -(over + pos);
        } else if (over < newPos) {
            move = over - pos;
        }
        return move;
    }
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.updateOverSize();
        this.mImageView.scrollTo(0, 0);
    }
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View v, MotionEvent event) {
        if (this.mImageScaleType == ImageView.ScaleType.FIT_CENTER) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.mTouchBeginX = event.getX();
                this.mTouchBeginY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                this.scrollImage(x, y);
                this.mTouchBeginX = x;
                this.mTouchBeginY = y;
                break;
            case MotionEvent.ACTION_UP:
                this.scrollImage(event.getX(), event.getY());
                break;
        }
        return true;
    }
    private void scrollImage(float x, float y) {
        int moveX;
        int moveY;
        if(this.mOverX==0) {
            moveX = 0;
        }else{
            moveX=calcScrollValue((int) (this.mTouchBeginX - x), this.mImageView.getScrollX(), this.mOverX);
        }
        if(this.mOverY==0){
            moveY=0;
        }else{
            moveY=calcScrollValue((int) (this.mTouchBeginY - y), this.mImageView.getScrollY(), this.mOverY);
        }
        this.mImageView.scrollBy(moveX, moveY);
    }

    private void updateOverSize() {
        Display display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Drawable image = this.mImageView.getDrawable();
        this.mOverX = calcOverValue(display.getWidth(), image.getIntrinsicWidth());
        this.mOverY = calcOverValue(display.getHeight(), image.getIntrinsicHeight());
    }

    private void Bestrefuge_print() {

    }

    private void Myrefuge_print() {

    }

    private void Aliverate_print() {
        float aliverate = 70.0f;
        float ather = 100.0f - aliverate;
        float[] Rate = {aliverate, ather};
        String[] Names = {"生存率", ""};

        List<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            pieEntries.add(new PieEntry(Rate[i], Names[i]));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "生存率");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);

        PieChart piechart = findViewById(R.id.pie_chart);
        piechart.setData(data);
        piechart.invalidate();
    }
}