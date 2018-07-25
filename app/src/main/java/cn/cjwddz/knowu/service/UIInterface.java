package cn.cjwddz.knowu.service;

/**
 * Created by Administrator on 2018/7/18.
 */

public interface UIInterface {
    void connectSuccess();
    void scanning();
    void stopScan();
    void hscanDevice();
    void scanSuccess(String name);
    void startService();
    void connectting();
    void setNotify(String status);
    void sendMessage(String status);
    void updateView(int count);
}
