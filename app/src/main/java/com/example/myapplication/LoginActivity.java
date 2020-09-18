package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.util.Log;

import org.w3c.dom.Text;

import static android.util.Log.i;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login_button = (Button)findViewById(R.id.button_login);

        login_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                TextView username_textview = (TextView)findViewById(R.id.username_edittext);
                TextView passwd_textview = (TextView)findViewById(R.id.passwd_edittext);

                String username = username_textview.getText().toString();
                String passwd = passwd_textview.getText().toString();

                Log.i("LoginActivity", "usename:"+username);
                Log.i("LoginActivity", "passwd:"+passwd);

                if (username.equals("2016211961") != true || passwd.equals("123456") != true) {
                    Toast.makeText(LoginActivity.this, "啊哦~登录失败！",Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(LoginActivity.this, "登录成功",Toast.LENGTH_SHORT).show();

                //构造一个Intent
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //启动下一个活动
                startActivity(intent);
            }
        });
    }
}
