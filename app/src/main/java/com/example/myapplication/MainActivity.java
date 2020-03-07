package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //测试日志信息打印
        Log.v(TAG,"This is a verbose log!");
        Log.d(TAG,"This is a Debug log!");
        Log.i(TAG,"This is a Info log!");
        Log.w(TAG,"This is a Warning log!");
        Log.e(TAG,"This is a Error log!");
    }
}
