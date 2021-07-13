package com.example.myclock.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.myclock.R;

public class BatteryView extends View {
    private int mPower = 100;
    private int width;
    private int height;
    private int mColor;
    public BatteryView(Context context) {
        super(context);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Battery);
        mColor = typedArray.getColor(R.styleable.Battery_batteryColor,0xFFFFFFFF);
        mPower = typedArray.getInt(R.styleable.Battery_batteryPower,0);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        typedArray.recycle();
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawHorizontalBattery(canvas);

    }
    private void drawHorizontalBattery(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(mColor);
        paint.setStyle(Paint.Style.STROKE);
        float strokeWidth = width / 20.f;
        float strokeWidth_2 = strokeWidth / 2;
        paint.setStrokeWidth(strokeWidth);
        RectF r1 = new RectF(strokeWidth_2, strokeWidth_2, width - strokeWidth - strokeWidth_2, height - strokeWidth_2);
        //设置外边框颜色为黑色
        paint.setColor(Color.WHITE);
        canvas.drawRoundRect(r1,4f,4f, paint);
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);
        //画电池内矩形电量
        float offset = (width - strokeWidth * 2) * mPower / 100.f;
        RectF r2 = new RectF(strokeWidth, strokeWidth, offset, height - strokeWidth);
        //根据电池电量决定电池内矩形电量颜色
        if (mPower < 30) {
            paint.setColor(Color.RED);
        }
        if (mPower >= 30 && mPower < 50) {
            paint.setColor(Color.BLUE);
        }
        if (mPower >= 50) {
            paint.setColor(Color.GREEN);
        }
        canvas.drawRect(r2, paint);
        //画电池头
        RectF r3 = new RectF(width - strokeWidth, height * 0.25f, width, height * 0.75f);
        //设置电池头颜色为黑色
        paint.setColor(Color.WHITE);
        canvas.drawRect(r3, paint);
        //画电量
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(strokeWidth/3);
        paint.setTextSize(height-strokeWidth*2);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf(mPower),r1.centerX(),r1.centerY()+(height - strokeWidth)/4,paint);
    }
    public void setPower(int power){
        this.mPower = power;
        if(power<0){
            power = 100;
        }
        invalidate();
    }
    public void setColor(int color){
        this.mColor = color;
        invalidate();
    }

}
