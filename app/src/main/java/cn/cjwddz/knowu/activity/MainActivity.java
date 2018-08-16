package cn.cjwddz.knowu.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;

import cn.cjwddz.knowu.FragmentUI;
import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.service.Constants;
import cn.cjwddz.knowu.service.KnowUBleService;
import cn.cjwddz.knowu.service.Protocol;
import cn.cjwddz.knowu.service.ServiceInterface;
import cn.cjwddz.knowu.service.UIInterface;
import cn.cjwddz.knowu.view.CountDownTimerView;
import cn.cjwddz.knowu.common.application.AppManager;
import cn.cjwddz.knowu.view.MyImageView;

import static cn.cjwddz.knowu.FragmentUI.*;

public class MainActivity extends BaseActivity implements RecyclerView.RecyclerListener,OnFragmentInteractionListener,UIInterface, OnClickListener,SeekBar.OnSeekBarChangeListener {
    private CountDownTimerView countDownTimerView;
    private DrawerLayout drawerLayout;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    private MyImageView header;
    private ImageButton fighting;
    private ImageButton keepfit;
    private ImageButton chira;
    private ImageButton relax;
    private ImageButton last;
    private TextView fighting_tv;
    private TextView keepfit_tv;
    private TextView chira_tv;
    private TextView relax_tv;
    private TextView last_tv;
    private SeekBar seekbar;
    private TextView count;
    private ImageView battery_iv;
    private DisplayMetrics displayMetrics;
    private double width,density;
    private int firstprogress = 0;
    private int progress = 0;
    private int lastprogress = 0;
    private float firstX;
    private float secondX;

    private long exitTime = 0;
    ServiceInterface serviceInterface;


    private KnowUBleService kUBService;
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

    public KnowUBleService getkUBService(){
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
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
    }

    @Override
    void initView() {
     setContentView(R.layout.slidemune);
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
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        count = (TextView) findViewById(R.id.count);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        battery_iv = (ImageView) findViewById(R.id.battery_iv);
       // battery_iv.setImageResource(R.drawable.battery100);
        fighting.setOnClickListener(this);
        keepfit.setOnClickListener(this);
        chira.setOnClickListener(this);
        relax.setOnClickListener(this);
        seekbar.setOnSeekBarChangeListener(this);
        //seekbar.setOnTouchListener(this);
        displayMetrics = getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        density = (width - dip2px(this, 51)) / 20;
        initImageView();
    }

    @Override
    void initData() {
        //打开蓝牙请求
        openBluetooth();
        Intent intent = new Intent(this,KnowUBleService.class);
        bindService(intent,serviceConnection,BIND_AUTO_CREATE);
        undateFragment(0);
        initSeekbarProgress();
    }
   private void initSeekbarProgress(){
       LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
               LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
       layoutParams.leftMargin = (int)(progress*density);
       count.setLayoutParams(layoutParams);
       count.setText("LV."+progress);
   }

    private void initImageView(){
        File file = new File(Environment.getExternalStorageDirectory().getPath(),"cutcamera.png");
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
    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        // 下拉刷新
    }
    public void startSearch(View v){

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

    public void connect(){
        if(bluetoothAdapter.isEnabled()){
            undateFragment(1);
            kUBService.scan();
           //serviceInterface.scan();
        }else{
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

    @SuppressLint("NewApi")
    private void openBluetooth(){
        bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()){
            Intent intent = new Intent(bluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(intent);
        }

    }

    @Override
    public void connectSuccess() {
        undateFragment(2);
        seekbar.setThumb(getResources().getDrawable(R.drawable.seekbar_clicked));
        fighting.setSelected(true);
        fighting_tv.setTextColor(getResources().getColor(R.color.text_color));
        count.setBackgroundResource(R.drawable.count_clicked);
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                undateFragment(3);
                kUBService.sendMessage(Protocol.getModeSetInstruct(0));
                kUBService.sendMessage(Protocol.getIntensityReadInstruct());
                kUBService.sendMessage(Protocol.getElectricGetInstruct());
            }
        };
        timer.schedule(timerTask,1000);
    }

    @Override
    public void scanning() {
       showToast("正在扫描设备...");
    }

    @Override
    public void stopScan() {
       showToast("停止扫描设备...");
    }

    @Override
    public void hscanDevice() {
        showToast("发现设备");
    }

    @Override
    public void scanSuccess(String name) {
       showToast("已扫描到公司设备："+name);
    }


    @Override
    public void startService() {
        showToast("Service已启动");
    }

    @Override
    public void connectting() {
        showToast("连接中...");
    }

    @Override
    public void setNotify(String status) {
        showToast(status);
    }

    @Override
    public void sendMessage(String status) {
        showToast(status);
    }

    @Override
    public void updateView(int count) {
        if(count<=75){
            count = 75;
        }else if(count<=50){
            count = 50;
        }else if(count<=25){
            count = 25;
        }else if(count<10) {
            count = 5;
        }
        // TODO: 更新电量图标
        switch (count){
            case 75:
                battery_iv.setImageResource(R.drawable.battery75);
                break;
            case 50:
                battery_iv.setImageResource(R.drawable.battery50);
                break;
            case 25:
                battery_iv.setImageResource(R.drawable.battery25);
                break;
            case 5 :
                battery_iv.setImageResource(R.drawable.battery0);
                showToast("电量过低！请为设备充电再使用！！！");
                break;
            default:
                battery_iv.setImageResource(R.drawable.battery100);
                break;
        }
    }
//连接错误或断开时回调
    @Override
    public void back() {
        undateFragment(0);
        seekbar.setThumb(getResources().getDrawable(R.drawable.seekbar));
        progress = 0;
        seekbar.setProgress(progress);
        initSeekbarProgress();
        fighting.setSelected(false);
        fighting_tv.setTextColor(getResources().getColor(R.color.mode_text_color));
        count.setBackgroundResource(R.drawable.count);
    }

    @Override
    public void setLastProgress(int progress) {
        seekbar.setProgress(progress);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initImageView();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.fighting:
                if(!kUBService.isLink()){
                    showToast("设备未连接！！！");
                    return;
                }
                kUBService.sendMessage(Protocol.getModeSetInstruct(0));
                fighting.setSelected(true);
                fighting_tv.setTextColor(getResources().getColor(R.color.text_color));
                if(last != fighting){
                    last.setSelected(false);
                    last_tv.setTextColor(getResources().getColor(R.color.mode_text_color));
                    last = fighting;
                    last_tv = fighting_tv;
                }
                break;
            case R.id.keepfit:
                if(!kUBService.isLink()){
                    showToast("设备未连接！！！");
                    return;
                }
                kUBService.sendMessage(Protocol.getModeSetInstruct(0));
                keepfit.setSelected(true);
                keepfit_tv.setTextColor(getResources().getColor(R.color.text_color));
                if(last != keepfit){
                    last.setSelected(false);
                    last_tv.setTextColor(getResources().getColor(R.color.mode_text_color));
                    last = keepfit;
                    last_tv = keepfit_tv;
                }
                break;
            case R.id.chira:
                if(!kUBService.isLink()){
                    showToast("设备未连接！！！");
                    return;
                }
                kUBService.sendMessage(Protocol.getModeSetInstruct(0));
               chira.setSelected(true);
                chira_tv.setTextColor(getResources().getColor(R.color.text_color));
                if(last != chira){
                    last.setSelected(false);
                    last_tv.setTextColor(getResources().getColor(R.color.mode_text_color));
                    last = chira;
                    last_tv = chira_tv;
                }
                break;
            case R.id.relax:
                if(!kUBService.isLink()){
                    showToast("设备未连接！！！");
                    return;
                }
                kUBService.sendMessage(Protocol.getModeSetInstruct(0));
                relax.setSelected(true);
                relax_tv.setTextColor(getResources().getColor(R.color.text_color));
                if(last != relax){
                    last.setSelected(false);
                    last_tv.setTextColor(getResources().getColor(R.color.mode_text_color));
                    last = relax;
                    last_tv = relax_tv;
                }
                break;
            case R.id.information:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,InformationActivity.class);
                startActivity(intent);
                break;
           default:break;
        }
    }

    public void addProgress(View view){
        progress = seekbar.getProgress();
        if(progress < 15){
            progress = progress +1;
            seekbar.setProgress(progress);
            lastprogress = progress;
            kUBService.sendMessage(Protocol.getIntensitySetInstruct(progress));
        }
    }
    public void minusProgress(View view){
        progress = seekbar.getProgress();
        if (progress > 0){
            progress = progress-1;
            seekbar.setProgress(progress);
            lastprogress = progress;
            kUBService.sendMessage(Protocol.getIntensitySetInstruct(progress));
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        // TODO: 滑块力度选择改变回调实现
       if(!kUBService.isLink()){
            showToast("设备未连接！！！");
            seekBar.setProgress(0);
            return;
        }
        progress = seekBar.getProgress();
        initSeekbarProgress();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO: 滑块力度选择触碰回调实现
        if(!kUBService.isLink()){
            showToast("设备未连接！！！");
            seekBar.setProgress(0);
            return;
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO: 滑块力度选择抬起回调实现
       if(!kUBService.isLink()){
            showToast("设备未连接！！！");
            seekBar.setProgress(0);
            return;
        }
       if(seekBar.getProgress()==14||seekBar.getProgress()==15){
           seekBar.setProgress(lastprogress);
           showToast("力度过大！！！请点击+号增加");
       }else{
           lastprogress = seekBar.getProgress();
           kUBService.sendMessage(Protocol.getIntensitySetInstruct(seekBar.getProgress()));
       }
    }


    private void showToast(String s){
        Toast.makeText(this,s, Toast.LENGTH_SHORT).show();
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

    public void getInformation(View view){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,InformationActivity.class);
        startActivity(intent);
    }

    public void openHeader(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(Environment.getExternalStorageDirectory().getPath(),"cutcamera.png");
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

    public void exit(View view){
        AppManager.getAppManager().finishAllActivity();
        System.exit(0);
    }

}