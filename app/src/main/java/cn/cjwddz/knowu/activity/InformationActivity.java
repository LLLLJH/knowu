package cn.cjwddz.knowu.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.adapters.MyAdapter;
import cn.cjwddz.knowu.common.utils.MyUtils;
import cn.cjwddz.knowu.service.Constants;
import cn.cjwddz.knowu.view.MyDialog;
import cn.cjwddz.knowu.view.MyImageView;

public class InformationActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private MyImageView myImageView;
    private ListView information_lv;
    private float listitemHeight;
    int statusBarHeight1 = -1;
    private  MyDialog dialog;
    private MyAdapter adapter;
    private View dialogView;
    private  DatePicker dp;
    private NumberPicker np;
    private int year=2000,month= 0,day=01;
    private int wh;
    private String nickName,bDate,height,weight,mDate;
    private String[] array = {"昵称","出生日期","身高","体重","经期"};
    private String[] info=new String[]{"lll","1976-1-1","150cm","40kg","15号"};

    //内存储
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    //private static String path = "/sdcard/myHead/";  // sd路径
   // Uri uri;

    private void initImageView(){
       // String path = sp.getString("imagePath",null);
        wh = myImageView.getWH();
        File file = new File(Environment.getExternalStorageDirectory().getPath(),"cutcamera.png");
        Uri uri0 = Uri.fromFile(file);
        try {
            //获取裁剪后的图片，并显示出来
            Bitmap bitmap = BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(uri0));
            //图片还未回收，先强制回收该图片
            BitmapDrawable bitmapDrawable = (BitmapDrawable) myImageView.getDrawable();
            if(bitmapDrawable != null && !bitmapDrawable.getBitmap().isRecycled()){
                bitmapDrawable.getBitmap().recycle();
            }
            //Drawable drawable = new BitmapDrawable(bitmap);
           // myImageView.setImageDrawable(drawable);
            myImageView.setImageBitmap(bitmap);
            //myImageView.setImageResource(R.drawable.important);
            //myImageView.startInvalidate();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch(requestCode){
                case 1:
                    //uri = data.getData();
                     //uri = data.getParcelableExtra("cutUri");
                    startActivityForResult(MyUtils.CutForPhoto(this,data.getData(),wh),3);
                    break;
                case 2:
                    //启动裁剪
                    //uri = data.getParcelableExtra("cutUri");
                    String path = getExternalCacheDir().getPath();
                    String name = "knowUHeader.png";
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{
                                Manifest.permission.CAMERA
                        },Constants.CAMERA_REQUEST_CODE);
                    }else{
                        startActivityForResult(MyUtils.CutForCamera(path,name,this,wh),3);
                    }
                    break;
                case 3:
                    try {
                        //editor.putString("imagePath",uri.toString());
                        //editor.commit();
                        File file = new File(Environment.getExternalStorageDirectory().getPath(),"cutcamera.png");
                        Uri uri = Uri.fromFile(file);
                        //获取裁剪后的图片，并显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(uri));

                        //图片还未回收，先强制回收该图片
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) myImageView.getDrawable();
                        if(bitmapDrawable != null && !bitmapDrawable.getBitmap().isRecycled()){
                            bitmapDrawable.getBitmap().recycle();
                        }
                        //myImageView.setImageResource(R.drawable.important);
                        //Drawable drawable = new BitmapDrawable(MyUtils.zoomBitmap(bitmap,wh,wh));
                        //myImageView.setImageDrawable(drawable);
                        myImageView.setImageBitmap(MyUtils.zoomBitmap(bitmap,wh,wh));
                        //myImageView.startInvalidate();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        //动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                    Manifest.permission.CAMERA
            }, Constants.PERMISSION_REQUEST_COARSE_LOCATION);
        }
        myImageView = (MyImageView) findViewById(R.id.myImageView);
        wh = myImageView.getDrawable().getBounds().width();
        myImageView.setOnClickListener(this);
        //获取内存储中的用户信息
        sp = getSharedPreferences("knowu",MODE_PRIVATE);
        editor = sp.edit();
        info[0] = sp.getString("nickName","lll");
        info[1] = sp.getString("bDate","1976-1-1");
        info[2] = sp.getString("height","150cm");
        info[3] = sp.getString("weight","40kg");
        info[4] = sp.getString("mDate","10号");
        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        listitemHeight = (dm.heightPixels-statusBarHeight1)/50*4;
        information_lv = (ListView) findViewById(R.id.information_lv);
        List<Map<String,String>> list = new ArrayList<Map<String,String>>();
        for(int i=0;i<array.length;i++){
            Map<String,String> item = new HashMap<String,String>();
            item.put("name",array[i]);
            item.put("value",info[i]);
            list.add(item);
        }
        adapter = new MyAdapter(this,list,R.layout.array_item,new String[]{"name","value"},new int[]{R.id.tv_information,R.id.tv_value});
        adapter.setHeight(listitemHeight);
        information_lv.setAdapter(adapter);
        information_lv.setOnItemClickListener(this);
        initImageView();
       // Toast.makeText(this,""+statusBarHeight1,Toast.LENGTH_LONG).show();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i){
            case 0:
                setNickName();
                break;
            case 1:
                setBDate();
                break;
            case 2:
               setHeight();
                break;
            case 3:
                setWeight();
                break;
            case 4:
                setMDate();
                break;
            default:
                Toast.makeText(this,"。。。",Toast.LENGTH_SHORT).show();
                break;
        }
    }

       //设置昵称
    private void setNickName(){
        MyDialog.Builder builder = new MyDialog.Builder(this);
        dialog = builder.setStyle(R.style.MyDialog)
                .setCancelTouchout(false)
                .setView(R.layout.dialog)
                .setWidthpx(getWindowManager().getDefaultDisplay().getWidth())
                .setHeihgtdp(166)
                .addViewOnclickListener(R.id.btn_cancel,listener)
                .addViewOnclickListener(R.id.btn_nn_ensure,listener)
                .build();
        dialog.show();
        dialogView = dialog.getView();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
    }

    //设置生日
    private void setBDate(){
        MyDialog.Builder builder = new MyDialog.Builder(this);
        dialog = builder.setStyle(R.style.MyDialog)
                .setCancelTouchout(false)
                .setView(R.layout.bdatedialog)
                .setWidthpx(getWindowManager().getDefaultDisplay().getWidth())
                .setHeihgtdp(290)
                .addViewOnclickListener(R.id.btn_cancel,listener)
                .addViewOnclickListener(R.id.btn_bd_ensure,listener)
                .build();
        dialog.show();
        dialogView = dialog.getView();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        dp = dialogView.findViewById(R.id.dp_bDate);
        dp.init(year,month,day,null);
    }

    //设置身高
    private void setHeight(){
        MyDialog.Builder builder = new MyDialog.Builder(this);
        dialog = builder.setStyle(R.style.MyDialog)
                .setCancelTouchout(false)
                .setView(R.layout.heightdialog)
                .setWidthpx(getWindowManager().getDefaultDisplay().getWidth())
                .setHeihgtdp(290)
                .addViewOnclickListener(R.id.btn_cancel,listener)
                .addViewOnclickListener(R.id.btn_h_ensure,listener)
                .build();
        dialog.show();
        dialogView = dialog.getView();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        np = dialogView.findViewById(R.id.np_height);
        np.setMinValue(130);
        np.setMaxValue(200);
        np.setValue(150);
    }

    //设置体重
    private void setWeight(){
        MyDialog.Builder builder = new MyDialog.Builder(this);
        dialog = builder.setStyle(R.style.MyDialog)
                .setCancelTouchout(false)
                .setView(R.layout.weightdialog)
                .setWidthpx(getWindowManager().getDefaultDisplay().getWidth())
                .setHeihgtdp(290)
                .addViewOnclickListener(R.id.btn_cancel,listener)
                .addViewOnclickListener(R.id.btn_w_ensure,listener)
                .build();
        dialog.show();
        dialogView = dialog.getView();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        np = dialogView.findViewById(R.id.np_weight);
        np.setMinValue(30);
        np.setMaxValue(200);
        np.setValue(40);
    }

    //设置经期
    private void setMDate(){
        MyDialog.Builder builder = new MyDialog.Builder(this);
        dialog = builder.setStyle(R.style.MyDialog)
                .setCancelTouchout(false)
                .setView(R.layout.mdatedialog)
                .setWidthpx(getWindowManager().getDefaultDisplay().getWidth())
                .setHeihgtdp(290)
                .addViewOnclickListener(R.id.btn_cancel,listener)
                .addViewOnclickListener(R.id.btn_md_ensure,listener)
                .build();
        dialog.show();
        dialogView = dialog.getView();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        np = dialogView.findViewById(R.id.np_mDate);
        np.setMinValue(1);
        np.setMaxValue(31);
        np.setValue(15);
    }


    View.OnClickListener listener = new View.OnClickListener() {
        @SuppressLint("NewApi")
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_cancel:
                    dialog.dismiss();
                    break;
                case R.id.btn_nn_ensure:
                    EditText et = dialogView.findViewById(R.id.et_nickname);
                    info[0]=et.getText().toString();
                    View view1 = information_lv.getChildAt(0);
                    TextView tv1 = view1.findViewById(R.id.tv_value);
                    tv1.setText(info[0]);
                    dialog.dismiss();
                    break;
                case R.id.btn_bd_ensure:
                    info[1] =dp.getYear()+"-"+(dp.getMonth()+1)+"-"+dp.getDayOfMonth();
                    View view2 = information_lv.getChildAt(1);
                    TextView tv2 = view2.findViewById(R.id.tv_value);
                    tv2.setText(info[1]);
                    dialog.dismiss();
                    break;
                case R.id.btn_h_ensure:
                    info[2] = String.valueOf(np.getValue())+"cm" ;
                    View view3 = information_lv.getChildAt(2);
                    TextView tv3 = view3.findViewById(R.id.tv_value);
                    tv3.setText(info[2]);
                    dialog.dismiss();
                    break;
                case R.id.btn_w_ensure:
                    info[3] = String.valueOf(np.getValue())+"kg" ;
                    View view4 = information_lv.getChildAt(3);
                    TextView tv4 = view4.findViewById(R.id.tv_value);
                    tv4.setText(info[3]);
                    dialog.dismiss();
                    break;
                case R.id.btn_md_ensure:
                    info[4] = String.valueOf(np.getValue())+"号" ;
                    View view5 = information_lv.getChildAt(4);
                    TextView tv5 = view5.findViewById(R.id.tv_value);
                    tv5.setText(info[4]);
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };
    //提交用户填写的数据
    public void submitInformation(View view){
        editor.putString("nickName",info[0]);
        editor.putString("bDate",info[1]);
        editor.putString("height",info[2]);
        editor.putString("weight",info[3]);
        editor.putString("mDate",info[4]);
        editor.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.myImageView:
                wh = myImageView.getWH();
                MyUtils.showTypeDialog(this);
                break;
        }
    }

}
