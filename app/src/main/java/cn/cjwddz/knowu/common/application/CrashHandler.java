package cn.cjwddz.knowu.common.application;

import android.content.Context;

import android.content.pm.PackageManager;
import android.os.Build;
import android.text.format.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.cjwddz.knowu.common.http.HttpClient;
import cn.cjwddz.knowu.common.utils.MyUtils;
import cn.cjwddz.knowu.service.Constants;

/**
 * Created by K.B. on 2018/9/6.
 *
 * 在Application中统一捕获异常，保存到文件中上传
 * 自定义异常类实现UncaughtExceptionHandler接口，当某个页面出现
 * 异常就会调用uncaughtException这个方法，我们可以在这个方法中获取
 * 异常信息、时间等，然后将获取到的信息发送到我们指定的服务器
 *
 */


public class CrashHandler implements Thread.UncaughtExceptionHandler{
   // private static final String logTag = CrashHandler.class.getSimpleName();
    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler defaultHandler;

    // CrashHandler实例
    private static CrashHandler crashHandlerInstance;

    // 程序的Context对象
    private Context context;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {}

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (crashHandlerInstance == null) {
            crashHandlerInstance = new CrashHandler();
        }

        return crashHandlerInstance;
    }

    /**
     * 初始化,注册Context对象,
     * 获取系统默认的UncaughtException处理器,
     * 设置该CrashHandler为程序的默认处理器
     *
     * @param context
     */
    public void init(Context context){
        this.context = context;
        //获取默认的handler
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置自己的handler
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     * @param t 出现异常的线程
     * @param e 异常报告
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if(!handleExcepton(t,e) && defaultHandler != null){
            //系统默认的异常处理器来处理异常
            defaultHandler.uncaughtException(t,e);
        }else{
            //如果自己处理了异常，则不会弹出错误对话框，则需要手动退出程序
            try{
                Thread.sleep(3000);
            }catch(InterruptedException ie){
                //kill myself
                android.os.Process.killProcess(android.os.Process.myPid());
                //退出程序
                System.exit(10);
            }
        }
    }

    /**
     * 自定义错误处理,收集错误信息
     * 发送错误报告等操作均在此完成.
     * true代表处理该异常，不再向上抛异常，
     * false代表不处理该异常(可以将该log信息存储起来)然后交给上层(这里就到了系统的异常处理)去处理，
     * 简单来说就是true不会弹出那个错误提示框，false就会弹出
     */
    private boolean handleExcepton(final Thread t,final Throwable e){
        if(e == null){
            return false;
        }
        //得到详细的错误信息
        final String errorInfo = this.getErrorInfo(e);
        //上传到服务器
        System.out.println("----错误日志上传------");
        MyUtils.postSystemLog(Constants.ADD_SYSTEM_LOG_URL,errorInfo);
        return true;
    }

    //获取详细的错误信息、应用程序的信息、手机型号版本
    private String getErrorInfo(Throwable e){
        //存储错误代码的位置和信息
        List<String> errorContents = new ArrayList<>();
        //获取错误代码的位置和信息
        StackTraceElement[] ste = e.getStackTrace();
        if(ste != null){
            for(StackTraceElement element:ste){
                if(element != null && element.toString() != null){
                    errorContents.add(e.toString());
                }
            }
        }
        Map<String,Object> errorInfoMap = new HashMap<>();
        //发生错误的时间
        errorInfoMap.put("errorTime", DateFormat.format("yyyy-MM-dd kk:mm:ss", new Date(System.currentTimeMillis())));
        //手机型号
        errorInfoMap.put("phoneType", Build.MODEL);
        //sdk版本
        errorInfoMap.put("androidSdkVersion", Build.VERSION.SDK);
        //系统版本号
        errorInfoMap.put("androidReleaseVersion", Build.VERSION.RELEASE);
        //手机品牌
        errorInfoMap.put("brand",Build.BRAND);
        errorInfoMap.put("errorMassage",e.getMessage());
        //得到的错误信息
        errorInfoMap.put("errorContents",errorContents);
        //
        return HttpClient.mapToQueryString(errorInfoMap);
    }

}
