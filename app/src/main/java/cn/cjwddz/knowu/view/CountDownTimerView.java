package cn.cjwddz.knowu.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.Timer;
import java.util.TimerTask;

import cn.cjwddz.knowu.R;


/**
 * Created by ljh on 2018/7/2.
 */

public class CountDownTimerView extends View{

    private  int ntextsize =100;
    private int stextsixe = 24;
    private int textcolor = R.color.text_color;
    private String countText ="00:00";

    private int linewidth = 7;
    private  int lineheight =30 ;
    private  int linecolor =R.color.line_color;
    private  int dflinecolor =R.color.dfline_color;
    private float linecircleradius  ;

    private int circlewidth = 3;
    private int circlecolor =R.color.circle_color;
    private float circleradius ;

    private int fingercolor =R.color.finger_color;

    private Paint dflinePaint;
    private Paint linePaint;
    private Paint circlePaint;
    private Paint textPaint;
    private Paint fingerPaint;

    private float vwidth ;
    private float vheigth ;

    //表盘刻度线数量
    private int slcount = 120;
    private int m;
    private int s;
    private int ttime = 0 ;
    private int angle = 0;
    private float angle_pick = 0;
    private int second = 0;
    private float x_center;
    private float y_center;
    private float x_pick ;
    private float y_pick ;
    private float x;
    private float y;


    private Bitmap bitmap;


    private  Timer timer;
    private boolean firstStart = true;
    private boolean start = false;
    private Context mcontext;

    public interface onTimer{
        void countdownStart(int second);
        void countdownFinished(int status);
        void countdownStop();
        //void getElectric();
    }

    private onTimer ontimer;
    public  void setTimer(onTimer ontimer){
        this.ontimer = ontimer;
    }


    public CountDownTimerView(Context context) {
        super(context);
        mcontext = context;
    }

    public CountDownTimerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mcontext = context;
        initStyle(attrs);
        setPaint();
        getView();
       // getPoint(angle_pick);
    }

    public CountDownTimerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mcontext = context;
        initStyle(attrs);
        setPaint();
        getView();
        //getPoint(angle_pick);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
                int x = 90+(event.getY()-y_center>0?1:-1)*(int) (180*Math.atan(Math.abs(event.getY()-y_center)/Math.abs(event.getX()-x_center))/Math.PI);
                int y = 90+(event.getY()-y_center<0?1:-1)*(int) (180*Math.atan(Math.abs(event.getY()-y_center)/Math.abs(event.getX()-x_center))/Math.PI);
                angle = (event.getX()-x_center>0?0:180)+(event.getX()-x_center>0?x:y);
                second = angle * 10;
                countText =getText(second);
                ttime = m * 2;
                invalidate();
        return true;
    }

   private void updata(){
       if(start){
           if(second > 0){
               second--;
               countText =getText(second);
               if(s<30){
                       ttime = m * 2-1;
               }else{
                   ttime = m * 2;
               }
               // angle_pick = second  / 10-second/10;
               // getPoint(angle_pick);
               postInvalidate();
           }else{
               countText ="00:00";
               ttime = 0;
               //getPoint(0);
               postInvalidate();
               ontimer.countdownFinished(0);
               start = false;
               //timer.cancel();
           }
       }

     }
    //定时刷新View
    private Thread thread = new Thread(){
        @Override
        public void run() {
            timer = new Timer();

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    ((Activity)mcontext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updata();
                            //ontimer.getElectric();
                        }
                    });
                }
            };
            timer.schedule(task,0,1000);
            }

    };

    //获取各种属性值
    private void initStyle(AttributeSet attrs){
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CountDownTimerView);
        int attrCount = typedArray.getIndexCount();
        for(int i=0 ; i < attrCount ; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr){
                case R.styleable.CountDownTimerView_number_text_size:
                    ntextsize = (int) typedArray.getDimension(attr,ntextsize);
                    break;
                case R.styleable.CountDownTimerView_string_text_size:
                    stextsixe = (int) typedArray.getDimension(attr,stextsixe);
                    break;
                case R.styleable.CountDownTimerView_text_color:
                    textcolor = typedArray.getColor(attr,textcolor);
                    break;

                case R.styleable.CountDownTimerView_line_width:
                    linewidth = (int) typedArray.getDimension(attr,linewidth);
                    break;
                case R.styleable.CountDownTimerView_line_height:
                    lineheight = (int) typedArray.getDimension(attr,lineheight);
                    break;
                case R.styleable.CountDownTimerView_line_color:
                    linecolor = typedArray.getColor(attr,linecolor);
                    break;
                case R.styleable.CountDownTimerView_line_circle_radius:
                    linecircleradius = (int) typedArray.getDimension(attr,linecircleradius);
                    break;

                case R.styleable.CountDownTimerView_circle_stroke_width:
                    circlewidth = (int) typedArray.getDimension(attr,circlewidth);
                    break;
                case R.styleable.CountDownTimerView_circle_stroke_color:
                    circlecolor = typedArray.getColor(attr,circlecolor);
                    break;
                case R.styleable.CountDownTimerView_circle_radius:
                    circleradius = (int) typedArray.getDimension(attr,circleradius);
                    break;

                case R.styleable.CountDownTimerView_finger_color:
                    fingercolor = typedArray.getColor(attr,fingercolor);
                    break;
            }
        }
        typedArray.recycle();
    }

    //设置各种画笔的属性
    private void setPaint(){
        //默认表盘画笔
        dflinePaint = new Paint();
        dflinePaint.setAntiAlias(true); //防锯齿
        dflinePaint.setDither(true); //防抖动
        dflinePaint.setStrokeWidth(linewidth);
        dflinePaint.setColor(getResources().getColor(dflinecolor));

        //表盘画笔
         linePaint = new Paint();
        linePaint.setAntiAlias(true); //防锯齿
        linePaint.setDither(true); //防抖动
        linePaint.setStrokeWidth(linewidth);
        linePaint.setColor(getResources().getColor(linecolor));

        //圆画笔
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setDither(true);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(circlewidth);
        circlePaint.setColor(getResources().getColor(circlecolor));

        //数值画笔
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(ntextsize);
        textPaint.setColor(getResources().getColor(textcolor));

        //指针画笔
        fingerPaint = new Paint();
        fingerPaint.setAntiAlias(true);
        fingerPaint.setDither(true);
        fingerPaint.setStyle(Paint.Style.FILL);
        fingerPaint.setColor(getResources().getColor(fingercolor));
        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.pick);
       // bitmap_start = BitmapFactory.decodeResource(getResources(),R.drawable.start);
    }


    //获得父view宽高
    public  void getView(){
        vwidth = getWidth();
        vheigth = getHeight();
        x_center = vwidth/2;
        y_center = vheigth/2;
        x = vwidth/100*3;
        y = vheigth/100*2;
        linecircleradius = vwidth/100*31;
        circleradius = vwidth/100*25;
        ntextsize = (int) (vwidth/100*15);


    }
/**
    public void setText(){
        countText = getText(second);
    }*/

    public String getText(int second){
         m = second/60;
         s = second%60;
        countText = (m<10?("0"+m):m)+":"+(s<10?("0"+s):s);
        return countText;
    }

    /**
     *
     * @param canvas 画布
     * @param paint  fingerPaint
     * @param bitmap  pick
     * @param rotation 旋转角度
     * @param posX 画布中心x坐标
     * @param posY 画布中心y坐标
     */

    private void drawRotatePick(Canvas canvas, Paint paint, Bitmap bitmap,
                                  float rotation, float posX, float posY) {
        Matrix matrix = new Matrix();
        int offsetX = bitmap.getWidth() / 2;
        int offsetY = bitmap.getHeight() / 2;
        matrix.postTranslate(-offsetX, -offsetY);
        matrix.postRotate(rotation);
        matrix.postTranslate(posX + offsetX, posY + offsetY);
        canvas.drawBitmap(bitmap, matrix, paint);
    }

    //获得pick的坐标点
    private void getPoint( float rotation){
        switch ((int) rotation){
            case 0 :
                x_pick = x_center-x;
                y_pick = y_center-y-circleradius;
                break;
            case 90 :
                x_pick = x_center-x+circleradius;
                y_pick = y_center-y;
                break;
            case 180 :
                x_pick = x_center-x;
                y_pick = y_center-y+circleradius;
                break;
            case 270 :
                x_pick = x_center-x-circleradius;
                y_pick = y_center-y;
                break;
            default:
                x_pick = (float) (x_center-x+circleradius*Math.sin(rotation*Math.PI/180));
                y_pick = (float) (y_center-y-circleradius*Math.cos(rotation*Math.PI/180));
                break;
        }

    }
    @Override
    protected void onDraw(Canvas canvas) {
        getView();
        canvas.save();
        //画圆
        canvas.drawCircle(x_center,y_center,circleradius,circlePaint);

        //画表盘
        for(int a=0;a <=ttime;a++){
                if(a == ttime){
                    if(ttime != 0){
                        drawRotatePick(canvas,fingerPaint,bitmap,angle_pick,x_center-bitmap.getWidth()/2,y_center-linecircleradius+(linecircleradius-circleradius)/3*2);
                        canvas.drawLine(x_center,y_center-linecircleradius-8,x_center,y_center-linecircleradius+lineheight+7,linePaint);
                        canvas.rotate(360/slcount,x_center,y_center);
                    }else {
                        drawRotatePick(canvas,fingerPaint,bitmap,angle_pick,x_center-bitmap.getWidth()/2,y_center-linecircleradius+(linecircleradius-circleradius)/3*2);
                       canvas.drawLine(x_center,y_center-linecircleradius,x_center,y_center-linecircleradius+lineheight,dflinePaint);
                        canvas.rotate(360/slcount,x_center,y_center);
                    }

                }else{
                    //drawRotatePick(canvas,fingerPaint,bitmap,angle_pick,x_center-bitmap.getWidth()/2,y_center-linecircleradius+(linecircleradius-circleradius)/3*2);
                    canvas.drawLine(x_center,y_center-linecircleradius,x_center,y_center-linecircleradius+lineheight,linePaint);
                    canvas.rotate(360/slcount,x_center,y_center);
                }
            }

        for (int b=ttime;b <slcount-1 ;b++){
            canvas.drawLine(x_center,y_center-linecircleradius,x_center,y_center-linecircleradius+lineheight,dflinePaint);
            canvas.rotate(360/slcount,x_center,y_center);
        }

        //画数值
        textPaint.setTextSize(ntextsize);
        canvas.drawText(countText,x_center,y_center+circleradius/4,textPaint);
        textPaint.setTextSize(stextsixe);
        canvas.drawText("分",x_center-30,y_center-circleradius/4,textPaint);
        canvas.drawText("秒",x_center+30,y_center-circleradius/4,textPaint);

        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, measure(heightMeasureSpec));
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
//调整画布高度
    private int measure(int origin) {
        int specSize = MeasureSpec.getSize(origin);
        return specSize/4*3;
    }

    public void start(){
        if(firstStart){
            thread.start();
            firstStart = false;
        }
        if(second !=0){
            ontimer.countdownStart(second);
            start = true;
            //thread.start();
        }
    }
    public int isZero(){
        return second;
    }
    public void stop(){
       //timer.cancel();
        start = false;
        ontimer.countdownStop();
    }
}
