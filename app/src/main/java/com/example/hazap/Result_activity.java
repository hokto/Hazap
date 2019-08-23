package com.example.hazap;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
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

public class Result_activity extends Activity   {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        ImageView dragView=findViewById(R.id.Map);
        ImageView dragView2=findViewById(R.id.MyRefuge);
        ImageView dragView3=findViewById(R.id.Bestrefuge);
        TextView advice=findViewById(R.id.Advice);
        String data="";
        data=text_change(data);
        advice.setText(data);
        DragViewListener listener=new DragViewListener(dragView,dragView2,dragView3);
        dragView.setOnTouchListener(listener);
        Aliverate_print();
        Button btn=findViewById(R.id.backhome_btn);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }
    public static String text_change(String text){
        try{
            ServerSocket hazap_client = new ServerSocket(10000);
            Socket sock = hazap_client.accept();
            byte[] data = new byte[1024];
            InputStream in = sock.getInputStream();
            int readSize = in.read(data);
            data = Arrays.copyOf(data, readSize);
            text=(new String(data,"UTF-8"));
            in.close();
            hazap_client.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        return text;
    }
    public class DragViewListener implements View.OnTouchListener{
        private ImageView dragView;
        private ImageView dragView2;
        private ImageView dragView3;
        private int oldx;
        private int oldy;
        private DragViewListener(ImageView dragView,ImageView dragView2,ImageView dragView3){
            this.dragView=dragView;
            this.dragView2=dragView2;
            this.dragView3=dragView3;
        }
        @Override
        public boolean onTouch(View view,MotionEvent event){
            int x=(int)event.getRawX();
            int y=(int)event.getRawY();
            switch(event.getAction()){
                case MotionEvent.ACTION_MOVE:
                    int left=dragView.getLeft()+(x-oldx);
                    int left2=dragView2.getLeft()+(x-oldx);
                    int left3=dragView3.getLeft()+(x-oldx);
                    int top=dragView.getTop()+(y-oldy);
                    int top2=dragView2.getTop()+(y-oldy);
                    int top3=dragView3.getTop()+(y-oldy);
                    dragView.layout(left,top,left+dragView.getWidth(),top+dragView.getHeight());
                    dragView2.layout(left2,top2,left2+dragView2.getWidth(),dragView2.getHeight());
                    dragView3.layout(left3,top3,left3+dragView3.getWidth(),dragView3.getHeight());
                    break;
            }
            oldx=x;
            oldy=y;
            return true;
        }
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
