package com.example.qintong.animatordemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class TestService extends Service {
    Handler mHandler;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("qintong1", "Service onBind");
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("qintong1", "Service onCreate");
        mHandler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("qintong1", "Service onStartCommand: " + (intent == null));
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("qintong1", "Service running ");
                Notification.Builder builder = new Notification.Builder(TestService.this);
                builder.setContentText("sbbbbbbb");
                builder.setSmallIcon(R.mipmap.ic_launcher);
                ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(1,builder.build());
                mHandler.postDelayed(this, 2000);
            }
        }, 2000);
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("qintong1", "Service onTaskRemoved");
    }
}
