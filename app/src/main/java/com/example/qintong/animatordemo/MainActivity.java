package com.example.qintong.animatordemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.example.qintong.library.InsLoadingView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("qintong1", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InsLoadingView view;
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("qintong1", "onResume");
    }
}
