package cn.cjwddz.knowu.interfaces;

import java.util.Map;

/**
 * Created by Administrator on 2018/12/19.
 */

public class SetDateListenerManager {
    private SetDate_interface mListener;
    public SetDateListenerManager() {

    }
    private static SetDateListenerManager manager = new SetDateListenerManager();

    public static SetDateListenerManager getInstance() {
        if(manager == null){
            synchronized (SetDateListenerManager.class){
                if(manager == null){
                    manager = new SetDateListenerManager();
                }
            }
        }
        return manager;

    }

    public void setConnectionStateListener(SetDate_interface mListener) {
        this.mListener = mListener;
    }
    public void getDateDay(String s,Map m,int p) {
        if (mListener != null) {
           mListener.getDateDay(s,m,p);
        }

    }
}
