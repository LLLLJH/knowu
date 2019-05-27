package cn.cjwddz.knowu.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Process;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.AppOpsManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.cjwddz.knowu.AFactory;
import cn.cjwddz.knowu.FragmentUI;
import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.common.http.MyHTTPClient;
import cn.cjwddz.knowu.common.utils.MyUtils;
import cn.cjwddz.knowu.common.utils.StringUtils;
import cn.cjwddz.knowu.interfaces.Activity_interface;
import cn.cjwddz.knowu.interfaces.Activity_interface_intensity;
import cn.cjwddz.knowu.interfaces.DeepStatusListenerManager;
import cn.cjwddz.knowu.interfaces.DefIntensityListenerManager;
import cn.cjwddz.knowu.interfaces.Get_userHeader;
import cn.cjwddz.knowu.interfaces.Get_userInfo;
import cn.cjwddz.knowu.service.Constants;
import cn.cjwddz.knowu.service.KnowUBleService;
import cn.cjwddz.knowu.service.MyInterface;
import cn.cjwddz.knowu.service.Protocol;
import cn.cjwddz.knowu.service.ServiceInterface;
import cn.cjwddz.knowu.service.UIInterface;
import cn.cjwddz.knowu.view.CountDownTimerView;
import cn.cjwddz.knowu.common.application.AppManager;
import cn.cjwddz.knowu.view.MyDialog;
import cn.cjwddz.knowu.view.MyImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static cn.cjwddz.knowu.FragmentUI.*;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;

public class MainActivity extends BaseActivity implements RecyclerView.RecyclerListener,OnFragmentInteractionListener,UIInterface, OnClickListener,MyInterface,Get_userInfo,Get_userHeader,Activity_interface,Activity_interface_intensity {
    private CountDownTimerView countDownTimerView;
    private DrawerLayout drawerLayout;
    public static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
    private String str;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private LocationManager locationManager;
    private boolean gps;
    private boolean netword;
    private  MyDialog dialog;
    private int isOpen;
    private AppOpsManager appOpsManager;
    private ContentResolver resolver;

    private MyImageView header;
    private File file;
    private ImageButton fighting;
    private ImageButton keepfit;
    private ImageButton chira;
    private ImageButton relax;
    private ImageButton last;
    private  TextView userName;
    private TextView fighting_tv;
    private TextView keepfit_tv;
    private TextView chira_tv;
    private TextView relax_tv;
    private TextView last_tv;
    private TextView bt_progress;
    private DisplayMetrics displayMetrics;
    private double width,density;
    private int firstprogress = 0;
    private int progress = 1;
    private int lastprogress = 0;
    private float firstX;
    private float secondX;

    private long exitTime = 0;
    ServiceInterface serviceInterface;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private String phoneNumber;
    private String filename;


    private static int STOPSPACE = 60;
    private static int CHANGESPACE = 10000;
    private long stopTime = 0;
    private long startTime = 0;
    private long endTime = 0;
    private long startStopTime = 0;
    private int model = 0;
    private int intensity = 1;
    private static int INTENSITY_MAX = 15;
    private static int INTENSITY_MIN = 1;
    private boolean isStop = false;//判断是否由暂停开始还是在启动时开始
    private boolean isStart = false;//判断是否由暂停开始还是在启动时开始

    private KnowUBleService kUBService;
    private MyInterface myInterface;
    private Get_userInfo get_userInfo;
    private Get_userHeader get_userHeader;
    public void setGet_userHeader(Get_userHeader get_userHeader){this.get_userHeader = get_userHeader;}
    public void setGet_userInfo(Get_userInfo get_userInfo){this.get_userInfo = get_userInfo;}
    public void setMyInterface(MyInterface myInterface){
        this.myInterface = myInterface;
    }
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            kUBService = ((KnowUBleService.MyBinder) iBinder).getService();
            kUBService.setUiInterface(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            kUBService = null;
        }
    };

    public  KnowUBleService getkUBService(){
        return kUBService;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO request success
                }
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                }, Constants.PERMISSION_REQUEST_COARSE_LOCATION);
            }
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, Constants.PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
        AFactory.mainActivity = this;
    }


    @Override
    void initView() {
     setContentView(R.layout.slidemune);
        setGet_userHeader(this);
        setMyInterface(this);
        setGet_userInfo(this);
        header = (MyImageView) findViewById(R.id.header);
         fighting = (ImageButton) findViewById(R.id.fighting);
        fighting_tv = (TextView) findViewById(R.id.fighting_tv);
        last = fighting;
        last_tv = fighting_tv;
         keepfit = (ImageButton) findViewById(R.id.keepfit);
        keepfit_tv = (TextView) findViewById(R.id.keepfit_tv);
         chira = (ImageButton) findViewById(R.id.chira);
        chira_tv = (TextView) findViewById(R.id.chira_tv);
         relax = (ImageButton) findViewById(R.id.relax);
        relax_tv = (TextView) findViewById(R.id.relax_tv);
        userName = (TextView) findViewById(R.id.username);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        bt_progress = (TextView) findViewById(R.id.bt_progress);
        fighting.setOnClickListener(this);
        keepfit.setOnClickListener(this);
        chira.setOnClickListener(this);
        relax.setOnClickListener(this);
        DeepStatusListenerManager.getInstance().setConnectionStateListener(this);
        DefIntensityListenerManager.getInstance().setConnectionStateListener(this);
        displayMetrics = getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        density = (width - dip2px(this, 51)) / 20;
        sp = getSharedPreferences("knowu",MODE_PRIVATE);
        editor = sp.edit();
        phoneNumber = sp.getString("phoneNumber",null);
        filename = MyUtils.hmacSha256("header",phoneNumber);
        file = new File(Environment.getExternalStorageDirectory().getPath(),filename+".png");
        if(!sp.getBoolean("isLogin",false)){
            getUserInfo(Constants.GET_USER_INFO_URL);
            getUserHeader(Constants.GETHEADER,filename+".png");
            editor.putBoolean("isLogin",true);
            editor.commit();
        }else if(file.length()==0){
            getUserInfo(Constants.GET_USER_INFO_URL);
            getUserHeader(Constants.GETHEADER,filename+".png");
        }

    }

    @Override
    void initData() {
        //打开蓝牙请求
        openBluetooth();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        netword = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        //appOpsManager= (AppOpsManager) getSystemService(APP_OPS_SERVICE);
        //isOpen = appOpsManager.checkOp(AppOpsManager.OPSTR_FINE_LOCATION, Process.myUid(),getPackageName());
        openGps();
        Intent intent = new Intent(this,KnowUBleService.class);
        undateFragment(0);
        initSeekbarProgress();
        bindService(intent,serviceConnection,BIND_AUTO_CREATE);
    }
   private void initSeekbarProgress(){
       LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
               LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
       layoutParams.leftMargin = (int)(progress*density);

   }

    private void initImageView(){
        String nickName = sp.getString("nickName","女有");
        userName.setText(nickName);
        if(file.length()!=0){
            Uri uri0 = Uri.fromFile(file);
            try {
                //获取裁剪后的图片，并显示出来
                Bitmap bitmap = BitmapFactory.decodeStream(
                        getContentResolver().openInputStream(uri0));
                //图片还未回收，先强制回收该图片
                BitmapDrawable bitmapDrawable = (BitmapDrawable) header.getDrawable();
                if(bitmapDrawable != null && !bitmapDrawable.getBitmap().isRecycled()){
                    bitmapDrawable.getBitmap().recycle();
                }
                header.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        // 下拉刷新
    }
    public void startSearch(View v){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,DeviceDetailsActivity.class);
        startActivity(intent);
    }


    public void startSlideMune(View v){
        showDrawerLayout();
    }

    private void showDrawerLayout(){
        if(!drawerLayout.isDrawerOpen(Gravity.LEFT)){
            drawerLayout.openDrawer(Gravity.LEFT);
        }else{
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * 计时开始
     * */
    @Override
    public void countdownStart() {
        /**if(isStop){
            stopTime += (int) (getLongTime()-startStopTime);//获得暂停的秒数
            startStopTime = 0;
            isStop = false;
        }else{
         startTime = getLongTime();
        }*/
        startTime = getLongTime();
        isStop = false;
        isStart = true;
    }

    /**
     * 暂停计时
     * */
    @Override
    public void countdownStop() {
        //startStopTime = getLongTime();
        endTime = getLongTime();
        long len = (endTime - startTime)/1000;
        postUserRecord(Constants.ADD_USER_RECORD_URL,startTime,len,model,intensity,stopTime);
        init();
        isStop = true;
        isStart = false;
    }

    /**
     * 计时结束
     * */
    @Override
    public void countdownFinished() {
        endTime = getLongTime();
        long len = (endTime - startTime)/1000;
        postUserRecord(Constants.ADD_USER_RECORD_URL,startTime,len,model,intensity,stopTime);
        init();
        isStart = false;
    }

    private void init(){
        startTime = 0;
        endTime = 0;
        stopTime = 0;
       startStopTime = 0;
    }

    private void initTime(){
        startTime = getLongTime();
        endTime = 0;
        stopTime = 0;
        startStopTime = 0;
    }

    public void connect(){
        //isOpen = appOpsManager.checkOp(AppOpsManager.OPSTR_FINE_LOCATION, Process.myUid(),getPackageName());
        gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        netword = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        openGps();
        if(bluetoothAdapter.isEnabled()&& kUBService != null){
                undateFragment(1);
                kUBService.scan();
                //serviceInterface.scan();
        }else{
                openBluetooth();
                Toast.makeText(this,"请打开蓝牙",Toast.LENGTH_SHORT).show();
        }

    }

    public void undateFragment(int position){
        Bundle bundle = new Bundle();
        bundle.putInt(FRAGMENT_ID,position);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentUI fragment =  new FragmentUI();
        fragment.setArguments(bundle);
        if(kUBService!=null){
            fragment.setBleService(kUBService);
        }
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        if(position != 0||position!=2){
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    /**
     * 打开检查蓝牙
     * */
    @SuppressLint("NewApi")
    private void openBluetooth(){
        bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()){
            Intent intent = new Intent(bluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(intent);
        }
    }

    /**
     * 弹窗请求打开定位服务
     * //拒绝则退出程序
     * */
    private void openGps(){
        if(gps){
            return;
        }else{
            MyDialog.Builder builder = new MyDialog.Builder(this);
            dialog = builder.setStyle(R.style.MyDialog)
                    .setCancelTouchout(false)
                    .setView(R.layout.gpsdialog)
                    .setWidthdp(300)
                    .setHeihgtdp(100)
                    .addViewOnclickListener(R.id.btn_cancel,listener)
                    .addViewOnclickListener(R.id.btn_ensure,listener)
                    .build();
            dialog.show();
        }
    }

    /**
     * 检查GPS状态
     * */
    public  boolean getGpsStatus(){
        resolver = getContentResolver();
        return Settings.Secure.isLocationProviderEnabled(resolver,LocationManager.GPS_PROVIDER);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @SuppressLint("NewApi")
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_cancel:
                    dialog.dismiss();
                    break;
                case R.id.btn_ensure:
                    //openGpsByphone();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent,0);
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 强制打开定位服务
     * */
    private void openGpsByphone(){
       Intent intent = new Intent();
        intent.setClassName("com.android.settings","com.android.settings.widget.SettingsAppWidgetProvider");
        intent.addCategory("android.intent.category.ALTERNATIVE");
        intent.setData(Uri.parse("custom:3"));
        try{
            PendingIntent.getBroadcast(this,0,intent,0).send();
        }catch (PendingIntent.CanceledException e){
            e.printStackTrace();
        }
    }



    @Override
    public void connectSuccess() {
        undateFragment(2);
        //seekbar.setThumb(getResources().getDrawable(R.drawable.seekbar_clicked));
        fighting.setSelected(true);
        fighting_tv.setTextColor(getResources().getColor(R.color.text_color));
        last = fighting;
        last_tv = fighting_tv;
        //count.setBackgroundResource(R.drawable.count_clicked);
        editor.putString("deviceID", StringUtils.deleteStr(kUBService.getMDevice().getDeviceID()," "));
        editor.putString("deviceVersion", kUBService.getMDevice().getDeviceVersion());
        editor.putString("deviceModel",kUBService.getMDevice().getDeviceModel());
        editor.commit();
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                undateFragment(3) ;
                kUBService.sendMessage(Protocol.getModeSetInstruct(0));
                kUBService.sendMessage(Protocol.getIntensityReadInstruct());
                kUBService.sendMessage(Protocol.getElectricGetInstruct());
                kUBService.sendMessage(Protocol.getDeepStatus());
                kUBService.sendMessage(Protocol.getDefIntensityReadInstruct());
                thread.start();
            }
        };
        timer.schedule(timerTask,1000);
    }

    @Override
    public void scanning() {
       Toast("正在扫描设备...");
    }

    @Override
    public void stopScan() {
       Toast("停止扫描设备...");
    }

    @Override
    public void hscanDevice() {
        Toast("发现设备");
    }

    @Override
    public void scanSuccess(String name) {
       Toast("已扫描到公司设备："+name);
    }


    @Override
    public void startService() {
        Toast("Service已启动");
    }

    @Override
    public void connectting() {
        Toast("连接中...");
    }

    @Override
    public void setNotify(String status) {
        Toast(status);
    }

    @Override
    public void sendMessage(String status) {
        Toast(status);
    }

    @Override
    public void updateView(int count) {
        showToast("电量过低！请为设备充电再使用！！！");
    }

    @Override
    public void getDefIntensity(int intensity) {
        //showToast(String.valueOf(intensity));
        editor.putInt("defIntensity",intensity);
        editor.commit();
    }
//连接错误或断开时回调
    @Override
    public void back() {
            backMain();
    }

    private void backMain(){
        undateFragment(0);
        progress = 1;
        bt_progress.setText(String.valueOf(progress));
        initSeekbarProgress();
        last.setSelected(false);
        last_tv.setTextColor(getResources().getColor(R.color.mode_text_color));
        endTime = getLongTime();
        if(isStart&&endTime - startTime>CHANGESPACE){
            long len = (endTime - startTime)/1000;
            postUserRecord(Constants.ADD_USER_RECORD_URL,startTime,len,model,intensity,stopTime);
        }
    }

    @Override
    public void setLastProgress(int progress) {
        bt_progress.setText(String.valueOf(progress));
        this.progress = progress;
    }

    @Override
    public void setDeepStatus(boolean deepStatus) {
        editor.putBoolean("deep",deepStatus);
        editor.commit();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        endTime = getLongTime();
        if(isStart&&endTime - startTime>CHANGESPACE){
            long len = (endTime - startTime)/1000;
            postUserRecord(Constants.ADD_USER_RECORD_URL,startTime,len,model,intensity,stopTime);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(kUBService!=null){
            if(kUBService.isErr() && !kUBService.isLink()){
               backMain();
            }
            kUBService.setErr(false);
        }
        initImageView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(kUBService != null){
            kUBService.setErr(true);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.fighting:
                if(!kUBService.isLink()){
                    showToast("设备未连接");
                    return;
                }
                if(last != fighting){
                    kUBService.sendMessage(Protocol.getModeSetInstruct(0));
                    fighting.setSelected(true);
                    fighting_tv.setTextColor(getResources().getColor(R.color.text_color));
                    last.setSelected(false);
                    last_tv.setTextColor(getResources().getColor(R.color.mode_text_color));
                    last = fighting;
                    last_tv = fighting_tv;
                    endTime = getLongTime();
                    if(isStart&&endTime - startTime>CHANGESPACE){
                        long len = (endTime - startTime)/1000;
                        postUserRecord(Constants.ADD_USER_RECORD_URL,startTime,len,model,intensity,stopTime);
                        initTime();
                    }
                    model = 0;
                }
                break;
            case R.id.keepfit:
                if(!kUBService.isLink()){
                    showToast("设备未连接");
                    return;
                }
                if(last != keepfit){
                    kUBService.sendMessage(Protocol.getModeSetInstruct(1));
                    keepfit.setSelected(true);
                    keepfit_tv.setTextColor(getResources().getColor(R.color.text_color));
                    last.setSelected(false);
                    last_tv.setTextColor(getResources().getColor(R.color.mode_text_color));
                    last = keepfit;
                    last_tv = keepfit_tv;
                    endTime = getLongTime();
                    if(isStart&&endTime - startTime>CHANGESPACE){
                        long len = (endTime - startTime)/1000;
                        postUserRecord(Constants.ADD_USER_RECORD_URL,startTime,len,model,intensity,stopTime);
                        initTime();
                    }
                    model = 1;
                }
                break;
            case R.id.chira:
                if(!kUBService.isLink()){
                    showToast("设备未连接");
                    return;
                }
                if(last != chira){
                    kUBService.sendMessage(Protocol.getModeSetInstruct(2));
                    chira.setSelected(true);
                    chira_tv.setTextColor(getResources().getColor(R.color.text_color));
                    last.setSelected(false);
                    last_tv.setTextColor(getResources().getColor(R.color.mode_text_color));
                    last = chira;
                    last_tv = chira_tv;
                    endTime = getLongTime();
                    if(isStart&&endTime - startTime>CHANGESPACE){
                        long len = (endTime - startTime)/1000;
                        postUserRecord(Constants.ADD_USER_RECORD_URL,startTime,len,model,intensity,stopTime);
                        initTime();
                    }
                    model = 2;
                }
                break;
            case R.id.relax:
                if(!kUBService.isLink()){
                    showToast("设备未连接");
                    return;
                }
                if(last != relax){
                    kUBService.sendMessage(Protocol.getModeSetInstruct(3));
                    relax.setSelected(true);
                    relax_tv.setTextColor(getResources().getColor(R.color.text_color));
                    last.setSelected(false);
                    last_tv.setTextColor(getResources().getColor(R.color.mode_text_color));
                    last = relax;
                    last_tv = relax_tv;
                    endTime = getLongTime();
                    if(isStart&&endTime - startTime>CHANGESPACE){
                        long len = (endTime - startTime)/1000;
                        postUserRecord(Constants.ADD_USER_RECORD_URL,startTime,len,model,intensity,stopTime);
                        initTime();
                    }
                    model = 3;
                }
                break;
            /**case R.id.information:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,InformationActivity.class);
                startActivity(intent);
                break;*/
           default:break;
        }
    }

    public void addProgress(View view){
        if(!kUBService.isLink()){
            showToast("设备未连接");
            return;
        }
        if(progress < INTENSITY_MAX){
            endTime = getLongTime();
            if(isStart&&endTime - startTime>CHANGESPACE){
                long len = (endTime - startTime)/1000;
                postUserRecord(Constants.ADD_USER_RECORD_URL,startTime,len,model,intensity,stopTime);
                initTime();
        }
            progress = progress +1;
            bt_progress.setText(String.valueOf(progress));
            lastprogress = progress;
            kUBService.sendMessage(Protocol.getIntensitySetInstruct(progress));
            intensity = progress;
        }
    }

    public void minusProgress(View view){
        if(!kUBService.isLink()){
            showToast("设备未连接");
            return;
        }
        if (progress > INTENSITY_MIN){
            endTime = getLongTime();
            if(isStart&&endTime - startTime>CHANGESPACE){
                long len = (endTime - startTime)/1000;
                postUserRecord(Constants.ADD_USER_RECORD_URL,startTime,len,model,intensity,stopTime);
                initTime();
        }
            progress = progress-1;
            bt_progress.setText(String.valueOf(progress));
            lastprogress = progress;
            kUBService.sendMessage(Protocol.getIntensitySetInstruct(progress));
            intensity = progress;
        }
    }


    private void showToast(String s){
        Toast.makeText(this,s, Toast.LENGTH_SHORT).show();
        //System.out.println(s);
    }

    public void Toast(String s){
        Toast.makeText(this,s, Toast.LENGTH_SHORT).show();
       // System.out.println(s);
    }
    /**
     * 根据手机分辨率从 px(像素) 单位 转成 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机分辨率从 dp 单位 转成 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //双击退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == event.KEYCODE_BACK&&event.getAction() ==KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime)>2000){
                Toast.makeText(this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }else{
                AppManager.getAppManager().finishAllActivity();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     *打开个人信息页
     */
    public void getInformation(View view){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,InformationActivity.class);
        startActivity(intent);
    }
  /**
   * 查看头像
   * */
    public void openHeader(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(Environment.getExternalStorageDirectory().getPath(),filename+".png");
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(this,
                    "com.ljh.knowU.fileProvider",
                    file);
        } else {
           uri = Uri.fromFile(file);
        }
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(uri,"image/*");
        startActivity(intent);
    }



    /**
     * 使用post方式访问服务器
     * 上传用户使用记录
     *
     * @param url
     * @param startTime  计时开始时间
     * @param len   使用时长
     * @param model   使用模式
     * @param intensity  使用力度
     * @param stopTime  暂停时间
     * */
    public  void postUserRecord(String url, final long startTime, long len, Integer model, Integer intensity, long stopTime){
        OkHttpClient okHttpClient = MyHTTPClient.getInstance().getOkHttpClient();
        JSONObject info = new JSONObject();
        JSONArray infoArray = new JSONArray();
        try {
            info.put("startTime",startTime);
            info.put("len",len);
            info.put("mode",model);
            info.put("intensity",intensity);
            info.put("stopTime",stopTime);
            infoArray.put(info);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody =  FormBody.create(JSON,infoArray.toString());
        final Request request = new Request.Builder()
                .url(url)
                .header(Constants.HEADER_CONTENT_TYPE_KEY,Constants.HEADER_CONTENT_TYPE_VAULE)
                .post(requestBody)
                .build();
        //Toast.makeText(this,"提交用户信息",Toast.LENGTH_SHORT).show();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                myInterface.failure(e);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                str = response.body().string();
                boolean status = false;
                JSONArray msg = new JSONArray();
                //System.out.println(response.code());
                //System.out.println(str);
                if(!str.isEmpty()){
                    try {
                        JSONObject jsonObject = new JSONObject(str);
                        status = jsonObject.getBoolean("status");
                        //msg = jsonObject.getJSONArray("errQueue");
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    myInterface.failed(call, response);
                }
                if (status) {
                    //System.out.println(status + msg.toString());
                    myInterface.successed(call, response);
                } else {
                    //System.out.println(status + msg.toString());
                    myInterface.failed(call, response);
                }
            }

        });
    }

    /**
     * 使用get方式访问服务器
     * 获取用户信息
     *
     * @param url
     *
     * */
    public  void getUserInfo(String url){
        OkHttpClient okHttpClient = MyHTTPClient.getInstance().getOkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                get_userInfo.failureGetuserInfo(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                   get_userInfo.successedGetuserInfo(call,response);
                }else{
                  get_userInfo.failedGetuserInfo(call,response);
                }
            }
        });
    }

    /**
     * 使用get方式访问服务器
     * 获取用户信息
     *
     * @param url
     *
     * */
    public  void getUserHeader(String url,String filename){
        OkHttpClient okHttpClient = MyHTTPClient.getInstance().getOkHttpClient();
        final Request request = new Request.Builder()
                .url(url+filename)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                get_userHeader.failureGetuserHeader(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    get_userHeader.successedGetuserHeader(call,response);
                }else{
                    get_userHeader.failedGetuserHeader(call,response);
                }
            }
        });
    }

    /**
     * 获取系统时间戳
     * */
    public long getLongTime(){
        long time=System.currentTimeMillis();//获取系统时间的10位的时间戳（单位毫秒）
        return time;
    }

    public String getStringTime(){
        long time=System.currentTimeMillis();//获取系统时间的10位的时间戳（单位毫秒）
        String  str=String.valueOf(time);
        return str;
    }

    /**
     * 退出登录
     * */
    public void exit(View view){
        if(kUBService.isLink()){
            kUBService.disConnectDevice();
        }
        editor.putBoolean("isLogin",false);
        editor.commit();
        AppManager.getAppManager().finishAllActivity();
        Intent intent = new Intent();
        intent = intent.setClass(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    /**
     * 打开用户记录页
     * */
    public void userRecord(View view){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,RecordActivity.class);
        startActivity(intent);
    }

    /**
     * 打开关于女有
     * */
    public void about(View view){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,AboutActivity.class);
        startActivity(intent);
    }

    /**
     * 打开新手指引页
     * */
    public void startGuide(View view){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,GuideActivity.class);
        startActivity(intent);
    }



    @Override
    public void successed(Call call, Response response) throws IOException {

    }

    @Override
    public void failure(IOException e) {
        //System.out.println("服务器异常！！！");
    }

    @Override
    public void failed(Call call, Response response) throws IOException {

    }

    @Override
    public void register() {

    }

    @Override
    public void successedGetuserInfo(Call call, Response response) throws IOException {
        try {
            JSONObject jsonObject = new JSONObject(response.body().string());
            //System.out.println(jsonObject.toString());
            if(jsonObject.getBoolean("status")){
                JSONObject data = jsonObject.getJSONObject("data");
                editor.putString("nickName",data.optString("nickName","女有"));
                editor.putString("bDate",data.optString("bDate","1976-1-1"));
                editor.putString("height",data.optString("height","150cm"));
                editor.putString("weight",data.optString("weight","40kg"));
                editor.putString("mDate",data.optString("mDate","10号"));
                editor.commit();
            }else{

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failureGetuserInfo(IOException e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast("服务器异常，获取个人信息失败");
            }
        });
    }

    @Override
    public void failedGetuserInfo(Call call, Response response) throws IOException {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast("服务异常，获取个人信息失败");
            }
        });
    }

    @Override
    public void successedGetuserHeader(Call call, Response response) throws IOException {
        //将响应数据转化为输入流数据
        InputStream inputStream=response.body().byteStream();
        if(inputStream != null){
            //将输入流数据转化为Bitmap位图数据
            Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
            File file = new File(Environment.getExternalStorageDirectory().getPath(),filename+".png");
            if (file.exists()){
                file.delete();
            }
            file.createNewFile();
            //创建文件输出流对象用来向文件中写入数据
            FileOutputStream out=new FileOutputStream(file);
            //将bitmap存储为jpg格式的图片
            bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
            //刷新文件流
            out.flush();
            out.close();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initImageView();
                }
            });
        }

    }

    @Override
    public void failureGetuserHeader(IOException e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast("服务器异常,获取头像失败");
            }
        });

    }

    @Override
    public void failedGetuserHeader(Call call, Response response) throws IOException {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast("服务异常,获取头像失败");
            }
        });

    }

    @Override
    public void openDeep() {
        if(!kUBService.isLink()) {
            Toast.makeText(getApplication(),"设备未连接",Toast.LENGTH_SHORT).show();
            return;
        }
        kUBService.sendMessage(Protocol.getOpenDeep());
        //showToast("OPENDEEP");
    }

    @Override
    public void closeDeep() {
        if(!kUBService.isLink()) {
            Toast.makeText(getApplication(),"设备未连接",Toast.LENGTH_SHORT).show();
            return;
        }
        kUBService.sendMessage(Protocol.getCloseDeep());
        //showToast("CLOSEDEEP");
    }

    //设置默认强度
    @Override
    public void setDefIntensity(int intensity) {
        if(!kUBService.isLink()){
            Toast.makeText(getApplication(),"设备未连接",Toast.LENGTH_SHORT).show();
            return;
        }
        kUBService.sendMessage(Protocol.getDefIntensitySetInstruct(intensity));
    }

    //每一秒扫描设备连接状态并且发送读电量信息
    private Thread thread = new Thread(){
        @Override
        public void run() {
            final Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if(kUBService.isLink())
                    {
                        kUBService.sendMessage(Protocol.getElectricGetInstruct());
                    }
                }
            };
            timer.schedule(task,0,30*1000);
        }
    };


}