package cn.cjwddz.knowu.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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

import cn.cjwddz.knowu.R;

public class RegisterActivity extends BaseActivity implements RecyclerView.RecyclerListener {

    private EditText phone_editText;
    private Button next_step;
    private boolean isStepedable =false;
    private long exitTime = 0;

    @Override
    void initView() {
        setContentView(R.layout.activity_register);
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

    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        // 下拉刷新
    }
    public void startSearch(View v){
        Intent it = new Intent();
        it.setClass(this,SearchActivity.class);
        startActivity(it);
    }

    public void sendMessage(View view){
        if(isStepedable){
            Intent intent = new Intent();
            intent.setClass(this,LoginActivity.class);
            startActivity(intent);
            Toast.makeText(this,"发送短信成功",Toast.LENGTH_SHORT).show();
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        }
    }

    //双击退出程序
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
}