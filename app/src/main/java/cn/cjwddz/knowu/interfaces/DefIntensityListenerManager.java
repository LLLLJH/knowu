package cn.cjwddz.knowu.interfaces;



/**
 * Created by Administrator on 2018/12/17.
 */

public class DefIntensityListenerManager {
    private Activity_interface_intensity mListener;
    public DefIntensityListenerManager() {

    }

    private static DefIntensityListenerManager manager = new DefIntensityListenerManager();

    public static DefIntensityListenerManager getInstance() {
        if(manager == null){
            synchronized (DefIntensityListenerManager.class){
                if(manager == null){
                    manager = new DefIntensityListenerManager();
                }
            }
        }
        return manager;

    }

    public void setConnectionStateListener(Activity_interface_intensity mListener) {
        this.mListener = mListener;

    }

    public void setDefIntensity(int intensity) {
        if (mListener != null) {
            mListener.setDefIntensity(intensity);
        }

    }



}
