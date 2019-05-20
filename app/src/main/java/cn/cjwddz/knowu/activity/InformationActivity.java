package cn.cjwddz.knowu.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.upyun.library.common.UpConfig;
import com.upyun.library.common.UploadEngine;
import com.upyun.library.listener.UpCompleteListener;
import com.upyun.library.listener.UpProgressListener;
import com.upyun.library.utils.UpYunUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.adapters.MyAdapter;
import cn.cjwddz.knowu.common.application.AppManager;
import cn.cjwddz.knowu.common.http.MyHTTPClient;
import cn.cjwddz.knowu.common.utils.MyUtils;
import cn.cjwddz.knowu.common.utils.StringUtils;
import cn.cjwddz.knowu.interfaces.Get_D_callback;
import cn.cjwddz.knowu.interfaces.Get_M_callback;
import cn.cjwddz.knowu.interfaces.Get_signature_callback;
import cn.cjwddz.knowu.service.Constants;
import cn.cjwddz.knowu.view.MyDatePicker;
import cn.cjwddz.knowu.view.MyDialog;
import cn.cjwddz.knowu.view.MyImageView;
import cn.cjwddz.knowu.view.MyPicker;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static cn.cjwddz.knowu.activity.MainActivity.JSON;
import static cn.cjwddz.knowu.service.Constants.ADD_USER_INFO_URL;


public class InformationActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener,Get_signature_callback {
    private MyImageView myImageView;
    private ListView information_lv;
    private float listitemHeight;
    int statusBarHeight1 = -1;
    private  MyDialog dialog;
    private MyAdapter adapter;
    private View dialogView;
    private MyDatePicker dp;
    private MyPicker np;
    private int year=2000,month= 0,day=01;
    private int wh;
    private  String phoneNumber;
    private String filename;
    private List<Map<String,String>> list;
    private String[] array = {"昵称","出生日期","身高","体重","经期"};
    private String[] info=new String[]{"女有","1976-1-1","160cm","40kg","15号"};
    private boolean changeFlag = false;

    //内存储
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    private static String OPERATER = "cjwddz";
    private static String PASSWORD = "admin123";
    private static String BUCKET = "mall-picture";
    private static String METHOD = "POST";
    private static String PATH = File.separator+"userHeader"+File.separator;
    private String signature;
    private String policy;
    private static final long EXPIRATION = System.currentTimeMillis()/1000 + 1000 * 5 * 10; //过期时间，必须大于当前时间
    private Get_signature_callback get_s_callback;
    public void setGet_s_callback(Get_signature_callback get_s_callback){this.get_s_callback = get_s_callback;}


    //private static String path = "/sdcard/myHead/";  // sd路径
   // Uri uri;
    android.support.v7.app.ActionBar actionBar;

    private ImageButton ibtn_turnBack;
    private Button submitInfo;
    private View divider;

    private void initImageView(){
       // String path = sp.getString("imagePath",null);
        wh = myImageView.getWH();
        filename = MyUtils.hmacSha256("header",phoneNumber);
        File file = new File(Environment.getExternalStorageDirectory().getPath(),filename+".png");
        if(file.length()!=0){
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
                    filename = MyUtils.hmacSha256("header",phoneNumber);
                    startActivityForResult(MyUtils.CutForPhoto(this,filename,data.getData(),wh),3);
                    break;
                case 2:
                    //启动裁剪
                    //uri = data.getParcelableExtra("cutUri");
                    String path = getExternalCacheDir().getPath();
                    filename = MyUtils.hmacSha256("header",phoneNumber);
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{
                                Manifest.permission.CAMERA
                        },Constants.CAMERA_REQUEST_CODE);
                    }else{
                        startActivityForResult(MyUtils.CutForCamera(path,filename+".png",this,wh),3);
                    }
                    break;
                case 3:
                    try {
                        //editor.putString("imagePath",uri.toString());
                        //editor.commit();
                        postHeader();
                        filename = MyUtils.hmacSha256("header",phoneNumber);
                        File file = new File(Environment.getExternalStorageDirectory().getPath(),filename+".png");
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
    //结束回调
    private UpCompleteListener completeListener = new UpCompleteListener() {
        @Override
        public void onComplete(boolean b, String s) {
           // System.out.println(b+s);
        }
    };
    //进度条回调
    private UpProgressListener progressListener = new UpProgressListener() {
        @Override
        public void onRequestProgress(long l, long l1) {
            //System.out.println(l+l1);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        AppManager.getAppManager().addActivity(this);
        setGet_s_callback(this);

        submitInfo = (Button) findViewById(R.id.submitInfo);
        divider = findViewById(R.id.dividerV);
       // submitInfo = (Button) findViewById(R.id.submitInfo);

        //动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, Constants.PERMISSION_REQUEST_COARSE_LOCATION);
        }
        myImageView = (MyImageView) findViewById(R.id.myImageView);
        wh = myImageView.getDrawable().getBounds().width();
        myImageView.setOnClickListener(this);
        //获取内存储中的用户信息
        sp = getSharedPreferences("knowu",MODE_PRIVATE);
        editor = sp.edit();
        if(sp.getBoolean("firstStart",true)){
            editor.putBoolean("firstStart",false);
            editor.commit();
            //标题栏返回按键
            actionBar = getSupportActionBar();
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
            //google的actionbar是分为上下两栏显示的，上面的代码只能设置顶部actionbar的背景色，为了让下面的背景色一致，还需要添加一行代码：
            actionBar.setSplitBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        }else{
            submitInfo.setClickable(false);
            submitInfo.setVisibility(View.INVISIBLE);
            divider.setVisibility(View.INVISIBLE);
            //标题栏返回按键
            actionBar = getSupportActionBar();
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
            //google的actionbar是分为上下两栏显示的，上面的代码只能设置顶部actionbar的背景色，为了让下面的背景色一致，还需要添加一行代码：
            actionBar.setSplitBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
            ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
            View actionBarView = View.inflate(this,R.layout.actionbar_information,null);
            if(actionBar != null){
                actionBar.setCustomView(actionBarView,lp);
                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            }
            ibtn_turnBack = actionBarView.findViewById(R.id.turnBack);
            ibtn_turnBack.setOnClickListener(listener);
        }
        phoneNumber = sp.getString("phoneNumber",null);
        filename = MyUtils.hmacSha256("header",phoneNumber);
        info[0] = sp.getString("nickName", StringUtils.deleteStr(MyUtils.getNetMacAddress(),":"));
        info[1] = sp.getString("bDate","1960-1-1");
        info[2] = sp.getString("height","150cm");
        info[3] = sp.getString("weight","40kg");
        info[4] = sp.getString("mDate","10号");
        //隐藏状态栏
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        listitemHeight = (dm.heightPixels-statusBarHeight1-10)/50*4;
        information_lv = (ListView) findViewById(R.id.information_lv);
        list = new ArrayList<>();
        getUpdateList();
        adapter = new MyAdapter(this,list,R.layout.array_item);
        adapter.setHeight(listitemHeight);
        information_lv.setAdapter(adapter);
        information_lv.setOnItemClickListener(this);
        initImageView();
       // Toast.makeText(this,""+statusBarHeight1,Toast.LENGTH_LONG).show();
    }

    private void getUpdateList(){
        for(int i=0;i<array.length;i++){
            Map<String,String> item = new HashMap<>();
            item.put("name",array[i]);
            item.put("value",info[i]);
            list.add(item);
        }
        setMDate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{dialog.dismiss();}catch (NullPointerException e){}

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
        dp.setDatePickerDividerColor(dp);
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
        np.setNumberPickerDividerColor(np);
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
        np.setNumberPickerDividerColor(np);
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
        np.setNumberPickerDividerColor(np);
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
                    if(et.getText().toString().isEmpty()){
                        Toast.makeText(InformationActivity.this,"昵称不能为空",Toast.LENGTH_SHORT).show();
                    }else{
                        info[0]= String.valueOf(et.getText());
                        adapter.setMData(0,info[0]);
                        changeFlag = true;
                    }
                    dialog.dismiss();
                    break;
                case R.id.btn_bd_ensure:
                    info[1] =dp.getYear()+"-"+(dp.getMonth()+1)+"-"+dp.getDayOfMonth();
                    adapter.setMData(1,info[1]);
                    View view2 = information_lv.getChildAt(1);
                    TextView tv2 = view2.findViewById(R.id.tv_value);
                    tv2.setText(info[1]);
                    changeFlag = true;
                    dialog.dismiss();
                    break;
                case R.id.btn_h_ensure:
                    info[2] = String.valueOf(np.getValue())+"cm" ;
                    adapter.setMData(2,info[2]);
                    View view3 = information_lv.getChildAt(2);
                    TextView tv3 = view3.findViewById(R.id.tv_value);
                    tv3.setText(info[2]);
                    dialog.dismiss();
                    changeFlag = true;
                    break;
                case R.id.btn_w_ensure:
                    info[3] = String.valueOf(np.getValue())+"kg" ;
                    adapter.setMData(3,info[3]);
                    View view4 = information_lv.getChildAt(3);
                    TextView tv4 = view4.findViewById(R.id.tv_value);
                    tv4.setText(info[3]);
                    dialog.dismiss();
                    changeFlag = true;
                    break;
                case R.id.btn_md_ensure:
                    info[4] = String.valueOf(np.getValue())+"号" ;
                    adapter.setMData(4,info[4]);
                    View view5 = information_lv.getChildAt(4);
                    TextView tv5 = view5.findViewById(R.id.tv_value);
                    tv5.setText(info[4]);
                    dialog.dismiss();
                    changeFlag = true;
                    break;
                case R.id.turnBack:
                    if(changeFlag){
                        submitInfo();
                    }
                    AppManager.getAppManager().finishActivity();
                    break;
                case R.id.select_photo:
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT,null);
                    intent.setType("image/*");
                    //dialog.dismiss();
                    startActivityForResult(intent,1);
                    break;
                case R.id.take_photo:
                    //创建一个file，用来存储拍照后的照片
                    File outputfile = new File(InformationActivity.this.getExternalCacheDir(),filename+".png");
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
                        imageuri = FileProvider.getUriForFile(InformationActivity.this,
                                "com.ljh.knowU.fileProvider", //可以是任意字符串
                                outputfile);
                    }else{
                        imageuri = Uri.fromFile(outputfile);
                    }
                    //启动相机程序
                    Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent1.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
                    //动态申请权限
                    if (Build.VERSION.SDK_INT >= 23) {
                        int checkCallPhonePermission = ContextCompat.checkSelfPermission(InformationActivity.this, Manifest.permission.CAMERA);
                        if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(InformationActivity.this,new String[]{Manifest.permission.CAMERA},222);
                            return;
                        }else{
                            startActivityForResult(intent1,2);//调用具体方法
                        }
                    } else {
                        startActivityForResult(intent1,2);//调用具体方法
                    }

                    break;
                default:
                    break;
            }
        }
    };

    //提交用户填写的数据
    public void submitInformation(View view){
        submitInfo();
    }

    private void submitInfo(){
        editor.putString("nickName",info[0]);
        editor.putString("bDate",info[1]);
        editor.putString("height",info[2]);
        editor.putString("weight",info[3]);
        editor.putString("mDate",info[4]);
        editor.commit();
        postUserInformation(this,ADD_USER_INFO_URL,info);
        Intent intent = new Intent();
        intent.setClass(this,MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.myImageView:
                wh = myImageView.getWH();
                MyDialog.Builder builder = new MyDialog.Builder(this);
                dialog = builder.setStyle(R.style.MyDialog)
                        .setCancelTouchout(true)
                        .setView(R.layout.dialog_select_photo)
                        .setHeihgtdp(166)
                        .setWidthdp(250)
                        .addViewOnclickListener(R.id.select_photo,listener)
                        .addViewOnclickListener(R.id.take_photo,listener)
                        .build();
                dialog.show();
                break;
        }
    }
    /**
     * 使用post方式访问服务器
     * 上传用户信息
     * */
    public  void postUserInformation(final Context context, String url, String[] infos){
        OkHttpClient okHttpClient = MyHTTPClient.getInstance().getOkHttpClient();
        JSONObject info = new JSONObject();
        try {
            info.put("nickName",infos[0]);
            info.put("bDate",infos[1]);
            info.put("height",infos[2]);
            info.put("weight",infos[3]);
            info.put("mDate",infos[4]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody =  FormBody.create(JSON,info.toString());
        final Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"提交失败，服务异常！！！",Toast.LENGTH_SHORT).show();
                    }
                });
                //System.out.println("提交用户信息shibai");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context,"提交成功",Toast.LENGTH_SHORT).show();
                        }
                    });
                    //System.out.println("提交用户信息success"+response.body()+"&"+response.message()+"&"+response.code()+"&"+response.request());
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context,"提交失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                    //System.out.println("提交用户信息shibai3");
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            //就像onActivityResult一样这个地方就是判断你是从哪来的。
            case 222:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //创建一个file，用来存储拍照后的照片
                    File outputfile = new File(InformationActivity.this.getExternalCacheDir(),filename+".png");
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
                        imageuri = FileProvider.getUriForFile(InformationActivity.this,
                                "com.ljh.knowU.fileProvider", //可以是任意字符串
                                outputfile);
                    }else{
                        imageuri = Uri.fromFile(outputfile);
                    }
                    //启动相机程序
                    Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent1.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
                    startActivityForResult(intent1,2);
                } else {
                    // Permission Denied
                    Toast.makeText(InformationActivity.this, "很遗憾你把相机权限禁用了。请务必开启相机权限享受我们提供的服务吧。", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 使用get方式访问服务器
     * 获取签名
     *
     * @param url
     * @param map 请求参数（"bucket": "upyun-temp", "save-key": "/demo.jpg", "expiration": "1478674618",
     *            "date": "Wed, 09 Nov 2016 14:26:58 GMT", "content-md5": "7ac66c0f148de9519b8bd264312c4d64"）
     * */
    public  void getSignature(String url,Map<String, Object> map){
        StringBuffer sb = new StringBuffer();
        String string = "";
        String result = "";
        //当用户传入null或者传了一个空的map
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (sb.length()==0) {
//                    sb = new StringBuffer ();
                    sb.append("?");
                } else {
                    //拼接好的网站去掉最后一个“&”符号
                    sb.append("&");
                }
                sb.append(entry.getKey() + "=" + entry.getValue());
            }
        }
        if (sb.toString() != null) {
            string = sb.toString();
            result = url + string;
        }
        OkHttpClient okHttpClient = MyHTTPClient.getInstance().getOkHttpClient();
        final Request request = new Request.Builder()
                .url(string == "" ? url : result)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                get_s_callback.failureGetS(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    get_s_callback.successedGetS(call,response);
                }else{
                    get_s_callback.failedGetS(call,response);
                }
            }
        });
    }

    @Override
    public void successedGetS(Call call, Response response) throws IOException {
        Boolean status = null;
        JSONObject datas;
        try {
            JSONObject jsonObject = new JSONObject(response.body().string());
            JSONObject data = jsonObject.getJSONObject("data");
            status = data.getBoolean("status");
            if(status){
                datas = data.getJSONObject("data");
                String authorization = datas.getString("Authorization");
                int index = authorization.indexOf(":");
                index = index + 1;
                System.out.println(authorization);
                signature = authorization.substring(index).trim();
                File file = new File(Environment.getExternalStorageDirectory().getPath(),filename+".png");
                Map<String,Object> params = new HashMap<>();
                params.put("save-key","/String/{random32}{.suffix}");
                params.put("expiration",EXPIRATION);
                params.put("bucket",BUCKET);
                policy = UpYunUtils.getPolicy(params);
                //System.out.println(policy);
                //System.out.println(signature);
                //System.out.println(OPERATER);
                UploadEngine.getInstance().formUpload(file,policy,OPERATER, signature, completeListener, progressListener);
            }else{

            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void failureGetS(IOException e) {
        //System.out.println("服务器异常");
    }

    @Override
    public void failedGetS(Call call, Response response) throws IOException {
        //System.out.println("提交用户信息shibai3");
        //System.out.println(response.body()+"&"+response.message()+"&"+response.code()+"&"+response.request());
    }

    public void postHeader(){
        File file = new File(Environment.getExternalStorageDirectory().getPath(),filename+".png");
        Date date = new Date(System.currentTimeMillis());
        Map<String,Object> params = new HashMap<>();
        params.put("save-key","/userHeader/{filename}{.suffix}");
        params.put("expiration",EXPIRATION);


        params.put("bucket",BUCKET);
        //params.put("date",MyUtils.getTimeToGMT(date));
        //System.out.println(MyUtils.getTimeToGMT(date));
        params.put("content-md5",UpYunUtils.md5Hex(file));
        //policy = UpYunUtils.getPolicy(params);

        UploadEngine.getInstance().formUpload(file,params,OPERATER, UpYunUtils.md5(PASSWORD), completeListener, progressListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(changeFlag){
            submitInfo();
        }
    }
}
