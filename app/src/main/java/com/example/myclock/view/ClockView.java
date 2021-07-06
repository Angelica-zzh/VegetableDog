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
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ClockView extends View {

    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    private Paint mPaint;
    private float secondDegree;
    private float hourDegree;
    private float minuteDegree;
    Rect textBound = new Rect();

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
    public void initPaint(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);

    }

//    public void updateClock(){
//        if(secondDegree == 360){
//            secondDegree = 0;
//        }
//        if(hourDegree == 360){
//            hourDegree = 0;
//        }
//        if(minuteDegree == 360){
//            minuteDegree = 0;
//        }
//        secondDegree += 6;
//        minuteDegree +=0.1f;
//        hourDegree += 1.0f/120;
//        invalidate();
//    }
    public void setTime(Calendar calendar){
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        Log.d(TAG,"hour"+hour);
        Log.d(TAG,"minute"+minute);
        Log.d(TAG,"second"+second);
        //24小时制
        if(hour >= 12){
            hourDegree = (hour + minute * 1.0f/60f + second *1.0f/3600f -12)*30f;
        }else {
            hourDegree = (hour + minute * 1.0f/60f + second *1.0f/3600f)*30f;
        }

        minuteDegree = (minute+second*1.0f/60f)*6f;
        secondDegree = second*6f;
        invalidate();
    }

    Point center = new Point();
    int radius;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        center.set(getWidth()/2, getHeight()/2);
        radius = getWidth()/3;

        //画圆
        mPaint.setStrokeWidth(3);
        canvas.drawCircle(center.x,center.y,radius,mPaint);
        //圆心
        mPaint.setStrokeWidth(8);
        canvas.drawPoint(center.x,center.y,mPaint);
        //画刻度
        mPaint.setStrokeWidth(1);
        canvas.translate(center.x,center.y);
        for(int i=0;i<360;i++){
            if(i%30 == 0){//长刻度
                canvas.drawLine(radius-25,0,radius,0,mPaint);
            }else if(i%6 == 0){//中刻度
                canvas.drawLine(radius-14,0,radius,0,mPaint);
        }else{
                canvas.drawLine(radius-9,0,radius,0,mPaint);
            }
            canvas.rotate(1);
        }

        //画数字
        mPaint.setTextSize(35);
        mPaint.setStyle(Paint.Style.FILL);
        for (int i=0;i<12;i++){
            if(i==0){
                mPaint.getTextBounds(12+"",0,(12+"").length(),textBound);
                canvas.drawText(12+"",-textBound.width()/2,-(radius-80),mPaint);
                canvas.rotate(30);
            }else{
                mPaint.getTextBounds(i+"",0,(i+"").length(),textBound);
                canvas.drawText(i+"",-textBound.width()/2,-(radius-80),mPaint);
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
        canvas.rotate(minuteDegree);
        canvas.drawLine(0,0,0,-130,mPaint);
        canvas.restore();
        //时针
        canvas.save();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(7);
        canvas.rotate(hourDegree);
        canvas.drawLine(0,0,0,-120,mPaint);
        canvas.restore();

    }

}
