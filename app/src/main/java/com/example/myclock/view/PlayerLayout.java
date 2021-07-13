package com.example.myclock.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.myclock.tools.DisplayUtils;

public class PlayerLayout extends FrameLayout {
    private final String TAG = getClass().getSimpleName();
    int mRatioWidth = 1;
    int mRatioHeight = 1;
    public PlayerLayout(Context context) {
        this(context,null);
    }

    public PlayerLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PlayerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        Log.d(TAG, "setAspectRatio: " + width + "x" + height);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure");
        int viewWidth  = getDefaultSize(getSuggestedMinimumWidth(),  widthMeasureSpec);
        int viewHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);

////        View parent = (View) getParent();
//        int parentWidth = DisplayUtils.getScreenWidthPixels((Activity) getContext());
//        int parentHeight = DisplayUtils.getScreenHeightPixels((Activity) getContext());
        Log.d(TAG, "onMeasure 1: " + viewWidth + "x" + viewHeight);
        if (mRatioWidth != 0 && mRatioHeight != 0) {
            if (1.0 * mRatioWidth / viewWidth > 1.0 * mRatioHeight / viewHeight) {
                viewHeight = (int) (1.0 * viewWidth / mRatioWidth *mRatioHeight);
            } else {
                viewWidth = (int) (1.0 * viewHeight /mRatioHeight * mRatioWidth);
            }
        }
        Log.d(TAG, "onMeasure 2: " + viewWidth + "x" + viewHeight);
        // 撑满屏幕的一边
        if (1.0 * viewWidth / viewHeight < 1.0 * mRatioWidth / mRatioHeight) {
//            viewWidth = parentWidth;
            viewHeight = (int) (1.0 * viewWidth / mRatioWidth *mRatioHeight);
        } else {
//            viewHeight = parentHeight;
            viewWidth = (int) (1.0 * viewHeight /mRatioHeight * mRatioWidth);
        }
        Log.d(TAG, "onMeasure 3: " + viewWidth + "x" + viewHeight);
        setMeasuredDimension(viewWidth, viewHeight);
        measureChildren(MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY));
    }
}
