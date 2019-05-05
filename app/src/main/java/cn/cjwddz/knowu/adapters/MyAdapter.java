package cn.cjwddz.knowu.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import cn.cjwddz.knowu.R;

/**
 * Created by K.B. on 2018/8/6.
 */

public class MyAdapter extends SimpleAdapter {

    private float height;
    private Context context;
    private List<Map<String,String>> mData;
    private int resource;
    public MyAdapter(Context context, List<Map<String,String>> data, int resource) {
        super(context, data, resource, null, null);
        this.context = context;
        this.mData = data;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(resource,parent, false);
        ListView.LayoutParams params = new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)height);
        v.setLayoutParams(params);
        TextView tv_info = v.findViewById(R.id.tv_information);
        TextView tv_value = v.findViewById(R.id.tv_value);
        tv_info.setText(mData.get(position).get("name"));
        tv_value.setText(mData.get(position).get("value"));
        return v;
    }

    public void setHeight(float height){
        this.height = height;
    }

    public void setMData(int position,String str){
        this.mData.get(position).put("value",str);
    }


}
