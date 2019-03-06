package cn.cjwddz.knowu.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.common.application.AppManager;
import cn.cjwddz.knowu.interfaces.Activity_interface;
import cn.cjwddz.knowu.interfaces.DeepStatusListenerManager;
import cn.cjwddz.knowu.service.KnowUBleService;

public class DeviceDetailsActivity extends AppCompatActivity {
    public TextView tv_deviceName;
    public TextView tv_model;
    public TextView tv_deviceId;
    public TextView tv_firmwareVersion;
    private Switch switch_hint;
    private Activity_interface anInterface;


    android.support.v7.app.ActionBar actionBar;

    private ImageButton ibtn_turnBack;

    private KnowUBleService kUBService;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_details);

        AppManager.getAppManager().addActivity(this);
        sp = getSharedPreferences("knowu",MODE_PRIVATE);
        editor = sp.edit();
        //标题栏返回按键
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        //google的actionbar是分为上下两栏显示的，上面的代码只能设置顶部actionbar的背景色，为了让下面的背景色一致，还需要添加一行代码：
        actionBar.setSplitBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View actionBarView = View.inflate(this,R.layout.actionbar_device,null);
        if(actionBar != null){
            actionBar.setCustomView(actionBarView,lp);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        }
        tv_deviceName = (TextView) findViewById(R.id.tv_deviceName);
        tv_model = (TextView) findViewById(R.id.tv_model);
        tv_deviceId = (TextView) findViewById(R.id.tv_deviceId);
        tv_firmwareVersion = (TextView) findViewById(R.id.tv_firmwareVersion);
        switch_hint = (Switch) findViewById(R.id.switch_hint);
        switch_hint.setChecked(sp.getBoolean("deep",true));
        switch_hint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DeepStatusListenerManager deepStatusListenerManager = DeepStatusListenerManager.getInstance();
                if(isChecked){
                    deepStatusListenerManager.openDeep();
                }else{
                    deepStatusListenerManager.closeDeep();
                }
                editor.putBoolean("deep",isChecked);
                editor.commit();
            }
        });
        ibtn_turnBack = actionBarView.findViewById(R.id.turnBack);
        ibtn_turnBack.setOnClickListener(listener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_deviceName.setText("REEAL-U");
        tv_deviceId.setText(sp.getString("deviceID", ""));
        tv_firmwareVersion.setText( sp.getString("deviceVersion", "01.12.01"));
       tv_model.setText( sp.getString("deviceModel","FU-DY01"));

    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.turnBack:
                    AppManager.getAppManager().finishActivity();
                    break;
                default:
                    break;
            }
        }
    };
}
