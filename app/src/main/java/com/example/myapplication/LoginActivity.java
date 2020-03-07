package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login_button = (Button)findViewById(R.id.button_login);

        login_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "登录成功",Toast.LENGTH_SHORT).show();

                //构造一个Intent
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //启动下一个活动
                startActivity(intent);
            }
        });
    }
}
