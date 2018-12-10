package cn.cjwddz.knowu.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.adapters.MyAdapter;
import cn.cjwddz.knowu.common.application.AppManager;
import cn.cjwddz.knowu.mview.DownloadView;
import cn.cjwddz.knowu.presenter.DownloadPresenter;
import cn.cjwddz.knowu.service.Constants;
import cn.cjwddz.knowu.view.MyDialog;

public class AboutActivity extends AppCompatActivity implements DownloadView{

    private TextView tv_appVersion;
    private ImageView iv_hadNewVersion;
    private String now_version;
    private DownloadPresenter downloadPresenter;
    private  MyDialog dialog;
    private View dialogView;
    private ProgressBar progressBar;
    private  TextView count_tv;
    private  TextView total_tv;

    android.support.v7.app.ActionBar actionBar;

    private ImageButton ibtn_turnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        AppManager.getAppManager().addActivity(this);
        //标题栏返回按键
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        //google的actionbar是分为上下两栏显示的，上面的代码只能设置顶部actionbar的背景色，为了让下面的背景色一致，还需要添加一行代码：
        actionBar.setSplitBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View actionBarView = View.inflate(this,R.layout.actionbar_about,null);
        if(actionBar != null){
            actionBar.setCustomView(actionBarView,lp);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        }
        ibtn_turnBack = actionBarView.findViewById(R.id.turnBack);
        ibtn_turnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        downloadPresenter = new DownloadPresenter(this,this);
        tv_appVersion = (TextView) findViewById(R.id.tv_appVersion);
        iv_hadNewVersion = (ImageView) findViewById(R.id.iv_hadNewVersion);
        try {
            PackageManager packageManager = this.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(),0);
            now_version = packageInfo.versionName;//获取原版本号
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tv_appVersion.setText(now_version);
    }

    /**
     * 检查版本更新
     * */
    public void checkUpdate(View view){
        //动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
            }, Constants.PERMISSION_REQUEST_COARSE_LOCATION);
        }
        //downloadPresenter.updateAPK(Constants.UPDATE,now_version);
        downloadApp(Constants.GETAPK);
    }

    /**
     * 返回
     * */
    public void turnBack(View view){
       finish();
    }

    /**
     * 意见反馈
     * */
    public void giveIdeas(View view){
        Intent intent = new Intent();
        intent.setClass(AboutActivity.this,OpinionActivity.class);
        startActivity(intent);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this)
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
                                    Uri contentUri = FileProvider.getUriForFile(AboutActivity.this, "com.ljh.knowU.fileProvider", new File(Environment.getExternalStorageDirectory(), String.valueOf(R.string.app_name)+".apk"));
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
                AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this)
                        .setView(R.layout.updatefaildialog);
                builder.create()
                        .show();
                e.printStackTrace();
            }
        });
    }

    /**
     * 显示已是最新版本
     * */
    @Override
    public void showMsg() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this)
                        .setView(R.layout.updatedialog);
                builder.create()
                        .show();
            }
        });
    }

    /**
     * 提示确认是否更新
     * */
    @Override
    public void showUpdateDialog(final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this)
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
                                    Toast.makeText(AboutActivity.this,"SD卡不可用，请插入SD卡",Toast.LENGTH_SHORT).show();
                                }
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
