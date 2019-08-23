package com.example.hazap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Zisin extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zisin);
        Button zisin_button=findViewById(R.id.zisin_button);

        zisin_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                //次に飛びたい処理が書かれたクラスに移動
            }
        });
    }
}
