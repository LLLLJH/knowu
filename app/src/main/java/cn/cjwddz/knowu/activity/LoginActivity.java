package cn.cjwddz.knowu.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.common.application.AppManager;
import cn.cjwddz.knowu.common.http.MyHTTPClient;
import cn.cjwddz.knowu.common.utils.MyUtils;
import cn.cjwddz.knowu.interfaces.Get_Code_callback;
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

public class LoginActivity extends AppCompatActivity implements MyInterface,Get_Code_callback {

    private EditText et1;
    private EditText et2;
    private EditText et3;
    private EditText et4;
    private int et1_index = 1;
    private int et2_index = 1;
    private int et3_index = 1;
    private int et4_index = 1;
    private boolean firstLogin = true;
    private Button login;
    private boolean isLoginable = false;
    private String phoneNumber;
    private MyInterface myInterface;
    private Get_Code_callback get_code_callback;

    android.support.v7.app.ActionBar actionBar;
    private ImageButton ibtn_turnBack;
    private TextView tv_countDownTimer;

    private String vCode;
    private String str;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    public void setMyInterface(MyInterface myInterface){
        this.myInterface = myInterface;
    }
    public void setGet_code_callback(Get_Code_callback get_code_callback){this.get_code_callback = get_code_callback;}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    void initView() {
        setContentView(R.layout.activity_login);
        AppManager.getAppManager().addActivity(this);
        setMyInterface(this);
        setGet_code_callback(this);
        //标题栏返回按键
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        //google的actionbar是分为上下两栏显示的，上面的代码只能设置顶部actionbar的背景色，为了让下面的背景色一致，还需要添加一行代码：
        actionBar.setSplitBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View actionBarView = View.inflate(this,R.layout.actionbar_register,null);
        if(actionBar != null){
            actionBar.setCustomView(actionBarView,lp);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        }
        tv_countDownTimer = (TextView) findViewById(R.id.tv_countDownTimer);
        ibtn_turnBack = actionBarView.findViewById(R.id.turnBack);
        ibtn_turnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                    et1.setFocusable(false);
                    et1.setCursorVisible(false);
                    et1.setFocusableInTouchMode(false);
                    et2.setFocusable(true);
                    et2.setCursorVisible(true);
                    et2.setFocusableInTouchMode(true);
                    et2.requestFocus();
                    et2.setEnabled(true);
                    /**
                     * et2.requestFocus();
                     et1.setEnabled(false);

                     */
                    et1_index = 1;
                    et2_index = 0;
                    //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                   // imm.showSoftInput(et2, InputMethodManager.SHOW_FORCED);// 显示输入法
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
                    et2.setFocusable(false);
                    et2.setCursorVisible(false);
                    et2.setFocusableInTouchMode(false);
                    et3.setFocusable(true);
                    et3.setCursorVisible(true);
                    et3.setFocusableInTouchMode(true);
                    et3.requestFocus();
                    et3.setEnabled(true);
                    et3_index = 0;
                    et2_index = 1;
                   // InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.showSoftInput(et3, InputMethodManager.SHOW_FORCED);// 显示输入法
                }
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
                    et3.setFocusable(false);
                    et3.setCursorVisible(false);
                    et3.setFocusableInTouchMode(false);
                    et4.setFocusable(true);
                    et4.setCursorVisible(true);
                    et4.setFocusableInTouchMode(true);
                    et4.requestFocus();
                    et4.setEnabled(true);
                    et4_index =0;
                    et3_index = 1;
                    //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.showSoftInput(et4, InputMethodManager.SHOW_FORCED);// 显示输入法
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
                if(charSequence.length() == 1) {
                    login.setBackgroundResource(R.drawable.login_selector);
                    et4_index = 1;
                    isLoginable = true;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et4.getWindowToken(), 0);//关闭软键盘
                }
               if(charSequence.length()==0 ){
                   login.setBackgroundResource(R.drawable.login);
                   isLoginable = false;
               }
            }
        });
        timer.start();
    }
    CountDownTimer timer = new CountDownTimer(60*1000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            tv_countDownTimer.setText("("+millisUntilFinished/1000+"s）");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tv_countDownTimer.setTextColor(getResources().getColor(R.color.grey,null));
            }else{
                tv_countDownTimer.setTextColor(getResources().getColor(R.color.grey));
            }
        }

        @Override
        public void onFinish() {
            tv_countDownTimer.setText("重发");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tv_countDownTimer.setTextColor(getResources().getColor(R.color.gold,null));
            }else{
                tv_countDownTimer.setTextColor(getResources().getColor(R.color.gold));
            }
        }
    };

    void initData() {
        //获取内存储中的用户信息及事务提交
        sp = getSharedPreferences("knowu",MODE_PRIVATE);
        editor = sp.edit();
        phoneNumber = sp.getString("phoneNumber",null);
        tv_countDownTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv_countDownTimer.getText().equals("重发")){
                    timer.start();
                    getCode(Constants.GET_CODE,phoneNumber);
                }
            }
        });
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
                        et2.setFocusable(false);
                        et2.setCursorVisible(false);
                        et2.setFocusableInTouchMode(false);
                        et1.setFocusable(true);
                        et1.setCursorVisible(true);
                        et1.setFocusableInTouchMode(true);
                        et1.requestFocus();
                        et1.setEnabled(true);
                        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        //imm.showSoftInput(et1, InputMethodManager.SHOW_FORCED);// 显示输入法
                    }

                    break;
                case R.id.et3:
                    et3_index--;
                    if(i == keyEvent.KEYCODE_DEL && et3_index ==-1 ){
                        et2.setText("");
                        et3.setFocusable(false);
                        et3.setCursorVisible(false);
                        et3.setFocusableInTouchMode(false);
                        et2.setFocusable(true);
                        et2.setCursorVisible(true);
                        et2.setFocusableInTouchMode(true);
                        et2.requestFocus();
                        et2.setEnabled(true);
                       // InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        //imm.showSoftInput(et2, InputMethodManager.SHOW_FORCED);// 显示输入法
                    }

                    break;
                case R.id.et4:
                    if(i == keyEvent.KEYCODE_DEL && et4_index ==-1 ){
                        et3.setText("");
                        et4.setFocusable(false);
                        et4.setCursorVisible(false);
                        et4.setFocusableInTouchMode(false);
                        et3.setFocusable(true);
                        et3.setCursorVisible(true);
                        et3.setFocusableInTouchMode(true);
                        et3.requestFocus();
                        et3.setEnabled(true);
                        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        //imm.showSoftInput(et3, InputMethodManager.SHOW_FORCED);// 显示输入法
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
        formBody.add("password", phoneNumber);
        //formBody.add("password", MyUtils.md5(phoneNumber));
        //System.out.println( MyUtils.md5(phoneNumber));
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
               // System.out.println(response.code());
                //System.out.println(str);
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
                   // System.out.println(status + msg);
                    myInterface.successed(call, response);
                } else {
                    if(msg.equals("用户不存在")){
                        myInterface.register();
                    }else{
                        //System.out.println(status + msg);
                        myInterface.failed(call, response);
                    }
                }
            }
        });
    }


    /**
     * 请求验证
     * */
    public  void getRegister(String url, final String phoneNumber, String vCode){
        OkHttpClient okHttpClient = MyHTTPClient.getInstance().getOkHttpClient();
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("phone",phoneNumber);
        formBody.add("password",phoneNumber);
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
                //System.out.println(response.code());
                //System.out.println(str);
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
                    //System.out.println(status + msg);
                    //myInterface.successed(call, response);
                } else {
                    if(msg.equals("验证码错误，请重新输入")){
                        myInterface.register();
                    }else if(msg.equals("该用户已经存在")){
                        firstLogin = false;
                        getLogin(Constants.LOGIN,phoneNumber);
                    }else{
                        //System.out.println(status + msg);
                        myInterface.failed(call, response);
                    }
                }
            }
        });
    }
    public void login(View view){
        if(isLoginable){
            vCode = et1.getText().toString()+et2.getText().toString()+et3.getText().toString()+et4.getText().toString();
            //System.out.println(vCode+phoneNumber);
            getRegister(Constants.REGIETER,phoneNumber,vCode);
            //getLogin(Constants.LOGIN,phoneNumber);
        }
    }


    @Override
    public void successed(Call call, Response response) throws IOException {
        if(firstLogin){
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this,"服务器异常",Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void failed(Call call, final Response response) throws IOException {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this,"登录失败！！！",Toast.LENGTH_LONG).show();
                if(response.code() ==504){
                    Toast.makeText(LoginActivity.this,"服务器异常",Toast.LENGTH_LONG).show();
                    //System.out.println("服务器异常");
                }
            }
        });

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
        Toast.makeText(this,"完善中，敬请期待...",Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取验证码
     * */
    public  void getCode(String url,String phoneNumber){
        OkHttpClient okHttpClient = MyHTTPClient.getInstance().getOkHttpClient();
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("phone",phoneNumber);
        final Request request = new Request.Builder()
                .url(url)
                .header(Constants.HEADER_CONTENT_TYPE_KEY,Constants.HEADER_CONTENT_TYPE_VAULE)
                .post(formBody.build())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

               get_code_callback.failureGetC(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                str = response.body().string();
                boolean status = false;
                String msg = null;
                if(!str.isEmpty()){
                    try {
                        JSONObject jsonObject = new JSONObject(str);
                        status = jsonObject.getBoolean("status");
                        msg = jsonObject.getString("msg");
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    get_code_callback.failedGetC(call, response);
                }
                if (status) {
                    //System.out.println(status + msg);
                   get_code_callback.successedGetC(call, response);
                } else {
                   // System.out.println(status + msg);
                    get_code_callback.failedGetC(call, response);
                }
            }
        });
    }

    @Override
    public void successedGetC(Call call, Response response) throws IOException {

    }

    @Override
    public void failureGetC(IOException e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this,"服务器异常",Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void failedGetC(Call call, final Response response) throws IOException {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(response.code() ==504){
                    Toast.makeText(LoginActivity.this,"服务器异常",Toast.LENGTH_LONG).show();
                    //System.out.println("服务器异常");
                }
            }
        });
    }
}
