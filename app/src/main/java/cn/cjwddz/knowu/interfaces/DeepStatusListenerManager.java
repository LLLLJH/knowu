package cn.cjwddz.knowu.interfaces;



/**
 * Created by Administrator on 2018/12/17.
 */

public class DeepStatusListenerManager {
    private Activity_interface mListener;
    public DeepStatusListenerManager() {

    }

    private static DeepStatusListenerManager manager = new DeepStatusListenerManager();

    public static DeepStatusListenerManager getInstance() {
        if(manager == null){
            synchronized (DeepStatusListenerManager.class){
                if(manager == null){
                    manager = new DeepStatusListenerManager();
                }
            }
        }
        return manager;

    }

    public void setConnectionStateListener(Activity_interface mListener) {
        this.mListener = mListener;

    }

    public void openDeep() {
        if (mListener != null) {
            mListener.openDeep();
        }

    }

    public void closeDeep(){
        if (mListener!=null){
            mListener.closeDeep();

        }

    }

}
