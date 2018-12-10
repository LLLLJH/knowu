package cn.cjwddz.knowu.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by K.B. on 2018/8/6.
 */

public class MyAdapter extends SimpleAdapter {

    private float height;
    public MyAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position,convertView,parent);
        ListView.LayoutParams params = new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)height);
        v.setLayoutParams(params);
        return v;
    }

    public void setHeight(float height){
        this.height = height;
    }
}
