package cn.cjwddz.knowu.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import cn.cjwddz.knowu.AFactory;
import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.common.application.AppManager;
import cn.cjwddz.knowu.interfaces.Activity_interface;
import cn.cjwddz.knowu.interfaces.DeepStatusListenerManager;
import cn.cjwddz.knowu.interfaces.DefIntensityListenerManager;
import cn.cjwddz.knowu.service.KnowUBleService;
import cn.cjwddz.knowu.service.Protocol;
import cn.cjwddz.knowu.view.MyDialog;
import cn.cjwddz.knowu.view.MyPicker;

public class DeviceDetailsActivity extends AppCompatActivity {
    public TextView tv_deviceName;
    public TextView tv_model;
    public TextView tv_deviceId;
    public TextView tv_firmwareVersion;
    private TextView tv_defIntensity;
    private Switch switch_hint;
    private LinearLayout ll_defIntensity;
    private LinearLayout ll_deep;
    private Activity_interface anInterface;
    private DefIntensityListenerManager defIntensityListenerManager;

    private String deviceVersion;
    private String deviceVersion1;
    private String deviceVersion2;
    private String deviceVersion3;

    private  MyDialog dialog;
    private View dialogView;
    private MyPicker np;
    private int defIntensity;


    android.support.v7.app.ActionBar actionBar;

    private ImageButton ibtn_turnBack;

    private KnowUBleService kUBService;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_details);
        kUBService = AFactory.mainActivity.getkUBService();
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
        ll_defIntensity = (LinearLayout) findViewById(R.id.ll_defIntensity);
        ll_deep = (LinearLayout) findViewById(R.id.ll_deep);
        tv_defIntensity = (TextView) findViewById(R.id.tv_defIntensity);
        switch_hint = (Switch) findViewById(R.id.switch_hint);
        ibtn_turnBack = actionBarView.findViewById(R.id.turnBack);
        ibtn_turnBack.setOnClickListener(listener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( kUBService!=null ){
            if(!kUBService.isLink()){
                return;
            }
           // kUBService.sendMessage(Protocol.getDefIntensityReadInstruct());
        }
        ll_deep.setVisibility(View.VISIBLE);
        switch_hint.setChecked(sp.getBoolean("deep",true));
        switch_hint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //DeepStatusListenerManager deepStatusListenerManager = DeepStatusListenerManager.getInstance();
                if(isChecked){
                    //deepStatusListenerManager.openDeep();
                    if( kUBService!=null ){
                        if(!kUBService.isLink()){
                            Toast.makeText(getApplication(),"设备未连接！！！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        kUBService.sendMessage(Protocol.getOpenDeep());
                    }
                }else{
                    // deepStatusListenerManager.closeDeep();
                    if( kUBService!=null ){
                        if(!kUBService.isLink()){
                            Toast.makeText(getApplication(),"设备未连接！！！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        kUBService.sendMessage(Protocol.getCloseDeep());
                    }
                }
                editor.putBoolean("deep",isChecked);
                editor.commit();
            }
        });
        deviceVersion = sp.getString("deviceVersion", "");
        deviceVersion1 = deviceVersion.substring(0,2);
        deviceVersion2 = deviceVersion.substring(3,5);
        deviceVersion3 = deviceVersion.substring(6,8);
        //Toast.makeText(this,deviceVersion+":"+deviceVersion1+";"+deviceVersion2+";"+deviceVersion3,Toast.LENGTH_SHORT).show();
        tv_deviceName.setText("FREAL-U");
        tv_deviceId.setText(sp.getString("deviceID", ""));
        tv_firmwareVersion.setText( deviceVersion);
        tv_model.setText( sp.getString("deviceModel","FU-DY01"));
        if(Integer.parseInt(deviceVersion1)>= 02){
            if(Integer.parseInt(deviceVersion2) >= 04){
                if(Integer.parseInt(deviceVersion3) >= 01){
                    ll_defIntensity.setClickable(true);
                    ll_defIntensity.setVisibility(View.VISIBLE);
                    tv_defIntensity.setText(String.valueOf(sp.getInt("defIntensity",0)));
                }
            }
        }else{
            ll_defIntensity.setClickable(false);
            ll_defIntensity.setVisibility(View.GONE);
        }
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.turnBack:
                    AppManager.getAppManager().finishActivity();
                    break;
                case R.id.btn_cancel:
                    dialog.dismiss();
                    break;
                case R.id.btn_w_ensure:
                    defIntensity = np.getValue();
                    //defIntensityListenerManager = DefIntensityListenerManager.getInstance();
                    //defIntensityListenerManager.setDefIntensity(defIntensity);
                    if( kUBService!=null ){
                        if(!kUBService.isLink()){
                            Toast.makeText(getApplication(),"设备未连接！！！",Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            return;
                        }
                        kUBService.sendMessage(Protocol.getDefIntensitySetInstruct(defIntensity));
                        kUBService.sendMessage(Protocol.getDefIntensityReadInstruct());
                        tv_defIntensity.setText(String.valueOf(defIntensity));
                    }
                    dialog.dismiss();
                default:
                    break;
            }
        }
    };


    public void setDefIntensity(){
        MyDialog.Builder builder = new MyDialog.Builder(this);
        dialog = builder.setStyle(R.style.MyDialog)
                .setCancelTouchout(false)
                .setView(R.layout.defintensitydialog)
                .setWidthpx(getWindowManager().getDefaultDisplay().getWidth())
                .setHeihgtdp(290)
                .addViewOnclickListener(R.id.btn_cancel,listener)
                .addViewOnclickListener(R.id.btn_w_ensure,listener)
                .build();
        dialog.show();
        dialogView = dialog.getView();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        np = dialogView.findViewById(R.id.np_weight);
        np.setMinValue(1);
        np.setMaxValue(8);
        np.setValue(sp.getInt("defIntensity",1));
        np.setNumberPickerDividerColor(np);
    }
    public void setIntensity(View view){
        setDefIntensity();
    }

    /**
     * 返回
     * */
    public void turnBack(View view){
        AppManager.getAppManager().finishActivity();
    }
}
