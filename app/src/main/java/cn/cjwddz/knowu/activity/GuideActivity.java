package cn.cjwddz.knowu.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.adapters.ViewPagerAdapter;

public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener,View.OnTouchListener{
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private List<View> mListView;
    private ImageView point_iv;
    private int currentItem;

    static ImageView guide_iv1_arrow;
    static ImageView guide_iv2_purplelight;
    static ImageView guide_iv2_hand;
    static ImageView guide_iv3_hand;
    static ImageView guide_iv4_deviceall;
    private  float startX;
    private float endX;

     Animation arrowdown;
    Animation a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        point_iv = findViewById(R.id.pointImageView);
        mListView = new ArrayList<View>();
        LayoutInflater layoutInflater =getLayoutInflater();
        View view1 = layoutInflater.inflate(R.layout.guide_1_layout,null);
        guide_iv1_arrow = view1.findViewById(R.id.guide_iv1_arrow);
        View view2 = layoutInflater.inflate(R.layout.guide_2_layout,null);
        guide_iv2_purplelight = view2.findViewById(R.id.guide_iv2_purplelight);
        guide_iv2_hand = view2.findViewById(R.id.guide_iv2_hand);
        View view3 = layoutInflater.inflate(R.layout.guide_3_layout,null);
        guide_iv3_hand = view3.findViewById(R.id.guide_iv3_hand);
        View view4 = layoutInflater.inflate(R.layout.guide_4_layout,null);
        guide_iv4_deviceall = view4.findViewById(R.id.guide_iv4_deviceall);
        View view5 = layoutInflater.inflate(R.layout.guide_5_layout,null);
        mListView.add(view1);
        mListView.add(view2);
        mListView.add(view3);
        mListView.add(view4);
        mListView.add(view5);

        viewPager = findViewById(R.id.guideViewPager);
        viewPagerAdapter = new ViewPagerAdapter(mListView);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(this);
        viewPager.setOnTouchListener(this);

        changePoint(0);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }
/**
 * 新页面被选中
 * */
    @Override
    public void onPageSelected(int position) {
        changePoint(position);
        currentItem = position;
    }
    public void changePoint(int position){
        switch (position){
            case 0:
                point_iv.setImageResource(R.drawable.point1);
                   arrowdown = AnimationUtils.loadAnimation(this,R.anim.arrowdown);
                arrowdown.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // TODO Auto-generated method stub
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        guide_iv1_arrow.startAnimation(arrowdown);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // TODO Auto-generated method stub
                    }
                });
                guide_iv1_arrow.startAnimation(arrowdown);
                break;

            case 1:
                point_iv.setImageResource(R.drawable.point2);
                arrowdown = AnimationUtils.loadAnimation(this,R.anim.handup);
                 a = AnimationUtils.loadAnimation(this,R.anim.lightalpha);
                arrowdown.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // TODO Auto-generated method stub
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        guide_iv2_hand.startAnimation(arrowdown);
                        guide_iv2_purplelight.startAnimation(a);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // TODO Auto-generated method stub
                    }
                });
                guide_iv2_hand.startAnimation(arrowdown);
                guide_iv2_purplelight.startAnimation(a);
                break;
            case 2:
                point_iv.setImageResource(R.drawable.point3);
                arrowdown = AnimationUtils.loadAnimation(this,R.anim.handup);
                arrowdown.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // TODO Auto-generated method stub
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                       guide_iv3_hand.startAnimation(arrowdown);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // TODO Auto-generated method stub
                    }
                });
                guide_iv3_hand.startAnimation(arrowdown);
                break;
            case 3:
                point_iv.setImageResource(R.drawable.point4);
                arrowdown = AnimationUtils.loadAnimation(this,R.anim.device_scale);
                arrowdown.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // TODO Auto-generated method stub
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        guide_iv4_deviceall.startAnimation(arrowdown);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // TODO Auto-generated method stub
                    }
                });
                guide_iv4_deviceall.startAnimation(arrowdown);
                break;
            case 4:
                point_iv.setImageResource(R.drawable.point5);
                break;
            default:break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX = motionEvent.getX();
                break;
            case MotionEvent.ACTION_UP:
                endX = motionEvent.getX();
                WindowManager w = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
               int width = w.getDefaultDisplay().getWidth();
                if(currentItem ==4&&(startX-endX)>=(width/4)){
                    Intent intent = new Intent();
                    intent.setClass(this,MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    finish();
                }
                break;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == event.KEYCODE_BACK&&event.getAction() ==KeyEvent.ACTION_DOWN){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
