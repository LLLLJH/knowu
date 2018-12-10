package cn.cjwddz.knowu.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2018/9/26.
 */

public class MyRecycleView extends RecyclerView{
    public MyRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayoutManager(new LinearLayoutManager(context));
        setNestedScrollingEnabled(false);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        //int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
            //    MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec,800);
    }
}
