package cn.cjwddz.knowu.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.cjwddz.knowu.R;

/**
 * Created by K.B. on 2018/9/28.
 */

public class GradientGraphView extends View {
    Paint linePaint;
    Paint textPaint;
    Paint graphPaint;
    Paint graphShadePaint;
    List<PointF> mValuePointsList = new ArrayList<>();
    List<PointF> mControlPointsList = new ArrayList<>();
    List<Integer> datas = new ArrayList<>();
    private static float SMOOTHNESS = 0.5f;

    private int lineStrokeWidth = 1;
    private int lineColor = R.color.gray;

    private int textColor = R.color.black;
    private int textStrokeWidth = 5;
    private int textSize = 25;

    private int graphStrokeWidth = 2;
    private int graphColor =R.color.mainColor;
    private int graphShadeColor = R.color.mainColor;

    private String startDataX;
    private String endDataX;

    private int[] shadeColors;

    /**
     * 重要参数，两点之间分为几段描画，数字愈大分段越多，描画的曲线就越精细.
     */
    private static final int STEPS = 12;

    float gridX,gridY,xSpace = 0,xValueSpace=0;
    List<String> dateX = new ArrayList<>();
    List<Float> dateY = null;

    List<Integer> colors = null;

    public void setColors(List<Integer> colors) {
        this.colors = colors;
    }

    public List<Float> getDateY() {
        return dateY;
    }

    public void setDateY(List<Float> dateY) {
        this.dateY = dateY;
    }

    public List<Integer> getDatas() {
        return datas;
    }

    public void setDatas(List<Integer> data) {
        this.datas = data;
    }

    public List<String> getDateX() {
        return dateX;
    }

    public void setDateX(List<String> dateX) {
        this.dateX = dateX;
    }


    public GradientGraphView(Context context) {
        super(context);
    }

    public GradientGraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initStyle(attrs);
        init();
    }

    public GradientGraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStyle(attrs);
        init();
    }

    PathEffect pathEffect;
    Path path = new Path();
    Path pathShader = new Path();

    /**
     * 获取xml文档设置的属性值
     * */
    private void initStyle(AttributeSet attrs){
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.GradientGraphView);
        int count = typedArray.getIndexCount();
        for(int i=0;i<count;i++){
            int attr = typedArray.getIndex(i);
            switch (attr){
                case R.styleable.GradientGraphView_lineStrokeWidth:
                    lineStrokeWidth = (int) typedArray.getDimension(attr,lineStrokeWidth);
                    break;
                case R.styleable.GradientGraphView_lineColor:
                    lineColor =  typedArray.getColor(attr,lineColor);
                    break;
                case R.styleable.GradientGraphView_textStrokeWidth:
                    textStrokeWidth = (int) typedArray.getDimension(attr,textStrokeWidth);
                    break;
                case R.styleable.GradientGraphView_textColor:
                    textColor =  typedArray.getColor(attr,textColor);
                    break;
                case R.styleable.GradientGraphView_textSize:
                    textSize = (int) typedArray.getDimension(attr,textSize);
                    break;
                case R.styleable.GradientGraphView_graphColor:
                    graphColor =  typedArray.getColor(attr,graphColor);
                    break;
                case R.styleable.GradientGraphView_graphStrokeWidth:
                    graphStrokeWidth = (int) typedArray.getDimension(attr,graphStrokeWidth);
                    break;
                case R.styleable.GradientGraphView_graphShadeColor:
                    graphShadeColor =  typedArray.getColor(attr,graphShadeColor);
                    break;
                case R.styleable.GradientGraphView_startDataX:
                    startDataX = typedArray.getString(attr);
                    break;
                case R.styleable.GradientGraphView_endDataX:
                    endDataX = typedArray.getString(attr);
                    break;
                default:break;
            }
        }
    }

    /**
     * 初始化画笔
     * */
    @TargetApi(Build.VERSION_CODES.M)
    private void init(){
        linePaint = new Paint();
        textPaint = new Paint();
        graphPaint = new Paint();
        graphShadePaint = new Paint();

        //绘制x轴
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setColor(getResources().getColor(lineColor,null));
        linePaint.setStrokeWidth(lineStrokeWidth);
        linePaint.setAntiAlias(true);

        //绘制文字
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(getResources().getColor(textColor,null));
        textPaint.setStrokeWidth(textStrokeWidth);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(textSize);

        //绘制路径
        graphPaint.setStyle(Paint.Style.STROKE);
        graphPaint.setAntiAlias(true);
        graphPaint.setStrokeWidth(graphStrokeWidth);
        graphPaint.setColor(getResources().getColor(graphColor,null));

        //绘制阴影
        //graphShadePaint.setStyle(Paint.Style.FILL);
        graphShadePaint.setAntiAlias(true);
        graphShadePaint.setStrokeWidth(graphStrokeWidth);
        //graphShadePaint.setColor(getResources().getColor(graphColor,null));

        shadeColors = new int[]{
                Color.argb(100, 255, 86, 86),
                Color.argb(0, 255, 86, 86)};

        datas.clear();
       /** datas.add(0);
        datas.add((int)(Math.random()*10));
        datas.add((int)(Math.random()*20));
        datas.add((int)(Math.random()*70));
        datas.add((int)(Math.random()*60));
        datas.add((int)(Math.random()*30));
        datas.add((int)(Math.random()*10));
        datas.add(0);*/
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawXaxis(canvas);
        if(datas!=null&&datas.size()>0){
            calculateValuePoint(datas,gridY-50,gridX);
        }
        if(mValuePointsList!=null&&mValuePointsList.size()>0){
            calculateControlPoint(mValuePointsList);
            drawPath(canvas,gridY-50);
        }

    }

    /**
     * 画想轴
     * */
    private void drawXaxis(Canvas canvas){
        dateX.clear();
        if(endDataX==null&&startDataX==null){
            dateX.add("0h");
            dateX.add("10h");
        }else{
            dateX.add(startDataX);
            dateX.add(endDataX);
        }

        //基准点。
        gridX = 140;
        gridY = getHeight();
        //X轴点间隔。
        if (dateX != null && dateX.size() > 0) {
            xSpace = (getWidth() - 2 * gridX) / 10;
        }
        float y = 0;
        //画X轴。
        y = gridY - 40;
        canvas.drawLine(gridX, y , getWidth() - gridX, y , linePaint);//X轴.

        //绘制X刻度坐标。
        float x = 0;

        if (dateX != null) {
            for (int n = 0; n < 11; n++) {
                //取X刻度坐标.
                x = gridX + (n) * xSpace;//在原点(0,0)处也画刻度（不画的话就是n+1）,向右移动一个跨度。
                //画X轴具体刻度值。
                canvas.drawCircle(x, y , 5, linePaint);
                if (n == 0) {
                    canvas.drawText(dateX.get(0), x, y + 35, textPaint);//X具体刻度值。
                }
                if (n == 10) {
                    canvas.drawText(dateX.get(1), x, y + 35, textPaint);//X具体刻度值。
                }
            }
        }
    }
    /**
     * 计算点坐标
     * */
    private void calculateValuePoint(List<Integer> data,float height,float gridX) {
        mValuePointsList.clear();
        xValueSpace = (getWidth() - 2 * gridX) / (data.size()-1);
        float max = 0;//数据中最大值
        if(data.size()==1){
            max = data.get(0);
        }else{
            max = data.get(0);
            for(int j=1;j<data.size();j++){
                if(max < data.get(j)){
                    max = data.get(j);
                }
            }
        }
        //System.out.println("-----"+max+"-----");
        //data.add(0);
        for(int i=0;i<data.size();i++){
            float value = data.get(i);
            float scale = value/max;
            float y;
            y = height - scale * (height-50);
            float x = gridX + (i) * xValueSpace;
            mValuePointsList.add(new PointF(x,y));
        }
        //mValuePointsList.add(data.size(),new PointF(getWidth() - 2 * gridX,height));
    }

    /**
     * 计算控制点坐标
     * */
    private void calculateControlPoint(List<PointF> points){
        int count = points.size()-1;
        mControlPointsList.clear();
        if(points.size()<=1){
            return;
        }
        PointF point ;
        PointF nextPoint ;
        PointF prePoint ;
        float preControlX ;
        float preControlY ;
        float nextControlX ;
        float nextControlY ;
        for(int i=0;i<points.size();i++){
           if(i==0){
               //如果是第一个数据点，添加后控制点
               point = points.get(i);
               nextPoint = points.get(i+1);
               nextControlX = point.x + (nextPoint.x - point.x) * SMOOTHNESS;
               nextControlY = point.y;
               mControlPointsList.add(new PointF(nextControlX,nextControlY));
           }else if(i==count){
               //如果是最后一个数据点，添加前控制点
               point = points.get(i);
               prePoint = points.get(i-1);
               preControlX = point.x - (point.x - prePoint.x) * SMOOTHNESS;
               preControlY = point.y;
               mControlPointsList.add(new PointF(preControlX,preControlY));
            }else{
               //中间数据点
               point = points.get(i);
               prePoint = points.get(i-1);
               nextPoint = points.get(i+1);
               float k = (nextPoint.y - prePoint.y)/(nextPoint.x - prePoint.x);
               float b = point.y - k * point.x;
               //添加前控制点
               preControlX = point.x - (point.x - prePoint.x) * SMOOTHNESS;
               preControlY = k * preControlX + b;
               mControlPointsList.add(new PointF(preControlX,preControlY));
               //添加后控制点
               nextControlX = point.x + (nextPoint.x - point.x ) * SMOOTHNESS;
               nextControlY = k * nextControlX + b;
               mControlPointsList.add(new PointF(nextControlX,nextControlY));
           }
        }
    }

    /**
     *绘制路径
     * */
    private void drawPath(Canvas canvas,float height){
        path.reset();
        PointF preControlPoint;
        PointF nextControlPoint;
        PointF nextPoint;
        int count = mValuePointsList.size();
        PointF firstPoint = mValuePointsList.get(0);
        path.moveTo(firstPoint.x,firstPoint.y);
        pathShader.moveTo(firstPoint.x,firstPoint.y);
        //path.lineTo(firstPoint.x,firstPoint.y);
        for(int i = 0;i< mControlPointsList.size();i=i+2){
            preControlPoint = mControlPointsList.get(i);
            nextControlPoint = mControlPointsList.get(i+1);
            nextPoint = mValuePointsList.get(i/2+1);
           // canvas.drawCircle(nextPoint.x,nextPoint.y,10,graphPaint);
            path.cubicTo(preControlPoint.x,preControlPoint.y,nextControlPoint.x,nextControlPoint.y,nextPoint.x,nextPoint.y);
            pathShader.cubicTo(preControlPoint.x,preControlPoint.y,nextControlPoint.x,nextControlPoint.y,nextPoint.x,nextPoint.y);
        }
        PointF lastPoint = mValuePointsList.get(count-1);
        path.lineTo(lastPoint.x,lastPoint.y);
        pathShader.lineTo(lastPoint.x,lastPoint.y);
       // path.lineTo(firstPoint.x,firstPoint.y);
        //Shader mShader = new LinearGradient(0,0,0,height,new int[] {R.color.mainColor,Color.TRANSPARENT},null,Shader.TileMode.CLAMP);
        Shader mShader = new LinearGradient(0,0,0,height,shadeColors,null,Shader.TileMode.REPEAT);
        //graphShadePaint.setAlpha(255);
        graphShadePaint.setShader(mShader);
        canvas.drawPath(pathShader,graphShadePaint);
        path.setLastPoint(lastPoint.x,lastPoint.y);
        canvas.drawPath(path,graphPaint);
    }
}
