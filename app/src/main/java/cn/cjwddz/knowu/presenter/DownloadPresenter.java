package cn.cjwddz.knowu.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.common.http.MyHTTPClient;
import cn.cjwddz.knowu.mview.DownloadView;
import cn.cjwddz.knowu.service.Constants;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



/**
 * Created by K.B. on 2018/10/22.
 */

public class DownloadPresenter {
    Context context;
    DownloadView downloadView;
    public DownloadPresenter(DownloadView downloadView,Context context) {
        this.downloadView = downloadView;
        this.context = context;
    }

    /**
     * 获取APP版本，判断是否需要更新
     * */
    public void updateAPK(String url, final String now_version){
        OkHttpClient okHttpClient = MyHTTPClient.getInstance().getOkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {downloadView.showMsg();}
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //System.out.println("版本更新检查");
                String url = "";
                String new_version = "";
               boolean forceUpdate = false;
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    System.out.println(jsonObject.toString());
                    //forceUpdate = jsonObject.getBoolean("forceUpdate");
                    url = jsonObject.getString("url");
                    new_version = jsonObject.getString("new_version");
                } catch(Exception e){
                    e.printStackTrace();
                }

                if(new_version.equals(now_version)){
                    downloadView.showMsg();
                }else{
                    downloadView.showUpdateDialog(url);
                }
            }
            });
    }

    /**
     * 下载APK文件
     * */
    public void downloadFile(String url, final Activity activity){
        OkHttpClient okHttpClient = MyHTTPClient.getInstance().getOkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                downloadView.downFail(e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    InputStream is = null;
                    FileOutputStream fos = null;
                    try{
                        is = response.body().byteStream();
                        long total = response.body().contentLength();
                        int max= 1000;
                        downloadView.setProgressMax((int) total);
                        if(is != null){
                            File file = new File(Environment.getExternalStorageDirectory(),activity.getResources().getString(R.string.app_name)+".apk");
                            if (file.exists()){
                                file.delete();
                            }
                            file.createNewFile();
                            fos = new FileOutputStream(file);
                            byte[] buf = new byte[1024];
                            int count = -1;
                            int process = 0;
                            while((count = is.read(buf)) != -1 ){
                                fos.write(buf,0,count);
                                process += count;
                                downloadView.updateProgress(process,process, (int) total);
                            }
                            fos.flush();
                            if(fos != null){
                                fos.close();
                            }
                            downloadView.downLoadSuccess();
                        }
                    }catch (Exception e){
                        downloadView.downFail(e);
                    }finally {
                        try{
                            if(is != null){
                                is.close();
                            }
                        }catch (Exception e){

                        }
                        try{
                            if(fos != null){
                                fos.close();
                            }
                        }catch (Exception e){

                        }
                    }
                }else{
                    //downloadView.downFail();
                }
            }
        });
    }
}
