package cn.cjwddz.knowu.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.adapters.SearchAdapter;

public class SearchActivity extends BaseActivity implements RecyclerView.RecyclerListener {

    private RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;

    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    void initView() {
     setContentView(R.layout.activity_search);
        // 初始化recycleView
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new SearchAdapter();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle);
        // 设置布局管理器
        mRecyclerView.setLayoutManager(mLayoutManager);
        // 设置adapter
        mRecyclerView.setAdapter(mAdapter);

    }
    @Override
    void initData() {

    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        // 下拉刷新
    }
    public void close(View v){
        finish();
    }
}