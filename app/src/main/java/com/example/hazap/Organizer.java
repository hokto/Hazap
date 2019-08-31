package com.example.hazap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import static android.support.v4.view.ViewCompat.generateViewId;

public class Organizer extends Activity {
    private String[] disasterItems={" ","地震","津波"};
    private String[] earthquakeItems={"震度1","震度2","震度3","震度4","震度5弱","震度5強","震度6弱","震度6強","震度7"};
    private String[] tsunamiItems={"規模小","規模大"};
    public static int allPlayers=0;
    private TextView playerNumText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizerhome);
        final Spinner disasterSpinner=findViewById(R.id.disasterspinner);
        final RelativeLayout relativeLayout=findViewById(R.id.relativeLayout);
        final Spinner nextSpinner=new Spinner(this);
        final MainActivity playDisplay=new MainActivity();
        ArrayAdapter<String> disasterAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,disasterItems);
        disasterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        disasterSpinner.setAdapter(disasterAdapter);
        disasterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int positio, long id){
                    switch((String)(((Spinner)parent).getSelectedItem())){
                        case "地震":
                            setSpinnerItems(nextSpinner,earthquakeItems,relativeLayout,playDisplay);
                            break;
                        case "津波":
                            setSpinnerItems(nextSpinner,tsunamiItems,relativeLayout,playDisplay);
                            break;
                        case " ":
                            String[] noneItems={"災害を選択してください"};
                            setSpinnerItems(nextSpinner,noneItems,relativeLayout,playDisplay);
                            break;
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent){
                    //何も選択されていないときは特に何もしない
                }
        });
        Button startbtn=new Button(this);
        startbtn.setText("訓練開始");
        RelativeLayout.LayoutParams btnParam=new RelativeLayout.LayoutParams(400*playDisplay.DisplayWidth/1080,150*playDisplay.DisplayHeight/1794);
        btnParam.leftMargin=540*playDisplay.DisplayWidth/1080;
        btnParam.topMargin=1500*playDisplay.DisplayHeight/1794;
        relativeLayout.addView(startbtn,btnParam);
        final Timer timer=new Timer();
        final Handler handler=new Handler();
        playerNumText=new TextView(this);
        RelativeLayout.LayoutParams textParam=new RelativeLayout.LayoutParams(500,80);
        textParam.leftMargin=300;
        textParam.topMargin=1200;
        playerNumText.setTextSize(25);
        relativeLayout.addView(playerNumText,textParam);
        final Server_activity organizerSocket=new Server_activity();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        organizerSocket.Connect("Allpeople:",null,null,Organizer.this);
                        playerNumText.setText("参加人数:"+Integer.valueOf(allPlayers));
                        try{
                            Thread.sleep(100); //100ミリ秒Sleepする（通信側の処理を反映させるため）
                        }catch(Exception e){}
                    }
                });
            }
        },0,1000);
        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //避難開始
                timer.cancel();
                organizerSocket.Connect("Start:0,0:"+(String)disasterSpinner.getSelectedItem()+":"+(String) nextSpinner.getSelectedItem(),null,null,null);
            }
        });
    }
    private void setSpinnerItems(Spinner spinner,String[] items,RelativeLayout relativeLayout,MainActivity playDisplay){
        relativeLayout.removeView(spinner);
        ArrayAdapter nextAdapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item,items);
        nextAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(nextAdapter);
        RelativeLayout.LayoutParams nextParams=new RelativeLayout.LayoutParams(700*playDisplay.DisplayWidth/1080,200*playDisplay.DisplayHeight/1794);
        nextParams.leftMargin=200*playDisplay.DisplayWidth/1080;
        nextParams.topMargin=600*playDisplay.DisplayHeight/1794;
        relativeLayout.addView(spinner,nextParams);
    }
}
