package cn.cjwddz.knowu.mview;

import java.io.IOException;

/**
 * Created by K.B. on 2018/10/22.
 */

public interface DownloadView {
    void downLoadSuccess();
    void updateProgress(int process,int percent);
    void setProgressMax(int max);
    void downFail(Exception e);//下载失败
    void showMsg(); //显示已是最新版本dialog
    void showUpdateDialog(String url);//显示下载更新的dialog
}
