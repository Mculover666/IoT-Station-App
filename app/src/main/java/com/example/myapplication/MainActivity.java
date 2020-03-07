package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button login_button = (Button)findViewById(R.id.button_exit);

        login_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "退出成功",Toast.LENGTH_SHORT).show();

                //构造一个Intent
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                //启动下一个活动
                startActivity(intent);
            }
        });
    }
}
