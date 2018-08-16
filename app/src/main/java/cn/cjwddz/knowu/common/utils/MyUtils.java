package cn.cjwddz.knowu.common.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.view.MyDialog;



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
     * 设置Dialog，可以选择图片或者拍照
     * */
    public static void showTypeDialog(final Activity activity){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()){
                    case R.id.select_photo:
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT,null);
                        intent.setType("image/*");
                        //dialog.dismiss();
                        activity.startActivityForResult(intent,1);
                        break;
                    case R.id.take_photo:
                        //创建一个file，用来存储拍照后的照片
                        File outputfile = new File(activity.getExternalCacheDir(),"knowUHeader.png");
                        try {
                            if (outputfile.exists()){
                                outputfile.delete();//删除
                            }
                            outputfile.createNewFile();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Uri imageuri ;
                        if (Build.VERSION.SDK_INT >= 24){
                            imageuri = FileProvider.getUriForFile(activity,
                                    "com.ljh.knowU.fileProvider", //可以是任意字符串
                                    outputfile);
                        }else{
                            imageuri = Uri.fromFile(outputfile);
                        }
                        //启动相机程序
                        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent1.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
                       // dialog.dismiss();
                        activity.startActivityForResult(intent1,2);
                        break;
                }
            }
        };
        final MyDialog dialog ;
        MyDialog.Builder builder = new MyDialog.Builder(activity);
        dialog = builder.setStyle(R.style.MyDialog)
                .setCancelTouchout(true)
                .setView(R.layout.dialog_select_photo)
                .setHeihgtdp(166)
                .setWidthdp(250)
                .addViewOnclickListener(R.id.select_photo,listener)
                .addViewOnclickListener(R.id.take_photo,listener)
                .build();

        dialog.show();
    }


    /**
     * 图片裁剪
     * @param uri
     * @param wh 裁剪图片宽高
     * @return
     */

    public static Intent CutForPhoto(Activity activity,Uri uri,int wh) {
        try {
            //直接裁剪
            Intent intent = new Intent("com.android.camera.action.CROP");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                //intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            //设置裁剪之后的图片路径文件
            File cutfile = new File(Environment.getExternalStorageDirectory().getPath(),"cutcamera.png"); //随便命名一个
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
                    "cutcamera.png"); //随便命名一个
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



}
