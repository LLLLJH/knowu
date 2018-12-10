package cn.cjwddz.knowu.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

/**
 * Created by Administrator on 2018/9/21.
 */

public class MyScrollView extends ScrollView{
    private View childView;//孩子view
    private float click_y;//点击时的y坐标
    private boolean isCount;//是否开始计时
    private Rect anim = new Rect();//矩形，判断是否需要实现动画


    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /***
     * 根据 XML 生成视图工作完成.该函数在生成视图的最后调用
     */
    @Override
    protected void onFinishInflate() {
        if(getChildCount()>0){
            childView = getChildAt(0);//获得孩子View
        }
    }

    /**
     * 监听触摸事件
     * */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(childView != null){
            mOnTouchEvent(ev);
        }
        return super.onTouchEvent(ev);
    }

    private void mOnTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch(action){
            case ACTION_DOWN:
                break;
            case ACTION_UP:
                //手指松开
                if(isNeedAnimation()){
                    animation();
                    isCount = false;
                }
                break;
            case ACTION_MOVE:
                float pre_y = click_y;
                float last_y = ev.getY();
                int distance = (int) (last_y - click_y);
                if(!isCount){
                    distance = 0;
                }
                click_y = last_y;
                if(isNeedMove()){
                    if(anim.isEmpty()){
                        anim.set(childView.getLeft(),childView.getTop(),childView.getRight(),childView.getBottom());
                    }
                    //开始移动布局
                    childView.layout(childView.getLeft(),childView.getTop()-distance/2,childView.getRight(),childView.getBottom()-distance/2);
                }
                isCount = true;
                break;
            default:break;
        }
    }

    private boolean isNeedMove() {
        int mheight = childView.getMeasuredHeight();//获取孩子视图的总高度
        int offHeight = mheight - getHeight();
        int scrollY = getScrollY();
        //等于0说明已下拉到顶部，等于offHeight说明到底部，都需要回弹动画
        if(scrollY == 0 || offHeight == scrollY){
            return true;
        }
        return false;
    }

    /**
     * 弹性恢复动画
     * */
    private void animation() {
        TranslateAnimation ta = new TranslateAnimation(0,0,childView.getTop(),anim.top);
        ta.setDuration(200);
        childView.startAnimation(ta);
        childView.layout(anim.left,anim.top,anim.right,anim.bottom);
        anim.setEmpty();
    }

    //是否开启回弹动画
    private boolean isNeedAnimation() {
        return !anim.isEmpty();
    }
}
