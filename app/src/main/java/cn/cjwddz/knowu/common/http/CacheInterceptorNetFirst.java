package cn.cjwddz.knowu.common.http;

import java.io.IOException;

import cn.cjwddz.knowu.common.utils.MyUtils;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by K.b. on 2018/10/19.
 */

public class CacheInterceptorNetFirst implements Interceptor{
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();//获取请求
        if(!MyUtils.isNetworkAvailable()){
            //没网络，去缓存读取数据
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }
        Response originalResponse = chain.proceed(request);
        if(MyUtils.isNetworkAvailable()){
            String cacheControl = request.cacheControl().toString();
            //有网络
            return originalResponse.newBuilder()
                    .header("Cache-Control","public, max-age=" + 0)  //加字段
                    .removeHeader("Pragma")     //减字段
                    .build();
        }else{
            int maxTime = 24*60*60;
            //无网络，读缓存
            return originalResponse.newBuilder()
                    .header("Cache-Control","public, only-if-cached, max-stale="+maxTime)
                    .removeHeader("Pragma")
                    .build();
        }
    }
}
