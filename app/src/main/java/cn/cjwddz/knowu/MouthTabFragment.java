package cn.cjwddz.knowu;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

import cn.cjwddz.knowu.activity.RecordActivity;
import cn.cjwddz.knowu.activity.RegisterActivity;
import cn.cjwddz.knowu.adapters.DataAdapter;
import cn.cjwddz.knowu.adapters.DataAdapter_mouth;
import cn.cjwddz.knowu.adapters.DateAdapter;
import cn.cjwddz.knowu.common.http.MyHTTPClient;
import cn.cjwddz.knowu.common.utils.MyUtils;
import cn.cjwddz.knowu.interfaces.Get_D_callback;
import cn.cjwddz.knowu.interfaces.Get_M_callback;
import cn.cjwddz.knowu.interfaces.SetDateListenerManager;
import cn.cjwddz.knowu.interfaces.SetDateListenerManager_m;
import cn.cjwddz.knowu.interfaces.SetDate_interface;
import cn.cjwddz.knowu.interfaces.SetDate_interface_m;
import cn.cjwddz.knowu.service.Constants;
import cn.cjwddz.knowu.view.GradientGraphView;
import cn.cjwddz.knowu.view.MyDatePicker;
import cn.cjwddz.knowu.view.MyDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static cn.cjwddz.knowu.R.id.rv_data;
import static cn.cjwddz.knowu.activity.MainActivity.JSON;

/**
 * Created by Administrator on 2018/12/19.
 */

public class MouthTabFragment extends Fragment implements Get_M_callback, Get_D_callback, SetDate_interface_m {
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
    private DataAdapter_mouth.ViewHolder lastHolder;
    private int lastposition;

    private String date;
    private int month;

    SharedPreferences sp;

    private TextView tv_date_year_month;
    private ImageButton iv_choose_date;
    private ImageButton ibtn_turnBack;

    private MyDialog dialog;
    //private MyAdapter adapter;
    private View dialogView;
    private MyDatePicker dp;

    private String phoneNumber;
    //内储存
    SharedPreferences preferences;

    private DataAdapter_mouth madapter;
    private DateAdapter adapter1;

    android.support.v7.app.ActionBar actionBar;

    private FragmentTabHost tabHost;

    private Get_M_callback get_m_callback;
    private Get_D_callback get_d_callback;

    public void setGet_m_callback(Get_M_callback get_m_callback) {
        this.get_m_callback = get_m_callback;
    }

    public void setGet_d_callback(Get_D_callback get_d_callback) {
        this.get_d_callback = get_d_callback;
    }

    private View fragmentView;

    @Nullable

    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.tabfragment, container, false);
        setGet_m_callback(this);
        setGet_d_callback(this);
        SetDateListenerManager_m.getInstance().setConnectionStateListener(this);
        preferences = getActivity().getSharedPreferences("knowu", MODE_PRIVATE);
        phoneNumber = preferences.getString("phoneNumber", null);
        findView(fragmentView);
        initmData();
        update();
        return fragmentView;
    }

    /**
     * 请求当月1号的使用数据
     */
    private void initmData() {
        StringBuffer sb = new StringBuffer();
        Map<String, Object> param = new HashMap<>();
        date = getStringDateShort();
        sb.append(date);
        param.put("reportType", "month");
        param.put("all", false);
        param.put("time", sb);
        //网络请求
        getUserRecord(Constants.GET_YEAR_OF_MONTH, param);
        //tv_date_year_month.setText(getStringDateShort());
    }

    private void findView(View v) {
        recyclerView_Data = (RecyclerView) v.findViewById(rv_data);
        recyclerView_Date = (RecyclerView) v.findViewById(R.id.rv_date);
        tv_sum_hour = (TextView) v.findViewById(R.id.tv_sum_hour);
        tv_sum_min = (TextView) v.findViewById(R.id.tv_sum_min);
        tv_fighting_hour = (TextView) v.findViewById(R.id.tv_fighting_hour);
        tv_fighting_min = (TextView) v.findViewById(R.id.tv_fighting_min);
        tv_userTime = (TextView) v.findViewById(R.id.tv_userTime);
        tv_fighting_one_min = (TextView) v.findViewById(R.id.tv_fighting_one_min);
        tv_fighting_max_intensity = (TextView) v.findViewById(R.id.tv_fighting_max_intensity);
        tv_fighting_average_intensity = (TextView) v.findViewById(R.id.tv_fighting_average_intensity);
        tv_percent_time = (TextView) v.findViewById(R.id.tv_percent_time);
        tv_percent_value = (TextView) v.findViewById(R.id.tv_percent_value);
        gradientGraphView_time = (GradientGraphView) v.findViewById(R.id.gradientGraphView_time);
        gradientGraphView_intensity = (GradientGraphView) v.findViewById(R.id.gradientGraphView_intensity);
        btn_user_feel = (Button) v.findViewById(R.id.btn_user_feel);
        btn_user_feel.setOnClickListener(listener1);
    }


    private void initDay() {
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

    private Map<String, Object> putMap(int index) {
        Map<String, Object> param = new HashMap<>();
        //param.put("userId",phoneNumber);
        param.put("reportType", "month");
        param.put("selectTime", date);
        param.put("index", index);
        return param;
    }

    private void update() {
        //要分别设置LinearLayoutManager，不然显示不出来
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
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
        madapter = new DataAdapter_mouth(mdata, month);

        madapter.setItemClickListener(new DataAdapter_mouth.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int position = recyclerView_Data.getChildAdapterPosition(view);
                DataAdapter_mouth.ViewHolder viewHolder = (DataAdapter_mouth.ViewHolder) recyclerView_Data.findViewHolderForAdapterPosition(position);
                if (position != lastposition) {
                    //网络请求
                    getUserRecordOfDay(Constants.GET_MONTH, putMap(position));
                    lastHolder = (DataAdapter_mouth.ViewHolder) recyclerView_Data.findViewHolderForAdapterPosition(lastposition);
                    madapter.setSelected(lastHolder, false, lastposition,isVisibleItem(lastposition));
                } else {
                    DataAdapter_mouth.ViewHolder mViewHolder = (DataAdapter_mouth.ViewHolder) recyclerView_Data.findViewHolderForAdapterPosition(0);
                    madapter.setSelected(mViewHolder, false, 0,isVisibleItem(0));
                }
                madapter.setSelected(viewHolder, true, position,true);
                lastHolder = viewHolder;
                lastposition = position;
            }
        });
        adapter1 = new DateAdapter(mdate);
        adapter1.setItemClickListener(new DateAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int position = recyclerView_Date.getChildAdapterPosition(view);
                DataAdapter_mouth.ViewHolder viewHolder = (DataAdapter_mouth.ViewHolder) recyclerView_Data.findViewHolderForAdapterPosition(position);
                if (position != lastposition) {
                    //网络请求
                    getUserRecordOfDay(Constants.GET_MONTH, putMap(position));
                    lastHolder = (DataAdapter_mouth.ViewHolder) recyclerView_Data.findViewHolderForAdapterPosition(lastposition);
                    madapter.setSelected(lastHolder, false, lastposition,isVisibleItem(lastposition));
                } else {
                    DataAdapter_mouth.ViewHolder mViewHolder = (DataAdapter_mouth.ViewHolder) recyclerView_Data.findViewHolderForAdapterPosition(0);
                    madapter.setSelected(mViewHolder, false, 0,isVisibleItem(0));
                }
                madapter.setSelected(viewHolder, true, position,true);
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
                if (recyclerView_Data.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    recyclerView_Date.scrollBy(dx, dy);
                }
            }
        });
        recyclerView_Date.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (recyclerView_Date.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    recyclerView_Data.scrollBy(dx, dy);
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
     */
    public void getUserRecord(String url, Map<String, Object> map) {
        StringBuffer sb = new StringBuffer();
        String string = "";
        String result = "";
        //当用户传入null或者传了一个空的map
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (sb.length() == 0) {
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
                .header(Constants.HEADER_CONTENT_TYPE_KEY, Constants.HEADER_CONTENT_TYPE_VAULE)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                get_m_callback.failureGetM(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    get_m_callback.successedGetM(call, response);
                } else {
                    get_m_callback.failedGetM(call, response);
                }
            }
        });
    }

    /**
     * 使用get方式访问服务器
     * 获取用户使用记录
     *
     * @param url
     */
    public void getUserRecordOfDay(String url, Map<String, Object> map) {
        StringBuffer sb = new StringBuffer();
        String string = "";
        String result = "";
        //当用户传入null或者传了一个空的map
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (sb.length() == 0) {
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
                .header(Constants.HEADER_CONTENT_TYPE_KEY, Constants.HEADER_CONTENT_TYPE_VAULE)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                get_d_callback.failureGetD(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    get_d_callback.successedGetD(call, response);
                } else {
                    get_d_callback.failedGetD(call, response);
                }
            }
        });
    }

    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyy年
     */
    public static String getStringDateShort() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    @Override
    public void successedGetD(Call call, Response response) throws IOException {
        try {
            JSONObject jsonObject = new JSONObject(response.body().string());
            if (jsonObject.getBoolean("status")) {
                JSONObject data = jsonObject.getJSONObject("data");
                JSONArray mode_len = data.getJSONArray("mode_len");
                if (mode_len.length() == 0) {
                    tv_fighting_min_value = MyUtils.second2min(0);
                    tv_fighting_hour_value = MyUtils.second2hour(0);
                } else {
                    JSONObject fighting = mode_len.getJSONObject(0);
                    tv_fighting_min_value = MyUtils.second2min(fighting.getInt("total_len"));
                    tv_fighting_hour_value = MyUtils.second2hour(fighting.getInt("total_len"));
                }

                if (!data.isNull("longest")) {
                    JSONObject longest = data.getJSONObject("longest");
                    tv_fighting_one_min_value = MyUtils.second2min(longest.getInt("len"));
                    tv_userTime_value = MyUtils.date2String(longest.getString("start"), longest.getInt("len"));
                } else {
                    tv_userTime_value = "00:00-00:00";
                    tv_fighting_one_min_value = "00";
                }

                tv_sum_min_value = MyUtils.second2min(data.getInt("total_len"));
                tv_sum_hour_value = MyUtils.second2hour(data.getInt("total_len"));
                tv_fighting_max_intensity_value = String.valueOf(data.getInt("bigest_intensity"));
                tv_fighting_average_intensity_value = String.valueOf(data.getInt("ave_intensity"));
                tv_percent_time_value = data.getString("today_pain_ranking");
                tv_percent_value_value = data.getString("tody_using_ranking");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initDay();
                    }
                });
            } else {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failureGetD(IOException e) {
        /**tv_fighting_min_value =  MyUtils.second2min((int) (Math.random()*Integer.parseInt(mdata.get(lastposition))));
        tv_fighting_hour_value = MyUtils.second2hour((int) (Math.random()*Integer.parseInt(mdata.get(lastposition))));
        tv_sum_min_value = MyUtils.second2min(Integer.parseInt(mdata.get(lastposition)));
        tv_sum_hour_value = MyUtils.second2hour(Integer.parseInt(mdata.get(lastposition)));
        tv_fighting_max_intensity_value = String.valueOf(7);
        tv_fighting_average_intensity_value = String.valueOf(5);
        tv_userTime_value = "11:04-11:57";
        tv_fighting_one_min_value = "53";
        tv_percent_time_value = String.valueOf((int) (Math.random()*100));
        tv_percent_value_value = String.valueOf((int) (Math.random()*100));
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initDay();
            }
        });
         */
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(getActivity(), "服务器异常,获取信息失败！！！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void failedGetD(Call call, Response response) throws IOException {
        /**tv_fighting_min_value =  MyUtils.second2min((int) (Math.random()*Integer.parseInt(mdata.get(lastposition))));
        tv_fighting_hour_value = MyUtils.second2hour((int) (Math.random()*Integer.parseInt(mdata.get(lastposition))));
        tv_sum_min_value = MyUtils.second2min(Integer.parseInt(mdata.get(lastposition)));
        tv_sum_hour_value = MyUtils.second2hour(Integer.parseInt(mdata.get(lastposition)));
        tv_fighting_max_intensity_value = String.valueOf(7);
        tv_fighting_average_intensity_value = String.valueOf(5);
        tv_userTime_value = "11:04-11:57";
        tv_fighting_one_min_value = "53";
        tv_percent_time_value = String.valueOf((int) (Math.random()*100));
        tv_percent_value_value = String.valueOf((int) (Math.random()*100));
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initDay();
            }
        });
         */
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(getActivity(), "服务器异常,获取信息失败！！！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void successedGetM(Call call, Response response) throws IOException {
        Boolean status = null;
        JSONObject data = null;
        try {
            JSONObject jsonObject = new JSONObject(response.body().string());
            status = jsonObject.getBoolean("status");
            if (status) {
                mdata.clear();
                mdate.clear();
                data = jsonObject.getJSONObject("data");
                JSONArray jsonArray = data.getJSONArray("report");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String msg = jsonArray.getString(i);
                    mdata.add(msg);
                    mdate.add(String.valueOf(i + 1));
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        madapter.setMData(mdata, month);
                        adapter1.setMDatae(mdate);
                    }
                });
                getUserRecordOfDay(Constants.GET_MONTH, putMap(month));
                // System.out.println("获得的数据mouth:"+mdata+mdata.size());
            } else {
                getLogin(Constants.LOGIN, phoneNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("提交用户信息success"+response.body()+"&"+response.message()+"&"+response.code()+"&"+response.request());
    }

    @Override
    public void failureGetM(IOException e) {
       /** for (int i = 0; i <12; i++) {
            String msg = String.valueOf((int)(Math.random()*5*60*60));
            mdata.add(msg);
            mdate.add(String.valueOf(i+1));
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                madapter.setMData(mdata,month);
                adapter1.setMDatae(mdate);
            }
        });
        */
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(getActivity(), "服务器异常,获取信息失败！！！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void failedGetM(Call call, Response response) throws IOException {
      /**  for (int i = 0; i <12; i++) {
            String msg = String.valueOf((int)(Math.random()*5*60*60));
            mdata.add(msg);
            mdate.add(String.valueOf(i+1));
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                madapter.setMData(mdata,month);
                adapter1.setMDatae(mdate);
            }
        });
       */
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(getActivity(), "服务器异常,获取信息失败！！！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    View.OnClickListener listener1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //弹出评价对话框
                case R.id.btn_user_feel:
                    setUserFeel();
                    break;
                //获取评价
                case R.id.ib_user_feel_nothing:
                    String day = date + "-" + lastposition + 1;
                    long time = MyUtils.date2TimeStamp(day, FORMAT);
                    //System.out.println(String.valueOf(time));
                    postUserFeel(Constants.ADD_USER_FEEL_URL, time, FEEL_NOTHING);
                    dialog.dismiss();
                    break;
                case R.id.ib_user_feel_little:
                    String day_l = date + "-" + lastposition + 1;
                    long time_l = MyUtils.date2TimeStamp(day_l, FORMAT);
                    //System.out.println(String.valueOf(time_l));
                    postUserFeel(Constants.ADD_USER_FEEL_URL, time_l, FEEL_LITLE);
                    dialog.dismiss();
                    break;
                case R.id.ib_user_feel_good:
                    String day_g = date + "-" + lastposition + 1;
                    long time_g = MyUtils.date2TimeStamp(day_g, FORMAT);
                    //System.out.println(String.valueOf(time_g));
                    postUserFeel(Constants.ADD_USER_FEEL_URL, time_g, FEEL_GOOD);
                    dialog.dismiss();
                    break;
                case R.id.ib_user_feel_best:
                    String day_b = date + "-" + lastposition + 1;
                    long time_b = MyUtils.date2TimeStamp(day_b, FORMAT);
                    //System.out.println(String.valueOf(time_b));
                    postUserFeel(Constants.ADD_USER_FEEL_URL, time_b, FEEL_BEST);
                    dialog.dismiss();
                    break;
            }

        }
    };

    /**
     * 获取用户评价
     */
    private void setUserFeel() {
        MyDialog.Builder builder = new MyDialog.Builder(getContext());
        dialog = builder.setStyle(R.style.MyDialog)
                .setCancelTouchout(false)
                .setView(R.layout.userfeeldialog)
                .setWidthpx(getActivity().getWindowManager().getDefaultDisplay().getWidth() - 80)
                .setHeihgtdp(100)
                .setMarginBottom(1.7)
                .addViewOnclickListener(R.id.ib_user_feel_nothing, listener1)
                .addViewOnclickListener(R.id.ib_user_feel_little, listener1)
                .addViewOnclickListener(R.id.ib_user_feel_good, listener1)
                .addViewOnclickListener(R.id.ib_user_feel_best, listener1)
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
     */
    public void postUserFeel(String url, long time, String feel) {
        OkHttpClient okHttpClient = MyHTTPClient.getInstance().getOkHttpClient();
        JSONObject infos = new JSONObject();
        JSONObject info = new JSONObject();
        try {
            infos.put("type", "user_comment");
            infos.put("time", time);
            info.put("content", feel);
            infos.put("info", info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = FormBody.create(JSON, infos.toString());
        final Request request = new Request.Builder()
                .url(url)
                .header(Constants.HEADER_CONTENT_TYPE_KEY, Constants.HEADER_CONTENT_TYPE_VAULE_JSON)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "服务器异常,提交失败！！！", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "提交成功！！！", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "服务器异常,提交失败！！！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void getDateMonth(String s, Map m, int p) {
        date = s;
        month = p - 1;
        lastposition = month;
        getUserRecord(Constants.GET_YEAR_OF_MONTH, m);
    }

    /**
     * 请求登录
     */
    public void getLogin(String url, String phoneNumber) {
        OkHttpClient okHttpClient = MyHTTPClient.getInstance().getOkHttpClient();
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("phone", phoneNumber);
        formBody.add("password", phoneNumber);
        final Request request = new Request.Builder()
                .url(url)
                .header(Constants.HEADER_CONTENT_TYPE_KEY, Constants.HEADER_CONTENT_TYPE_VAULE)
                .post(formBody.build())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }
        });
    }

    /**
     *确定item是否可见
     **/
    public Boolean isVisibleItem(int position){
        int firstItemPosition = 0;
        int lastItemPosition = 12;
        RecyclerView.LayoutManager layoutManager =  recyclerView_Data.getLayoutManager();
        if(layoutManager instanceof LinearLayoutManager){
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            //获取第一个可见Item的position
            firstItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            //获取最后一个Item的position
            lastItemPosition = linearLayoutManager.findLastVisibleItemPosition();

        }
        if(firstItemPosition <= position && position <= lastItemPosition){
            return true;
        }else{
            return false;
        }

    }
}