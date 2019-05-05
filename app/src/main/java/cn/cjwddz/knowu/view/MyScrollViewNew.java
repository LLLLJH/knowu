package cn.cjwddz.knowu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2019/3/25.
 */

public class MyScrollViewNew extends ScrollView {
    private OnScrollChangeListener mOnScrollChangeListener;
    public MyScrollViewNew(Context context) {
        super(context);
    }

    public MyScrollViewNew(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollViewNew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyScrollViewNew(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(mOnScrollChangeListener!=null){
            mOnScrollChangeListener.onScrollChange(this,l,t,oldl,oldt);
        }
    }

    public  void setOnScrollChangedListener(OnScrollChangeListener listener){
        mOnScrollChangeListener = listener;
    }

    public interface OnScrollChangeListener{
        void onScrollChange(ScrollView view,int l,int t,int oldl,int oldt);
    }
}
