package com.example.hazap;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Tsunami extends Activity {/*津波という関数*/
    EditText tsunamiHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tsunami);
        /*使用するボタンの宣言 */
        Button ok=findViewById(R.id.ok);
        tsunamiHeight=(EditText) findViewById(R.id.tsunamiHeight);
        ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //次に飛びたい処理が書かれたクラスに移動
                String text=tsunamiHeight.getText().toString();/*字を取得*/
                finish();/*選択画面から抜ける */
            }
        });

    }
}

