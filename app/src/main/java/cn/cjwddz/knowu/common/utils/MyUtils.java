package cn.cjwddz.knowu.common.utils;

import android.app.Activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;

import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.common.application.AppContext;
import cn.cjwddz.knowu.common.http.MyHTTPClient;

import cn.cjwddz.knowu.service.Constants;
import cn.cjwddz.knowu.view.MyDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static cn.cjwddz.knowu.common.utils.StringUtils.isEmpty;


/**
 * Created by ljh on 2018/8/7.
 */

public class MyUtils {
    /**
     * 根据手机分辨率从 px(像素) 单位 转成 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机分辨率从 dp 单位 转成 px(像素)
     */
    public static int dip2px( Context context,float dpValue) {

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }



    /**
     * 图片裁剪
     * @param uri
     * @param wh 裁剪图片宽高
     * @return
     */

    public static Intent CutForPhoto(Activity activity,String filename,Uri uri,int wh) {
        try {
            //直接裁剪
            Intent intent = new Intent("com.android.camera.action.CROP");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                //intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            //设置裁剪之后的图片路径文件
            File cutfile = new File(Environment.getExternalStorageDirectory().getPath(),filename+".png"); //随便命名一个
           // File cutfile = new File(getResourcesUri(activity,R.drawable.important),"important.png"); //随便命名一个
            if (cutfile.exists()){ //如果已经存在，则先删除,这里应该是上传到服务器，然后再删除本地的，没服务器，只能这样了
                cutfile.delete();
            }
            cutfile.createNewFile();
            //初始化 uri
            Uri imageUri = uri; //返回来的 uri
            Uri outputUri = null; //真实的 uri
            outputUri = Uri.fromFile(cutfile);
           // Uri cutUri = outputUri;
            // crop为true是设置在开启的intent中设置显示的view可以剪裁
            intent.putExtra("crop",true);
            // aspectX,aspectY 是宽高的比例，这里设置正方形
            intent.putExtra("aspectX",1);
            intent.putExtra("aspectY",1);
            //设置要裁剪的宽高(若图片过小会被填充，实际大小会与剪切一致，但显示效果会发现没有缩放)
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("scale",true);
            //如果图片过大，会导致oom，这里设置为false
            intent.putExtra("return-data",false);
            if (imageUri != null) {
                intent.setDataAndType(imageUri, "image/*");
            }
            if (outputUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            }
            intent.putExtra("noFaceDetection", true);
            //intent.putExtra("cutUri",cutUri);
            //压缩图片
            intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
            return intent;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 拍照之后，启动裁剪
     * @param camerapath 路径
     * @param imgname img 的名字
     * @param activity
     * @param wh 裁剪图片宽高
     * @return
     */
    public static Intent CutForCamera(String camerapath,String imgname,Activity activity,int wh) {
        try {

            //设置裁剪之后的图片路径文件
            File cutfile = new File(Environment.getExternalStorageDirectory().getPath(),
                    imgname); //随便命名一个
            if (cutfile.exists()){ //如果已经存在，则先删除,这里应该是上传到服务器，然后再删除本地的，没服务器，只能这样了
                cutfile.delete();
            }
            cutfile.createNewFile();
            //初始化 uri
            Uri imageUri = null; //返回来的 uri
            Uri outputUri = null; //真实的 uri
            Intent intent = new Intent("com.android.camera.action.CROP");
            //拍照留下的图片
            File camerafile = new File(camerapath,imgname);
            if (Build.VERSION.SDK_INT >= 24) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                imageUri = FileProvider.getUriForFile(activity,
                        "com.ljh.knowU.fileProvider",
                        camerafile);
            } else {
                imageUri = Uri.fromFile(camerafile);
            }
            outputUri = Uri.fromFile(cutfile);
            //把这个 uri 提供出去，就可以解析成 bitmap了
           // Uri cutUri = outputUri;
            // crop为true是设置在开启的intent中设置显示的view可以剪裁
            intent.putExtra("crop",true);
            // aspectX,aspectY 是宽高的比例，这里设置正方形
            intent.putExtra("aspectX",1);
            intent.putExtra("aspectY",1);
            //设置要裁剪的宽高
            intent.putExtra("outputX", wh);
            intent.putExtra("outputY",wh);
            intent.putExtra("scale",true);
            //如果图片过大，会导致oom，这里设置为false
            intent.putExtra("return-data",false);
            if (imageUri != null) {
                intent.setDataAndType(imageUri, "image/*");
            }
            if (outputUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            }
            //intent.putExtra("cutUri",cutUri);
            intent.putExtra("noFaceDetection", true);
            //压缩图片
            intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
            return intent;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存图片并显示出来
     * @param data 图片信息
     * @param imageView 显示控件
     * */
    public static void showImageToView(Intent data, ImageView imageView,String path){
        Bitmap bitmap = data.getParcelableExtra("data");
            if(bitmap != null){
                imageView.setImageBitmap(bitmap);
                //savePhoto(bitmap,path);
            }
    }

    /**
     * 保存裁剪的图片
     * @param bitmap
     * @param path 图片保存路径
     * */
    public static void savePhoto(Bitmap bitmap,String path){
        String sdStatus = Environment.getExternalStorageState();
        if(!sdStatus.equals(Environment.MEDIA_MOUNTED))
        {
            return;
        }
        FileOutputStream b = null;
        File file = new File(path);
        file.mkdirs();//创建文件夹
        String fileName = path+"head.jpg";
        try{
            b = new FileOutputStream(fileName);
            //把数据写入文件
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,b);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }finally {
            try {
                b.flush();
                b.close();
            }catch (IOException ie){
                ie.printStackTrace();
            }
        }
    }

    /**
    * 将Bitmap进行缩放
    * */
    public static Bitmap zoomBitmap(Bitmap bitmap,int width,int height){
        float scaleX ;
        float scaleY ;
        Matrix matrix = new Matrix();
        int mWidth = bitmap.getWidth();
        int mHeight = bitmap.getHeight();
        //int wh = Math.min(width,height);
        if(width<=0||height<=0){
            scaleX = 2.0f;
            scaleY = 2.0f;
        }else{
            scaleX = (float) width/mWidth;
            scaleY = (float) height/mHeight;
        }
        matrix.setScale(scaleX,scaleY);
        Bitmap result = Bitmap.createBitmap(bitmap,0,0,mWidth,mHeight,matrix,true);
        //Bitmap result = Bitmap.createScaledBitmap(bitmap,width,height,true);
        return result;
    }

    /**
     * 将Drawable转化为Bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap(Drawable drawable){
        if(drawable instanceof BitmapDrawable){
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            return bitmapDrawable.getBitmap();
        }
        //获取图片固有的宽度和高度（单位为dp）
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        //创建画布
        Bitmap bitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0,0,w,h);
        drawable.draw(canvas);
        return bitmap;
    }
/**
 * 通过资源id获取文件夹路径
 * */

    public static String getResourcesUri(Activity activity,@DrawableRes int id) {
        Resources resources = activity.getResources();
        String uriPath = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(id) + "/" +
                resources.getResourceTypeName(id) + "/" +
                resources.getResourceEntryName(id);
        return uriPath;
    }


    /**
     * 判断网络是否可用
     * */
    public static boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) AppContext.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 使用post方式访问服务器
     * 上传崩溃日志
     * */
    public static void postSystemLog(String url,String logs){
        OkHttpClient okHttpClient = MyHTTPClient.getInstance().getOkHttpClient();
        JSONObject info = new JSONObject();
        try {
            info.put("log",logs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // SystemLogBody body = new SystemLogBody();
        Map<String,Object> param = new HashMap<>();
        param.put("type","addSystemLog");
        param.put("user","K.B.");
        param.put("msg","SystemLog");
        param.put("info",info);
        param.put("call_stack","/a/b/c.go");
        param.put("platform","APP");
       // body.setType("addSystemLog");
       // body.setUser("K.B.");
       // body.setMsg("SystemLog");
       // body.setInfo(info);
       // body.setCall_stack("/a/b/c.go");
       // body.setPlatform("APP");
       // Gson gson = new Gson();
       // String jsonString = gson.toJson(body);
        RequestBody requestBody =  FormBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"),param.toString());
        /**RequestBody requestBody = new FormBody.Builder()
                .add("type","addSystemLog")
                .add("user","K.B.")
                .add("msg","SystemLog")
                .add("info",logs)
                .add("call_stack","/a/b/c.go")
                .add("platform","APP")
                .build();*/

        Request request = new Request.Builder()
                .url(url)
                .header(Constants.HEADER_CONTENT_TYPE_KEY,Constants.HEADER_CONTENT_TYPE_VAULE)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if(jsonObject.get("status").equals("ok")){
                            //连接成功
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }



    /**
     * 获取系统时间戳
     * */
    public static long getLongTime(){
        long time=System.currentTimeMillis();//获取系统时间的10位的时间戳（单位毫秒）
        return time;
    }

    public static String getStringTime(){
        long time=System.currentTimeMillis();//获取系统时间的10位的时间戳（单位毫秒）
        String  str=String.valueOf(time);
        return str;
    }

    /**
     * 日期格式字符串转换成时间戳
     * @param date_str 字符串日期
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static long date2TimeStamp(String date_str,String format){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return Long.parseLong(String.valueOf(sdf.parse(date_str).getTime()/1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String second2min(int second){
        int min = (second%3600)/60;
        if(min<10){
            return new String("0"+min);
        }else{
            return String.valueOf(min);
        }
    }

    public static String second2hour(int second){
        int min = second/3600;
        return String.valueOf(min);
    }

    public static String date2String(String date,int len){
        date = date.replace("Z", " UTC");
        System.out.println(date);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        Date d = null;
        try {
            d = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        String min_start = calendar.get(Calendar.MINUTE)>=10?String.valueOf(calendar.get(Calendar.MINUTE)):"0"+calendar.get(Calendar.MINUTE);
       String start = calendar.get(Calendar.HOUR_OF_DAY)+":"+min_start;
        int hour = calendar.get(Calendar.HOUR_OF_DAY)+len/3600;
        int min = calendar.get(Calendar.MINUTE)+(len%3600)/60;
        if(min>=60){
            min = min%60;
            hour = hour + min/60;
        }
        if(hour>=24){
            hour = hour%24;
        }
        String end = null;
        if(min<10){
            end = hour+":"+"0"+min;
            return new String(start+"-"+end);
        }
        if(hour<10){
            end = "0"+hour+":"+min;
            return new String(start+"-"+end);
        }
        if (hour<10&&min<10){
            end = "0"+hour+":"+"0"+min;
            return new String(start+"-"+end);
        }
        end = +hour+":"+min;
        return new String(start+"-"+end);
    }

    @NonNull
    public static String md5(String string) {
        if (isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String hmacSha1(String KEY, String VALUE) {
        return hmacSha(KEY, VALUE, "HmacSHA1");
    }

    public static String hmacSha256(String KEY, String VALUE) {
        return hmacSha(KEY, VALUE, "HmacSHA256");
    }

    private static String hmacSha(String KEY, String VALUE, String SHA_TYPE) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(KEY.getBytes("UTF-8"), SHA_TYPE);
            Mac mac = Mac.getInstance(SHA_TYPE);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(VALUE.getBytes("UTF-8"));

            byte[] hexArray = {
                    (byte)'0', (byte)'1', (byte)'2', (byte)'3',
                    (byte)'4', (byte)'5', (byte)'6', (byte)'7',
                    (byte)'8', (byte)'9', (byte)'a', (byte)'b',
                    (byte)'c', (byte)'d', (byte)'e', (byte)'f'
            };
            byte[] hexChars = new byte[rawHmac.length * 2];
            for ( int j = 0; j < rawHmac.length; j++ ) {
                int v = rawHmac[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 将日期转换成GMT日期 方法
     * @param d
     * @return
     */
    public static String getTimeToGMT(Date d){
        SimpleDateFormat sdf  =  new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        DateFormat gmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        gmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        String gmtStr=gmt.format(d);//日期 转成 字符串
        Date gmt_date =null;
        try {
            gmt_date = sdf.parse(gmtStr); //字符串 转成 日期
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return String.valueOf(gmt_date);
    }

    /**
     * 字节数组拆分
     *
     * @param paramArrayOfByte 原始数组
     * @param paramInt1        起始下标
     * @param paramInt2        要截取的长度
     * @return 处理后的数组
     */
    public static byte[] SubArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        byte[] arrayOfByte = new byte[paramInt2];
        int i = 0;
        while (true) {
            if (i >= paramInt2)
                return arrayOfByte;
            arrayOfByte[i] = paramArrayOfByte[(i + paramInt1)];
            i += 1;
        }
    }

    /**
     * byte数组装String
     * */
    public static String byte2hex(byte [] buffer){
        String h = "";

        for(int i = 0; i < buffer.length; i++){
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if(temp.length() == 1){
                temp = "0" + temp;
            }
            h = h + " "+ temp;
        }

        return h;

    }

}
