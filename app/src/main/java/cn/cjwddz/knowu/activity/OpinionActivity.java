package cn.cjwddz.knowu.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.common.application.AppManager;
import cn.cjwddz.knowu.common.http.MyHTTPClient;
import cn.cjwddz.knowu.common.utils.MyUtils;
import cn.cjwddz.knowu.service.Constants;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static cn.cjwddz.knowu.activity.MainActivity.JSON;

public class OpinionActivity extends AppCompatActivity {
    android.support.v7.app.ActionBar actionBar;
    private ImageButton ibtn_turnBack;
    private EditText et_opinion;
    private Button btn_user_opinion;
    
    private String opinionString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opinion);
        AppManager.getAppManager().addActivity(this);
        //标题栏返回按键
        actionBar = getSupportActionBar();
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View actionBarView = View.inflate(this,R.layout.actionbar_opinion,null);
        ibtn_turnBack = actionBarView.findViewById(R.id.turnBack);
        et_opinion = (EditText) findViewById(R.id.et_opinion);
        btn_user_opinion = (Button) findViewById(R.id.btn_user_opinion);
        ibtn_turnBack.setOnClickListener(listener);
        btn_user_opinion.setOnClickListener(listener);
        if(actionBar != null){
            actionBar.setCustomView(actionBarView,lp);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        }
    }

    /**
     * 使用post方式访问服务器
     * 上传用户建议
     *
     * @param url
     * @param opinion
     * */
    public  void postUserOpinion(String url,long time,String opinion){
        OkHttpClient okHttpClient = MyHTTPClient.getInstance().getOkHttpClient();
        JSONObject infos = new JSONObject();
        JSONObject info = new JSONObject();
        try {
            infos.put("type","user_opinion");
            infos.put("time",time);
            info.put("content",opinion);
            infos.put("info",info);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody =  FormBody.create(JSON,infos.toString());
        final Request request = new Request.Builder()
                .url(url)
                .header(Constants.HEADER_CONTENT_TYPE_KEY,Constants.HEADER_CONTENT_TYPE_VAULE_JSON)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //System.out.println("提交用户信息shibai");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    //System.out.println("提交用户信息success"+response.body()+"&"+response.message()+"&"+response.code()+"&"+response.request());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OpinionActivity.this,"建议成功提交！！！",Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{
                    //System.out.println("提交用户信息shibai3"+response.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OpinionActivity.this,"建议提交失败！！！",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_user_opinion:
                    opinionString = et_opinion.getText().toString();
                    if(opinionString.isEmpty()){
                        long time = MyUtils.getLongTime();
                        postUserOpinion(Constants.ADD_USER_FEEL_URL,time,opinionString);
                    }
                    break;
                case R.id.turnBack:
                    AppManager.getAppManager().finishActivity();
                    break;
                default:
                    break;
            }
        }
    };
}
