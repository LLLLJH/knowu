package cn.cjwddz.knowu.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.view.FlashCircle;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{
    DeviceItemAdapter adapter = new DeviceItemAdapter();
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(getContent(parent));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 绑定数据

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    // 创建内容区
    private View getContent(ViewGroup parent){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_search_content, parent, false);
        FlashCircle circle = v.findViewById(R.id.flashCircle);
        ListView lv = v.findViewById(R.id.lv_device);
        lv.setAdapter(adapter);
        return v;
    }
}