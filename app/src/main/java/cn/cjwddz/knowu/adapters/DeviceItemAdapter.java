package cn.cjwddz.knowu.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import cn.cjwddz.knowu.R;

public class DeviceItemAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
    }
}