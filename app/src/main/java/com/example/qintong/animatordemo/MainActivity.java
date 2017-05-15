package com.example.qintong.animatordemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.example.qintong.library.InsLoadingView;

public class MainActivity extends AppCompatActivity {
    InsLoadingView mInsLoadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInsLoadingView = (InsLoadingView) findViewById(R.id.loading_view);
/*        mInsLoadingView.setCircleDuration(2000);
        mInsLoadingView.setRotateDuration(10000);
        mInsLoadingView.setStartColor(Color.YELLOW);
        mInsLoadingView.setEndColor(Color.BLUE);*/
    }
}
