package cn.cjwddz.knowu.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.common.application.AppManager;
import cn.cjwddz.knowu.common.http.MyHTTPClient;
import cn.cjwddz.knowu.common.utils.MyUtils;
import cn.cjwddz.knowu.mview.DownloadView;
import cn.cjwddz.knowu.presenter.DownloadPresenter;
import cn.cjwddz.knowu.service.Constants;
import cn.cjwddz.knowu.service.MyInterface;
import cn.cjwddz.knowu.view.MyDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class StartActivity extends Activity implements MyInterface,DownloadView{
   private AnimationDrawable anim;
    private boolean isstart = false;

    private String phoneNumber;
    //内储存
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private Boolean isLogin;

    private String now_version;
    private DownloadPresenter downloadPresenter;
    private  MyDialog dialog;
    private View dialogView;
    private ProgressBar progressBar;
    private TextView count_tv;
    private  TextView total_tv;

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
        //动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
            }, Constants.PERMISSION_REQUEST_COARSE_LOCATION);
        }
        preferences = getSharedPreferences("knowu",MODE_PRIVATE);
        isLogin = preferences.getBoolean("isLogin",false);
        phoneNumber = preferences.getString("phoneNumber","default");
        downloadPresenter = new DownloadPresenter(this,this);
        try {
            PackageManager packageManager = this.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(),0);
            now_version = packageInfo.versionName;//获取原版本号
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        editor = preferences.edit();
        ImageView imageView = findViewById(R.id.iv_Gif);
        anim = (AnimationDrawable) imageView.getBackground();
        anim.start();
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(phoneNumber.equals("default")){
                    startApp();
                }else{
                    if( !isstart){
                        isstart = true;
                        getLogin(Constants.LOGIN,phoneNumber);
                        //downloadPresenter.updateAPK(Constants.UPDATE,now_version);
                    }
                }

            }
        },2000);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phoneNumber.equals("default")){
                    startApp();
                }else{
                    if( !isstart){
                        isstart = true;
                        getLogin(Constants.LOGIN,phoneNumber);
                    }
                }
                //downloadPresenter.updateAPK(Constants.UPDATE,now_version);
                //timer.cancel();
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
        Intent intent = new Intent();
        intent = intent.setClass(StartActivity.this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        finish();
    }

    /**
     * 请求登录
     * */
    public  void getLogin(String url,String phoneNumber){
        OkHttpClient okHttpClient = MyHTTPClient.getInstance().getOkHttpClient();
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("phone",phoneNumber);
        formBody.add("password", phoneNumber);
        //formBody.add("password", MyUtils.md5(phoneNumber));
        //System.out.println( MyUtils.md5(phoneNumber));
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
                    System.out.println(status+msg);
                } catch(Exception e){
                    e.printStackTrace();
                }
                if (status.equals("true")) {
                    //System.out.println(status + msg);
                    myInterface.successed(call, response);
                } else {
                    if(msg.equals("用户不存在")){
                        myInterface.register();
                    }else if(msg.equals("用户名参数错误")){
                        myInterface.register();
                    }else{
                        //System.out.println(status + msg);
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(StartActivity.this,"服务器异常",Toast.LENGTH_LONG).show();
            }
        });
        //Intent intent = new Intent();
        //intent = intent.setClass(StartActivity.this, RegisterActivity.class);
        //startActivity(intent);
       // overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
       // finish();
    }

    @Override
    public void failed(Call call, Response response) throws IOException {
        if(response.code() ==504){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(StartActivity.this,"Res服务器异常",Toast.LENGTH_LONG).show();
                }
            });

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

    /**
     * 下载成功
     * */
    @Override
    public void downLoadSuccess() {
        if(progressBar != null&&progressBar.getVisibility() == View.VISIBLE){
            dialog.dismiss();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this)
                        .setTitle("下载完成")
                        .setIcon(R.drawable.logo_u)
                        .setMessage("是否安装？")
                        .setCancelable(false)
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //aandroid N的权限问题
                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    Uri contentUri = FileProvider.getUriForFile(StartActivity.this, "com.ljh.knowU.fileProvider", new File(Environment.getExternalStorageDirectory(), String.valueOf(R.string.app_name)+".apk"));
                                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                                } else {
                                    intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(),getResources().getString(R.string.app_name)+".apk")), "application/vnd.android.package-archive");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                }
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.create()
                        .show();
            }
        });
    }

    /**
     * 更新进度条
     * */
    @Override
    public void updateProgress(int process, final int count, final int total) {
        progressBar.setProgress(process);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                count_tv.setText(String.valueOf(count));
                total_tv.setText(String.valueOf(total));
            }
        });
    }

    /**
     * 设置进度条最大值
     * */
    @Override
    public void setProgressMax(int max) {
        progressBar.setMax(max);
    }

    /**
     * 更新失败
     * */
    @Override
    public void downFail(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this)
                        .setView(R.layout.updatefaildialog);
                builder.create()
                        .show();
                e.printStackTrace();
            }
        });
    }

    @Override
    public void showMsg() {
        if(!isLogin){
            System.out.println(isLogin);
            Intent intent = new Intent();
            intent = intent.setClass(StartActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            finish();
        }else{
            getLogin(Constants.LOGIN,phoneNumber);
        }
    }

    @Override
    public void showUpdateDialog(final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this)
                        .setTitle("更新提示")
                        .setIcon(R.drawable.logo_u)
                        .setMessage("检查到新版本"+"\n是否更新")
                        .setCancelable(false)
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                                    downloadApp(url);
                                }else{
                                    Toast.makeText(StartActivity.this,"SD卡不可用，请插入SD卡",Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppManager.getAppManager().finishAllActivity();
                                System.exit(0);
                            }
                        });
                builder.create()
                        .show();
            }
        });
    }

    /**
     * 开始下载
     * */
    private void downloadApp(final String url){
        MyDialog.Builder builder = new MyDialog.Builder(this);
        dialog = builder.setStyle(R.style.MyDialog)
                .setCancelTouchout(false)
                .setView(R.layout.downloaddialog)
                .setWidthdp(200)
                .setHeihgtdp(166)
                .build();
        dialog.show();
        downloadPresenter.downloadFile(url,this);
        dialogView = dialog.getView();
        progressBar = dialogView.findViewById(R.id.downProgressBar);
        count_tv = dialogView.findViewById(R.id.count);
        total_tv = dialogView.findViewById(R.id.total);
    }
}
