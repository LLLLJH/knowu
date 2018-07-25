package cn.cjwddz.knowu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.cjwddz.knowu.R;

public class LoginActivity extends BaseActivity {

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


    @Override
    void initView() {
        setContentView(R.layout.activity_login);
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

    public void login(View view){
        if(isLoginable){
            Intent intent = new Intent();
            intent.setClass(this,GuideActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            finish();
        }
    }


}
