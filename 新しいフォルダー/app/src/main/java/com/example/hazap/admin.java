package com.example.hazap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
public class admin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        admincfg();/*admincfg関数を実行*/
    }

    public void admincfg(){
        setContentView(R.layout.activity_admin);
        /**  地震のボタン**/
        Button zisin_button =findViewById(R.id.zisin_button);
        Button tsunami_button=findViewById(R.id.tsunami_button);
        zisin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent zisin_intent = new Intent(getApplication(), Zisin.class);
                startActivity(zisin_intent);/*zisin.classに移動して実行*/
            }
        });
        tsunami_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tsunami_intent = new Intent(getApplication(), Tsunami.class);
                startActivity(tsunami_intent);
            }
        });
    }


}
