package cn.cjwddz.knowu.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.view.InformationView;


/**
 * Created by K.B. on 2018/9/12.
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {


    public static interface OnItemClickListener{
        void onItemClick(View view);
    }
    private DataAdapter.OnItemClickListener itemClickListener;
    public void setItemClickListener(DataAdapter.OnItemClickListener listener){
        this.itemClickListener = listener;
    }


    private List<String> mdata ;
    private int position;
    private List<Boolean> isClicks;//控件是否被点击,默认为false，如果被点击，改变值，控件根据值改变自身颜色

    public DataAdapter(List<String> data,int position) {
        this.mdata = data;
        this.position = position;
        isClicks = new ArrayList<>();
        if(mdata.size()==0){
           for(int i=0;i<30;i++){
               if(i==this.position){
                   isClicks.add(true);
               }else{
                   isClicks.add(false);
               }
           }
        }else{
            for(int i = 0;i<mdata.size();i++){
                if(i==this.position){
                    isClicks.add(true);
                }else{
                    isClicks.add(false);
                }
            }
        }

    }

    public void setMData(List<String> data,int position){
        this.mdata = data;
        this.position = position;
        isClicks.clear();
        for(int i = 0;i<mdata.size();i++){
            if(i==this.position){
                isClicks.add(true);
            }else{
                isClicks.add(false);
            }
        }
        notifyDataSetChanged();
    }

    public void setItemSelected(int position,boolean isSelected){
        //holder.iv_data.setSelected(selected);
        isClicks.set(position,isSelected);
        //notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public InformationView iv_data;
        public ViewHolder(View itemView) {
            super(itemView);
            iv_data = itemView.findViewById(R.id.informationView_data);
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.information_list_item,parent, false);
        final ViewHolder holder= new ViewHolder(view);
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
        //String date = mdate.get(position);
        String data = mdata.get(position);
        holder.iv_data.setIHeight(Integer.parseInt(data));
        if(!isClicks.isEmpty()){
            if(isClicks.get(position)){
                setSelected(holder,true,position);
            }else{
                setSelected(holder,false,position);
            }
        }
       // holder.tv_date.setText(date);
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public void setSelected(ViewHolder holder,Boolean selected,int position) {
        holder.iv_data.setSelected(selected);
        isClicks.set(position,selected);
    }
}
