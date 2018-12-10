package cn.cjwddz.knowu.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/1.
 */

public class BaseFundChartView extends View {
    Paint linePaint;
    Paint textPaint;
    Paint xyChartPaint;
    Paint chartLinePaint;
    Paint chartJianbianPaint;
    List<Point> points;
    public BaseFundChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BaseFundChartView(Context context) {
        this(context, null);
    }

    public BaseFundChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    PathEffect effect;
    Path path;

    private void init() {
        linePaint = new Paint();
        textPaint = new Paint();
        xyChartPaint = new Paint();
        chartLinePaint = new Paint();
        chartJianbianPaint = new Paint();

        //设置绘制模式为-虚线作为背景线。
        effect = new DashPathEffect(new float[] { 6, 6, 6, 6, 6}, 2);
        //背景虚线路径.
        path = new Path();
        //只是绘制的XY轴
        linePaint.setStyle(Paint.Style.FILL);
//        linePaint.setStrokeWidth((float) 0.7);
        linePaint.setStrokeWidth((float) 1.0);             //设置线宽
        linePaint.setColor(Color.BLACK);
        linePaint.setAntiAlias(true);// 锯齿不显示

        //XY刻度上的字
        textPaint.setStyle(Paint.Style.FILL);// 设置非填充
        textPaint.setStrokeWidth(5);// 笔宽5像素
        textPaint.setColor(Color.BLUE);// 设置为蓝笔
        textPaint.setAntiAlias(true);// 锯齿不显示
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(24);

        //绘制XY轴上的字：Y开关状态、X时间
        xyChartPaint.setStyle(Paint.Style.FILL);
        xyChartPaint.setStrokeWidth(1);
        xyChartPaint.setColor(Color.BLUE);
        xyChartPaint.setAntiAlias(true);
        xyChartPaint.setTextAlign(Paint.Align.CENTER);
        xyChartPaint.setTextSize(18);
        //绘制的折线
        chartLinePaint.setStyle(Paint.Style.STROKE);
        chartLinePaint.setStrokeWidth(5);
        chartLinePaint.setColor(Color.BLUE);
        chartLinePaint.setAntiAlias(true);

        //绘制的折线
        chartJianbianPaint.setStyle(Paint.Style.FILL);
        chartJianbianPaint.setStrokeWidth(5);
        //chartJianbianPaint.setColor(Color.YELLOW);

        chartJianbianPaint.setAntiAlias(true);

    }

    /**
     * 重要参数，两点之间分为几段描画，数字愈大分段越多，描画的曲线就越精细.
     */
    private static final int STEPS = 12;

    float gridX,gridY,xSpace = 0,ySpace = 0,spaceYT = 0,yStart=0;
    List<String> dateX = new ArrayList<>();
    List<Float> dateY = null;

    List<List<Float>> data = null;
    List<Float> datas = new ArrayList<>();

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

    public List<List<Float>> getDatas() {
        return data;
    }

    public void setDatas(List<List<Float>> data) {
        this.data = data;
    }

    public List<String> getDateX() {
        return dateX;
    }

    public void setDateX(List<String> dateX) {
        this.dateX = dateX;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        dateX.add("0h");
        dateX.add("10h");
        //基准点。
        gridX = 140;
        gridY = getHeight() - 50;
        //X轴点间隔。
        if (dateX != null && dateX.size() > 0) {
            xSpace = (getWidth() - 2*gridX) / 10;
            }





        float y = 0;
        //画X轴。
        y = gridY - 20;
        canvas.drawLine(gridX, y - 10, getWidth()-gridX, y - 10, linePaint);//X轴.

        //绘制X刻度坐标。
        float x = 0;

        if (dateX != null) {
            for (int n = 0; n < 11; n++) {
                //取X刻度坐标.
                x = gridX + (n) * xSpace;//在原点(0,0)处也画刻度（不画的话就是n+1）,向右移动一个跨度。
                //画X轴具体刻度值。
                    //canvas.drawLine(x, gridY - 30, x, gridY - 18, linePaint);//短X刻度。
                    canvas.drawCircle(x, y - 10,5,linePaint);
                    if(n==0){
                        canvas.drawText(dateX.get(0), x, gridY + 5, textPaint);//X具体刻度值。
                    }
                    if(n==10){
                        canvas.drawText(dateX.get(1), x, gridY + 5, textPaint);//X具体刻度值。
                    }

            }
        }

        if (data != null && data.size() > 0) {
            float lastPointX = 0; //前一个点
            float lastPointY = 0;
            float currentPointX = 0;//当前点
            float currentPointY = 0;
            for (int n = 0; n < data.size(); n++) {
                List<Float> da = data.get(n);
                List<Float> da_x = new ArrayList<>();
                List<Float> da_y = new ArrayList<>();
                /**
                 * 曲线路劲
                 */
                Path curvePath = new Path();
                /**
                 * 渐变色路径
                 */
                Path jianBianPath = new Path();

                for (int m = 0; m < da.size(); m++) {
                    currentPointX = m * xSpace + gridX;
                    currentPointY = gridY - 30 - ((da.get(m) - yStart) * (ySpace / spaceYT));
                    da_x.add(currentPointX);
                    da_y.add(currentPointY);
//                    if(m>0){
//                        canvas.drawLine(lastPointX, lastPointY, currentPointX, currentPointY, chartLinePaint);
//                    }
//                    lastPointX = currentPointX;
//                    lastPointY = currentPointY;
                }
                List<Cubic> calculate_y = calculate(da_y);
                List<Cubic> calculate_x = calculate(da_x);
                curvePath.moveTo(calculate_x.get(0).eval(0), calculate_y.get(0).eval(0));
                jianBianPath.moveTo(gridX, gridY - 20 - 10);
                jianBianPath.lineTo(calculate_x.get(0).eval(0), calculate_y.get(0).eval(0));
                chartLinePaint.setColor(colors.get(n));
                float lastx = 0;
                for (int i = 0; i < calculate_x.size(); i++) {
                    for (int j = 1; j <= STEPS; j++) {
                        float u = j / (float) STEPS;
                        curvePath.lineTo(calculate_x.get(i).eval(u), calculate_y.get(i)
                                .eval(u));
                        jianBianPath.lineTo(calculate_x.get(i).eval(u), calculate_y.get(i)
                                .eval(u));
                        lastx = calculate_x.get(i).eval(u);
                    }
                }
                jianBianPath.lineTo(lastx, gridY - 20 - 10);
                canvas.drawPath(curvePath, chartLinePaint);
                Shader mShader = new LinearGradient(0, 30 + 10, 0, gridY - 20 - 10, new int[]{colors.get(n), Color.TRANSPARENT}, null, Shader.TileMode.REPEAT);
//新建一个线性渐变，前两个参数是渐变开始的点坐标，第三四个参数是渐变结束的点的坐标。连接这2个点就拉出一条渐变线了，玩过PS的都懂。然后那个数组是渐变的颜色。下一个参数是渐变颜色的分布，如果为空，每个颜色就是均匀分布的。最后是模式，这里设置的是循环渐变

                chartJianbianPaint.setShader(mShader);
                canvas.drawPath(jianBianPath, chartJianbianPaint);
            }
        }
    }

//    /**
//     * 画点.
//     *
//     * @param canvas
//     */
//    private void drawPoints(Canvas canvas) {
//        for (int i = 0; i < points.size(); i++) {
//            Point p = points.get(i);
//            canvas.drawCircle(p.x, p.y, 5, paint);
//        }
//    }


        /**
         * 计算曲线.
         *
         * @param x
         * @return
         */
        private List<Cubic> calculate ( List<Float> x) {
            int n = x.size() - 1;
            float[] gamma = new float[n + 1];
            float[] delta = new float[n + 1];
            float[] D = new float[n + 1];
            int i;
        /*
         * We solve the equation [2 1 ] [D[0]] [3(x[1] - x[0]) ] |1 4 1 | |D[1]|
         * |3(x[2] - x[0]) | | 1 4 1 | | . | = | . | | ..... | | . | | . | | 1 4
         * 1| | . | |3(x[n] - x[n-2])| [ 1 2] [D[n]] [3(x[n] - x[n-1])]
         *
         * by using row operations to convert the matrix to upper triangular and
         * then back sustitution. The D[i] are the derivatives at the knots.
         */

            gamma[0] = 1.0f / 2.0f;
            for (i = 1; i < n; i++) {
                gamma[i] = 1 / (4 - gamma[i - 1]);
            }
            gamma[n] = 1 / (2 - gamma[n - 1]);

            delta[0] = 3 * (x.get(1) - x.get(0)) * gamma[0];
            for (i = 1; i < n; i++) {
                delta[i] = (3 * (x.get(i + 1) - x.get(i - 1)) - delta[i - 1])
                        * gamma[i];
            }
            delta[n] = (3 * (x.get(n) - x.get(n - 1)) - delta[n - 1]) * gamma[n];

            D[n] = delta[n];
            for (i = n - 1; i >= 0; i--) {
                D[i] = delta[i] - gamma[i] * D[i + 1];
            }

        /* now compute the coefficients of the cubics */
            List<Cubic> cubics = new LinkedList<Cubic>();
            for (i = 0; i < n; i++) {
                Cubic c = new Cubic(x.get(i), D[i], 3 * (x.get(i + 1) - x.get(i))
                        - 2 * D[i] - D[i + 1], 2 * (x.get(i) - x.get(i + 1)) + D[i]
                        + D[i + 1]);
                cubics.add(c);
            }
            return cubics;
        }

        class Cubic {
            float a, b, c, d;         /* a + b*u + c*u^2 +d*u^3 */

            public Cubic(float a, float b, float c, float d) {
                this.a = a;
                this.b = b;
                this.c = c;
                this.d = d;
            }


            /**
             * evaluate cubic
             */
            public float eval(float u) {
                return (((d * u) + c) * u + b) * u + a;
            }
        }

}
