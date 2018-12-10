package cn.cjwddz.knowu.common.http;



import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.compat.BuildConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.cjwddz.knowu.common.application.AppContext;
import okhttp3.Cache;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by K.B. on 2018/9/10.
 * 定义一个OKHTTP全局请求单例类，全局统一使用单一OkHttpClient
 * 在需要使用OKHttpClient的地方实现如下代码即可
 * OkHttpClient okHttpClient = MyHTTPClient.getInstance().getOkHttpClient();
 */

public class MyHTTPClient {

    //定义读取超时
    private static final long DEFAULT_READ_TIMEOUT =15 * 1000;
    //定义写入超时
    private static final long DEFAULT_WRITE_TIMEOUT = 20 * 1000;
    //定义连接超时
    private static final long DEFAULT_CONNECT_TIMEOUT = 20 * 1000;
    //定义缓存磁盘大小
    private static final long HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 60 * 1024 * 1024;
    private static MyHTTPClient mInstance;
    private OkHttpClient mOkHttpClient;
    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private MyHTTPClient(){
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        if(BuildConfig.DEBUG){
            //调试状态，显示日志
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }else{
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
       /** try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();*/

            mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT,TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT,TimeUnit.MILLISECONDS)
                   /** .sslSocketFactory(sslSocketFactory)
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })*/
                .addInterceptor(logInterceptor)
                    .addNetworkInterceptor(new CacheInterceptor())   //有无网络都会读取缓存
                //.addInterceptor(new CacheInterceptorNetFirst())
                //.addNetworkInterceptor(new CacheInterceptorNetFirst())  //无网络才读取缓存数据，缓存为1天
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url.host(),cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null?cookies:new ArrayList<Cookie>();
                    }
                })
                .build();
        /**catch (Exception e) {
        throw new RuntimeException(e);
    }*/
    }

    /**
     * 获取MyOkHTTPClient实例
     * */
    public static MyHTTPClient getInstance(){
        if(mInstance == null){
            synchronized (MyHTTPClient.class){
                if(mInstance == null){
                    mInstance = new MyHTTPClient();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取OkHttpClient
     * */
    public OkHttpClient getOkHttpClient(){
        setCache();
        return mOkHttpClient;
    }

    /**
     * 设置缓存
     * */
   public void setCache(){
        final File baseDir = AppContext.getInstance().getCacheDir();
        if(baseDir != null){
            final File cacheDir = new File(baseDir,"HttpResponseCache");
            //设置缓存以及缓存磁盘大小
            mOkHttpClient.newBuilder().cache(new Cache(cacheDir,HTTP_RESPONSE_DISK_CACHE_MAX_SIZE));
        }
    }


}
