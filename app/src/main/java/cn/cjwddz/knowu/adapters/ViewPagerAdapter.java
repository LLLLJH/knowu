package cn.cjwddz.knowu.adapters;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2018/7/13.
 */

public class ViewPagerAdapter extends PagerAdapter {

    //View的ID
    private static final int VIEW_NO_1 = 0;
    private static final int VIEW_NO_2 = 1;
    private static final int VIEW_NO_3 = 2;
    private static final int VIEW_NO_4 = 3;
    private static final int VIEW_NO_5 = 4;

    private List<View> views;

    public ViewPagerAdapter(List<View> views){
        this.views = views;
    }
    @Override
    public int getCount() {
        return views.size();
    }

/**
 * 实例化引导页
 * */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position) ;
        container.addView(view, 0);// 添加页卡
        switch (position) {
            case VIEW_NO_1:

                break;
            case VIEW_NO_2:

                break;
            case VIEW_NO_3:

                break;
            case VIEW_NO_4:

                break;
            case VIEW_NO_5:

                break;
            default:
                break;
        }

        return views.get(position);

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = views.get(position) ;
        BitmapDrawable drawable = (BitmapDrawable)view.getBackground() ;
        if (drawable != null) {
            drawable.getBitmap().recycle() ;
        }
        switch (position) {
            case VIEW_NO_1:

                break;
            case VIEW_NO_2:

                break;
            case VIEW_NO_3:

                break;
            case VIEW_NO_4:

                break;
            case VIEW_NO_5:

                break;
            default:
                break;
        }
        container.removeView(views.get(position));

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
