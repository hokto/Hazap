package com.example.hazap;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Zisin extends Activity {
    EditText  zisinphase;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zisin);
        /*下で使う処理用にボタンアドをあらかじめ宣言*/
        zisinphase=(EditText) findViewById(R.id.zisinphase);
        Button enter=findViewById(R.id.enter);
        enter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //次に飛びたい処理が書かれたクラスに移動
                String text=zisinphase.getText().toString();/*字を取得*/
                finish();
            }
        });


    }
}