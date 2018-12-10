package cn.cjwddz.knowu.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.view.InformationView;


/**
 * Created by K.B. on 2018/9/12.
 */

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {

    public static interface OnItemClickListener{
        void onItemClick(View view);
    }

    private DateAdapter.OnItemClickListener itemClickListener;
    public void setItemClickListener(DateAdapter.OnItemClickListener listener){
        this.itemClickListener = listener;
    }

    private List<String> mdate;

    public DateAdapter(List<String> date) {
        this.mdate = date;
    }

    public void setMDatae(List<String> date){
        this.mdate = date;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_date;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.information_list_date_item,parent, false);
        ViewHolder holder= new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(v);
            }
        });
        //设置item的宽度
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.width = parent.getWidth()/13;
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String date = mdate.get(position);
        holder.tv_date.setText(date);
    }

    @Override
    public int getItemCount() {
        return mdate.size();
    }
}
