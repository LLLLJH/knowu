package cn.cjwddz.knowu.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TabHost;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.cjwddz.knowu.MouthTabFragment;
import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.DayTabFragment;
import cn.cjwddz.knowu.adapters.DataAdapter;
import cn.cjwddz.knowu.adapters.DateAdapter;
import cn.cjwddz.knowu.common.application.AppManager;
import cn.cjwddz.knowu.common.http.MyHTTPClient;
import cn.cjwddz.knowu.common.utils.MyUtils;
import cn.cjwddz.knowu.interfaces.DeepStatusListenerManager;
import cn.cjwddz.knowu.interfaces.Get_D_callback;
import cn.cjwddz.knowu.interfaces.Get_M_callback;
import cn.cjwddz.knowu.interfaces.SetDateListenerManager;
import cn.cjwddz.knowu.interfaces.SetDateListenerManager_m;
import cn.cjwddz.knowu.service.Constants;
import cn.cjwddz.knowu.view.GradientGraphView;
import cn.cjwddz.knowu.view.MyDatePicker;
import cn.cjwddz.knowu.view.MyDialog;
import cn.cjwddz.knowu.view.TabView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static cn.cjwddz.knowu.R.id.datePicker_Y;
import static cn.cjwddz.knowu.R.id.rv_data;
import static cn.cjwddz.knowu.activity.MainActivity.JSON;

public class RecordActivity extends AppCompatActivity{

    private static String FEEL_NOTHING = "nothing";
    private static String FEEL_LITLE = "little";
    private static String FEEL_GOOD = "good";
    private static String FEEL_BEST = "best";
    private static String FORMAT = "yyyy-MM-dd";
    private List<String> mdata = new ArrayList<>();
    private List<String> mdate = new ArrayList<>();
    private RecyclerView recyclerView_Data;
    private RecyclerView recyclerView_Date;
    private TextView tv_sum_hour;
    private TextView tv_sum_min;
    private TextView tv_fighting_hour;
    private TextView tv_fighting_min;
    private TextView tv_userTime;
    private TextView tv_fighting_one_min;
    private TextView tv_fighting_max_intensity;
    private TextView tv_fighting_average_intensity;
    private TextView tv_percent_time;
    private TextView tv_percent_value;
    private String tv_sum_hour_value = "0";
    private String tv_sum_min_value = "00";
    private String tv_fighting_hour_value = "0";
    private String tv_fighting_min_value = "00";
    private String tv_userTime_value = "00:00-00:00";
    private String tv_fighting_one_min_value = "00";
    private String tv_fighting_max_intensity_value = "0";
    private String tv_fighting_average_intensity_value = "0";
    private String tv_percent_time_value = "0";
    private String tv_percent_value_value = "0";
    private GradientGraphView gradientGraphView_time;
    private GradientGraphView gradientGraphView_intensity;
    private Button btn_user_feel;
    private DataAdapter.ViewHolder lastHolder;
    private int lastposition;


    private int statusBarHeight1 = -1;
    private int params_height;
    private int params_width;

    SharedPreferences sp;

    private TextView tv_date_year_month;
    private ImageButton iv_choose_date;
    private ImageButton ibtn_turnBack;

    private  MyDialog dialog;
    //private MyAdapter adapter;
    private View dialogView;
    private MyDatePicker dp;
    private NumberPicker datePicker_Y;
    private NumberPicker datePicker_M;
    private int year;
    private int month;
    private int position;

    private String phoneNumber;

    private  DataAdapter madapter ;
    private DateAdapter adapter1;

    android.support.v7.app.ActionBar actionBar;

    private FragmentTabHost tabHost;

    private Get_M_callback get_m_callback;
    private Get_D_callback get_d_callback;

    private  SetDateListenerManager dateListenerManager;
    private SetDateListenerManager_m dateListenerManager_m;

    public RecordActivity() {
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_record);

        Calendar cd = Calendar.getInstance();
        year = cd.get(Calendar.YEAR);
        month = cd.get(Calendar.MONTH)+1;
        //获取内存储中的用户信息
        sp = getSharedPreferences("knowu",MODE_PRIVATE);
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        params_height = (dm.heightPixels-statusBarHeight1)/12;
        params_width = dm.widthPixels/2;
        //获取该Activity里面的TabHost组件
        tabHost = (FragmentTabHost) findViewById(R.id.tabhost);
        tabHost.setup(this,getSupportFragmentManager(), R.id.realtabcontent);
        //使用Intent添加Tab页
        TabView view = null;

        dateListenerManager = SetDateListenerManager.getInstance();
        dateListenerManager_m = SetDateListenerManager_m.getInstance();

        view = new TabView(this,R.drawable.day_background, R.drawable.day_background_selected,null);
       // view.setBackground(this.getResources().getDrawable(R.drawable.tabbackground_selector,null));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(params_width,params_height,Gravity.CENTER);
        view.setLayoutParams(params);
        view.setDividerPadding(5);
        TabHost.TabSpec dSpec=tabHost.newTabSpec("day");
        dSpec.setIndicator(view);
        view = new TabView(this,R.drawable.mouth_background, R.drawable.mouth_background_selected,null);
       // view.setBackground(this.getResources().getDrawable(R.drawable.tabbackground_selector,null));
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(params_width,params_height,Gravity.CENTER);
        view.setLayoutParams(params1);
        view.setDividerPadding(5);
        TabHost.TabSpec mhSpec=tabHost.newTabSpec("month");
        mhSpec.setIndicator(view);
        tabHost.addTab(dSpec, DayTabFragment.class,null);
        tabHost.addTab(mhSpec,MouthTabFragment.class,null);
        tabHost.setCurrentTab(0);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                String date = null;
                switch(tabId){
                    case "day":
                        date = getStringDateShortOfM();
                        break;
                    case "month":
                        date = getStringDateShortY();
                        break;
                    default:break;
                }
                tv_date_year_month.setText(date);

            }
        });
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    void initView() {
        //标题栏返回按键
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        //google的actionbar是分为上下两栏显示的，上面的代码只能设置顶部actionbar的背景色，为了让下面的背景色一致，还需要添加一行代码：
        actionBar.setSplitBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View actionBarView = View.inflate(this,R.layout.text,null);
        tv_date_year_month = actionBarView.findViewById(R.id.tv_date_year_month);
        tv_date_year_month.setText(getStringDateShortOfM());
        iv_choose_date = actionBarView.findViewById(R.id.iv_choose_date);
        ibtn_turnBack = actionBarView.findViewById(R.id.turnBack);
        ibtn_turnBack.setOnClickListener(listener);
        iv_choose_date.setOnClickListener(listener);
        if(actionBar != null){
            actionBar.setCustomView(actionBarView,lp);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        }
    }



    View.OnClickListener listener = new View.OnClickListener() {
        @SuppressLint("NewApi")
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                //弹出日期选择对话框
                case R.id.iv_choose_date:
                    if(tabHost.getCurrentTabTag().equals("day")){
                        setDateOfDay();
                    }else{
                        setDateOfMonth();
                    }
                    
                    break;
                case R.id.btn_cancel:
                    dialog.dismiss();
                    break;
                //选择查询日期
                case R.id.btn_chooseDate_ensure:
                    String date_d;
                    if(dp.getMonth()<10){
                         date_d=dp.getYear()+"年"+"0"+(dp.getMonth()+1)+"月";
                    }else{
                         date_d =dp.getYear()+"年"+(dp.getMonth()+1)+"月";
                    }
                    position = dp.getDayOfMonth()-1;
                   // String date =dp.getYear()+"-"+(dp.getMonth()+1);
                    Map<String,Object> param = new HashMap<>();
                    //param.put("userId",phoneNumber);
                    param.put("reportType","date");
                    param.put("all",false);
                    param.put("time",date_d.toString());
                    //网络请求
                    dateListenerManager.getDateDay(date_d,param,position);
                    //getUserRecord(Constants.GET_M,param);
                    //getUserRecordOfDay(Constants.GET_D);
                    dialog.dismiss();
                    tv_date_year_month.setText(date_d);
                    break;
                //选择查询日期
                case R.id.btn_chooseDate_ensure_m:
                    String date_m =datePicker_Y.getValue()+"年";
                    month = datePicker_M.getValue();
                    System.out.print(month);
                    // String date =dp.getYear()+"-"+(dp.getMonth()+1);
                    Map<String,Object> param1 = new HashMap<>();
                    //param.put("userId",phoneNumber);
                    param1.put("reportType","month");
                    param1.put("all",false);
                    param1.put("time",date_m.toString());
                    //网络请求
                    dateListenerManager_m.getDateMouth(date_m,param1,month);
                    //getUserRecord(Constants.GET_M,param);
                    //getUserRecordOfDay(Constants.GET_D);
                    dialog.dismiss();
                    tv_date_year_month.setText(date_m);
                    break;
                case R.id.turnBack:
                    AppManager.getAppManager().finishActivity();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 获取时间
     *弹出对话框选择查询日期
     */
    private void setDateOfMonth(){
        MyDialog.Builder builder = new MyDialog.Builder(this);
        dialog = builder.setStyle(R.style.MyDialog)
                .setCancelTouchout(false)
                .setView(R.layout.choosedatedialog_m)
                .setWidthpx(getWindowManager().getDefaultDisplay().getWidth())
                .setHeihgtdp(290)
                .addViewOnclickListener(R.id.btn_cancel,listener)
                .addViewOnclickListener(R.id.btn_chooseDate_ensure_m,listener)
                .build();
        dialogView = dialog.getView();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        datePicker_Y = dialogView.findViewById(R.id.datePicker_Y);
        datePicker_M = dialogView.findViewById(R.id.datePicker_M);
        datePicker_Y.setMinValue(2019);
        datePicker_Y.setMaxValue(2030);
        datePicker_Y.setValue(year);
        datePicker_Y.setWrapSelectorWheel(false);
        datePicker_M.setMinValue(1);
        datePicker_M.setMaxValue(12);
        datePicker_M.setValue(month);
        datePicker_M.setWrapSelectorWheel(false);
        dialog.show();
        //dp.setDatePickerDividerColor(dp);
        // dp.init(year,month,day,null);
    }

    /**
     * 获取时间
     *弹出对话框选择查询日期
     */
    private void setDateOfDay(){
        MyDialog.Builder builder = new MyDialog.Builder(this);
        dialog = builder.setStyle(R.style.MyDialog)
                .setCancelTouchout(false)
                .setView(R.layout.choosedatedialog)
                .setWidthpx(getWindowManager().getDefaultDisplay().getWidth())
                .setHeihgtdp(290)
                .addViewOnclickListener(R.id.btn_cancel,listener)
                .addViewOnclickListener(R.id.btn_chooseDate_ensure,listener)
                .build();
        dialog.show();
        dialogView = dialog.getView();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dp = dialogView.findViewById(R.id.dp_chooseDate);
        dp.setDatePickerDividerColor(dp);
        // dp.init(year,month,day,null);
    }

    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyy年MM月
     */
    public static String getStringDateShortOfM() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyy年
     */
    public static String getStringDateShortY() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

}
