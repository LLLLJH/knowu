package cn.cjwddz.knowu.interfaces;

import java.util.Map;

/**
 * Created by Administrator on 2018/12/19.
 */

public class SetDateListenerManager_m {
    private SetDate_interface_m mListener;
    public SetDateListenerManager_m() {

    }
    private static SetDateListenerManager_m manager = new SetDateListenerManager_m();

    public static SetDateListenerManager_m getInstance() {
        if(manager == null){
            synchronized (SetDateListenerManager_m.class){
                if(manager == null){
                    manager = new SetDateListenerManager_m();
                }
            }
        }
        return manager;

    }

    public void setConnectionStateListener(SetDate_interface_m mListener) {
        this.mListener = mListener;
    }
    public void getDateMouth(String s,Map m,int p) {
        if (mListener != null) {
           mListener.getDateMonth(s,m,p);
        }

    }
}
