package com.prince.login_oa;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class SMSActivity extends AppCompatActivity {
    private Button sendPhonenumber;
    private Button commitNumber;
    private EditText phoneNumber;
    private EditText number;
    public EventHandler eventHandler;
    private TimeCount mTimeCount;

    /*
    SMS验证码Mob平台
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        initEvent();
        init();
        Log.d("SMS测试页面","初始化成功！");

        commitNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!phoneNumber.getText().toString().trim().equals("")) {
                    if (checkTel(phoneNumber.getText().toString().trim())) {
                        if (!number.getText().toString().trim().equals("")) {
                            Log.d("SMS测试页面","SMSSDK.sub调用");
                            SMSSDK.submitVerificationCode("86",phoneNumber.getText().toString().trim(),number.getText().toString().trim());//提交验证
                        }else{
                            Toast.makeText(SMSActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(SMSActivity.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(SMSActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                }

            }
        });
        sendPhonenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SMSSDK.getSupportedCountries();//获取短信目前支持的国家列表
                if(!phoneNumber.getText().toString().trim().equals("")){
                    if (checkTel(phoneNumber.getText().toString().trim())) {
                        SMSSDK.getVerificationCode("86",phoneNumber.getText().toString());//获取验证码
                        mTimeCount.start();
                    }else{
                        Toast.makeText(SMSActivity.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(SMSActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void initEvent(){
        phoneNumber=findViewById(R.id.sms_phone);
        number=findViewById(R.id.sms_number);
        sendPhonenumber=findViewById(R.id.sendPhone_btn);
        commitNumber=findViewById(R.id.commit_number_btn);
        //sendPhonenumber.setOnClickListener(this);
        //commitNumber.setOnClickListener();
        mTimeCount = new TimeCount(120000, 1000);
    }
    /*
     *初始化事件接收器
     */
    private void init(){
        eventHandler=new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                Log.d("SMS测试页面","EventHandler()");
                if(result == SMSSDK.RESULT_COMPLETE){//回调完成
                    Log.d("SMS测试页面","回调完成");
                    Log.d("SMS测试页面",String.valueOf(event));
                    if(event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){//提交验证码成功  3
                      //  Toast.makeText(SMSActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                        Log.d("SMS测试页面","验证成功！");
                    }
                    else if(event ==SMSSDK.EVENT_GET_VERIFICATION_CODE){//获取验证码成功   2
                        Log.d("SMS测试页面","获取验证码成功！");
                    }
                    else if(event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//返回支持发送验证码的国家列表
                        Log.d("SMS测试页面","支持国家列表");
                    }
                    else{
                        ((Throwable)data).printStackTrace();
                        String string=data.toString();
                        Log.d("SMS测试页面","验证失败！错误信息"+string);
                        //Toast.makeText(SMSActivity.this, string, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Log.d("SMS测试页面","回调不成功，检查输入手机号码及验证码！");
                    Toast.makeText(SMSActivity.this, "验证失败，请检查验证码是否正确，稍后再试!", Toast.LENGTH_SHORT).show();
                }
            }
        };
        SMSSDK.registerEventHandler(eventHandler); //注册短信回调
    }

    /**
     * 正则匹配手机号码
     */
    public boolean checkTel(String tel){
        Pattern p = Pattern.compile(getString(R.string.tel));
        Matcher matcher = p.matcher(tel);
        return matcher.matches();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }
    /**
     * 计时器
     */
    class TimeCount extends CountDownTimer {

        TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long l) {
            sendPhonenumber.setClickable(false);
            sendPhonenumber.setText(l/1000 + "秒后重新获取");
        }

        @Override
        public void onFinish() {
            sendPhonenumber.setClickable(true);
            sendPhonenumber.setText("获取验证码");
        }
    }
}
