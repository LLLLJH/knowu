package cn.cjwddz.knowu.common.http;

import java.io.IOException;

import cn.cjwddz.knowu.common.utils.MyUtils;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by K.B. on 2018/9/11.
 * 实现缓存
 */

public class CacheInterceptor implements Interceptor{
    @Override
    public Response intercept(Chain chain) throws IOException {
        //获取拦截到的网络请求
        Request request = chain.request();
        //判断是否有网络请求，没有网络则从缓存读取数据，否则发送请求
        if(!MyUtils.isNetworkAvailable()){
            //没网络，去缓存读取数据
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }
        Response originalResponse = chain.proceed(request);
        int maxTime = 60;
        if(MyUtils.isNetworkAvailable()){
            //有网络
            return originalResponse.newBuilder()
                    .header("Cache-Control","public, max-age=" + maxTime)  //加字段
                    .removeHeader("Pragma")     //减字段
                    .build();
        }else{
            //无网络，读缓存
            return originalResponse.newBuilder()
                    .header("Cache-Control","public, only-if-cached, max-stale="+maxTime)
                    .removeHeader("Pragma")
                    .build();
        }
    }
}
