package cn.cjwddz.knowu.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.adapters.DataAdapter;
import cn.cjwddz.knowu.adapters.DateAdapter;
import cn.cjwddz.knowu.common.application.AppManager;
import cn.cjwddz.knowu.common.http.MyHTTPClient;
import cn.cjwddz.knowu.common.utils.MyUtils;
import cn.cjwddz.knowu.interfaces.Get_D_callback;
import cn.cjwddz.knowu.interfaces.Get_M_callback;
import cn.cjwddz.knowu.service.Constants;
import cn.cjwddz.knowu.view.GradientGraphView;
import cn.cjwddz.knowu.view.MyDatePicker;
import cn.cjwddz.knowu.view.MyDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.id.list;
import static cn.cjwddz.knowu.R.id.rv_data;
import static cn.cjwddz.knowu.activity.MainActivity.JSON;

public class RecordActivity extends AppCompatActivity implements Get_D_callback,Get_M_callback{

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


    
    SharedPreferences sp;

    private TextView tv_date_year_month;
    private ImageButton iv_choose_date;
    private ImageButton ibtn_turnBack;

    private  MyDialog dialog;
    //private MyAdapter adapter;
    private View dialogView;
    private MyDatePicker dp;

    private String phoneNumber;

    private  DataAdapter madapter ;
    private DateAdapter adapter1;

    android.support.v7.app.ActionBar actionBar;

    private Get_M_callback get_m_callback;
    private Get_D_callback get_d_callback;
    public void setGet_m_callback(Get_M_callback get_m_callback){this.get_m_callback = get_m_callback;}
    public void setGet_d_callback(Get_D_callback get_d_callback){this.get_d_callback = get_d_callback;}
    public RecordActivity() {
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        //获取内存储中的用户信息
        sp = getSharedPreferences("knowu",MODE_PRIVATE);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initmData();
    }

    /**
     * 返回true，表示拦截事件。
     * 返回false，表示不做任何处理，交给子View处理。
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        /**
         * 如果用户的手指同时放在屏幕上滑动，不要触发滚动事件。
         *
         */
        if(ev.getPointerCount() >= 2){
            return true;
        }

        /**
         * 如果上侧的RecyclerView_Data在滚动中，但是此时用户又在RecyclerView_Date中触发滚动事件，则停止所有滚动，等待新一轮滚动。
         */
        if(recyclerView_Data.getScrollState() != RecyclerView.SCROLL_STATE_IDLE){
            if(touchEventInView(recyclerView_Date,ev.getX(),ev.getY())){
                recyclerView_Data.stopScroll();
                recyclerView_Date.stopScroll();
                return true;
            }
        }

        /**
         * 如果上侧的RecyclerView_Date在滚动中，但是此时用户又在RecyclerView_Data中触发滚动事件，则停止所有滚动，等待新一轮滚动。
         */
        if(recyclerView_Date.getScrollState() != RecyclerView.SCROLL_STATE_IDLE){
            if(touchEventInView(recyclerView_Data,ev.getX(),ev.getY())){
                recyclerView_Data.stopScroll();
                recyclerView_Date.stopScroll();
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 判断点击是否在给定的view里面
     * @param view
     * @param x
     * @param y
     * @return
     * */
    private boolean touchEventInView(View view, float x, float y) {
        if (view == null) {
            return false;
        }

        int[] location = new int[2];
        view.getLocationOnScreen(location);

        int left = location[0];
        int top = location[1];

        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();

        if (y >= top && y <= bottom && x >= left && x <= right) {
            return true;
        }
        return false;
    }




    void initView() {
        setContentView(R.layout.activity_record);
        //标题栏返回按键
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        //google的actionbar是分为上下两栏显示的，上面的代码只能设置顶部actionbar的背景色，为了让下面的背景色一致，还需要添加一行代码：
        actionBar.setSplitBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View actionBarView = View.inflate(this,R.layout.text,null);
        tv_date_year_month = actionBarView.findViewById(R.id.tv_date_year_month);
        iv_choose_date = actionBarView.findViewById(R.id.iv_choose_date);
        ibtn_turnBack = actionBarView.findViewById(R.id.turnBack);
        ibtn_turnBack.setOnClickListener(listener);
        iv_choose_date.setOnClickListener(listener);
        setGet_m_callback(this);
        setGet_d_callback(this);
       // textView.setText("keyile");
        if(actionBar != null){
            actionBar.setCustomView(actionBarView,lp);
            //actionBar.setCustomView(R.layout.text);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
           // actionBar.setHomeButtonEnabled(true);
            //actionBar.setDisplayHomeAsUpEnabled(true);
        }
        findView();
        //update();
        //initmData();
       // lastHolder = (DataAdapter.ViewHolder) recyclerView_Data.findViewHolderForAdapterPosition(0);
        //lastposition = 0;
        /**
        DataAdapter.ViewHolder viewHolder = (DataAdapter.ViewHolder) recyclerView_Data.findViewHolderForAdapterPosition(0);
        if(lastHolder != null){
            madapter.setSelected(lastHolder,false,lastposition);
        }
        madapter.setSelected(viewHolder,true,0);
        lastHolder = viewHolder;
        lastposition = 0;
         */
    }


    /**
     * 请求当月1号的使用数据
     * */
    private void initmData(){
        StringBuffer sb = new StringBuffer();
        Map<String,Object> param = new HashMap<>();
        sb.append(getStringDateShort());
        param.put("all",false);
        param.put("month",sb.toString());
        //网络请求
        getUserRecord(Constants.GET_M,param);

      tv_date_year_month.setText(getStringDateShort());
       /** int x = 0;
        for(int i=1;i<31;i++){
            if(i>24){
               x = i-24;
            }else{
                x = i;
            }
            mdata.add(String.valueOf(x));
            mdate.add(String.valueOf(i));
        }*/
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void findView(){
        recyclerView_Data = (RecyclerView) findViewById(rv_data);
        recyclerView_Date = (RecyclerView) findViewById(R.id.rv_date);
         tv_sum_hour = (TextView) findViewById(R.id.tv_sum_hour);
         tv_sum_min = (TextView) findViewById(R.id.tv_sum_min);
         tv_fighting_hour = (TextView) findViewById(R.id.tv_fighting_hour);
         tv_fighting_min = (TextView) findViewById(R.id.tv_fighting_min);
         tv_userTime = (TextView) findViewById(R.id.tv_userTime);
         tv_fighting_one_min = (TextView) findViewById(R.id.tv_fighting_one_min);
         tv_fighting_max_intensity = (TextView) findViewById(R.id.tv_fighting_max_intensity);
         tv_fighting_average_intensity = (TextView) findViewById(R.id.tv_fighting_average_intensity);
         tv_percent_time = (TextView) findViewById(R.id.tv_percent_time);
         tv_percent_value = (TextView) findViewById(R.id.tv_percent_value);
         gradientGraphView_time = (GradientGraphView) findViewById(R.id.gradientGraphView_time);
         gradientGraphView_intensity = (GradientGraphView) findViewById(R.id.gradientGraphView_intensity);
        btn_user_feel = (Button) findViewById(R.id.btn_user_feel);
        btn_user_feel.setOnClickListener(listener);
    }

    private void initDay(){
        tv_sum_hour.setText(tv_sum_hour_value);
        tv_sum_min.setText(tv_sum_min_value);
        tv_fighting_hour.setText(tv_fighting_hour_value);
        tv_fighting_min.setText(tv_fighting_min_value);
        tv_userTime.setText(tv_userTime_value);
        tv_fighting_one_min.setText(tv_fighting_one_min_value);
        tv_fighting_max_intensity.setText(tv_fighting_max_intensity_value);
        tv_fighting_average_intensity.setText(tv_fighting_average_intensity_value);
        tv_percent_time.setText(tv_percent_time_value);
        tv_percent_value.setText(tv_percent_value_value);
    }

    private void update(){
        //要分别设置LinearLayoutManager，不然显示不出来
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView_Data.setLayoutManager(linearLayoutManager);
        recyclerView_Date.setLayoutManager(linearLayoutManager1);
        //解决数据加载不完的问题
        recyclerView_Data.setNestedScrollingEnabled(false);
        recyclerView_Data.setHasFixedSize(true);
//解决数据加载完成后, 没有停留在顶部的问题
        recyclerView_Date.setFocusable(false);
        //解决数据加载不完的问题
        recyclerView_Date.setNestedScrollingEnabled(false);
        recyclerView_Date.setHasFixedSize(true);
//解决数据加载完成后, 没有停留在顶部的问题
        recyclerView_Data.setFocusable(false);
        madapter = new DataAdapter(mdata);

        madapter.setItemClickListener(new DataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                // TODO: 2018/9/17 发送查询指令
                int position = recyclerView_Data.getChildAdapterPosition(view);
                if(position!=lastposition){
                    //网络请求
                    getUserRecordOfDay(Constants.GET_D);
                }
                DataAdapter.ViewHolder viewHolder = (DataAdapter.ViewHolder) recyclerView_Data.findViewHolderForAdapterPosition(position);
                if(lastHolder != null){
                    madapter.setSelected(lastHolder,false,lastposition);
                }else{
                    DataAdapter.ViewHolder mViewHolder = (DataAdapter.ViewHolder) recyclerView_Data.findViewHolderForAdapterPosition(0);
                    madapter.setSelected(mViewHolder,false,0);
                }
                madapter.setSelected(viewHolder,true,position);
                lastHolder = viewHolder;
                lastposition = position;
            }
        });
        adapter1 = new DateAdapter(mdate);
        adapter1.setItemClickListener(new DateAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                // TODO: 2018/9/17 发送查询指令
                int position = recyclerView_Date.getChildAdapterPosition(view);
                if(position!=lastposition){
                    //网络请求
                    getUserRecordOfDay(Constants.GET_D);
                }
                DataAdapter.ViewHolder viewHolder = (DataAdapter.ViewHolder) recyclerView_Data.findViewHolderForAdapterPosition(position);
                if(lastHolder != null){
                    madapter.setSelected(lastHolder,false,lastposition);
                }else{
                    DataAdapter.ViewHolder mViewHolder = (DataAdapter.ViewHolder) recyclerView_Data.findViewHolderForAdapterPosition(0);
                    madapter.setSelected(mViewHolder,false,0);
                }
                madapter.setSelected(viewHolder,true,position);
                lastHolder = viewHolder;
                lastposition = position;
                // adapter.notifyItemChanged(position);
            }
        });
        recyclerView_Data.setAdapter(madapter);
        recyclerView_Date.setAdapter(adapter1);
        //实现两个RecycleView联动
        recyclerView_Data.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(recyclerView_Data.getScrollState() != RecyclerView.SCROLL_STATE_IDLE){
                    recyclerView_Date.scrollBy(dx,dy);
                }
            }
        });
        recyclerView_Date.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(recyclerView_Date.getScrollState() != RecyclerView.SCROLL_STATE_IDLE){
                    recyclerView_Data.scrollBy(dx,dy);
                }
            }
        });
    }


    /**
     * 使用get方式访问服务器
     * 获取用户使用记录
     *
     * @param url
     * @param map 请求参数（id与日期）
     * */
    public  void getUserRecord(String url,Map<String, Object> map){
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
                .header(Constants.HEADER_CONTENT_TYPE_KEY,Constants.HEADER_CONTENT_TYPE_VAULE)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
               get_m_callback.failureGetM(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    get_m_callback.successedGetM(call,response);
                }else{
                    get_m_callback.failedGetM(call,response);
                }
            }
        });
    }

    /**
     * 使用get方式访问服务器
     * 获取用户使用记录
     *
     * @param url
     *
     * */
    public  void getUserRecordOfDay(String url){
        OkHttpClient okHttpClient = MyHTTPClient.getInstance().getOkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .header(Constants.HEADER_CONTENT_TYPE_KEY,Constants.HEADER_CONTENT_TYPE_VAULE)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                get_d_callback.failureGetD(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    get_d_callback.successedGetD(call,response);
                }else{
                    get_d_callback.failedGetD(call,response);
                }
            }
        });
    }
    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyy-MM
     */
    public static String getStringDateShort() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取时间
     *弹出对话框选择查询日期
     */
    private void setDate(){
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
     * 获取用户评价
     */
    private void setUserFeel(){
        MyDialog.Builder builder = new MyDialog.Builder(this);
        dialog = builder.setStyle(R.style.MyDialog)
                .setCancelTouchout(false)
                .setView(R.layout.userfeeldialog)
                .setWidthpx(getWindowManager().getDefaultDisplay().getWidth()-80)
                .setHeihgtdp(100)
                .setMarginBottom(1.7)
                .addViewOnclickListener(R.id.ib_user_feel_nothing,listener)
                .addViewOnclickListener(R.id.ib_user_feel_little,listener)
                .addViewOnclickListener(R.id.ib_user_feel_good,listener)
                .addViewOnclickListener(R.id.ib_user_feel_best,listener)
                .build();
        dialog.show();
        dialogView = dialog.getView();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        /**获取屏幕宽高
        WindowManager wm = (WindowManager) context	.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int height=display.getHeight();

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.y =  */
        // dp.init(year,month,day,null);
    }


    /**
     * 使用post方式访问服务器
     * 上传用户评价
     *
     * @param url
     * @param feel
     * */
    public  void postUserFeel(String url,long time,String feel){
        OkHttpClient okHttpClient = MyHTTPClient.getInstance().getOkHttpClient();
        JSONObject infos = new JSONObject();
        JSONObject info = new JSONObject();
        try {
            infos.put("type","user_comment");
            infos.put("time",time);
            info.put("content",feel);
            infos.put("info",info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody =  FormBody.create(JSON,infos.toString());
        final Request request = new Request.Builder()
                .url(url)
                .header(Constants.HEADER_CONTENT_TYPE_KEY,Constants.HEADER_CONTENT_TYPE_VAULE_JSON)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("提交用户信息shibai");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    System.out.println("提交用户信息success"+response.body()+"&"+response.message()+"&"+response.code()+"&"+response.request());

                }else{
                    System.out.println("提交用户信息shibai3"+response.toString());
                }
            }
        });
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @SuppressLint("NewApi")
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                //弹出日期选择对话框
                case R.id.iv_choose_date:
                    setDate();
                    break;
                case R.id.btn_cancel:
                    dialog.dismiss();
                    break;
                //选择查询日期
                case R.id.btn_chooseDate_ensure:
                    String date_m =dp.getYear()+"-"+(dp.getMonth()+1);
                   String date =dp.getYear()+"-"+(dp.getMonth()+1)+"-"+dp.getDayOfMonth();
                    Map<String,Object> param = new HashMap<>();
                    //param.put("userId",phoneNumber);
                    param.put("all",false);
                    param.put("month",date_m.toString());
                    //网络请求
                    getUserRecord(Constants.GET_M,param);
                    getUserRecordOfDay(Constants.GET_D);
                    dialog.dismiss();
                    tv_date_year_month.setText(date_m);
                    break;
                //弹出评价对话框
                case R.id.btn_user_feel:
                    setUserFeel();
                    break;
                //获取评价
                case R.id.ib_user_feel_nothing:
                    String day = tv_date_year_month.getText()+"-"+lastposition+1;
                    long time = MyUtils.date2TimeStamp(day,FORMAT);
                    System.out.println(String.valueOf(time));
                    postUserFeel(Constants.ADD_USER_FEEL_URL,time,FEEL_NOTHING);
                    dialog.dismiss();
                    break;
                case R.id.ib_user_feel_little:
                    String day_l = tv_date_year_month.getText()+"-"+lastposition+1;
                    long time_l = MyUtils.date2TimeStamp(day_l,FORMAT);
                    System.out.println(String.valueOf(time_l));
                    postUserFeel(Constants.ADD_USER_FEEL_URL,time_l,FEEL_LITLE);
                    dialog.dismiss();
                    break;
                case R.id.ib_user_feel_good:
                    String day_g = tv_date_year_month.getText()+"-"+lastposition+1;
                    long time_g = MyUtils.date2TimeStamp(day_g,FORMAT);
                    System.out.println(String.valueOf(time_g));
                    postUserFeel(Constants.ADD_USER_FEEL_URL,time_g,FEEL_GOOD);
                    dialog.dismiss();
                    break;
                case R.id.ib_user_feel_best:
                    String day_b = tv_date_year_month.getText()+"-"+lastposition+1;
                    long time_b = MyUtils.date2TimeStamp(day_b,FORMAT);
                    System.out.println(String.valueOf(time_b));
                    postUserFeel(Constants.ADD_USER_FEEL_URL,time_b,FEEL_BEST);
                    dialog.dismiss();
                    break;
                case R.id.turnBack:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void successedGetD(Call call, Response response) throws IOException {
        try {
            JSONObject jsonObject = new JSONObject(response.body().string());
            System.out.println(jsonObject.toString());
            if(jsonObject.getBoolean("status")){
                JSONObject data = jsonObject.getJSONObject("data");
                JSONArray mode_len = data.getJSONArray("mode_len");
                JSONObject fighting = mode_len.getJSONObject(0);
                JSONObject longest = data.getJSONObject("longest");
                tv_fighting_one_min_value = MyUtils.second2min(longest.getInt("len"));
                tv_userTime_value = MyUtils.date2String(longest.getString("start"),longest.getInt("len"));
                tv_fighting_min_value =  MyUtils.second2min(fighting.getInt("total_len"));
                tv_fighting_hour_value = MyUtils.second2hour(fighting.getInt("total_len"));
                tv_sum_min_value = MyUtils.second2min(data.getInt("total_len"));
                tv_sum_hour_value = MyUtils.second2hour(data.getInt("total_len"));
                tv_fighting_max_intensity_value = String.valueOf(data.getInt("bigest_intensity"));
                tv_fighting_average_intensity_value = String.valueOf(data.getInt("ave_intensity"));
                tv_percent_time_value = data.getString("today_pain_ranking");
                tv_percent_value_value = data.getString("tody_using_ranking");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initDay();
                    }
                });
            }else{

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failureGetD(IOException e) {
        System.out.println("服务器异常");
    }

    @Override
    public void failedGetD(Call call, Response response) throws IOException {
        System.out.println("提交用户信息shibai3");
        System.out.println(response.body()+"&"+response.message()+"&"+response.code()+"&"+response.request());
    }

    @Override
    public void successedGetM(Call call, Response response) throws IOException {
        Boolean status = null;
         JSONObject data = null;
        try {
            JSONObject jsonObject = new JSONObject(response.body().string());
            status = jsonObject.getBoolean("status");
            if(status){
                mdata.clear();
                mdate.clear();
                data = jsonObject.getJSONObject("data");
                JSONArray jsonArray =  data.getJSONArray("month_report");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String msg = jsonArray.getString(i);
                    mdata.add(msg);
                    mdate.add(String.valueOf(i+1));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        update();
                    }
                });
                getUserRecordOfDay(Constants.GET_D);
                System.out.println("获得的数据:"+mdata+mdata.size());
            }else{}
        } catch(Exception e){
            e.printStackTrace();
        }
        //System.out.println("提交用户信息success"+response.body()+"&"+response.message()+"&"+response.code()+"&"+response.request());
    }

    @Override
    public void failureGetM(IOException e) {
        System.out.println("服务器异常");
    }

    @Override
    public void failedGetM(Call call, Response response) throws IOException {
        System.out.println("提交用户信息shibai3");
        System.out.println(response.body()+"&"+response.message()+"&"+response.code()+"&"+response.request());
    }
}
