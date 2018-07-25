package cn.cjwddz.knowu.service;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.inuker.bluetooth.library.BluetoothClient;
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
import java.util.UUID;

import cn.cjwddz.knowu.activity.MainActivity;
import cn.cjwddz.knowu.view.CountDownTimerView;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;

public class KnowUBleService extends Service implements ServiceInterface{

    private MyBinder myBinder = new MyBinder();
    private boolean isScanning = false;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    BluetoothClient ble;
    MyDevice linkingDevice;
    MyDevice lastDevice;
    private List<MyDevice> deviceArray = new ArrayList<>();
    private int rssi = -1000;

    private UIInterface uiInterface;
    public void setUiInterface(UIInterface uiInterface){
        this.uiInterface = uiInterface;
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

    /**
     * 是否连接着设备
     * @return
     */
    public boolean isLink(){
        return linkingDevice!=null;
    }

    @Override
    public void scan() {
        //uiInterface.startService();
     SearchRequest request = new SearchRequest.Builder()
             .searchBluetoothLeDevice(3000,2)  //扫描ble设备2次每次五秒
             .build();
     SearchResponse response = new SearchResponse() {
      @Override
      public void onSearchStarted() {
       isScanning = true;
      // uiInterface.scanning();
      }

      @Override
      public void onDeviceFounded(SearchResult device) {
          //uiInterface.hscanDevice();
          MyDevice fDevice = new MyDevice(device);
       if(!fDevice.ok)
        return;
       //搜寻到本公司设备后自行连接
          // todo 存入数组
          deviceArray.add(fDevice);
          //uiInterface.scanSuccess(fDevice.getDevice().getAddress());
          if(deviceArray.size()>=2){
              ble.stopSearch();
          }
      }

      @Override
      public void onSearchStopped() {
          isScanning = false;
          connecctDevice();
      }

      @Override
      public void onSearchCanceled() {
          isScanning = false;
          connecctDevice();
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
    public void disConnectDevice(MyDevice device) {
     ble.disconnect(device.getDevice().getAddress());
    }

    @Override
    public void sendMessage(final byte[] msg) {
        final String mac = linkingDevice.getDevice().getAddress();
        ble.write(mac, UUID.fromString(Constants.SERVICE_UUID), UUID.fromString(Constants.CHARACTER_UUID), msg, new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                if (code != com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS) {
                    // TODO: 信息发送失败处理
                 // uiInterface.sendMessage("信息发送失败！！！");
                    return;
                }
                //uiInterface.sendMessage("信息发送成功！！！");
            }
        });
    }

 private void connecctDevice() {

  String mac = null;
  int status = -1;
  if(linkingDevice!=null){
   mac=linkingDevice.getDevice().getAddress();
   status = ble.getConnectStatus(mac);
  }
  if(status== BluetoothProfile.STATE_CONNECTED && linkingDevice !=null)
   ble.disconnect(mac);
  linkingDevice =null;
     for( MyDevice a:deviceArray){
        // uiInterface.scanSuccess("取得rssi:"+a.getDevice().rssi);
         lastDevice = a.getDevice().rssi>rssi?a:lastDevice;
         rssi = a.getDevice().rssi>rssi?a.getDevice().rssi:rssi;
                 }
  mac=lastDevice.getDevice().getAddress();
     final String finalMac = mac;
  //设置连接应答
  BleConnectResponse reponse = new BleConnectResponse() {
   @Override
   public void onResponse(int code, BleGattProfile data) {
       if(code== com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS)
           linkingDevice=lastDevice;

       // 连接成功通知notify监听器
        if(code!= com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS)
           return;
       ble.notify(finalMac, UUID.fromString(Constants.SERVICE_UUID), UUID.fromString(Constants.CHARACTER_UUID), new BleNotifyResponse() {
          @Override
           public void onNotify(UUID service, UUID character, byte[] value) {
              switch (value[3]){
                  // TODO: 读应答响应
                  case 0x03:
                      switch (value[4]){
                          // TODO: 读取力度值
                          case (byte) 0xc2:
                              break;
                          // TODO: 读取电量值
                          case (byte) 0xc5:
                              uiInterface.updateView(value[5]);
                              break;
                          default:break;
                      }
                      break;
                  // TODO: 写应答响应
                  case 0x04:
                      break;
                  default:break;

              }
           }
           @Override
           public void onResponse(int code) {
               if (code == com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS) {
                   //uiInterface.setNotify("添加notify成功！");
               }else{
                   // todo 添加notify失败！！
                   //uiInterface.setNotify("添加notify失败！");
               }
           }
       });
    if(code == REQUEST_SUCCESS){
     //蓝牙连接成功
     uiInterface.connectSuccess();
        ble.stopSearch();
        stopScan();
    }
   }
  };
  //设置连接错误处理
  BleConnectOptions optionns = new BleConnectOptions.Builder()
          .setConnectRetry(2)
          .setConnectTimeout(10000)
          .setServiceDiscoverRetry(3)
          .setServiceDiscoverTimeout(10000)
          .build();

     //uiInterface.connectting();
  ble.connect(mac,optionns,reponse);
 }

}
