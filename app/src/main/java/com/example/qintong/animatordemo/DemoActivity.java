package com.example.qintong.animatordemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import com.qintong.library.InsLoadingView;

import static android.widget.Toast.LENGTH_SHORT;

public class DemoActivity extends AppCompatActivity {
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
        mInsLoadingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mInsLoadingView.getStatus()) {
                    case UNCLICKED:
                        mInsLoadingView.setStatus(InsLoadingView.Status.LOADING);
                        break;
                    case LOADING:
                        mInsLoadingView.setStatus(InsLoadingView.Status.CLICKED);
                        break;
                    case CLICKED:
                        mInsLoadingView.setStatus(InsLoadingView.Status.UNCLICKED);
                }
                Toast.makeText(DemoActivity.this,"click !", LENGTH_SHORT).show();
            }
        });
        mInsLoadingView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(DemoActivity.this,"long click !", LENGTH_SHORT).show();
                return true;
            }
        });
    }
}
