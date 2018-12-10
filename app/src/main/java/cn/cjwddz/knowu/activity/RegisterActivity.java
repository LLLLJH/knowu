package cn.cjwddz.knowu.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.common.http.MyHTTPClient;
import cn.cjwddz.knowu.service.Constants;
import cn.cjwddz.knowu.service.MyInterface;
import cn.cjwddz.knowu.service.UIInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class RegisterActivity extends BaseActivity implements RecyclerView.RecyclerListener,MyInterface {

    private EditText phone_editText;
    private Button next_step;
    private boolean isStepedable =false;
    private long exitTime = 0;

    private String str;
    //内存储
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    private MyInterface myInterface;
    public void setMyInterface(MyInterface myInterface){
        this.myInterface = myInterface;
    }
    @Override
    void initView() {
        setContentView(R.layout.activity_register);
        setMyInterface(this);
        phone_editText = (EditText) findViewById(R.id.phone_editText);
        next_step = (Button) findViewById(R.id.next_step);

        /**
         * 设置文本delete点击事件
         */
        phone_editText.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("NewApi")

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Drawable drawable = phone_editText.getCompoundDrawables()[2];
                if(drawable ==null) return false;
                if(motionEvent.getAction()!= motionEvent.ACTION_UP) return false;
                if(motionEvent.getX() > phone_editText.getWidth()-phone_editText.getPaddingEnd()-drawable.getIntrinsicWidth()){
                    phone_editText.setText("");
                }
                return false;
            }
        });

        /**
         * 设置文本变化监听
         */
        phone_editText.addTextChangedListener(new TextWatcher() {
            private CharSequence charSequence;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                this.charSequence = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() < 11){
                    next_step.setBackgroundResource(R.drawable.next_step);
                    isStepedable =false;
                }
            }

            @SuppressLint("NewApi")
            @Override
            public void afterTextChanged(Editable editable) {
                if(charSequence.length()==11){
                    next_step.setBackgroundResource(R.drawable.next_step_selector);
                    isStepedable = true;
                }
            }
        });
    }
    @Override
    void initData() {
        //动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_WIFI_STATE
            }, Constants.PERMISSION_REQUEST_COARSE_LOCATION);
        }
        //获取内存储中的用户信息及事务提交
        sp = getSharedPreferences("knowu",MODE_PRIVATE);
        editor = sp.edit();
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        // 下拉刷新
    }

    public void loginWithOther(View v){
        Toast.makeText(this,"完善中，敬请期待...",Toast.LENGTH_SHORT);
    }

    public void sendMessage(View view){
        if(isStepedable){
            String phoneNumber = String.valueOf(phone_editText.getText());
            getCode(Constants.GET_CODE,phoneNumber);
            editor.putString("phoneNumber",phoneNumber);
            editor.commit();
        }
    }

    /**
     * 获取验证码
     * */
    public  void getCode(String url,String phoneNumber){
        OkHttpClient okHttpClient = MyHTTPClient.getInstance().getOkHttpClient();
        //Map<String,String> param = new HashMap<>();
        //param.put("phone",phoneNumber);
        //RequestBody requestBody = FormBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=utf-8"),param.toString());
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("phone",phoneNumber);
        final Request request = new Request.Builder()
                .url(url)
                .header(Constants.HEADER_CONTENT_TYPE_KEY,Constants.HEADER_CONTENT_TYPE_VAULE)
                .post(formBody.build())
                .build();
        System.out.println("获取验证码:"+phoneNumber);
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
                    System.out.println(status + msg);
                    myInterface.failed(call, response);
                }
            }
        });
    }

    /**
     * 双击退出程序
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == event.KEYCODE_BACK&&event.getAction() ==KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime)>2000){
                Toast.makeText(this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }else{
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void successed(Call call, Response response) throws IOException {
        Intent intent = new Intent();
        intent.setClass(this,LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
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

    }

}