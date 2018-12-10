package cn.cjwddz.knowu.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import cn.cjwddz.knowu.R;

/**
 * Created by K.B. on 2018/9/13.
 */

public class InformationView extends View {
    private int fill_color;
    private Paint paint;
    private int width = 1;
    private Boolean isSelected;
    private int height = 12;
    private float viewWidth;
    private float viewHeight;
    private float radius;
    private int interval;
    private float circle_centerX;
    private float circle_centerY;
    private float left;
    private float right;
    private float top;
    private float bottom;
    private int vWidth;
    private int vHeight;
    private static  int MAX_HEIGHT = 6*60*60;





    public InformationView(Context context) {
        super(context);
        setPaint();
        //getValue();
    }

    public InformationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initStyle(attrs);
        setPaint();
        //getValue();
    }

    public InformationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStyle(attrs);
        setPaint();
       // getValue();
    }

    //获取各种属性值
    @TargetApi(Build.VERSION_CODES.M)
    private void initStyle(AttributeSet attrs){
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.InformationView);
        int attrCount = typedArray.getIndexCount();
        for(int i=0;i<attrCount;i++){
            int attr = typedArray.getIndex(i);
            switch(attr){
                case R.styleable.InformationView_fill_color:
                    fill_color = typedArray.getColor(attr,getResources().getColor(R.color.informationView_fill_color,null));
            }
        }
        typedArray.recycle();
    }


    private void setPaint(){
        paint = new Paint();
        paint.setAntiAlias(true);//方锯齿
        paint.setDither(true);//防抖动
        paint.setStrokeWidth(width);
        paint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //调整画布大小
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
        //vWidth = MeasureSpec.getSize(widthMeasureSpec);
        //getValue(widthMeasureSpec,heightMeasureSpec);
    }

    //调整画布高度
    private int measure(int origin) {
        int specSize = MeasureSpec.getSize(origin);
        return specSize/13;
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onDraw(Canvas canvas) {
        //setIHeight(12);
        if(isSelected){
            paint.setColor(getResources().getColor(R.color.informationView_fill_selected_color,null));
        }else{
            paint.setColor(fill_color);
        }
        //setPaint();
        getValue();
        //canvas.drawCircle(0,0,50,paint);
        canvas.drawCircle(circle_centerX,circle_centerY,radius,paint);
        canvas.drawRect(left,top,right,bottom,paint);
       // canvas.drawRect(0,0,vWidth,vHeight,paint);
        isSelected = false;
    }

    public void getValue(){
        vWidth = getWidth();
        vHeight = getHeight();
        viewWidth = vWidth;
        radius = viewWidth/2;
        float scale;
        scale = height*1.0f/MAX_HEIGHT;
        //interval = (vWidth/12-viewWidth)/2;
        //viewHeight = scale * vHeight;
        if(scale == 0){
            viewHeight = 0;
        }else{
            viewHeight = scale * vHeight;
        }
        circle_centerX = viewWidth/2;
        circle_centerY = vHeight - viewHeight + radius;
        //circle_centerY =  radius;
        left = 0;
        right =  viewWidth;
        top = circle_centerY;
        bottom = vHeight;
    }

    public void setIHeight(int height) {
        this.height = height;
        invalidate();
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
        invalidate();
    }

}
