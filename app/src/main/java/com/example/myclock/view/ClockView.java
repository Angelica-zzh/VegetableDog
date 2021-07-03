package com.example.myclock.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.myclock.R;
import com.example.myclock.tools.Utils;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class ClockView extends View {

    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    private Paint mPaint;
    private float secondDegree;
    private float hourDegree;
    private float minuteDegree;

    public ClockView(Context context) {
        super(context);
        this.mContext = context;
        initPaint();
        
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initPaint();

    }
    private Timer timer = new Timer();
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if(secondDegree == 360){
                secondDegree = 0;
            }
            secondDegree += 6;
            postInvalidate();
        }
    };
    public void start(){
        timer.schedule(timerTask,0,1000);
    }

    //时钟画笔
    private Paint circlePaint,numPaint,poiPaint;

    //时钟的圆形宽度，时钟半径，刻度宽度，刻度长度，时针宽度 ，分针宽度，秒针宽度
    private float circleWidth,circleRadius,pointerWidth,pointerLength,hourWidth,minuteWidth,secondWidth;

    //外圆的颜色，刻度的颜色，时针的颜色，分针颜色，秒针的颜色，数字颜色
//    private int circleColor,pointerColor,hourColor,minuteColor,secondColor,numColor;

//    public void Clock(Context context,AttributeSet attributeSet){
//        init(context,attributeSet);
//        initPaint();
//        Calendar calendar = Calendar.getInstance();
//        int hour = calendar.get(Calendar.HOUR);
//        int minute = calendar.get(Calendar.MINUTE);
//        int second = calendar.get(Calendar.SECOND);
//        setTime(hour,minute,second);
//
//
//    }

//    public void init(Context context,AttributeSet attributeSet){
//        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ClockView);
//        circleWidth = typedArray.getDimension(R.styleable.ClockView_clockRingWidth, Utils.dip2px(context,4));
//        pointerWidth = typedArray.getDimension(R.styleable.ClockView_pointerWidth, Utils.dip2px(context,1));
//        hourWidth = typedArray.getDimension(R.styleable.ClockView_hourWidth,5);
//        minuteWidth = typedArray.getDimension(R.styleable.ClockView_minuteWidth,3);
//        secondWidth = typedArray.getDimension(R.styleable.ClockView_secondWidth,2);
//
//        circleColor = typedArray.getColor(R.styleable.ClockView_clockColor, Color.GREEN);
//        pointerColor = typedArray.getColor(R.styleable.ClockView_pointerColor,Color.YELLOW);
//        hourColor = typedArray.getColor(R.styleable.ClockView_hourColor,Color.RED);
//        minuteColor = typedArray.getColor(R.styleable.ClockView_minuteColor,Color.RED);
//        secondColor = typedArray.getColor(R.styleable.ClockView_sencondColor,Color.BLACK);
//        typedArray.recycle();
//
//    }
    //设置画笔
    public void initPaint(){
//        //时钟画笔
//        circlePaint = new Paint();
//        circlePaint.setAntiAlias(true);
//        circlePaint.setStyle(Paint.Style.STROKE);
//        //指针画笔
//        poiPaint= new Paint();
//        poiPaint.setAntiAlias(true);
//        poiPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//        poiPaint.setStrokeCap(Paint.Cap.ROUND);
//        //数字画笔
//        numPaint = new Paint();
//        numPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//        numPaint.setTextSize(60);
//        numPaint.setColor(numColor);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);

    }

    public void setTime(int hour,int minute,int second){

    }

    Point center = new Point();
    int radius;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        center.set(getWidth()/2, getHeight()/2);
        radius = getWidth()/3;
//        Log.d(TAG, "ondraw: " + center + ", radius: " + radius);

        //画圆
        mPaint.setStrokeWidth(3);
        canvas.drawCircle(getWidth()/2,getHeight()/2,getWidth()/3,mPaint);
        //圆心
        mPaint.setStrokeWidth(8);
        canvas.drawPoint(getWidth()/2,getHeight()/2,mPaint);
        //画刻度
        mPaint.setStrokeWidth(1);
        canvas.translate(getWidth()/2,getHeight()/2);
        for(int i=0;i<360;i++){
            if(i%30 == 0){//长刻度
                canvas.drawLine(getWidth()/3-25,0,getWidth()/3,0,mPaint);
            }else if(i%6 == 0){//中刻度
                canvas.drawLine(getWidth()/3-14,0,getWidth()/3,0,mPaint);
        }else{
                canvas.drawLine(getWidth()/3-9,0,getWidth()/3,0,mPaint);
            }
            canvas.rotate(1);
        }

        //画数字
        mPaint.setTextSize(35);
        mPaint.setStyle(Paint.Style.FILL);
        Rect textBound = new Rect();
        for (int i=0;i<12;i++){
            if(i==0){
                mPaint.getTextBounds(12+"",0,(12+"").length(),textBound);
                canvas.drawText(12+"",-textBound.width()/2,-(getWidth()/3-80),mPaint);
                canvas.rotate(30);
            }else{
                mPaint.getTextBounds(i+"",0,(i+"").length(),textBound);
                canvas.drawText(i+"",-textBound.width()/2,-(getWidth()/3-80),mPaint);
                canvas.rotate(30);
            }
        }
        //画秒针
        canvas.save();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        canvas.rotate(secondDegree);
        canvas.drawLine(0,0,0,-190,mPaint);
        canvas.restore();
        //分针
        canvas.save();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        canvas.rotate(30);
        canvas.drawLine(0,0,0,-130,mPaint);
        canvas.restore();
        //时针
        canvas.save();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(7);
        canvas.rotate(90);
        canvas.drawLine(0,0,0,-120,mPaint);
        canvas.restore();

//        drawCircle(canvas);
    }
//    public void drawCircle(Canvas canvas){
//        circlePaint.setStrokeWidth(circleWidth);
//        circlePaint.setColor(circleColor);
//        canvas.drawCircle(0,0,circleRadius,circlePaint);
//        for (int i=0;i<60;i++){
//            circlePaint.setStrokeWidth(pointerWidth);
//            circlePaint.setStrokeWidth(pointerColor);
//            canvas.drawLine(0,-circleRadius+circleWidth/2,0,-circleRadius+pointerWidth,circlePaint);
//            canvas.rotate(6);
//        }
//
//    }

}
