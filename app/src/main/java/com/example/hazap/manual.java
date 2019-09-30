package com.example.hazap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.joanzapata.pdfview.PDFView;

public class manual extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf);

        PDFView pdfView = (PDFView) findViewById(R.id.pdfview);
        pdfView.fromAsset("manual.pdf")
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
                .load();
    }
}
