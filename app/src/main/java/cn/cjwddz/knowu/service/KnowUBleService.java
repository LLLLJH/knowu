package cn.cjwddz.knowu.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadRssiResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import cn.cjwddz.knowu.FragmentUI;
import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.activity.MainActivity;
import cn.cjwddz.knowu.common.application.AppManager;
import cn.cjwddz.knowu.view.CountDownTimerView;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DEVICE_CONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DEVICE_CONNECTING;
import static com.inuker.bluetooth.library.Constants.STATUS_DEVICE_DISCONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DISCONNECTED;

public class KnowUBleService extends Service implements ServiceInterface{

    private MyBinder myBinder = new MyBinder();
    private boolean isScanning = false;
    private String mac;
    int status = -1;
    BluetoothClient ble;
    MyDevice linkingDevice;
    MyDevice lastDevice;
    private List<MyDevice> deviceArray = new ArrayList<>();
    private int rssi = -1000;
    public boolean err = false;
    public int connectCount = 0;

    private FragmentUIInterface fragmentUIInterface;
    public void setFragmentUIInterface(FragmentUIInterface fragmentUIInterface){
        this.fragmentUIInterface = fragmentUIInterface;
    }
    private UIInterface uiInterface;
    public void setUiInterface(UIInterface uiInterface){
        this.uiInterface = uiInterface;
    }

    private BatteryInterface batteryInterface;
    public void setBatteryInterface(BatteryInterface batteryInterface){
        this.batteryInterface = batteryInterface;
    }

    public class MyBinder extends Binder
    {
        public KnowUBleService getService(){
            return KnowUBleService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ble = new BluetoothClient(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ble.unregisterConnectStatusListener(mac,bleConnectStatusListener);
    }

    /**
     * 是否不在Mainactivity退出蓝牙连接
     * @return
     */
    public boolean isErr(){
        return err;
    }

    /**
     * 是否不在Mainactivity退出蓝牙连接
     * @return
     */
    public void setErr(boolean err){
        this.err = err;
    }
    /**
     * 是否连接着设备
     * @return
     */
    public boolean isLink(){
        return linkingDevice!=null;
    }

    /**
     * 是否连接着设备
     * @return
     */
    public MyDevice getMDevice(){
        return linkingDevice;
    }

    @Override
    public void scan() {
        //uiInterface.startService();
     SearchRequest request = new SearchRequest.Builder()
             .searchBluetoothLeDevice(5000,2)  //扫描ble设备2次每次五秒
             .build();
     SearchResponse response = new SearchResponse() {
      @Override
      public void onSearchStarted() {
       isScanning = true;
       //uiInterface.scanning();
      }

      @Override
      public void onDeviceFounded(SearchResult device) {
          //uiInterface.hscanDevice();
          MyDevice fDevice = new MyDevice(device);
       if(!fDevice.ok)
        return;
       //搜寻到本公司设备后自行连接
          //  存入数组
          deviceArray.add(fDevice);
          //uiInterface.scanSuccess(fDevice.getDevice().getAddress());
          if(deviceArray.size()>=2){
              ble.stopSearch();
          }
      }

      @Override
      public void onSearchStopped() {
          isScanning = false;
          uiInterface.back();
          //System.out.print("onSearchStopped");
      }

      @Override
      public void onSearchCanceled() {
          isScanning = false;
          connecctDevice();
         // System.out.print("onSearchCanceled");
      }
     };
     ble.search(request,response);
    }

    @Override
    public void stopScan() {
     ble.stopSearch();
        //uiInterface.stopScan();
    }

    @Override
    public void disConnectDevice() {
        if(ble != null && linkingDevice != null){
           // ble.disconnect(linkingDevice.getDevice().getAddress());
            status = ble.getConnectStatus(mac);
        }
       // if(linkingDevice!=null){
        //    mac=linkingDevice.getDevice().getAddress();
        //    status = ble.getConnectStatus(mac);
       // }
        if(status== BluetoothProfile.STATE_CONNECTED && linkingDevice !=null)
            ble.disconnect(mac);
        linkingDevice =null;
        mac = null;
        deviceArray.clear();
        uiInterface.back();
    }

    @Override
    public void sendMessage(final byte[] msg) {
        if(linkingDevice!=null){
            final String mac = linkingDevice.getDevice().getAddress();
            /**   if(ble.getConnectStatus(mac) == STATUS_DISCONNECTED||ble.getConnectStatus(mac)==STATUS_DEVICE_DISCONNECTED){
             linkingDevice = null;
             uiInterface.back();
             return;
             }*/
            ble.write(mac, UUID.fromString(Constants.SERVICE_UUID), UUID.fromString(Constants.CHARACTER_UUID), msg, new BleWriteResponse() {
                @Override
                public void onResponse(int code) {
                    if (code != com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS) {
                        //  信息发送失败处理
                        //uiInterface.sendMessage("信息发送失败！！！");
                        return;
                    }
                    //uiInterface.sendMessage("信息发送成功！！！");
                }
            });
        }

    }

 private void connecctDevice() {
     /** 2019/05/20
     if(linkingDevice!=null){
         mac=linkingDevice.getDevice().getAddress();
         status = ble.getConnectStatus(mac);
     }
     if(status== BluetoothProfile.STATE_CONNECTED && linkingDevice !=null)
         ble.disconnect(mac);
      */
     linkingDevice =null;
     lastDevice = null;
     mac = null;
     rssi = -1000;
     for( MyDevice a:deviceArray){
         //lastDevice = deviceArray.get(0);
         // uiInterface.scanSuccess("取得rssi:"+a.getDevice().rssi);
         lastDevice = a.getDevice().rssi>rssi?a:lastDevice;
         rssi = lastDevice.getDevice().rssi;
     }
     if(lastDevice!=null){
         mac=lastDevice.getDevice().getAddress();
     }
     if(mac !=null){
         final String finalMac = mac;
         //设置连接应答
         BleConnectResponse response = new BleConnectResponse() {
             @Override
             public void onResponse(int code, BleGattProfile data) {
                 if(code== REQUEST_SUCCESS)
                 {
                     //stopScan();
                     // 连接成功通知notify监听器
                     // if(code!= com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS)

                     ble.notify(finalMac, UUID.fromString(Constants.SERVICE_UUID), UUID.fromString(Constants.CHARACTER_UUID), new BleNotifyResponse() {
                         @Override
                         public void onNotify(UUID service, UUID character, byte[] value) {
                             // System.out.println(value.toString());
                             switch (value[3]){
                                 case 0x03:
                                     switch (value[4]){
                                         //  读取力度值
                                         case (byte) 0xc2:
                                             uiInterface.setLastProgress(value[5]);
                                             break;
                                         //  读取电量值
                                         case (byte) 0xc5:
                                             if(value[5]<=10){
                                                 uiInterface.updateView(value[5]);
                                             }
                                             batteryInterface.setBattery(value[5]);
                                             break;
                                         case (byte)0xc6:
                                             switch (value[5]){
                                                 case (byte) 0x00:
                                                     uiInterface.setDeepStatus(false);
                                                     break;
                                                 case (byte) 0x01:
                                                     uiInterface.setDeepStatus(true);
                                                     break;
                                             }
                                             break;
                                         case (byte)0xc8:
                                             uiInterface.getDefIntensity(value[5]);
                                             break;
                                         default:break;
                                     }
                                     break;
                                 //  写应答响应
                                 case 0x04:
                                     //写应答是否成功
                                     switch (value[4]){
                                         //写加热状态
                                         case (byte) 0xc7:
                                             switch (value[5]){
                                                 case (byte) 0x00:
                                                     fragmentUIInterface.openSuccess();
                                                     break;
                                                 case (byte) 0x01:
                                                     fragmentUIInterface.openFail();
                                                     break;
                                                 case (byte) 0x02:
                                                     fragmentUIInterface.closeSuccess();
                                                     break;
                                                 case (byte) 0x03:
                                                     fragmentUIInterface.closeSuccess();
                                                     break;
                                                 default:break;
                                             }
                                             break;
                                         //写提示音状态
                                         case (byte) 0x06:
                                             break;
                                         default:break;
                                     }
                                     break;
                                 default:break;

                             }
                         }
                         @Override
                         public void onResponse(int code) {
                             if (code == com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS) {
                                 //uiInterface.setNotify("添加notify成功！");
                             }else{
                                 //  添加notify失败！！
                                 // uiInterface.setNotify("添加notify失败！");
                                 //ble.disconnect(linkingDevice.getDevice().getAddress());
                             }
                         }
                     });
                 }
             }
         };
         //设置连接错误处理
         BleConnectOptions optionns = new BleConnectOptions.Builder()
                 .setConnectRetry(2)
                 .setConnectTimeout(6000)
                 .setServiceDiscoverRetry(2)
                 .setServiceDiscoverTimeout(5000)
                 .build();
         ble.registerConnectStatusListener(mac,bleConnectStatusListener);
         ble.connect(mac,optionns,response);
     }
 }

//蓝牙连接监听
 private final BleConnectStatusListener bleConnectStatusListener = new BleConnectStatusListener() {
     @Override
     public void onConnectStatusChanged(String mac, int status) {
         if(status == STATUS_CONNECTED){
             setErr(false);
             linkingDevice=lastDevice;
             //蓝牙连接成功
             uiInterface.connectSuccess();
             ble.stopSearch();
         }else if(status == STATUS_DISCONNECTED||status == STATUS_DEVICE_DISCONNECTED){
             if(linkingDevice == null && connectCount < 1){
                 connectCount++;
                 return;
             }else{
                 connectCount = 0;
                 linkingDevice = null;
                 mac = null;
                 deviceArray.clear();
                 //ble.disconnect(mac);
                 ble.unregisterConnectStatusListener(mac,bleConnectStatusListener);
                 if(!err){
                     uiInterface.back();
                 }
             }
         }
     }

};


}
