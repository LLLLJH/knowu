package cn.cjwddz.knowu.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;


import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.common.http.MyHTTPClient;
import cn.cjwddz.knowu.service.Constants;
import cn.cjwddz.knowu.service.MyInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class StartActivity extends Activity implements MyInterface{
   private AnimationDrawable anim;
    private boolean isstart = false;

    private String phoneNumber;
    //内储存
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private Boolean isLogin;

    private MyInterface myInterface;
    public void setMyInterface(MyInterface myInterface){
        this.myInterface = myInterface;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);
        setMyInterface(this);
        preferences = getSharedPreferences("knowu",MODE_PRIVATE);
        isLogin = preferences.getBoolean("isLogin",false);
        phoneNumber = preferences.getString("phoneNumber",null);
        editor = preferences.edit();
        ImageView imageView = findViewById(R.id.iv_Gif);
        anim = (AnimationDrawable) imageView.getBackground();
        anim.start();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!isstart){
                    startApp();
                }
            }
        },3000);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isstart = true;
               startApp();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        anim.stop();
        super.onDestroy();
    }

    public void startApp(){
        if(!isLogin){
            Intent intent = new Intent();
            intent = intent.setClass(StartActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            finish();
        }else{
            getLogin(Constants.LOGIN,phoneNumber);
        }

    }

    /**
     * 请求登录
     * */
    public  void getLogin(String url,String phoneNumber){
        OkHttpClient okHttpClient = MyHTTPClient.getInstance().getOkHttpClient();
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("phone",phoneNumber);
        formBody.add("password",phoneNumber);
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
                String status = null;
                String msg = null;
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    status = jsonObject.getString("status");
                    msg = jsonObject.getString("msg");
                } catch(Exception e){
                    e.printStackTrace();
                }
                if (status.equals("true")) {
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

    @Override
    public void successed(Call call, Response response) throws IOException {
        Intent intent = new Intent();
        intent = intent.setClass(StartActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        finish();
    }

    @Override
    public void failure(IOException e) {
        Intent intent = new Intent();
        intent = intent.setClass(StartActivity.this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        finish();
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
        Intent intent = new Intent();
        intent = intent.setClass(StartActivity.this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        finish();
    }
}
