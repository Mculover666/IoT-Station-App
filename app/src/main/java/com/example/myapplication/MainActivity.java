package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private MqttClient client;
    private MqttConnectOptions options;

    private String host = "tcp://122.51.89.94:1883";
    private String userName = "android";
    private String passWord = "android";
    private String mqtt_id = "client-0002";

    private Handler handler;
    private ScheduledExecutorService scheduler;

    //控件对象列表
    //四个文本显示
    private TextView temp_text_view;
    private TextView humi_text_view;
    private TextView lightness_text_view;
    private TextView gas_text_view;

    //8个设置按钮
    private Button max_temp_set_btn;
    private Button min_temp_set_btn;
    private Button max_humi_set_btn;
    private Button min_humi_set_btn;
    private Button max_light_set_btn;
    private Button min_light_set_btn;
    private Button max_gas_set_btn;
    private Button min_gas_set_btn;

    //8个阈值编辑框
    private EditText max_temp_edittext;
    private EditText min_temp_edittext;
    private EditText max_humi_edittext;
    private EditText min_humi_edittext;
    private EditText max_light_edittext;
    private EditText min_light_edittext;
    private EditText max_gas_edittext;
    private EditText min_gas_edittext;

    private TextView txtDate;
    private TextView txtTime;
    private Button btnDate;
    private Button btnTime;
    DateFormat format= DateFormat.getDateTimeInstance();
    Calendar calendar= Calendar.getInstance(Locale.CHINA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //寻找控件
        txtTime = findViewById(R.id.time_textview);
        temp_text_view = findViewById(R.id.temp_textview);
        humi_text_view = findViewById(R.id.humi_textview);
        lightness_text_view = findViewById(R.id.lightness_textview);
        gas_text_view = findViewById(R.id.gas_textview);

        max_temp_set_btn = findViewById(R.id.max_temp_button);
        min_temp_set_btn = findViewById(R.id.min_temp_button);
        max_humi_set_btn = findViewById(R.id.max_humi_button);
        min_humi_set_btn = findViewById(R.id.min_hump_button);
        max_light_set_btn = findViewById(R.id.max_lux_button);
        min_light_set_btn = findViewById(R.id.min_lux_button);
        max_gas_set_btn = findViewById(R.id.max_gas_button);
        min_gas_set_btn = findViewById(R.id.min_gas_button);

        max_temp_edittext = findViewById(R.id.max_temp_edittext);
        min_temp_edittext = findViewById(R.id.min_temp_edittext);
        max_humi_edittext = findViewById(R.id.max_humi_edittext);
        min_humi_edittext = findViewById(R.id.min_hump_edittext);
        max_light_edittext = findViewById(R.id.max_lux_edittext);
        min_light_edittext = findViewById(R.id.min_lux_edittext);
        max_gas_edittext = findViewById(R.id.max_gas_edittext);
        min_gas_edittext = findViewById(R.id.min_gas_edittext);

        Button login_button = (Button)findViewById(R.id.button_timer);
        login_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showTimePickerDialog(MainActivity.this,  4, txtTime, calendar);
            }
        });

        Mqtt_init();
        startReconnect();

        //设置温度最大按钮回调函数
        max_temp_set_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try{
                    String max_temp_str = max_temp_edittext.getText().toString();
                    int max_temp_value = Integer.valueOf(max_temp_str);
                    String max_temp_json = "{\"name\":\"max_temp\",\"max_temp\":"+String.valueOf(max_temp_value)+"}";          //{"name":"max_temp","max_temp":"23"}
                    publishmessageplus("sub_test",max_temp_json);
                    Toast.makeText(MainActivity.this, max_temp_json,Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, "设置失败，请输入正确整数值",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //设置温度最小按钮回调函数
        min_temp_set_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try{
                    String min_temp_str = min_temp_edittext.getText().toString();
                    int min_temp_value = Integer.valueOf(min_temp_str);
                    String min_temp_json = "{\"name\":\"min_temp\",\"min_temp\":"+String.valueOf(min_temp_value)+"}";          //{"max_temp":"23"}
                    publishmessageplus("sub_test",min_temp_json);
                    Toast.makeText(MainActivity.this, min_temp_json,Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, "设置失败，请输入正确整数值",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //设置湿度最大按钮回调函数
        max_humi_set_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try{
                    String max_humi_str = max_humi_edittext.getText().toString();
                    int max_humi_value = Integer.valueOf(max_humi_str);
                    String max_humi_json = "{\"name\":\"max_humi\",\"max_humi\":"+String.valueOf(max_humi_value)+"}";          //{"max_temp":"23"}
                    publishmessageplus("sub_test",max_humi_json);
                    Toast.makeText(MainActivity.this, max_humi_json,Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, "设置失败，请输入正确整数值",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //设置湿度最小按钮回调函数
        min_humi_set_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try{
                    String min_humi_str = min_humi_edittext.getText().toString();
                    int min_humi_value = Integer.valueOf(min_humi_str);
                    String min_humi_json = "{\"name\":\"min_humi\",\"min_humi\":"+String.valueOf(min_humi_value)+"}";          //{"max_temp":"23"}
                    publishmessageplus("sub_test",min_humi_json);
                    Toast.makeText(MainActivity.this, min_humi_json,Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, "设置失败，请输入正确整数值",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //设置甲醛最大按钮回调函数
        max_light_set_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try{
                    String max_lux_str = max_light_edittext.getText().toString();
                    int max_lux_value = Integer.valueOf(max_lux_str);
                    String max_lux_json = "{\"name\":\"max_lux\",\"max_lux\":"+String.valueOf(max_lux_value)+"}";          //{"max_temp":"23"}
                    publishmessageplus("sub_test",max_lux_json);
                    Toast.makeText(MainActivity.this, max_lux_json,Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, "设置失败，请输入正确整数值",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //设置甲醛最小按钮回调函数
        min_light_set_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try{
                    String min_lux_str = min_light_edittext.getText().toString();
                    int min_lux_value = Integer.valueOf(min_lux_str);
                    String min_lux_json = "{\"name\":\"min_lux\",\"min_lux\":"+String.valueOf(min_lux_value)+"}";          //{"max_temp":"23"}
                    publishmessageplus("sub_test",min_lux_json);
                    Toast.makeText(MainActivity.this, min_lux_json,Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, "设置失败，请输入正确整数值",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //设置气体最大按钮回调函数
        max_gas_set_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try{
                    String max_gas_str = max_gas_edittext.getText().toString();
                    int max_gas_value = Integer.valueOf(max_gas_str);
                    String max_gas_json = "{\"name\":\"max_gas\",\"max_gas\":"+String.valueOf(max_gas_value)+"}";          //{"max_temp":"23"}
                    publishmessageplus("sub_test",max_gas_json);
                    Toast.makeText(MainActivity.this, max_gas_json,Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, "设置失败，请输入正确整数值",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //设置气体最小按钮回调函数
        min_gas_set_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try{
                    String min_gas_str = min_gas_edittext.getText().toString();
                    int min_gas_value = Integer.valueOf(min_gas_str);
                    String min_gas_json = "{\"name\":\"min_gas\",\"min_gas\":"+String.valueOf(min_gas_value)+"}";          //{"max_temp":"23"}
                    publishmessageplus("sub_test",min_gas_json);
                    Toast.makeText(MainActivity.this, min_gas_json,Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, "设置失败，请输入正确整数值",Toast.LENGTH_SHORT).show();
                }
            }
        });

        handler = new Handler() {
            @SuppressLint("SetTextI18n")
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1: 
                        break;
                    case 2:
                        break;
                    case 3:
                        temp_text_view.setText(msg.obj.toString());
                        break;
                    case 4: 
                        humi_text_view.setText(msg.obj.toString());
                        break;
                    case 5:  
                        lightness_text_view.setText(msg.obj.toString());
                        break;
                    case 6:  
                        gas_text_view.setText(msg.obj.toString());
                        break;
                    case 30:  //连接失败
                        Toast.makeText(MainActivity.this,"连接失败" ,Toast.LENGTH_SHORT).show();
                        break;
                    case 31:   //连接成功
                        Toast.makeText(MainActivity.this,"连接成功" ,Toast.LENGTH_SHORT).show();
                        try {
                            client.subscribe("temp",1);
                            client.subscribe("humi",1);
                            client.subscribe("ch20",1);
                            client.subscribe("gas",1);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }
    /**
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getIMEI(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Method method = manager.getClass().getMethod("getImei", int.class);
            String imei1 = (String) method.invoke(manager, 0);
            String imei2 = (String) method.invoke(manager, 1);
            if(TextUtils.isEmpty(imei2)){
                return imei1;
            }
            if(!TextUtils.isEmpty(imei1)){
                //因为手机卡插在不同位置，获取到的imei1和imei2值会交换，所以取它们的最小值,保证拿到的imei都是同一个
                String imei = "";
                if(imei1.compareTo(imei2) <= 0){
                    imei = imei1;
                }else{
                    imei = imei2;
                }
                return imei;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return manager.getDeviceId();
        }
        return "";
    }

    public void showTimePickerDialog(Activity activity, int themeResId, final TextView tv, Calendar calendar) {
        new TimePickerDialog( activity,themeResId,
                // 绑定监听器
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        tv.setText(hourOfDay + ":" + minute);
                        try{
                            String time_json = "{\"name\":\"alarm\",\"hour\":"+String.valueOf(hourOfDay)+",\"min\":"+String.valueOf(minute)+"}";          //{"max_temp":"23"}
                            publishmessageplus("sub_test",time_json);
                            Toast.makeText(MainActivity.this, "定时成功",Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e) {
                            Toast.makeText(MainActivity.this, "定时失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                // 设置初始时间
                , calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE)
                // true表示采用24小时制
                ,true).show();
    }

    //Mqtt初始化函数
    private void Mqtt_init()
    {
        try {
            client = new MqttClient(host, mqtt_id,
                    new MemoryPersistence());
            options = new MqttConnectOptions();
           
            options.setCleanSession(false);
            
            options.setUserName(userName);
            
            options.setPassword(passWord.toCharArray());
            
            options.setConnectionTimeout(10);
            
            options.setKeepAliveInterval(20);
            
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                   
                    System.out.println("connectionLost----------");
                   
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                   
                    System.out.println("deliveryComplete---------"
                            + token.isComplete());
                }
                @Override
                public void messageArrived(String topicName, MqttMessage message)
                        throws Exception {
                   
                    System.out.println("messageArrived----------");
                    Message msg = new Message();
                    if(topicName.equals("temp"))
                    {
                        //收到温度数据
                        msg.what = 3;   //收到消息标志位
                        msg.obj = message.toString() + " ℃";
                        handler.sendMessage(msg);    // hander 回传
                    }
                    else if (topicName.equals("humi"))
                    {
                        //收到湿度数据
                        msg.what = 4;   //收到消息标志位
                        msg.obj = message.toString() + " %RPH";
                        handler.sendMessage(msg);    // hander 回传
                    }
                    else if(topicName.equals("ch20"))
                    {
                        //收到亮度数据
                        msg.what = 5;   //收到消息标志位
                        msg.obj = message.toString() + " mg/m3";
                        handler.sendMessage(msg);    // hander 回传
                    }
                    else if(topicName.equals("gas"))
                    {
                        //收到气体数据
                        msg.what = 6;   //收到消息标志位
                        msg.obj = message.toString() + " ug/m3";
                        handler.sendMessage(msg);    // hander 回传
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //MQTT连接EMQ-X函数
    private void Mqtt_connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(!(client.isConnected()) )  //如果还未连接
                    {
                        client.connect(options);
                        Message msg = new Message();
                        msg.what = 31;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 30;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
    //MQTT重连
    private void startReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!client.isConnected()) {
                    Mqtt_connect();
                }
            }
        }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
    }
    //MQTT发布消息
    private void publishmessageplus(String topic,String message2)
    {
        if (client == null || !client.isConnected()) {
            return;
        }
        MqttMessage message = new MqttMessage();
        message.setPayload(message2.getBytes());
        try {
            client.publish(topic,message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
