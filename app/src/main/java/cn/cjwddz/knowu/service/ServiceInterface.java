package cn.cjwddz.knowu.service;

/**
 * Created by Administrator on 2018/7/18.
 */

public interface ServiceInterface {
    void scan();
    void stopScan();
   // void connecctDivice();
    void disConnectDevice(MyDevice device);
    void sendMessage(byte[] msg);

}
