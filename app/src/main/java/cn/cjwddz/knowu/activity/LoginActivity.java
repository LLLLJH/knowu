package cn.cjwddz.knowu.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.common.http.MyHTTPClient;
import cn.cjwddz.knowu.common.utils.MyUtils;
import cn.cjwddz.knowu.service.Constants;
import cn.cjwddz.knowu.service.MyInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends BaseActivity implements MyInterface {

    private EditText et1;
    private EditText et2;
    private EditText et3;
    private EditText et4;
    private int et1_index = 1;
    private int et2_index = 1;
    private int et3_index = 1;
    private int et4_index = 1;
    private boolean del_status;
    private Button login;
    private boolean isLoginable = false;
    private String phoneNumber;
    private MyInterface myInterface;

    private String vCode;
    private String str;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    public void setMyInterface(MyInterface myInterface){
        this.myInterface = myInterface;
    }
    @Override
    void initView() {
        setContentView(R.layout.activity_login);
        setMyInterface(this);
        login = (Button) findViewById(R.id.login);
        et1 = (EditText) findViewById(R.id.et1);
        et2 = (EditText) findViewById(R.id.et2);
        et3 = (EditText) findViewById(R.id.et3);
        et4 = (EditText) findViewById(R.id.et4);
        et1.setOnKeyListener(keyListener);
        et2.setOnKeyListener(keyListener);
        et3.setOnKeyListener(keyListener);
        et4.setOnKeyListener(keyListener);

        et1.addTextChangedListener(new TextWatcher() {
            private CharSequence charSequence;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               this.charSequence = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(charSequence.length() == 1){
                    et2.requestFocus();
                    et2.setEnabled(true);
                    et1_index = 1;
                    et2_index = 0;
                }

            }
        });

        et2.addTextChangedListener(new TextWatcher() {
            private CharSequence charSequence;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                this.charSequence = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(charSequence.length() == 1){
                    et3.requestFocus();
                    et3.setEnabled(true);
                    et3_index = 0;
                    et2_index = 1;
                }
               // if(charSequence.length()==0){
                //  et2_index = 0;
               // }
            }
        });

        et3.addTextChangedListener(new TextWatcher() {
            private CharSequence charSequence;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                this.charSequence = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(charSequence.length() == 1){
                    et4.requestFocus();
                    et4.setEnabled(true);
                    et4_index =0;
                    et3_index = 1;
                }
            }
        });



        et4.addTextChangedListener(new TextWatcher() {
            private CharSequence charSequence;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                this.charSequence = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(charSequence.length() == 1){
                  login.setBackgroundResource(R.drawable.login_selector);
                    et4_index = 1;
                    isLoginable = true;
                }
               if(charSequence.length()==0 ){
                   login.setBackgroundResource(R.drawable.login);
                   isLoginable = false;
               }
            }
        });

    }

    @Override
    void initData() {
        //获取内存储中的用户信息及事务提交
        sp = getSharedPreferences("knowu",MODE_PRIVATE);
        editor = sp.edit();
        phoneNumber = sp.getString("phoneNumber",null);
    }

    View.OnKeyListener keyListener =new View.OnKeyListener(){
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            switch (view.getId()){
                case R.id.et1:

                    break;
                case R.id.et2:
                    et2_index--;
                    if(i == keyEvent.KEYCODE_DEL && et2_index ==-1 ){
                        et1.setText("");
                        et1.requestFocus();
                        et2.setEnabled(false);
                    }

                    break;
                case R.id.et3:
                    et3_index--;
                    if(i == keyEvent.KEYCODE_DEL && et3_index ==-1 ){
                        et2.setText("");
                        et2.requestFocus();
                        et3.setEnabled(false);
                    }

                    break;
                case R.id.et4:
                    if(i == keyEvent.KEYCODE_DEL && et4_index ==-1 ){
                        et3.setText("");
                        et3.requestFocus();
                        et4.setEnabled(false);
                    }
                    et4_index--;
                    break;
                default:break;
            }
            return false;
        }
    };

    /**
     * 请求登录
     * */
    public  void getLogin(String url, final String phoneNumber){
        OkHttpClient okHttpClient = MyHTTPClient.getInstance().getOkHttpClient();
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("phone",phoneNumber);
        formBody.add("password", MyUtils.md5(phoneNumber));
        //formBody.add("vcode",vCode);
        final Request request = new Request.Builder()
                .url(url)
                .header(Constants.HEADER_CONTENT_TYPE_KEY,Constants.HEADER_CONTENT_TYPE_VAULE)
                .post(formBody.build())
                .build();
        //System.out.println("请求登录！！！");
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                myInterface.failure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                str = response.body().string();
                boolean status = false;
                String msg = null;
                System.out.println(response.code());
                System.out.println(str);
                if(!str.isEmpty()){
                    try {
                        JSONObject jsonObject = new JSONObject(str);
                        status = jsonObject.getBoolean("status");
                        msg = jsonObject.getString("msg");
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    myInterface.failed(call, response);
                }
                if (status) {
                    System.out.println(status + msg);
                    myInterface.successed(call, response);
                } else {
                    if(msg.equals("用户不存在")){
                        myInterface.register();
                    }else{
                        System.out.println(status + msg);
                        myInterface.failed(call, response);
                    }
                }
            }
        });
    }


    /**
     * 请求登录
     * */
    public  void getRegister(String url, final String phoneNumber, String vCode){
        OkHttpClient okHttpClient = MyHTTPClient.getInstance().getOkHttpClient();
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("phone",phoneNumber);
        formBody.add("password",MyUtils.md5(phoneNumber));
        formBody.add("vcode",vCode);
        final Request request = new Request.Builder()
                .url(url)
                .header(Constants.HEADER_CONTENT_TYPE_KEY,Constants.HEADER_CONTENT_TYPE_VAULE)
                .post(formBody.build())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                myInterface.failure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                str = response.body().string();
                boolean status = false;
                String msg = null;
                System.out.println(response.code());
                System.out.println(str);
                if(!str.isEmpty()){
                    try {
                        JSONObject jsonObject = new JSONObject(str);
                        status = jsonObject.getBoolean("status");
                        msg = jsonObject.getString("msg");
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    myInterface.failed(call, response);
                }
                if (status) {
                    getLogin(Constants.LOGIN,phoneNumber);
                    System.out.println(status + msg);
                    //myInterface.successed(call, response);
                } else {
                    if(msg.equals("验证码错误，请重新输入")){
                        myInterface.register();
                    }else if(msg.equals("该用户已经存在")){
                        getLogin(Constants.LOGIN,phoneNumber);
                    }else{
                        System.out.println(status + msg);
                        myInterface.failed(call, response);
                    }
                }
            }
        });
    }
    public void login(View view){
        if(isLoginable){
            vCode = et1.getText().toString()+et2.getText().toString()+et3.getText().toString()+et4.getText().toString();
            System.out.println(vCode+phoneNumber);
            getRegister(Constants.REGIETER,phoneNumber,vCode);
            //getLogin(Constants.LOGIN,phoneNumber);
        }
    }


    @Override
    public void successed(Call call, Response response) throws IOException {
        if(sp.getBoolean("firstStart",true)){
            editor.putBoolean("firstStart",false);
            editor.commit();
            Intent intent = new Intent();
            intent = intent.setClass(LoginActivity.this, GuideActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        }else{
            Intent intent = new Intent();
            intent = intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        }
        finish();
    }

    @Override
    public void failure(IOException e) {
        Toast.makeText(this,"服务器异常",Toast.LENGTH_LONG).show();
    }

    @Override
    public void failed(Call call, Response response) throws IOException {
        if(response.code() ==504){
            Toast.makeText(this,"服务器异常",Toast.LENGTH_LONG).show();
            //System.out.println("服务器异常");
        }
    }

    @Override
    public void register() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, "验证码错误，请重新输入", Toast.LENGTH_SHORT).show();
            }
        });
        //getRegister(Constants.REGIETER,phoneNumber,vCode);
    }

    public void loginWithOther(View v){
        Toast.makeText(this,"完善中，敬请期待...",Toast.LENGTH_SHORT);
    }

}
