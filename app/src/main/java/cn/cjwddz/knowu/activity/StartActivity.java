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


import java.util.Timer;
import java.util.TimerTask;

import cn.cjwddz.knowu.R;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class StartActivity extends Activity {
   private AnimationDrawable anim;
    private boolean isstart = false;

    //内储存
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);
        preferences = getSharedPreferences("knowu",MODE_PRIVATE);
        editor = preferences.edit();
        ImageView imageView = findViewById(R.id.iv_Gif);
        anim = (AnimationDrawable) imageView.getBackground();
        anim.start();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!isstart){
                    startapp();

                }
            }
        },3000);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isstart = true;
               startapp();
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

    public void startapp(){
        if(preferences.getBoolean("firstStart",true)){
            editor.putBoolean("firstStart",false);
            editor.commit();
            Intent intent = new Intent();
            intent = intent.setClass(StartActivity.this, GuideActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        }else{
            Intent intent = new Intent();
            intent = intent.setClass(StartActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        }
        finish();
    }
}
