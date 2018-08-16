package cn.cjwddz.knowu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.Toast;

import cn.cjwddz.knowu.R;
import cn.cjwddz.knowu.common.utils.MyUtils;

/**
 * Created by ljh on 2018/8/9.
 * 任何重写view都需要的几个步骤:
 * 继承view
 * 自定义属性
 * 重写onMeasure方法【可不重写】
 * 重写onDraw方法
 */

public class MyImageView extends android.support.v7.widget.AppCompatImageView {

    private Matrix matrix;
    private Paint paint,dPaint;
    private int type;
    private static final int TYPE_CIRCLE = 0;
    private static final int TYPE_ROUND = 1;
    private int borderRadius;
    // 默认圆角宽度
    private static final int BORDER_RADIUS_DEFAULT = 10;
    //直径与半径
    private int width;
    private int radius;
    //原点坐标
    private int point;
    //圆角范围
    private RectF rect;

    // 渲染器,使用图片填充形状
    private BitmapShader bitmapShader;

    //在java代码创建视图的时候被调用
    public MyImageView(Context context) {
        super(context,null);
        init(context, null, 0);
    }
    //xml文件中定义MyImageView时被调用
    public MyImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs,0);
        init(context, attrs, 0);
    }

    //给View提供一个基本的style，如果我们没有对View设置某些属性，就使用这个style中的属性。
    public MyImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
       init(context, attrs, defStyleAttr);
    }
    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
        //初始化各属性
        matrix = new Matrix();
        paint = new Paint();
        dPaint = new Paint();
        dPaint.setAntiAlias(true);
        dPaint.setStyle(Paint.Style.FILL);
        dPaint.setColor(Color.WHITE);
        paint.setAntiAlias(true);

        //获取自定义属性值
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyImageView,defStyleAttr,0);
        int count = array.getIndexCount();
        for(int i=0;i<count;i++){
            int attr = array.getIndex(i);
            switch (attr){
                case R.styleable.MyImageView_borderRadius:
                    borderRadius = array.getDimensionPixelSize(R.styleable.MyImageView_borderRadius,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BORDER_RADIUS_DEFAULT, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.MyImageView_imageType:
                    type = array.getInt(R.styleable.MyImageView_imageType,TYPE_CIRCLE);
                    break;
            }
        }
        array.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rect = new RectF(0,0,getWidth(),getHeight());
        //rect = new RectF(0,0,getWidth(),getHeight());
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(type == TYPE_CIRCLE){
            //获取父布局的最小值作为ImageView的宽高
            width = Math.min(getMeasuredWidth(),getMeasuredHeight());
            //设置圆形圆心点坐标
            point = width/4;
            //设置圆半径
            radius = width/4;
            //设置当前ImageView控件的宽高
            setMeasuredDimension(width/2,width/2);
        }
        if(type == TYPE_ROUND){
            width = Math.min(getMeasuredWidth(),getMeasuredHeight());
            setMeasuredDimension(width/2,width/2);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(getDrawable() ==null){
            if(type == TYPE_CIRCLE){
                //在ImageView上无图画圆
                canvas.drawCircle(point,point,radius,dPaint);
            }else{
                //在ImageView上无图画圆角矩形
                canvas.drawRoundRect(rect,borderRadius,borderRadius,dPaint);
            }
        }else{
            setShader();
            if(type == TYPE_CIRCLE){
                //在ImageView上以位图渲染画圆
                canvas.drawCircle(point,point,radius,paint);
            }else{
                //在ImageView上以位图渲染画圆角矩形
                canvas.drawRoundRect(rect,borderRadius,borderRadius,paint);
            }
        }

    }

    private void setShader(){
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        //将drawable转换成bitmap
        Bitmap bitmap = drawable2Bitmap(drawable);
        //CLAMP(拉伸)、REPEAT(重复)、MIRROR(镜像)，shader的拉伸方式为拉伸最后一像素
        bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        //图片缩放，若图片仍小于ImageView则拉伸最后一像素，否则不拉伸
        float scale = 1.0f;
        if(type == TYPE_CIRCLE){
            // bitmap.getxxx获取位图的宽高，getxxx获取ImageView的宽高取小值，如果取大值的话，则不能覆盖view
            int bitmapWidth = Math.min(bitmap.getWidth(),getHeight());
            scale = getWidth()*1.0f/bitmapWidth;//scale=2.0
        }else if(type ==TYPE_ROUND){
            scale = Math.max(getWidth()*1.0f/bitmap.getWidth(),getHeight()*1.0f/bitmap.getHeight());//scale=2.0
        }
        matrix.setScale(scale,scale);
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);
    }

    /**
     * 将Drawable转化为Bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawable2Bitmap(Drawable drawable){
        if(drawable instanceof BitmapDrawable){
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            return bitmapDrawable.getBitmap();
        }
        //获取图片固有的宽度和高度（单位为dp）
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        //创建画布
        Bitmap bitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0,0,w,h);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
    * 对外公布的设置borderRadius方法
    *
    * @param borderRadius
    */
    public void setBorderRadius(int borderRadius){
        int value = MyUtils.dip2px(getContext(),borderRadius);
        if(this.borderRadius != value){
            this.borderRadius = value;
            invalidate();
        }
    }

    /**
     * 对外公布的设置形状的方法
     *
     * @param type
     */
    public void setType(int type){
        if(this.type!=TYPE_ROUND||this.type!=TYPE_CIRCLE){
            if(this.type != type){
                this.type = type;
                // 这个时候改变形状了，就需要调用父布局的onLayout，那么此view的onMeasure方法也会被调用
                requestLayout();
            }
        }
    }

    public int getWH(){
        return width/4;
    }

    public void startInvalidate(){
        invalidate();
    }
}
