package cn.cjwddz.knowu.service;

import android.util.Log;

import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.beacon.BeaconItem;
import com.inuker.bluetooth.library.search.SearchResult;

import java.util.Arrays;

/**
 * Created by Administrator on 2018/7/18.
 */

public class MyDevice {
    private SearchResult device;
    private byte status;
    private byte[] id;
    private byte call;
    boolean ok=false;

    public SearchResult getDevice(){
        return device;
    }

    public boolean isOk() {
        return ok;
    }

    /**
     * 获取状态
     * @return
     */
    public byte getStatus() {
        return status;
    }

    /**
     * 获取设备id
     * @return
     */
    public byte[] getId() {
        return id;
    }

    /**
     * 是否呼叫状态
     * @return
     */
    public byte getCall() {
        return call;
    }

    public MyDevice(SearchResult device){
        Beacon beacon = new Beacon(device.scanRecord);
        this.device=device;
        ok=false;
        for(BeaconItem ad : beacon.mItems){
            switch (ad.type){
                case 0xf0:
                    if(ad.len==2)
                        this.status=ad.bytes[0];
                    break;
                case 0xf1:
                    if(ad.len==7)
                        this.id=ad.bytes;
                    break;
                case 0xf2:
                    if(ad.len==2)
                        this.call=ad.bytes[0];
                    break;
                case 0x09:
                    String band = new String(ad.bytes);
                    if(band.equals("KNOW U")){
                        ok=true;
                    }
                    break;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyDevice mtDevice = (MyDevice) o;

        if (ok != mtDevice.ok) return false;
        return Arrays.equals(id, mtDevice.id);

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(id);
        result = 31 * result + (ok ? 1 : 0);
        return result;
    }
}
